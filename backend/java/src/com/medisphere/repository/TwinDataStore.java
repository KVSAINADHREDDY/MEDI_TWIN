package com.medisphere.repository;

import com.medisphere.model.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TwinDataStore {
    private static final TwinDataStore INSTANCE = new TwinDataStore();
    public static TwinDataStore getInstance() { return INSTANCE; }

    private final Map<String, Patient> patients = new ConcurrentHashMap<>();
    private final Map<String, List<Vitals>> vitalsHistory = new ConcurrentHashMap<>();
    private final Map<String, Vitals> baselineVitals = new ConcurrentHashMap<>();
    private final Map<String, LabResult> labs = new ConcurrentHashMap<>();
    private final Map<String, HealthTwin> twins = new ConcurrentHashMap<>();
    private final Map<String, RiskPrediction> predictions = new ConcurrentHashMap<>();
    private final Map<String, Alert> alerts = new ConcurrentHashMap<>();
    private final Map<String, Careplan> careplans = new ConcurrentHashMap<>();
    private final List<Provider> providers = new ArrayList<>();
    private final List<AuditLog> auditLogs = Collections.synchronizedList(new ArrayList<>());
    private FLModel flModel;

    private int totalPatientsOnboarded = 1247;
    private long fhirResourcesSynced = 2418900L;
    private int federatedRound = 47;
    private double modelAccuracy = 91.4;
    private int onlineWearables = 892;
    private double avgResponseTimeMinutes = 3.2;
    private int hospitalizationsPreventedPercent = 23;

    private TwinDataStore() {
        seedInitialData();
    }

    private void seedInitialData() {
        providers.add(new Provider("prov-001", "Dr. Meera Iyer", "1945678901", "Primary Care / Internal Medicine", "Metro Health System", "m.iyer@metrohealth.org"));
        providers.add(new Provider("prov-002", "Dr. Alicia Ferrer", "1723456789", "Cardiology", "St. Jude Heart Institute", "a.ferrer@stjude.org"));
        providers.add(new Provider("prov-003", "Dr. Samuel Owusu", "1834567890", "Endocrinology", "Valley Health Medical", "s.owusu@valleyhealth.org"));

        // John Doe (Primary Specimen)
        String p1Id = "pat-john-doe";
        Patient john = new Patient(
            p1Id, "Patient/Epic-EHR-789210", "John Doe", 58, "M",
            Arrays.asList("Hypertension", "Type 2 Diabetes", "Hyperlipidemia"),
            true, true, true, "Dr. Meera Iyer", "Epic Systems EHR (FHIR R4)",
            Arrays.asList("Metformin 500mg BID", "Lisinopril 10mg Daily"),
            Instant.now().minus(45, ChronoUnit.DAYS).toString()
        );
        patients.put(p1Id, john);

        Vitals johnVitals = new Vitals(
            "vit-001", p1Id, Instant.now().minus(2, ChronoUnit.MINUTES).toString(),
            72, 130, 85, 98, 36.8, 16, "Apple Watch Ultra + Omron BP Stream", "Normal Sinus Rhythm"
        );
        vitalsHistory.put(p1Id, new ArrayList<>(List.of(johnVitals)));
        baselineVitals.put(p1Id, new Vitals("base-001", p1Id, Instant.now().minus(30, ChronoUnit.DAYS).toString(), 70, 128, 82, 98, 36.6, 15, "Clinic Baseline", "Normal"));

        LabResult johnLabs = new LabResult("lab-001", p1Id, Instant.now().minus(3, ChronoUnit.DAYS).toString(), 7.2, 120, 42, 195, 65, 1.2, 142);
        labs.put(p1Id, johnLabs);

        Map<String, Double> johnOrganRisks = new HashMap<>();
        johnOrganRisks.put("cardiovascular", 0.243);
        johnOrganRisks.put("endocrine", 0.320);
        johnOrganRisks.put("renal", 0.180);
        johnOrganRisks.put("vascular", 0.280);
        johnOrganRisks.put("respiratory", 0.050);
        johnOrganRisks.put("cerebral", 0.120);
        HealthTwin johnTwin = new HealthTwin("twin-001", p1Id, "v3.2", Instant.now().toString(), 0.98, johnOrganRisks);
        twins.put(p1Id, johnTwin);

        List<RiskPrediction.FactorContribution> johnContribs = new ArrayList<>();
        johnContribs.add(new RiskPrediction.FactorContribution("HbA1c (7.2%)", 0.08, "Glycemic dysregulation (+8.0%)", "positive"));
        johnContribs.add(new RiskPrediction.FactorContribution("Blood Pressure (130/85 mmHg)", 0.06, "Stage 1 systolic hypertension (+6.0%)", "positive"));
        johnContribs.add(new RiskPrediction.FactorContribution("Age (58 yrs)", 0.05, "Chronological vascular exposure (+5.0%)", "positive"));
        johnContribs.add(new RiskPrediction.FactorContribution("LDL Cholesterol (120 mg/dL)", 0.03, "Atherogenic lipoprotein burden (+3.0%)", "positive"));
        johnContribs.add(new RiskPrediction.FactorContribution("Smoking History", 0.023, "Current smoker status (+2.3%)", "positive"));

        RiskPrediction johnPred = new RiskPrediction(
            "pred-001", p1Id, "Cardiovascular Risk (10-year)", "CVD-Risk-v3.2",
            47, 0.243, "High Risk", 0.121, 2.01, 0.285, 0.142,
            johnContribs,
            Arrays.asList("Intensify statin therapy (Atorvastatin 40mg)", "Target BP <130/80 mmHg via dual antihypertensive", "Tighten glycemic control to target HbA1c <7.0%"),
            Instant.now().minus(1, ChronoUnit.HOURS).toString()
        );
        predictions.put(p1Id, johnPred);

        List<Careplan.Goal> johnGoals = new ArrayList<>();
        johnGoals.add(new Careplan.Goal("g-1", "Reduce HbA1c to <7.0% in 3 months", "HbA1c < 7.0%", "Increase Metformin to 1000mg BID", "Weekly glucose logs via patient mobile app", "medication"));
        johnGoals.add(new Careplan.Goal("g-2", "Reach Blood Pressure target <130/80 mmHg", "BP < 130/80", "Add Amlodipine 5mg Daily", "Daily continuous BP telemetry from wearable", "medication"));
        johnGoals.add(new Careplan.Goal("g-3", "Reduce LDL Cholesterol to <70 mg/dL", "LDL < 70 mg/dL", "Intensify Atorvastatin to 40mg QHS", "Fasting lipid panel at 6 weeks", "medication"));

        Careplan johnCareplan = new Careplan(
            "plan-001", p1Id, "John Doe", "v2.1",
            johnGoals, 0.243, 0.162, 0.87, "approved",
            "Dr. Meera Iyer (NPI: 1945678901)",
            Instant.now().minus(2, ChronoUnit.DAYS).toString(),
            "SHA256:8f4b23c89a7702e1bdfa3592ec491c107f903e1a0b38c2017a5619e07892ca81",
            Instant.now().minus(2, ChronoUnit.DAYS).toString()
        );
        careplans.put(p1Id, johnCareplan);

        // Sarah M. (Surveillance Specimen)
        String p2Id = "pat-sarah-m";
        Patient sarah = new Patient(
            p2Id, "Patient/Cerner-EHR-512093", "Sarah M.", 46, "F",
            Arrays.asList("Atrial Fibrillation (history)", "Mild Hypertension"),
            false, true, true, "Dr. Alicia Ferrer", "Cerner Millennium (FHIR R4)",
            Arrays.asList("Metoprolol Tartrate 25mg BID"),
            Instant.now().minus(90, ChronoUnit.DAYS).toString()
        );
        patients.put(p2Id, sarah);

        Vitals sarahVitals = new Vitals("vit-002", p2Id, Instant.now().minus(14, ChronoUnit.MINUTES).toString(), 145, 138, 88, 96, 37.0, 22, "BioTelemetry Wearable Stream (Kafka)", "Atrial Fibrillation / Tachycardia Spike");
        vitalsHistory.put(p2Id, new ArrayList<>(List.of(sarahVitals)));
        baselineVitals.put(p2Id, new Vitals("base-002", p2Id, Instant.now().minus(60, ChronoUnit.DAYS).toString(), 68, 122, 78, 99, 36.6, 14, "Resting Baseline", "Normal"));

        LabResult sarahLabs = new LabResult("lab-002", p2Id, Instant.now().minus(10, ChronoUnit.DAYS).toString(), 5.4, 95, 55, 168, 92, 0.8, 94);
        labs.put(p2Id, sarahLabs);

        Map<String, Double> sarahOrganRisks = new HashMap<>();
        sarahOrganRisks.put("cardiovascular", 0.340);
        sarahOrganRisks.put("endocrine", 0.040);
        sarahOrganRisks.put("renal", 0.060);
        sarahOrganRisks.put("vascular", 0.190);
        sarahOrganRisks.put("respiratory", 0.080);
        sarahOrganRisks.put("cerebral", 0.220);
        HealthTwin sarahTwin = new HealthTwin("twin-002", p2Id, "v3.2", Instant.now().toString(), 0.99, sarahOrganRisks);
        twins.put(p2Id, sarahTwin);

        Alert sarahAlert = new Alert(
            "alt-sarah-001", p2Id, "Sarah M.", "critical",
            "ALERT: Patient Sarah M. | HR spike 145 bpm | Time: 14:23",
            "Context: At rest | No exercise | Previous avg: 68 bpm | Kafka Stream: 12K vitals/sec | Lag: 0.8s | Twin Update: Arrhythmia risk increased to 34%",
            0.89, "Auto-actions: Notified cardiologist | Scheduled ECG",
            false, Instant.now().minus(14, ChronoUnit.MINUTES).toString(),
            "telemetry.cardiac.high-frequency", 0.8, "Atrial Fibrillation"
        );
        alerts.put(sarahAlert.getAlertId(), sarahAlert);

        seedAdditionalPatients();

        List<FLModel.HospitalNode> nodes = new ArrayList<>();
        nodes.add(new FLModel.HospitalNode("node-01", "Metro General Hospital", 48200, 0.142, "Aggregated", 0.45));
        nodes.add(new FLModel.HospitalNode("node-02", "St. Jude Heart Center", 39150, 0.128, "Aggregated", 0.42));
        nodes.add(new FLModel.HospitalNode("node-03", "Valley Regional Clinic", 27400, 0.155, "Aggregated", 0.50));
        flModel = new FLModel("fl-cvd-v3.2", "Federated CVD & Diabetes Predictor", 47, 91.4, 2.1, 0.138, nodes, "TensorFlow Federated 0.25 (Differential Privacy Enabled)", Instant.now().minus(4, ChronoUnit.HOURS).toString());

        logAudit("Dr. Meera Iyer", "CLINICIAN", "READ", "HealthTwin", "pat-john-doe", "SUCCESS");
        logAudit("KafkaConsumerService", "SYSTEM", "INGEST_STREAM", "Vitals", "pat-sarah-m", "SUCCESS");
        logAudit("AnomalyDetectionEngine", "AI_SERVICE", "TRIGGER_ALERT", "Alert", "alt-sarah-001", "SUCCESS");
        logAudit("Dr. Meera Iyer", "CLINICIAN", "APPROVE_PLAN", "Careplan", "plan-001", "SUCCESS");
    }

    private void seedAdditionalPatients() {
        Object[][] list = new Object[][] {
            {"pat-david-k", "Patient/Epic-EHR-334102", "David K.", 64, "M", Arrays.asList("CKD Stage 2", "Hypertension"), false, true, "Dr. Samuel Owusu", 68, 142, 90, 97, 6.1, 135, 58, Arrays.asList("Losartan 50mg")},
            {"pat-maria-g", "Patient/Allscripts-88192", "Maria Garcia", 52, "F", Arrays.asList("Type 2 Diabetes"), false, false, "Dr. Meera Iyer", 75, 128, 82, 99, 7.8, 110, 88, Arrays.asList("Metformin 850mg BID")},
            {"pat-ravi-k", "Patient/Epic-EHR-992014", "Ravi Kapoor", 67, "M", Arrays.asList("Coronary Artery Disease", "Hyperlipidemia"), false, true, "Dr. Alicia Ferrer", 64, 136, 84, 96, 5.9, 148, 72, Arrays.asList("Atorvastatin 40mg", "Clopidogrel 75mg")},
            {"pat-emily-s", "Patient/Cerner-EHR-109244", "Emily Silva", 41, "F", Arrays.asList("Essential Hypertension"), false, false, "Dr. Meera Iyer", 70, 132, 86, 99, 5.2, 105, 98, Arrays.asList("Hydrochlorothiazide 25mg")},
            {"pat-wei-c", "Patient/Epic-EHR-447812", "Wei Chen", 59, "M", Arrays.asList("Type 2 Diabetes", "Hypertension"), true, true, "Dr. Samuel Owusu", 78, 145, 92, 97, 8.4, 140, 62, Arrays.asList("Glipizide 10mg", "Lisinopril 20mg")},
            {"pat-aisha-p", "Patient/Epic-EHR-661290", "Aisha Patel", 39, "F", Arrays.asList("Asthma"), false, false, "Dr. Meera Iyer", 74, 118, 76, 98, 5.1, 90, 104, Arrays.asList("Albuterol Inhaler")},
            {"pat-robert-o", "Patient/Epic-EHR-772901", "Robert Okafor", 71, "M", Arrays.asList("Heart Failure (Class II)", "Hypertension", "CKD Stage 2"), false, true, "Dr. Alicia Ferrer", 82, 148, 94, 95, 6.5, 115, 52, Arrays.asList("Carvedilol 25mg", "Furosemide 40mg", "Sacubitril/Valsartan")},
            {"pat-elena-n", "Patient/Cerner-EHR-338190", "Elena Novak", 55, "F", Arrays.asList("Hyperlipidemia", "Pre-diabetes"), false, true, "Dr. Meera Iyer", 69, 124, 80, 98, 6.2, 155, 85, Arrays.asList("Rosuvastatin 10mg")}
        };

        for (Object[] item : list) {
            String pid = (String) item[0];
            String fhir = (String) item[1];
            String name = (String) item[2];
            int age = (int) item[3];
            String sex = (String) item[4];
            @SuppressWarnings("unchecked")
            List<String> conds = (List<String>) item[5];
            boolean smk = (boolean) item[6];
            boolean fh = (boolean) item[7];
            String prov = (String) item[8];
            int hr = (int) item[9];
            int sys = (int) item[10];
            int dia = (int) item[11];
            int spo2 = (int) item[12];
            double hba1c = (double) item[13];
            int ldl = (int) item[14];
            int egfr = (int) item[15];
            @SuppressWarnings("unchecked")
            List<String> meds = (List<String>) item[16];

            Patient p = new Patient(pid, fhir, name, age, sex, conds, smk, fh, true, prov, "Integrated Health Network EHR", meds, Instant.now().minus(20, ChronoUnit.DAYS).toString());
            patients.put(pid, p);

            Vitals v = new Vitals("vit-" + pid, pid, Instant.now().minus(10, ChronoUnit.MINUTES).toString(), hr, sys, dia, spo2, 36.7, 16, "Continuous Telemetry", "Normal Sinus Rhythm");
            vitalsHistory.put(pid, new ArrayList<>(List.of(v)));
            baselineVitals.put(pid, v);

            LabResult lab = new LabResult("lab-" + pid, pid, Instant.now().minus(5, ChronoUnit.DAYS).toString(), hba1c, ldl, 45, 180, egfr, 1.0, 110);
            labs.put(pid, lab);

            Map<String, Double> organRisks = new HashMap<>();
            double cvdRisk = Math.min(0.85, (age > 50 ? 0.08 : 0.03) + (sys > 130 ? 0.07 : 0.02) + (hba1c > 7 ? 0.09 : 0.02) + (smk ? 0.06 : 0.0));
            organRisks.put("cardiovascular", Math.round(cvdRisk * 1000.0) / 1000.0);
            organRisks.put("endocrine", hba1c > 6.5 ? 0.25 : 0.04);
            organRisks.put("renal", egfr < 60 ? 0.22 : 0.05);
            organRisks.put("vascular", sys > 140 ? 0.26 : 0.10);
            organRisks.put("respiratory", spo2 < 96 ? 0.15 : 0.03);
            organRisks.put("cerebral", (age > 65 || sys > 145) ? 0.18 : 0.07);

            HealthTwin twin = new HealthTwin("twin-" + pid, pid, "v3.2", Instant.now().toString(), 0.96, organRisks);
            twins.put(pid, twin);
        }
    }

    public void logAudit(String actor, String role, String action, String resourceType, String resourceId, String outcome) {
        String logId = "aud-" + UUID.randomUUID().toString().substring(0, 8);
        String ts = Instant.now().toString();
        String raw = logId + "|" + ts + "|" + actor + "|" + role + "|" + action + "|" + resourceType + "|" + resourceId + "|" + outcome;
        String hash = "SHA256:" + Integer.toHexString(raw.hashCode()) + "e4c9" + Long.toHexString(System.currentTimeMillis());
        AuditLog log = new AuditLog(logId, ts, actor, role, action, resourceType, resourceId, "10.240.18.92", outcome, hash);
        auditLogs.add(0, log);
        if (auditLogs.size() > 100) auditLogs.remove(auditLogs.size() - 1);
    }

    public List<AuditLog> getAuditLogs() { return new ArrayList<>(auditLogs); }
    public Map<String, Patient> getPatients() { return patients; }
    public Patient getPatient(String id) { return patients.get(id); }
    public Vitals getLatestVitals(String patientId) {
        List<Vitals> list = vitalsHistory.get(patientId);
        return (list != null && !list.isEmpty()) ? list.get(list.size() - 1) : null;
    }
    public Vitals getBaselineVitals(String patientId) { return baselineVitals.get(patientId); }
    public void addVitals(String patientId, Vitals vitals) {
        vitalsHistory.computeIfAbsent(patientId, k -> new ArrayList<>()).add(vitals);
    }
    public LabResult getLatestLabs(String patientId) { return labs.get(patientId); }
    public HealthTwin getTwin(String patientId) { return twins.get(patientId); }
    public void setTwin(String patientId, HealthTwin twin) { twins.put(patientId, twin); }
    public RiskPrediction getPrediction(String patientId) { return predictions.get(patientId); }
    public void setPrediction(String patientId, RiskPrediction pred) { predictions.put(patientId, pred); }
    public List<RiskPrediction> getAllPredictions() { return new ArrayList<>(predictions.values()); }
    public Alert getAlert(String alertId) { return alerts.get(alertId); }
    public List<Alert> getAllAlerts() {
        List<Alert> list = new ArrayList<>(alerts.values());
        list.sort((a, b) -> b.getTs().compareTo(a.getTs()));
        return list;
    }
    public void addAlert(Alert alert) { alerts.put(alert.getAlertId(), alert); }
    public Careplan getCareplan(String patientId) { return careplans.get(patientId); }
    public void setCareplan(String patientId, Careplan plan) { careplans.put(patientId, plan); }
    public List<Careplan> getAllCareplans() { return new ArrayList<>(careplans.values()); }
    public FLModel getFLModel() { return flModel; }
    public void incrementFLRound() {
        this.federatedRound++;
        if (this.flModel != null) {
            this.flModel.setCurrentRound(this.federatedRound);
            this.flModel.setGlobalAccuracy(Math.min(96.5, this.flModel.getGlobalAccuracy() + 0.1));
            this.flModel.setLastAggregation(Instant.now().toString());
        }
    }

    public int getTotalPatientsOnboarded() { return totalPatientsOnboarded; }
    public long getFhirResourcesSynced() { return fhirResourcesSynced; }
    public int getFederatedRound() { return federatedRound; }
    public double getModelAccuracy() { return modelAccuracy; }
    public int getOnlineWearables() { return onlineWearables; }
    public double getAvgResponseTimeMinutes() { return avgResponseTimeMinutes; }
    public int getHospitalizationsPreventedPercent() { return hospitalizationsPreventedPercent; }
}
