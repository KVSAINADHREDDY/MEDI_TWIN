package com.medisphere.model;

import java.util.*;

public class Patient {
    private String patientId;
    private String fhirId;
    private String name;
    private int age;
    private String sex;
    private List<String> conditions;
    private boolean smoking;
    private boolean familyHistoryCVD;
    private boolean consent;
    private String provider;
    private String ehrSource;
    private List<String> medications;
    private String onboardedAt;

    public Patient() {
        this.conditions = new ArrayList<>();
        this.medications = new ArrayList<>();
    }

    public Patient(String patientId, String fhirId, String name, int age, String sex,
                   List<String> conditions, boolean smoking, boolean familyHistoryCVD,
                   boolean consent, String provider, String ehrSource, List<String> medications, String onboardedAt) {
        this.patientId = patientId;
        this.fhirId = fhirId;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.conditions = conditions != null ? new ArrayList<>(conditions) : new ArrayList<>();
        this.smoking = smoking;
        this.familyHistoryCVD = familyHistoryCVD;
        this.consent = consent;
        this.provider = provider;
        this.ehrSource = ehrSource != null ? ehrSource : "Epic EHR (FHIR R4)";
        this.medications = medications != null ? new ArrayList<>(medications) : new ArrayList<>();
        this.onboardedAt = onboardedAt;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getFhirId() { return fhirId; }
    public void setFhirId(String fhirId) { this.fhirId = fhirId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }
    public boolean isSmoking() { return smoking; }
    public void setSmoking(boolean smoking) { this.smoking = smoking; }
    public boolean isFamilyHistoryCVD() { return familyHistoryCVD; }
    public void setFamilyHistoryCVD(boolean familyHistoryCVD) { this.familyHistoryCVD = familyHistoryCVD; }
    public boolean isConsent() { return consent; }
    public void setConsent(boolean consent) { this.consent = consent; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getEhrSource() { return ehrSource; }
    public void setEhrSource(String ehrSource) { this.ehrSource = ehrSource; }
    public List<String> getMedications() { return medications; }
    public void setMedications(List<String> medications) { this.medications = medications; }
    public String getOnboardedAt() { return onboardedAt; }
    public void setOnboardedAt(String onboardedAt) { this.onboardedAt = onboardedAt; }
}
