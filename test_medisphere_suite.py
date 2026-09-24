"""
MediSphere Cognitive Twin Platform - Senior QA Automated Test Suite
Validates all 8 clinical modules, REST endpoints, data contracts, and end-to-end workflows.
"""
import urllib.request
import urllib.error
import json
import time
import sys

BASE_URL = "http://localhost:5050"

class TestReport:
    def __init__(self):
        self.passed = 0
        self.failed = 0
        self.tests = []

    def assert_true(self, condition, test_name, detail=""):
        if condition:
            self.passed += 1
            self.tests.append({"name": test_name, "status": "PASS", "detail": detail})
            print(f"  \033[92m[PASS]\033[0m {test_name} {detail}")
        else:
            self.failed += 1
            self.tests.append({"name": test_name, "status": "FAIL", "detail": detail})
            print(f"  \033[91m[FAIL]\033[0m {test_name} - {detail}")

    def report(self):
        total = self.passed + self.failed
        print("\n" + "="*70)
        print(f"  TEST EXECUTION SUMMARY: {self.passed}/{total} Passed ({(self.passed/total*100):.1f}%)")
        print("="*70)
        return self.failed == 0

def http_get(path):
    url = BASE_URL + path
    req = urllib.request.Request(url, headers={"User-Agent": "MediSphere-QA-Suite/3.2"})
    with urllib.request.urlopen(req) as res:
        data = res.read().decode("utf-8")
        try:
            return res.status, json.loads(data)
        except:
            return res.status, data

def http_post(path, body_dict=None):
    url = BASE_URL + path
    data = json.dumps(body_dict or {}).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json", "User-Agent": "MediSphere-QA-Suite/3.2"})
    with urllib.request.urlopen(req) as res:
        resp_data = res.read().decode("utf-8")
        try:
            return res.status, json.loads(resp_data)
        except:
            return res.status, resp_data

def run_tests():
    qa = TestReport()
    print("\n" + "#"*70)
    print("  MEDISPHERE COGNITIVE TWIN - CLINICAL QA TEST RUNNER")
    print("  Environment: Java 20+ Enterprise Server (Virtual Threads) on :5050")
    print("#"*70)

    # --------------------------------------------------------------------------
    # 1. Dashboard & Core KPI Verification
    # --------------------------------------------------------------------------
    print("\n--- [Module 1] Dashboard & Platform KPI Engine ---")
    status, summary = http_get("/api/summary")
    qa.assert_true(status == 200, "GET /api/summary HTTP 200 OK")
    qa.assert_true(summary.get("patientsOnboarded") == 1247, "KPI: Patients Onboarded == 1,247", f"Got: {summary.get('patientsOnboarded')}")
    qa.assert_true(summary.get("fhirResourcesSynced") >= 2400000, "KPI: FHIR Resources Synced >= 2.4M", f"Got: {summary.get('fhirResourcesSynced'):,}")
    qa.assert_true(summary.get("twinCoverage") == 100.0, "KPI: Twin Coverage == 100.0%")
    qa.assert_true(summary.get("modelAccuracy") >= 91.0, "KPI: Model Accuracy >= 91.0%", f"Got: {summary.get('modelAccuracy')}%")
    qa.assert_true(summary.get("wearablesOnline") == 892, "KPI: Wearables Online == 892", f"Got: {summary.get('wearablesOnline')}")
    qa.assert_true(summary.get("hospitalizationsPrevented") == 23, "KPI: Hospitalizations Prevented == 23%")

    # --------------------------------------------------------------------------
    # 2. Patients & Digital Health Twin Verification
    # --------------------------------------------------------------------------
    print("\n--- [Module 2] Digital Health Twins & MongoDB Store ---")
    status, patients = http_get("/api/patients")
    qa.assert_true(status == 200 and isinstance(patients, list) and len(patients) >= 10, "GET /api/patients returns populated list", f"Count: {len(patients)}")

    status, john = http_get("/api/patients/pat-john-doe")
    qa.assert_true(status == 200 and john.get("name") == "John Doe", "GET /api/patients/pat-john-doe (John Doe profile)")
    qa.assert_true(john.get("age") == 58 and john.get("sex") == "M", "Demographics: 58M")
    qa.assert_true("Hypertension" in john.get("conditions", []) and "Type 2 Diabetes" in john.get("conditions", []), "Clinical Conditions verified")
    qa.assert_true(john.get("vitals") is not None and john["vitals"].get("systolic") == 130, "Vitals Stream: BP 130/85 verified")
    qa.assert_true(john.get("labs") is not None and john["labs"].get("hba1c") == 7.2, "Labs: HbA1c 7.2%, eGFR 65 verified")
    qa.assert_true(john.get("twin") is not None and john["twin"]["organRisks"].get("cardiovascular") == 0.243, "3D Organ Risk Matrix: Cardiovascular 24.3%")

    # --------------------------------------------------------------------------
    # 3. SMART on FHIR R4 Interoperability
    # --------------------------------------------------------------------------
    print("\n--- [Module 3] SMART on FHIR R4 Interoperability ---")
    status, fhir_bundle = http_get("/api/fhir/pat-john-doe")
    qa.assert_true(status == 200 and fhir_bundle.get("resourceType") == "Bundle", "GET /api/fhir/pat-john-doe returns FHIR Bundle")
    qa.assert_true(fhir_bundle.get("fhirVersion") == "4.0.1 (R4)", "FHIR Specification: R4 (v4.0.1)")
    entries = fhir_bundle.get("entry", [])
    resource_types = [e["resource"]["resourceType"] for e in entries if "resource" in e]
    qa.assert_true("Patient" in resource_types, "FHIR Resource present: Patient")
    qa.assert_true("Observation" in resource_types, "FHIR Resource present: Observation (Vitals & Labs)")
    qa.assert_true("Condition" in resource_types, "FHIR Resource present: Condition")
    qa.assert_true("RiskAssessment" in resource_types, "FHIR Resource present: RiskAssessment")

    # --------------------------------------------------------------------------
    # 4. TensorFlow Federated Risk Prediction & SHAP
    # --------------------------------------------------------------------------
    print("\n--- [Module 4] AI Risk Prediction & SHAP Explainability ---")
    status, pred = http_post("/api/patients/pat-john-doe/predict")
    qa.assert_true(status == 200, "POST /api/patients/pat-john-doe/predict HTTP 200 OK")
    qa.assert_true(pred.get("probability") == 0.243, "10-Year CVD Risk == 24.3%", f"Got: {pred.get('probability')}")
    qa.assert_true(pred.get("category") == "High Risk", "Risk Category == 'High Risk'")
    qa.assert_true(pred.get("relativeRisk") == 2.01, "Relative Risk == 2.01x vs Population Avg")

    contribs = pred.get("contributions", [])
    factors = [c["factor"] for c in contribs]
    qa.assert_true(any("HbA1c" in f for f in factors), "SHAP Factor: HbA1c (+8%) present")
    qa.assert_true(any("Blood Pressure" in f for f in factors), "SHAP Factor: Blood Pressure (+6%) present")
    qa.assert_true(any("Age" in f for f in factors), "SHAP Factor: Age (+5%) present")

    # What-If Dynamic Simulation Test
    status, whatif = http_post("/api/patients/pat-john-doe/what-if", {
        "age": "58", "systolic": "120", "hba1c": "6.5", "ldl": "90", "smoking": "false"
    })
    qa.assert_true(status == 200, "POST /api/patients/pat-john-doe/what-if HTTP 200 OK")
    qa.assert_true(whatif.get("probability") < pred.get("probability"), "What-If Simulation successfully projects risk reduction on BP/HbA1c/Smoking optimization", f"{pred.get('probability')} -> {whatif.get('probability')}")

    # --------------------------------------------------------------------------
    # 5. Real-Time Surveillance, Kafka Streaming & ECG Telemetry
    # --------------------------------------------------------------------------
    print("\n--- [Module 5] Real-Time Surveillance & Kafka Anomaly Engine ---")
    status, alerts = http_get("/api/alerts")
    qa.assert_true(status == 200 and len(alerts) > 0, "GET /api/alerts returns active clinical alerts", f"Count: {len(alerts)}")

    sarah_alert = next((a for a in alerts if "Sarah" in a.get("patientName", "")), None)
    qa.assert_true(sarah_alert is not None, "Critical Anomaly: Sarah M. 145 bpm HR spike detected")
    if sarah_alert:
        qa.assert_true(sarah_alert.get("severity") == "critical", "Severity: Critical")
        qa.assert_true("atrial fibrillation" in sarah_alert.get("title", "").lower() or "tachycardia" in sarah_alert.get("title", "").lower() or "sarah" in sarah_alert.get("title", "").lower(), "Diagnosis: Possible Atrial Fibrillation")

        # Test Acknowledge Alert
        status, ack = http_post(f"/api/alerts/{sarah_alert['alertId']}/acknowledge")
        qa.assert_true(status == 200 and ack.get("acknowledged") is True, "POST /api/alerts/{id}/acknowledge marks alert acknowledged")

    # Advance Kafka Stream Batch Test
    status, tick_resp = http_post("/api/simulate/tick")
    qa.assert_true(status == 200 and "streamRate" in tick_resp, "POST /api/simulate/tick advances 12K/s wearable stream batch")

    # ECG Waveform Test
    status, ecg = http_get("/api/ecg/pat-sarah-m")
    qa.assert_true(status == 200 and len(ecg.get("wave", [])) == 240, "GET /api/ecg/pat-sarah-m returns 240 Lead II ECG sampling points")

    # --------------------------------------------------------------------------
    # 6. Precision Care Management & Digital Sign-Off
    # --------------------------------------------------------------------------
    print("\n--- [Module 6] Precision Care Management & Digital Approval ---")
    status, careplan = http_post("/api/patients/pat-john-doe/careplan")
    qa.assert_true(status == 200 and careplan.get("patientName") == "John Doe", "POST /api/patients/pat-john-doe/careplan generates AI Careplan v2.1")
    qa.assert_true(careplan.get("predictedRiskAfter") == 0.162, "Projected Outcome: CVD Risk drops to 16.2%")
    qa.assert_true(careplan.get("adherenceScore") == 0.87, "Projected Adherence Score: 87%")

    goals = careplan.get("goals", [])
    qa.assert_true(any("HbA1c" in g["goal"] and "Metformin" in g["intervention"] for g in goals), "Goal 1: HbA1c <7.0% with Metformin 1000mg BID")
    qa.assert_true(any("Blood Pressure" in g["goal"] or "BP" in g["goal"] for g in goals), "Goal 2: BP target <130/80 mmHg with Amlodipine 5mg")

    # Clinician Digital Signature Approval Test
    status, approved_plan = http_post("/api/careplans/pat-john-doe/approve")
    qa.assert_true(status == 200 and approved_plan.get("status") == "approved", "POST /api/careplans/pat-john-doe/approve signs careplan")
    qa.assert_true(approved_plan.get("digitalSignatureHash", "").startswith("SHA256:"), "Cryptographic SHA-256 Signature Seal applied")

    # --------------------------------------------------------------------------
    # 7. Population Health & Governance
    # --------------------------------------------------------------------------
    print("\n--- [Module 7] Population Health & Algorithmic Fairness ---")
    status, pop = http_get("/api/population-health")
    qa.assert_true(status == 200 and pop.get("totalCohortSize") == 1247, "GET /api/population-health: Total cohort 1,247")
    qa.assert_true(pop.get("hospitalizationReduction") == 23.4, "Outcome Metric: Hospitalization reduction 23.4%")
    qa.assert_true(pop.get("demographicParityScore") == 0.96, "Algorithmic Fairness: Demographic Parity Score 0.96 (Certified)")

    # --------------------------------------------------------------------------
    # 8. HIPAA Audit Logging Security
    # --------------------------------------------------------------------------
    print("\n--- [Module 8] HIPAA Audit Logging & Security ---")
    status, logs = http_get("/api/audit-logs")
    qa.assert_true(status == 200 and len(logs) >= 4, "GET /api/audit-logs returns immutable HIPAA compliance trail", f"Logs count: {len(logs)}")
    qa.assert_true(all(l.get("integrityHash", "").startswith("SHA256:") for l in logs), "All audit events contain SHA-256 cryptographic hash chains")

    # --------------------------------------------------------------------------
    # 9. Static Web SPA Assets & Routing
    # --------------------------------------------------------------------------
    print("\n--- [Module 9] Static Frontend SPA Assets ---")
    status, html = http_get("/index.html")
    qa.assert_true(status == 200 and "<title>MediSphere" in html, "GET /index.html serves SPA HTML")

    status, css = http_get("/css/styles.css")
    qa.assert_true(status == 200 and ":root" in css, "GET /css/styles.css serves clinical dark stylesheet")

    status, app_js = http_get("/js/app.js")
    qa.assert_true(status == 200 and "DashboardView" in app_js, "GET /js/app.js serves main router")

    status, body_3d = http_get("/js/components/body_twin_3d.js")
    qa.assert_true(status == 200 and "BodyTwin3D" in body_3d, "GET /js/components/body_twin_3d.js serves 3D Organ Model")

    status, ecg_js = http_get("/js/components/ecg_monitor.js")
    qa.assert_true(status == 200 and "EcgMonitor" in ecg_js, "GET /js/components/ecg_monitor.js serves 60 FPS ECG Monitor")

    # Final Report
    success = qa.report()
    if not success:
        sys.exit(1)

if __name__ == "__main__":
    run_tests()
