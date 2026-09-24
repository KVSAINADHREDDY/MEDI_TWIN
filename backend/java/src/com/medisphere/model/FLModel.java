package com.medisphere.model;

import java.util.*;

public class FLModel {
    public static class HospitalNode {
        private String nodeId;
        private String hospitalName;
        private int localRecords;
        private double localLoss;
        private String status;
        private double epsilon;

        public HospitalNode() {}
        public HospitalNode(String nodeId, String hospitalName, int localRecords, double localLoss, String status, double epsilon) {
            this.nodeId = nodeId;
            this.hospitalName = hospitalName;
            this.localRecords = localRecords;
            this.localLoss = localLoss;
            this.status = status;
            this.epsilon = epsilon;
        }

        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        public String getHospitalName() { return hospitalName; }
        public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
        public int getLocalRecords() { return localRecords; }
        public void setLocalRecords(int localRecords) { this.localRecords = localRecords; }
        public double getLocalLoss() { return localLoss; }
        public void setLocalLoss(double localLoss) { this.localLoss = localLoss; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public double getEpsilon() { return epsilon; }
        public void setEpsilon(double epsilon) { this.epsilon = epsilon; }
    }

    private String modelId;
    private String modelName;
    private int currentRound;
    private double globalAccuracy;
    private double accuracyDelta;
    private double globalLoss;
    private List<HospitalNode> participatingNodes;
    private String framework;
    private String lastAggregation;

    public FLModel() {
        this.participatingNodes = new ArrayList<>();
    }

    public FLModel(String modelId, String modelName, int currentRound, double globalAccuracy,
                   double accuracyDelta, double globalLoss, List<HospitalNode> participatingNodes,
                   String framework, String lastAggregation) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.currentRound = currentRound;
        this.globalAccuracy = globalAccuracy;
        this.accuracyDelta = accuracyDelta;
        this.globalLoss = globalLoss;
        this.participatingNodes = participatingNodes != null ? new ArrayList<>(participatingNodes) : new ArrayList<>();
        this.framework = framework != null ? framework : "TensorFlow Federated (TFF 0.25)";
        this.lastAggregation = lastAggregation;
    }

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public int getCurrentRound() { return currentRound; }
    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
    public double getGlobalAccuracy() { return globalAccuracy; }
    public void setGlobalAccuracy(double globalAccuracy) { this.globalAccuracy = globalAccuracy; }
    public double getAccuracyDelta() { return accuracyDelta; }
    public void setAccuracyDelta(double accuracyDelta) { this.accuracyDelta = accuracyDelta; }
    public double getGlobalLoss() { return globalLoss; }
    public void setGlobalLoss(double globalLoss) { this.globalLoss = globalLoss; }
    public List<HospitalNode> getParticipatingNodes() { return participatingNodes; }
    public void setParticipatingNodes(List<HospitalNode> participatingNodes) { this.participatingNodes = participatingNodes; }
    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }
    public String getLastAggregation() { return lastAggregation; }
    public void setLastAggregation(String lastAggregation) { this.lastAggregation = lastAggregation; }
}
