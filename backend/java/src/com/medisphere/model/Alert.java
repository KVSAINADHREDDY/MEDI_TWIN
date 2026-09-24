package com.medisphere.model;

public class Alert {
    private String alertId;
    private String patientId;
    private String patientName;
    private String severity;
    private String title;
    private String context;
    private double confidence;
    private String autoAction;
    private boolean acknowledged;
    private String acknowledgedBy;
    private String acknowledgedAt;
    private String ts;
    private String kafkaTopic;
    private double lagSeconds;
    private String rhythmType;

    public Alert() {}

    public Alert(String alertId, String patientId, String patientName, String severity,
                 String title, String context, double confidence, String autoAction,
                 boolean acknowledged, String ts, String kafkaTopic, double lagSeconds, String rhythmType) {
        this.alertId = alertId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.severity = severity;
        this.title = title;
        this.context = context;
        this.confidence = confidence;
        this.autoAction = autoAction;
        this.acknowledged = acknowledged;
        this.ts = ts;
        this.kafkaTopic = kafkaTopic != null ? kafkaTopic : "vitals.wearables.stream";
        this.lagSeconds = lagSeconds;
        this.rhythmType = rhythmType != null ? rhythmType : "AFib";
    }

    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public String getAutoAction() { return autoAction; }
    public void setAutoAction(String autoAction) { this.autoAction = autoAction; }
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
    public String getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(String acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }
    public String getKafkaTopic() { return kafkaTopic; }
    public void setKafkaTopic(String kafkaTopic) { this.kafkaTopic = kafkaTopic; }
    public double getLagSeconds() { return lagSeconds; }
    public void setLagSeconds(double lagSeconds) { this.lagSeconds = lagSeconds; }
    public String getRhythmType() { return rhythmType; }
    public void setRhythmType(String rhythmType) { this.rhythmType = rhythmType; }
}
