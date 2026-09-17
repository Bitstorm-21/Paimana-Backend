package com.example.demo.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatModel chatModel;

    public GeminiService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generateDecisionBrief(
            String projectName,
            String projectState,
            Double riskScore,
            String riskLevel,
            Double expectedOverrun,
            Double worstCaseOverrun,
            Double shockBuffer,
            String shapDrivers,
            String intervention
    ) {

        String prompt = """
                You are Project Sentinel,
                an infrastructure project decision-support assistant.

                IMPORTANT RULES:

                1. Do NOT invent facts.
                2. Use only the supplied project and ML information.
                3. Do NOT change the ML risk score.
                4. Do NOT create a new risk classification.
                5. Explain why the project is risky.
                6. Respect the recommended intervention.
                7. Give concise executive-level advice.

                PROJECT
                Name: %s
                State: %s

                ML RISK
                Risk Score: %.2f
                Risk Level: %s

                FINANCIAL FORECAST
                Expected Overrun: %.2f
                Worst Case Overrun: %.2f
                Shock Buffer: %.2f

                SHAP RISK DRIVERS
                %s

                RULE-BASED INTERVENTION
                %s

                Produce a decision brief with:

                1. Executive Summary
                2. Current Risk
                3. Main Risk Drivers
                4. Financial Exposure
                5. Recommended Action
                6. Immediate Next Step

                Keep the response concise and suitable
                for a government project monitoring dashboard.
                """.formatted(
                projectName,
                projectState,
                riskScore,
                riskLevel,
                expectedOverrun,
                worstCaseOverrun,
                shockBuffer,
                shapDrivers,
                intervention
        );

        return chatModel.call(prompt);
    }
}