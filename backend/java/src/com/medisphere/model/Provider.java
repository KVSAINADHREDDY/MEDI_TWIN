package com.medisphere.model;

public class Provider {
    private String providerId;
    private String name;
    private String npi;
    private String specialty;
    private String hospitalAffiliation;
    private String email;

    public Provider() {}

    public Provider(String providerId, String name, String npi, String specialty, String hospitalAffiliation, String email) {
        this.providerId = providerId;
        this.name = name;
        this.npi = npi;
        this.specialty = specialty;
        this.hospitalAffiliation = hospitalAffiliation;
        this.email = email;
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNpi() { return npi; }
    public void setNpi(String npi) { this.npi = npi; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getHospitalAffiliation() { return hospitalAffiliation; }
    public void setHospitalAffiliation(String hospitalAffiliation) { this.hospitalAffiliation = hospitalAffiliation; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
