import os
import joblib
import numpy as np


BASE_DIR = os.path.dirname(
    os.path.dirname(os.path.abspath(__file__))
)

MODEL_DIR = os.path.join(BASE_DIR, "models")


def load_model(filename):

    path = os.path.join(MODEL_DIR, filename)

    if not os.path.exists(path):
        raise FileNotFoundError(
            f"Model not found: {path}"
        )

    return joblib.load(path)


# ============================================================
# LOAD TRAINED MODELS
# ============================================================

rf_model = load_model(
    "sentinel_rf_classifier.pkl"
)

lower_model = load_model(
    "sentinel_quantile_lower.pkl"
)

median_model = load_model(
    "sentinel_quantile_median.pkl"
)

upper_model = load_model(
    "sentinel_quantile_upper.pkl"
)


# ============================================================
# FEATURES
# ============================================================

FEATURE_NAMES = [
    "physicalProgress",
    "financialBurnPct",
    "progressVelocity",
    "progressFinancialDivergence",
    "isStagnant"
]


# ============================================================
# PREDICTION
# ============================================================

def predict(features_dict):

    features = np.array([[
        features_dict["physicalProgress"],
        features_dict["financialBurnPct"],
        features_dict["progressVelocity"],
        features_dict["progressFinancialDivergence"],
        features_dict["isStagnant"]
    ]])

    # --------------------------------------------------------
    # RISK
    # --------------------------------------------------------

    risk_score = (
            rf_model.predict_proba(features)[0][1] * 100
    )

    if risk_score >= 75:
        risk_level = "CRITICAL"

    elif risk_score >= 50:
        risk_level = "AMBER"

    else:
        risk_level = "ON TRACK"

    # --------------------------------------------------------
    # FINANCIAL FORECAST
    # --------------------------------------------------------

    best_case = max(
        0,
        float(lower_model.predict(features)[0])
    )

    expected = max(
        0,
        float(median_model.predict(features)[0])
    )

    worst_case = max(
        0,
        float(upper_model.predict(features)[0])
    )

    shock_buffer = max(
        0,
        worst_case - expected
    )

    return {
        "riskScore": round(float(risk_score), 2),
        "riskLevel": risk_level,

        "bestCaseOverrun": round(best_case, 2),
        "expectedOverrun": round(expected, 2),
        "worstCaseOverrun": round(worst_case, 2),

        "shockBuffer": round(shock_buffer, 2)
    }