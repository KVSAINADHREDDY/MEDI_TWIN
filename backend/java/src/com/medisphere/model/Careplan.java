package com.medisphere.model;

import java.util.*;

public class Careplan {
    public static class Goal {
        private String goalId;
        private String goal;
        private String targetMetric;
        private String intervention;
        private String monitoring;
        private String category;

        public Goal() {}
        public Goal(String goalId, String goal, String targetMetric, String intervention, String monitoring, String category) {
            this.goalId = goalId;
            this.goal = goal;
            this.targetMetric = targetMetric;
            this.intervention = intervention;
            this.monitoring = monitoring;
            this.category = category;
        }

        public String getGoalId() { return goalId; }
        public void setGoalId(String goalId) { this.goalId = goalId; }
        public String getGoal() { return goal; }
        public void setGoal(String goal) { this.goal = goal; }
        public String getTargetMetric() { return targetMetric; }
        public void setTargetMetric(String targetMetric) { this.targetMetric = targetMetric; }
        public String getIntervention() { return intervention; }
        public void setIntervention(String intervention) { this.intervention = intervention; }
        public String getMonitoring() { return monitoring; }
        public void setMonitoring(String monitoring) { this.monitoring = monitoring; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    private String planId;
    private String patientId;
    private String patientName;
    private String version;
    private List<Goal> goals;
    private double predictedRiskBefore;
    private double predictedRiskAfter;
    private double adherenceScore;
    private String status;
    private String approvedBy;
    private String approvedAt;
    private String digitalSignatureHash;
    private String generatedAt;

    public Careplan() {
        this.goals = new ArrayList<>();
    }

    public Careplan(String planId, String patientId, String patientName, String version,
                    List<Goal> goals, double predictedRiskBefore, double predictedRiskAfter,
                    double adherenceScore, String status, String approvedBy, String approvedAt,
                    String digitalSignatureHash, String generatedAt) {
        this.planId = planId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.version = version;
        this.goals = goals != null ? new ArrayList<>(goals) : new ArrayList<>();
        this.predictedRiskBefore = predictedRiskBefore;
        this.predictedRiskAfter = predictedRiskAfter;
        this.adherenceScore = adherenceScore;
        this.status = status != null ? status : "pending_approval";
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.digitalSignatureHash = digitalSignatureHash;
        this.generatedAt = generatedAt;
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public List<Goal> getGoals() { return goals; }
    public void setGoals(List<Goal> goals) { this.goals = goals; }
    public double getPredictedRiskBefore() { return predictedRiskBefore; }
    public void setPredictedRiskBefore(double predictedRiskBefore) { this.predictedRiskBefore = predictedRiskBefore; }
    public double getPredictedRiskAfter() { return predictedRiskAfter; }
    public void setPredictedRiskAfter(double predictedRiskAfter) { this.predictedRiskAfter = predictedRiskAfter; }
    public double getAdherenceScore() { return adherenceScore; }
    public void setAdherenceScore(double adherenceScore) { this.adherenceScore = adherenceScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getApprovedAt() { return approvedAt; }
    public void setApprovedAt(String approvedAt) { this.approvedAt = approvedAt; }
    public String getDigitalSignatureHash() { return digitalSignatureHash; }
    public void setDigitalSignatureHash(String digitalSignatureHash) { this.digitalSignatureHash = digitalSignatureHash; }
    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}
