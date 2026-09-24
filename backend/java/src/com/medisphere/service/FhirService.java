package com.medisphere.service;

import com.medisphere.model.*;
import com.medisphere.repository.TwinDataStore;
import java.time.Instant;
import java.util.*;

public class FhirService {
    public Map<String, Object> getFhirPatientBundle(String patientId) {
        TwinDataStore store = TwinDataStore.getInstance();
        Patient patient = store.getPatient(patientId);
        if (patient == null) return null;

        Vitals vitals = store.getLatestVitals(patientId);
        LabResult labs = store.getLatestLabs(patientId);
        RiskPrediction pred = store.getPrediction(patientId);

        Map<String, Object> bundle = new LinkedHashMap<>();
        bundle.put("resourceType", "Bundle");
        bundle.put("id", "bundle-" + patient.getPatientId());
        bundle.put("type", "collection");
        bundle.put("timestamp", Instant.now().toString());
        bundle.put("fhirVersion", "4.0.1 (R4)");

        List<Map<String, Object>> entries = new ArrayList<>();

        // Patient Resource
        Map<String, Object> patientRes = new LinkedHashMap<>();
        patientRes.put("resourceType", "Patient");
        patientRes.put("id", patient.getFhirId().replace("Patient/", ""));
        patientRes.put("active", true);
        patientRes.put("name", List.of(Map.of("use", "official", "text", patient.getName())));
        patientRes.put("gender", "M".equalsIgnoreCase(patient.getSex()) ? "male" : "female");
        patientRes.put("birthDate", (2026 - patient.getAge()) + "-04-12");
        patientRes.put("generalPractitioner", List.of(Map.of("display", patient.getProvider())));
        patientRes.put("managingOrganization", Map.of("display", patient.getEhrSource()));
        entries.add(Map.of("fullUrl", "urn:uuid:" + patient.getPatientId(), "resource", patientRes));

        // Observation: Heart Rate
        if (vitals != null) {
            Map<String, Object> hrObs = new LinkedHashMap<>();
            hrObs.put("resourceType", "Observation");
            hrObs.put("status", "final");
            hrObs.put("category", List.of(Map.of("coding", List.of(Map.of("system", "http://terminology.hl7.org/CodeSystem/observation-category", "code", "vital-signs")))));
            hrObs.put("code", Map.of("coding", List.of(Map.of("system", "http://loinc.org", "code", "8867-4", "display", "Heart rate"))));
            hrObs.put("subject", Map.of("reference", patient.getFhirId()));
            hrObs.put("valueQuantity", Map.of("value", vitals.getHeartRate(), "unit", "beats/minute", "system", "http://unitsofmeasure.org", "code", "/min"));
            hrObs.put("effectiveDateTime", vitals.getTs());
            entries.add(Map.of("resource", hrObs));

            // Observation: Blood Pressure
            Map<String, Object> bpObs = new LinkedHashMap<>();
            bpObs.put("resourceType", "Observation");
            bpObs.put("status", "final");
            bpObs.put("category", List.of(Map.of("coding", List.of(Map.of("system", "http://terminology.hl7.org/CodeSystem/observation-category", "code", "vital-signs")))));
            bpObs.put("code", Map.of("coding", List.of(Map.of("system", "http://loinc.org", "code", "85354-9", "display", "Blood pressure panel"))));
            bpObs.put("subject", Map.of("reference", patient.getFhirId()));
            bpObs.put("component", List.of(
                Map.of("code", Map.of("coding", List.of(Map.of("system", "http://loinc.org", "code", "8480-6", "display", "Systolic blood pressure"))), "valueQuantity", Map.of("value", vitals.getSystolic(), "unit", "mmHg")),
                Map.of("code", Map.of("coding", List.of(Map.of("system", "http://loinc.org", "code", "8462-4", "display", "Diastolic blood pressure"))), "valueQuantity", Map.of("value", vitals.getDiastolic(), "unit", "mmHg"))
            ));
            bpObs.put("effectiveDateTime", vitals.getTs());
            entries.add(Map.of("resource", bpObs));
        }

        // Observation: HbA1c
        if (labs != null) {
            Map<String, Object> hba1cObs = new LinkedHashMap<>();
            hba1cObs.put("resourceType", "Observation");
            hba1cObs.put("status", "final");
            hba1cObs.put("code", Map.of("coding", List.of(Map.of("system", "http://loinc.org", "code", "4548-4", "display", "Hemoglobin A1c/Hemoglobin.total in Blood"))));
            hba1cObs.put("subject", Map.of("reference", patient.getFhirId()));
            hba1cObs.put("valueQuantity", Map.of("value", labs.getHba1c(), "unit", "%", "system", "http://unitsofmeasure.org", "code", "%"));
            entries.add(Map.of("resource", hba1cObs));
        }

        // Conditions
        for (String cond : patient.getConditions()) {
            Map<String, Object> condRes = new LinkedHashMap<>();
            condRes.put("resourceType", "Condition");
            condRes.put("clinicalStatus", Map.of("coding", List.of(Map.of("system", "http://terminology.hl7.org/CodeSystem/condition-clinical", "code", "active"))));
            condRes.put("verificationStatus", Map.of("coding", List.of(Map.of("system", "http://terminology.hl7.org/CodeSystem/condition-ver-status", "code", "confirmed"))));
            condRes.put("code", Map.of("text", cond));
            condRes.put("subject", Map.of("reference", patient.getFhirId()));
            entries.add(Map.of("resource", condRes));
        }

        // RiskAssessment
        if (pred != null) {
            Map<String, Object> riskRes = new LinkedHashMap<>();
            riskRes.put("resourceType", "RiskAssessment");
            riskRes.put("status", "final");
            riskRes.put("subject", Map.of("reference", patient.getFhirId()));
            riskRes.put("method", Map.of("text", pred.getModel() + " (TensorFlow Federated)"));
            riskRes.put("prediction", List.of(Map.of(
                "outcome", Map.of("text", pred.getCondition()),
                "probabilityDecimal", pred.getProbability(),
                "qualitativeRisk", Map.of("text", pred.getCategory()),
                "relativeRisk", pred.getRelativeRisk()
            )));
            entries.add(Map.of("resource", riskRes));
        }

        bundle.put("total", entries.size());
        bundle.put("entry", entries);
        return bundle;
    }
}
