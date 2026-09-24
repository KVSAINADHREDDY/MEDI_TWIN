package com.medisphere.model;

public class Vitals {
    private String vitalsId;
    private String patientId;
    private String ts;
    private int heartRate;
    private int systolic;
    private int diastolic;
    private String bp;
    private int spo2;
    private double temperature;
    private int respiratoryRate;
    private String source;
    private String rhythmStatus;

    public Vitals() {}

    public Vitals(String vitalsId, String patientId, String ts, int heartRate,
                  int systolic, int diastolic, int spo2, double temperature,
                  int respiratoryRate, String source, String rhythmStatus) {
        this.vitalsId = vitalsId;
        this.patientId = patientId;
        this.ts = ts;
        this.heartRate = heartRate;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.bp = systolic + "/" + diastolic;
        this.spo2 = spo2;
        this.temperature = temperature;
        this.respiratoryRate = respiratoryRate;
        this.source = source != null ? source : "Wearable Stream (Kafka)";
        this.rhythmStatus = rhythmStatus != null ? rhythmStatus : "Normal Sinus Rhythm";
    }

    public String getVitalsId() { return vitalsId; }
    public void setVitalsId(String vitalsId) { this.vitalsId = vitalsId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }
    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }
    public int getSystolic() { return systolic; }
    public void setSystolic(int systolic) {
        this.systolic = systolic;
        this.bp = this.systolic + "/" + this.diastolic;
    }
    public int getDiastolic() { return diastolic; }
    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
        this.bp = this.systolic + "/" + this.diastolic;
    }
    public String getBp() { return bp; }
    public void setBp(String bp) { this.bp = bp; }
    public int getSpo2() { return spo2; }
    public void setSpo2(int spo2) { this.spo2 = spo2; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(int respiratoryRate) { this.respiratoryRate = respiratoryRate; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRhythmStatus() { return rhythmStatus; }
    public void setRhythmStatus(String rhythmStatus) { this.rhythmStatus = rhythmStatus; }
}
