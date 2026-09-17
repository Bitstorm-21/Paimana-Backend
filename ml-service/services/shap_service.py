import shap
import numpy as np

from services.prediction_service import (
    rf_model,
    FEATURE_NAMES
)


explainer = shap.TreeExplainer(rf_model)


def explain(features_dict):

    features = np.array([[
        features_dict["physicalProgress"],
        features_dict["financialBurnPct"],
        features_dict["progressVelocity"],
        features_dict["progressFinancialDivergence"],
        features_dict["isStagnant"]
    ]])

    shap_values = explainer.shap_values(features)

    # Handle common SHAP output formats
    if isinstance(shap_values, list):

        values = shap_values[1][0]

    elif len(np.shape(shap_values)) == 3:

        values = shap_values[0, :, 1]

    else:

        values = shap_values[0]

    drivers = []

    for name, value in zip(
            FEATURE_NAMES,
            values
    ):

        drivers.append({
            "feature": name,
            "impact": round(float(value), 4),
            "direction":
                "INCREASES_RISK"
                if value > 0
                else "REDUCES_RISK"
        })

    drivers.sort(
        key=lambda x: abs(x["impact"]),
        reverse=True
    )

    return drivers