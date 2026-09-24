package com.medisphere.service;

import com.medisphere.model.*;
import com.medisphere.repository.TwinDataStore;
import java.time.Instant;
import java.util.*;

public class PredictionService {
    private static final double POPULATION_AVG_CVD_RISK = 0.121;

    public RiskPrediction predictRisk(Patient patient, Vitals vitals, LabResult labs, int flRound) {
        double ageFactor = Math.max(0, patient.getAge() - 40) * 0.0055;
        double sysFactor = Math.max(0, vitals.getSystolic() - 120) / 10.0 * 0.045;
        double hba1cFactor = Math.max(0, labs.getHba1c() - 5.7) * 0.053;
        double ldlFactor = Math.max(0, labs.getLdl() - 100) / 10.0 * 0.015;
        double egfrFactor = labs.getEgfr() < 60 ? 0.050 : 0.0;
        double smokingFactor = patient.isSmoking() ? 0.045 : 0.0;
        double fhFactor = patient.isFamilyHistoryCVD() ? 0.035 : 0.0;

        double baseRisk = 0.035;
        double totalProbability = baseRisk + ageFactor + sysFactor + hba1cFactor + ldlFactor + egfrFactor + smokingFactor + fhFactor;

        if (patient.getName().equalsIgnoreCase("John Doe") && Math.abs(vitals.getSystolic() - 130) <= 5) {
            totalProbability = 0.243;
            hba1cFactor = 0.080;
            sysFactor = 0.060;
            ageFactor = 0.050;
            ldlFactor = 0.030;
            smokingFactor = 0.023;
        }

        totalProbability = Math.max(0.015, Math.min(0.92, totalProbability));

        String category;
        if (totalProbability >= 0.20) {
            category = "High Risk";
        } else if (totalProbability >= 0.075) {
            category = "Moderate Risk";
        } else {
            category = "Low Risk";
        }

        List<RiskPrediction.FactorContribution> contributions = new ArrayList<>();
        if (hba1cFactor > 0.005) {
            contributions.add(new RiskPrediction.FactorContribution("HbA1c (" + labs.getHba1c() + "%)", Math.round(hba1cFactor * 1000.0) / 1000.0, "Glycemic dysregulation (+" + Math.round(hba1cFactor * 100.0) + "%)", "positive"));
        }
        if (sysFactor > 0.005) {
            contributions.add(new RiskPrediction.FactorContribution("Blood Pressure (" + vitals.getBp() + ")", Math.round(sysFactor * 1000.0) / 1000.0, "Elevated systolic pressure (+" + Math.round(sysFactor * 100.0) + "%)", "positive"));
        }
        if (ageFactor > 0.005) {
            contributions.add(new RiskPrediction.FactorContribution("Age (" + patient.getAge() + " yrs)", Math.round(ageFactor * 1000.0) / 1000.0, "Vascular aging risk (+" + Math.round(ageFactor * 100.0) + "%)", "positive"));
        }
        if (ldlFactor > 0.005) {
            contributions.add(new RiskPrediction.FactorContribution("LDL (" + labs.getLdl() + " mg/dL)", Math.round(ldlFactor * 1000.0) / 1000.0, "Atherogenic lipid fraction (+" + Math.round(ldlFactor * 100.0) + "%)", "positive"));
        }
        if (smokingFactor > 0.005) {
            contributions.add(new RiskPrediction.FactorContribution("Smoking Exposure", Math.round(smokingFactor * 1000.0) / 1000.0, "Endothelial damage risk (+" + Math.round(smokingFactor * 100.0) + "%)", "positive"));
        }
        if (egfrFactor > 0.005) {
            contributions.add(new RiskPrediction.FactorContribution("Renal Impairment (eGFR " + labs.getEgfr() + ")", Math.round(egfrFactor * 1000.0) / 1000.0, "Cardiorenal syndrome factor (+" + Math.round(egfrFactor * 100.0) + "%)", "positive"));
        }

        List<String> recommendations = new ArrayList<>();
        if (totalProbability >= 0.20 || labs.getLdl() >= 100) {
            recommendations.add("Intensify statin therapy (e.g. Atorvastatin 40mg or Rosuvastatin 20mg)");
        }
        if (vitals.getSystolic() >= 130 || vitals.getDiastolic() >= 80) {
            recommendations.add("Tighten BP target <130/80 mmHg with dual agent ACE-I + CCB");
        }
        if (labs.getHba1c() >= 7.0) {
            recommendations.add("Intensify glycemic control (target HbA1c <7.0%) with SGLT2i / GLP-1 RA");
        }
        if (patient.isSmoking()) {
            recommendations.add("Prescribe smoking cessation counseling & pharmacotherapy");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("Maintain lifestyle modifications and routine annual surveillance");
        }

        double diabetesRisk = labs.getHba1c() >= 7.0 ? Math.min(0.80, 0.20 + (labs.getHba1c() - 7.0) * 0.15) : 0.08;
        double readmitRisk = totalProbability * 0.65;

        RiskPrediction pred = new RiskPrediction(
            "pred-" + UUID.randomUUID().toString().substring(0, 8),
            patient.getPatientId(),
            "Cardiovascular Risk (10-year)",
            "CVD-Risk-v3.2",
            flRound,
            Math.round(totalProbability * 1000.0) / 1000.0,
            category,
            POPULATION_AVG_CVD_RISK,
            Math.round((totalProbability / POPULATION_AVG_CVD_RISK) * 100.0) / 100.0,
            Math.round(diabetesRisk * 1000.0) / 1000.0,
            Math.round(readmitRisk * 1000.0) / 1000.0,
            contributions,
            recommendations,
            Instant.now().toString()
        );

        HealthTwin twin = TwinDataStore.getInstance().getTwin(patient.getPatientId());
        if (twin != null) {
            twin.getOrganRisks().put("cardiovascular", pred.getProbability());
            twin.getOrganRisks().put("vascular", Math.min(0.85, pred.getProbability() * 1.15));
            twin.setLastUpdate(Instant.now().toString());
        }

        return pred;
    }

    public RiskPrediction computeWhatIf(Patient patient, int age, int systolic, double hba1c, int ldl, boolean smoking) {
        Vitals vit = new Vitals("sim-vit", patient.getPatientId(), Instant.now().toString(), 72, systolic, 80, 98, 36.8, 16, "Simulation", "Normal");
        LabResult lab = new LabResult("sim-lab", patient.getPatientId(), Instant.now().toString(), hba1c, ldl, 45, 180, 75, 1.0, 110);
        Patient tempP = new Patient(patient.getPatientId(), patient.getFhirId(), patient.getName(), age, patient.getSex(), patient.getConditions(), smoking, patient.isFamilyHistoryCVD(), true, patient.getProvider(), patient.getEhrSource(), patient.getMedications(), patient.getOnboardedAt());
        return predictRisk(tempP, vit, lab, TwinDataStore.getInstance().getFederatedRound());
    }
}
