package com.medisphere.model;

public class AuditLog {
    private String logId;
    private String timestamp;
    private String actor;
    private String role;
    private String action;
    private String resourceType;
    private String resourceId;
    private String ipAddress;
    private String outcome;
    private String integrityHash;

    public AuditLog() {}

    public AuditLog(String logId, String timestamp, String actor, String role, String action,
                    String resourceType, String resourceId, String ipAddress, String outcome, String integrityHash) {
        this.logId = logId;
        this.timestamp = timestamp;
        this.actor = actor;
        this.role = role;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.ipAddress = ipAddress;
        this.outcome = outcome;
        this.integrityHash = integrityHash;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public String getIntegrityHash() { return integrityHash; }
    public void setIntegrityHash(String integrityHash) { this.integrityHash = integrityHash; }
}
