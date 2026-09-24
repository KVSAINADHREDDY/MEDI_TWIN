package com.medisphere.model;

public class FHIRResource {
    private String resourceId;
    private String resourceType;
    private String patientId;
    private String jsonContent;
    private boolean valid;
    private String fhirVersion;
    private String lastUpdated;

    public FHIRResource() {}

    public FHIRResource(String resourceId, String resourceType, String patientId, String jsonContent, boolean valid, String fhirVersion, String lastUpdated) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.patientId = patientId;
        this.jsonContent = jsonContent;
        this.valid = valid;
        this.fhirVersion = fhirVersion != null ? fhirVersion : "R4 (v4.0.1)";
        this.lastUpdated = lastUpdated;
    }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getJsonContent() { return jsonContent; }
    public void setJsonContent(String jsonContent) { this.jsonContent = jsonContent; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getFhirVersion() { return fhirVersion; }
    public void setFhirVersion(String fhirVersion) { this.fhirVersion = fhirVersion; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
