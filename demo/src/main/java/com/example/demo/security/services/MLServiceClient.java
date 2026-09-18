package com.example.demo.security.services;

import com.example.demo.payload.MLPredictionRequest;
import com.example.demo.payload.MLPredictionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MLServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(MLServiceClient.class);

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;
    private final ObjectMapper objectMapper;

    public MLServiceClient(
            @Value("${ml.service.url:http://localhost:8000}") String mlServiceUrl) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(4000);
        factory.setReadTimeout(6000);

        this.restTemplate = new RestTemplate(factory);
        this.mlServiceUrl = mlServiceUrl;
        this.objectMapper = new ObjectMapper();
    }

    public MLPredictionResponse predict(MLPredictionRequest request) {
        String url = mlServiceUrl + "/predict";
        logger.info("Invoking ML Service at: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MLPredictionRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<MLPredictionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    MLPredictionResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Successfully received prediction from ML service: RiskLevel={}, RiskScore={}",
                        response.getBody().getRiskLevel(), response.getBody().getRiskScore());
                return response.getBody();
            }
        } catch (Exception e) {
            logger.warn("ML Service unavailable at {}: {}. Activating rule-based fallback predictions.",
                    url, e.getMessage());
        }

        return generateFallbackPrediction(request);
    }

    public String serializeShapDrivers(List<Object> shapDrivers) {
        if (shapDrivers == null || shapDrivers.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(shapDrivers);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize SHAP drivers", e);
            return "[]";
        }
    }

    public Object deserializeShapDrivers(String shapDriversJson) {
        if (shapDriversJson == null || shapDriversJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(shapDriversJson, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Intelligent heuristic fallback when Python ML service is offline.
     */
    private MLPredictionResponse generateFallbackPrediction(MLPredictionRequest req) {
        double physical = req.getPhysicalProgress() != null ? req.getPhysicalProgress() : 0.0;
        double burn = req.getFinancialBurnPct() != null ? req.getFinancialBurnPct() : 0.0;
        double divergence = req.getProgressFinancialDivergence() != null ? req.getProgressFinancialDivergence() : 0.0;
        int isStagnant = req.getIsStagnant() != null ? req.getIsStagnant() : 0;
        double originalCost = req.getOriginalCost() != null ? req.getOriginalCost() : 0.0;

        // Calculate heuristic risk score (0 - 100)
        double riskScore = 20.0;
        if (divergence > 15) riskScore += 35;
        else if (divergence > 5) riskScore += 20;

        if (isStagnant == 1) riskScore += 30;
        if (burn > 80 && physical < 70) riskScore += 25;
        riskScore = Math.min(99.0, Math.max(5.0, riskScore));

        String riskLevel;
        if (riskScore >= 75) {
            riskLevel = "CRITICAL";
        } else if (riskScore >= 50) {
            riskLevel = "AMBER";
        } else {
            riskLevel = "ON TRACK";
        }

        // Overrun estimates
        double expectedOverrun = Math.max(0.0, (divergence / 100.0) * originalCost);
        double bestCaseOverrun = Math.max(0.0, expectedOverrun * 0.4);
        double worstCaseOverrun = expectedOverrun * 1.8;
        double shockBuffer = Math.max(0.0, worstCaseOverrun - expectedOverrun);

        // Intervention recommendation
        String code = "MONITOR";
        String recommendation = "Continue routine project monitoring.";
        String priority = "LOW";

        if ("CRITICAL".equals(riskLevel)) {
            if (divergence > 20) {
                code = "SOP-A1";
                recommendation = "Immediate Financial Audit & Fund Freeze";
                priority = "URGENT";
            } else if (isStagnant == 1) {
                code = "SOP-B2";
                recommendation = "Urgent Site Inspection & Contractor Review";
                priority = "URGENT";
            } else {
                code = "SOP-C1";
                recommendation = "Require Escalation to Ministry Level";
                priority = "HIGH";
            }
        } else if ("AMBER".equals(riskLevel)) {
            if (isStagnant == 1) {
                code = "SOP-D4";
                recommendation = "Issue Warning Letter for Stagnation";
                priority = "MEDIUM";
            } else {
                code = "SOP-E5";
                recommendation = "Request Detailed Monthly Status Update";
                priority = "MEDIUM";
            }
        }

        List<Object> fallbackDrivers = new ArrayList<>();
        fallbackDrivers.add(Map.of("feature", "progressFinancialDivergence", "impact", divergence, "direction", divergence > 0 ? "INCREASES_RISK" : "REDUCES_RISK"));
        fallbackDrivers.add(Map.of("feature", "isStagnant", "impact", isStagnant, "direction", isStagnant == 1 ? "INCREASES_RISK" : "REDUCES_RISK"));

        MLPredictionResponse res = new MLPredictionResponse();
        res.setRiskScore(Math.round(riskScore * 100.0) / 100.0);
        res.setRiskLevel(riskLevel);
        res.setBestCaseOverrun(Math.round(bestCaseOverrun * 100.0) / 100.0);
        res.setExpectedOverrun(Math.round(expectedOverrun * 100.0) / 100.0);
        res.setWorstCaseOverrun(Math.round(worstCaseOverrun * 100.0) / 100.0);
        res.setShockBuffer(Math.round(shockBuffer * 100.0) / 100.0);
        res.setShapDrivers(fallbackDrivers);
        res.setInterventionCode(code);
        res.setInterventionRecommendation(recommendation);
        res.setInterventionPriority(priority);

        return res;
    }
}
