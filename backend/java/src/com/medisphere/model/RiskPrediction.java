package com.medisphere.model;

import java.util.*;

public class RiskPrediction {
    public static class FactorContribution {
        private String factor;
        private double contribution;
        private String detail;
        private String direction;

        public FactorContribution() {}
        public FactorContribution(String factor, double contribution, String detail, String direction) {
            this.factor = factor;
            this.contribution = contribution;
            this.detail = detail;
            this.direction = direction != null ? direction : "positive";
        }

        public String getFactor() { return factor; }
        public void setFactor(String factor) { this.factor = factor; }
        public double getContribution() { return contribution; }
        public void setContribution(double contribution) { this.contribution = contribution; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
    }

    private String predId;
    private String patientId;
    private String condition;
    private String model;
    private int federatedRound;
    private double probability;
    private String category;
    private double populationAvg;
    private double relativeRisk;
    private double diabetesComplicationRisk;
    private double readmissionRisk;
    private List<FactorContribution> contributions;
    private List<String> recommendations;
    private String generatedAt;

    public RiskPrediction() {
        this.contributions = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public RiskPrediction(String predId, String patientId, String condition, String model,
                          int federatedRound, double probability, String category,
                          double populationAvg, double relativeRisk, double diabetesComplicationRisk,
                          double readmissionRisk, List<FactorContribution> contributions,
                          List<String> recommendations, String generatedAt) {
        this.predId = predId;
        this.patientId = patientId;
        this.condition = condition;
        this.model = model;
        this.federatedRound = federatedRound;
        this.probability = probability;
        this.category = category;
        this.populationAvg = populationAvg;
        this.relativeRisk = relativeRisk;
        this.diabetesComplicationRisk = diabetesComplicationRisk;
        this.readmissionRisk = readmissionRisk;
        this.contributions = contributions != null ? new ArrayList<>(contributions) : new ArrayList<>();
        this.recommendations = recommendations != null ? new ArrayList<>(recommendations) : new ArrayList<>();
        this.generatedAt = generatedAt;
    }

    public String getPredId() { return predId; }
    public void setPredId(String predId) { this.predId = predId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getFederatedRound() { return federatedRound; }
    public void setFederatedRound(int federatedRound) { this.federatedRound = federatedRound; }
    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPopulationAvg() { return populationAvg; }
    public void setPopulationAvg(double populationAvg) { this.populationAvg = populationAvg; }
    public double getRelativeRisk() { return relativeRisk; }
    public void setRelativeRisk(double relativeRisk) { this.relativeRisk = relativeRisk; }
    public double getDiabetesComplicationRisk() { return diabetesComplicationRisk; }
    public void setDiabetesComplicationRisk(double diabetesComplicationRisk) { this.diabetesComplicationRisk = diabetesComplicationRisk; }
    public double getReadmissionRisk() { return readmissionRisk; }
    public void setReadmissionRisk(double readmissionRisk) { this.readmissionRisk = readmissionRisk; }
    public List<FactorContribution> getContributions() { return contributions; }
    public void setContributions(List<FactorContribution> contributions) { this.contributions = contributions; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}
