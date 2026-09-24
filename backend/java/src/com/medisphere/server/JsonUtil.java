package com.medisphere.server;

import com.medisphere.model.*;
import java.util.*;

public class JsonUtil {
    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escape((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map) return mapToJson((Map<?, ?>) obj);
        if (obj instanceof Iterable) return listToJson((Iterable<?>) obj);
        if (obj instanceof Patient) return patientToJson((Patient) obj);
        if (obj instanceof HealthTwin) return twinToJson((HealthTwin) obj);
        if (obj instanceof Vitals) return vitalsToJson((Vitals) obj);
        if (obj instanceof LabResult) return labsToJson((LabResult) obj);
        if (obj instanceof RiskPrediction) return predictionToJson((RiskPrediction) obj);
        if (obj instanceof Careplan) return careplanToJson((Careplan) obj);
        if (obj instanceof Alert) return alertToJson((Alert) obj);
        if (obj instanceof AuditLog) return auditToJson((AuditLog) obj);
        if (obj instanceof FLModel) return flModelToJson((FLModel) obj);
        return "\"" + escape(obj.toString()) + "\"";
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escape(String.valueOf(entry.getKey()))).append("\":");
            sb.append(toJson(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    public static String listToJson(Iterable<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(",");
            first = false;
            sb.append(toJson(item));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String patientToJson(Patient p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("patientId", p.getPatientId());
        m.put("fhirId", p.getFhirId());
        m.put("name", p.getName());
        m.put("age", p.getAge());
        m.put("sex", p.getSex());
        m.put("conditions", p.getConditions());
        m.put("smoking", p.isSmoking());
        m.put("familyHistoryCVD", p.isFamilyHistoryCVD());
        m.put("consent", p.isConsent());
        m.put("provider", p.getProvider());
        m.put("ehrSource", p.getEhrSource());
        m.put("medications", p.getMedications());
        m.put("onboardedAt", p.getOnboardedAt());
        return mapToJson(m);
    }

    public static String twinToJson(HealthTwin t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("twinId", t.getTwinId());
        m.put("patientId", t.getPatientId());
        m.put("modelVersion", t.getModelVersion());
        m.put("lastUpdate", t.getLastUpdate());
        m.put("dataCompleteness", t.getDataCompleteness());
        m.put("organRisks", t.getOrganRisks());
        return mapToJson(m);
    }

    public static String vitalsToJson(Vitals v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("vitalsId", v.getVitalsId());
        m.put("patientId", v.getPatientId());
        m.put("ts", v.getTs());
        m.put("heartRate", v.getHeartRate());
        m.put("systolic", v.getSystolic());
        m.put("diastolic", v.getDiastolic());
        m.put("bp", v.getBp());
        m.put("spo2", v.getSpo2());
        m.put("temperature", v.getTemperature());
        m.put("respiratoryRate", v.getRespiratoryRate());
        m.put("source", v.getSource());
        m.put("rhythmStatus", v.getRhythmStatus());
        return mapToJson(m);
    }

    public static String labsToJson(LabResult l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("resultId", l.getResultId());
        m.put("patientId", l.getPatientId());
        m.put("ts", l.getTs());
        m.put("hba1c", l.getHba1c());
        m.put("ldl", l.getLdl());
        m.put("hdl", l.getHdl());
        m.put("totalCholesterol", l.getTotalCholesterol());
        m.put("egfr", l.getEgfr());
        m.put("creatinine", l.getCreatinine());
        m.put("fastingGlucose", l.getFastingGlucose());
        return mapToJson(m);
    }

    public static String predictionToJson(RiskPrediction p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("predId", p.getPredId());
        m.put("patientId", p.getPatientId());
        m.put("condition", p.getCondition());
        m.put("model", p.getModel());
        m.put("federatedRound", p.getFederatedRound());
        m.put("probability", p.getProbability());
        m.put("category", p.getCategory());
        m.put("populationAvg", p.getPopulationAvg());
        m.put("relativeRisk", p.getRelativeRisk());
        m.put("diabetesComplicationRisk", p.getDiabetesComplicationRisk());
        m.put("readmissionRisk", p.getReadmissionRisk());

        List<Map<String, Object>> contribs = new ArrayList<>();
        for (RiskPrediction.FactorContribution fc : p.getContributions()) {
            contribs.add(Map.of(
                "factor", fc.getFactor(),
                "contribution", fc.getContribution(),
                "detail", fc.getDetail(),
                "direction", fc.getDirection()
            ));
        }
        m.put("contributions", contribs);
        m.put("recommendation", p.getRecommendations());
        m.put("generatedAt", p.getGeneratedAt());
        return mapToJson(m);
    }

    public static String careplanToJson(Careplan cp) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("planId", cp.getPlanId());
        m.put("patientId", cp.getPatientId());
        m.put("patientName", cp.getPatientName());
        m.put("version", cp.getVersion());

        List<Map<String, Object>> goals = new ArrayList<>();
        for (Careplan.Goal g : cp.getGoals()) {
            goals.add(Map.of(
                "goalId", g.getGoalId(),
                "goal", g.getGoal(),
                "targetMetric", g.getTargetMetric() != null ? g.getTargetMetric() : "",
                "intervention", g.getIntervention(),
                "monitoring", g.getMonitoring(),
                "category", g.getCategory() != null ? g.getCategory() : "medication"
            ));
        }
        m.put("goals", goals);
        m.put("predictedRiskBefore", cp.getPredictedRiskBefore());
        m.put("predictedRiskAfter", cp.getPredictedRiskAfter());
        m.put("adherenceScore", cp.getAdherenceScore());
        m.put("status", cp.getStatus());
        m.put("approvedBy", cp.getApprovedBy() != null ? cp.getApprovedBy() : "");
        m.put("approvedAt", cp.getApprovedAt() != null ? cp.getApprovedAt() : "");
        m.put("digitalSignatureHash", cp.getDigitalSignatureHash() != null ? cp.getDigitalSignatureHash() : "");
        m.put("generatedAt", cp.getGeneratedAt());
        return mapToJson(m);
    }

    public static String alertToJson(Alert a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("alertId", a.getAlertId());
        m.put("patientId", a.getPatientId());
        m.put("patientName", a.getPatientName());
        m.put("severity", a.getSeverity());
        m.put("title", a.getTitle());
        m.put("context", a.getContext());
        m.put("confidence", a.getConfidence());
        m.put("autoAction", a.getAutoAction());
        m.put("acknowledged", a.isAcknowledged());
        m.put("acknowledgedBy", a.getAcknowledgedBy() != null ? a.getAcknowledgedBy() : "");
        m.put("acknowledgedAt", a.getAcknowledgedAt() != null ? a.getAcknowledgedAt() : "");
        m.put("ts", a.getTs());
        m.put("kafkaTopic", a.getKafkaTopic());
        m.put("lagSeconds", a.getLagSeconds());
        m.put("rhythmType", a.getRhythmType());
        return mapToJson(m);
    }

    public static String auditToJson(AuditLog a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("logId", a.getLogId());
        m.put("timestamp", a.getTimestamp());
        m.put("actor", a.getActor());
        m.put("role", a.getRole());
        m.put("action", a.getAction());
        m.put("resourceType", a.getResourceType());
        m.put("resourceId", a.getResourceId());
        m.put("ipAddress", a.getIpAddress());
        m.put("outcome", a.getOutcome());
        m.put("integrityHash", a.getIntegrityHash());
        return mapToJson(m);
    }

    public static String flModelToJson(FLModel m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("modelId", m.getModelId());
        map.put("modelName", m.getModelName());
        map.put("currentRound", m.getCurrentRound());
        map.put("globalAccuracy", m.getGlobalAccuracy());
        map.put("accuracyDelta", m.getAccuracyDelta());
        map.put("globalLoss", m.getGlobalLoss());
        map.put("framework", m.getFramework());
        map.put("lastAggregation", m.getLastAggregation());

        List<Map<String, Object>> nodes = new ArrayList<>();
        for (FLModel.HospitalNode n : m.getParticipatingNodes()) {
            nodes.add(Map.of(
                "nodeId", n.getNodeId(),
                "hospitalName", n.getHospitalName(),
                "localRecords", n.getLocalRecords(),
                "localLoss", n.getLocalLoss(),
                "status", n.getStatus(),
                "epsilon", n.getEpsilon()
            ));
        }
        map.put("participatingNodes", nodes);
        return mapToJson(map);
    }
}
