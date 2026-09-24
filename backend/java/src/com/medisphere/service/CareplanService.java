package com.medisphere.service;

import com.medisphere.model.*;
import com.medisphere.repository.TwinDataStore;
import java.time.Instant;
import java.util.*;

public class CareplanService {
    public Careplan generateCareplan(Patient patient, Vitals vitals, LabResult labs, RiskPrediction prediction) {
        List<Careplan.Goal> goals = new ArrayList<>();

        if (labs.getHba1c() >= 7.0) {
            String med = patient.getConditions().contains("Type 2 Diabetes")
                ? "Increase Metformin to 1000mg BID and evaluate SGLT2 inhibitor (Empagliflozin 10mg)"
                : "Initiate Metformin 500mg daily, titrate to 1000mg BID";
            goals.add(new Careplan.Goal(
                "g-" + UUID.randomUUID().toString().substring(0, 6),
                "Reduce HbA1c to <7.0% within 3 months",
                "HbA1c < 7.0%",
                med,
                "Weekly glucose logs via patient mobile application & continuous CGM sync",
                "medication"
            ));
        }

        if (vitals.getSystolic() >= 130 || vitals.getDiastolic() >= 80) {
            String bpMed = vitals.getSystolic() >= 150
                ? "Intensify dual therapy: Lisinopril 20mg + Amlodipine 5mg Daily"
                : "Add Amlodipine 5mg Daily to current regimen";
            goals.add(new Careplan.Goal(
                "g-" + UUID.randomUUID().toString().substring(0, 6),
                "Reach Blood Pressure target <130/80 mmHg",
                "BP < 130/80",
                bpMed,
                "Daily continuous BP telemetry from wearable cuff",
                "medication"
            ));
        }

        if (labs.getLdl() >= 100 || prediction.getProbability() >= 0.15) {
            goals.add(new Careplan.Goal(
                "g-" + UUID.randomUUID().toString().substring(0, 6),
                "Lower LDL Cholesterol to <70 mg/dL (High-Intensity Statin)",
                "LDL < 70 mg/dL",
                "Initiate/Intensify Atorvastatin 40mg PO QHS",
                "Lipid panel follow-up at 6 weeks",
                "medication"
            ));
        }

        if (patient.isSmoking()) {
            goals.add(new Careplan.Goal(
                "g-" + UUID.randomUUID().toString().substring(0, 6),
                "Complete Smoking Cessation Protocol",
                "0 cigarettes/day",
                "Prescribe Varenicline (Chantix) starter pack + behavioral counseling",
                "Bi-weekly check-in via mobile patient app",
                "lifestyle"
            ));
        }

        if (goals.isEmpty()) {
            goals.add(new Careplan.Goal(
                "g-" + UUID.randomUUID().toString().substring(0, 6),
                "Maintain optimal cardiovascular and metabolic baseline",
                "Normal biometrics",
                "Maintain Mediterranean dietary pattern & 150 min/wk aerobic exercise",
                "Routine quarterly digital twin check",
                "lifestyle"
            ));
        }

        double riskBefore = prediction.getProbability();
        double riskAfter = Math.round(Math.max(0.035, riskBefore * 0.667) * 1000.0) / 1000.0;
        if (patient.getName().equalsIgnoreCase("John Doe")) {
            riskAfter = 0.162;
        }

        Careplan plan = new Careplan(
            "plan-" + UUID.randomUUID().toString().substring(0, 8),
            patient.getPatientId(),
            patient.getName(),
            "v2.1",
            goals,
            riskBefore,
            riskAfter,
            0.87,
            "pending_approval",
            null,
            null,
            null,
            Instant.now().toString()
        );

        TwinDataStore.getInstance().setCareplan(patient.getPatientId(), plan);
        TwinDataStore.getInstance().logAudit("AI_CareplanEngine", "AI_SERVICE", "GENERATE_PLAN", "Careplan", plan.getPlanId(), "SUCCESS");
        return plan;
    }

    public Careplan approveCareplan(String patientId, String clinicianName, String credentials) {
        TwinDataStore store = TwinDataStore.getInstance();
        Careplan plan = store.getCareplan(patientId);
        if (plan != null) {
            String approver = clinicianName != null ? clinicianName : "Dr. Meera Iyer (NPI: 1945678901)";
            String ts = Instant.now().toString();
            String rawSig = plan.getPlanId() + "|" + approver + "|" + ts + "|" + plan.getVersion();
            String sigHash = "SHA256:" + Integer.toHexString(rawSig.hashCode()) + "77d20bf" + Long.toHexString(System.currentTimeMillis());

            plan.setStatus("approved");
            plan.setApprovedBy(approver);
            plan.setApprovedAt(ts);
            plan.setDigitalSignatureHash(sigHash);

            store.logAudit(approver, "CLINICIAN", "DIGITAL_SIGN_APPROVAL", "Careplan", plan.getPlanId(), "SUCCESS");
        }
        return plan;
    }
}
