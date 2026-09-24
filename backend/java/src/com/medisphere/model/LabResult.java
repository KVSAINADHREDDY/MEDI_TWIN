package com.medisphere.model;

public class LabResult {
    private String resultId;
    private String patientId;
    private String ts;
    private double hba1c;
    private int ldl;
    private int hdl;
    private int totalCholesterol;
    private int egfr;
    private double creatinine;
    private int fastingGlucose;

    public LabResult() {}

    public LabResult(String resultId, String patientId, String ts, double hba1c, int ldl,
                     int hdl, int totalCholesterol, int egfr, double creatinine, int fastingGlucose) {
        this.resultId = resultId;
        this.patientId = patientId;
        this.ts = ts;
        this.hba1c = hba1c;
        this.ldl = ldl;
        this.hdl = hdl;
        this.totalCholesterol = totalCholesterol;
        this.egfr = egfr;
        this.creatinine = creatinine;
        this.fastingGlucose = fastingGlucose;
    }

    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }
    public double getHba1c() { return hba1c; }
    public void setHba1c(double hba1c) { this.hba1c = hba1c; }
    public int getLdl() { return ldl; }
    public void setLdl(int ldl) { this.ldl = ldl; }
    public int getHdl() { return hdl; }
    public void setHdl(int hdl) { this.hdl = hdl; }
    public int getTotalCholesterol() { return totalCholesterol; }
    public void setTotalCholesterol(int totalCholesterol) { this.totalCholesterol = totalCholesterol; }
    public int getEgfr() { return egfr; }
    public void setEgfr(int egfr) { this.egfr = egfr; }
    public double getCreatinine() { return creatinine; }
    public void setCreatinine(double creatinine) { this.creatinine = creatinine; }
    public int getFastingGlucose() { return fastingGlucose; }
    public void setFastingGlucose(int fastingGlucose) { this.fastingGlucose = fastingGlucose; }
}
