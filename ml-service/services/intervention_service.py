def recommend_intervention(
        risk_level,
        divergence,
        is_stagnant,
        progress_velocity
):

    # ========================================================
    # CRITICAL
    # ========================================================

    if risk_level == "CRITICAL":

        if divergence > 20:

            return {
                "code": "SOP-A1",
                "recommendation":
                    "Immediate Financial Audit & Fund Freeze",
                "priority": "URGENT"
            }

        elif is_stagnant == 1:

            return {
                "code": "SOP-B2",
                "recommendation":
                    "Urgent Site Inspection & Contractor Review",
                "priority": "URGENT"
            }

        else:

            return {
                "code": "SOP-C1",
                "recommendation":
                    "Require Escalation to Ministry Level",
                "priority": "HIGH"
            }

    # ========================================================
    # AMBER
    # ========================================================

    elif risk_level == "AMBER":

        if is_stagnant == 1:

            return {
                "code": "SOP-D4",
                "recommendation":
                    "Issue Warning Letter for Stagnation",
                "priority": "MEDIUM"
            }

        else:

            return {
                "code": "SOP-E5",
                "recommendation":
                    "Request Detailed Monthly Status Update",
                "priority": "MEDIUM"
            }

    # ========================================================
    # ON TRACK
    # ========================================================

    return {
        "code": "MONITOR",
        "recommendation":
            "Continue Standard Monitoring",
        "priority": "LOW"
    }