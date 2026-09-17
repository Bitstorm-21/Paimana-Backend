from fastapi import FastAPI
from pydantic import BaseModel

from services.prediction_service import predict
from services.shap_service import explain
from services.intervention_service import recommend_intervention


app = FastAPI(
    title="Project Sentinel ML Service"
)


# ============================================================
# REQUEST
# ============================================================

class PredictionRequest(BaseModel):

    physicalProgress: float
    financialBurnPct: float
    progressVelocity: float
    progressFinancialDivergence: float
    isStagnant: int

    originalCost: float


# ============================================================
# RESPONSE
# ============================================================

class PredictionResponse(BaseModel):

    riskScore: float
    riskLevel: str

    bestCaseOverrun: float
    expectedOverrun: float
    worstCaseOverrun: float

    shockBuffer: float

    shapDrivers: list

    interventionCode: str
    interventionRecommendation: str
    interventionPriority: str


# ============================================================
# HEALTH
# ============================================================

@app.get("/")
def home():

    return {
        "message":
            "Project Sentinel ML Service is running"
    }


# ============================================================
# PREDICT
# ============================================================

@app.post(
    "/predict",
    response_model=PredictionResponse
)
def predict_endpoint(request: PredictionRequest):

    features = {
        "physicalProgress":
            request.physicalProgress,

        "financialBurnPct":
            request.financialBurnPct,

        "progressVelocity":
            request.progressVelocity,

        "progressFinancialDivergence":
            request.progressFinancialDivergence,

        "isStagnant":
            request.isStagnant
    }

    # --------------------------------------------------------
    # ML PREDICTION
    # --------------------------------------------------------

    prediction = predict(features)

    # --------------------------------------------------------
    # SHAP
    # --------------------------------------------------------

    shap_drivers = explain(features)

    # --------------------------------------------------------
    # INTERVENTION
    # --------------------------------------------------------

    intervention = recommend_intervention(

        prediction["riskLevel"],

        request.progressFinancialDivergence,

        request.isStagnant,

        request.progressVelocity
    )

    return {

        **prediction,

        "shapDrivers":
            shap_drivers,

        "interventionCode":
            intervention["code"],

        "interventionRecommendation":
            intervention["recommendation"],

        "interventionPriority":
            intervention["priority"]
    }