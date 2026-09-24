package com.medisphere.model;

import java.util.*;

public class HealthTwin {
    private String twinId;
    private String patientId;
    private String modelVersion;
    private String lastUpdate;
    private double dataCompleteness;
    private Map<String, Double> organRisks;

    public HealthTwin() {
        this.organRisks = new HashMap<>();
    }

    public HealthTwin(String twinId, String patientId, String modelVersion, String lastUpdate,
                      double dataCompleteness, Map<String, Double> organRisks) {
        this.twinId = twinId;
        this.patientId = patientId;
        this.modelVersion = modelVersion;
        this.lastUpdate = lastUpdate;
        this.dataCompleteness = dataCompleteness;
        this.organRisks = organRisks != null ? new HashMap<>(organRisks) : new HashMap<>();
    }

    public String getTwinId() { return twinId; }
    public void setTwinId(String twinId) { this.twinId = twinId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    public double getDataCompleteness() { return dataCompleteness; }
    public void setDataCompleteness(double dataCompleteness) { this.dataCompleteness = dataCompleteness; }
    public Map<String, Double> getOrganRisks() { return organRisks; }
    public void setOrganRisks(Map<String, Double> organRisks) { this.organRisks = organRisks; }
}
