package com.medisphere.service;

import com.medisphere.model.*;
import com.medisphere.repository.TwinDataStore;
import java.time.Instant;
import java.util.*;

public class AlertService {
    private final Random random = new Random(1337);

    public List<Alert> simulateKafkaStreamTick() {
        TwinDataStore store = TwinDataStore.getInstance();
        List<Alert> triggeredAlerts = new ArrayList<>();

        for (Patient patient : store.getPatients().values()) {
            String pid = patient.getPatientId();
            Vitals baseline = store.getBaselineVitals(pid);
            if (baseline == null) baseline = store.getLatestVitals(pid);

            int hr = baseline.getHeartRate() + random.nextInt(9) - 4;
            int sys = baseline.getSystolic() + random.nextInt(7) - 3;
            int dia = baseline.getDiastolic() + random.nextInt(5) - 2;
            int spo2 = Math.min(100, Math.max(92, baseline.getSpo2() + random.nextInt(3) - 1));
            String rhythm = "Normal Sinus Rhythm";

            double roll = random.nextDouble();
            if (patient.getName().equalsIgnoreCase("Sarah M.") || roll < 0.08) {
                if (patient.getName().equalsIgnoreCase("Sarah M.") || roll < 0.04) {
                    hr = 145 + random.nextInt(10);
                    rhythm = "Atrial Fibrillation / Rapid Ventricular Response";
                    Alert alert = new Alert(
                        "alt-" + UUID.randomUUID().toString().substring(0, 8),
                        pid, patient.getName(), "critical",
                        "ALERT: Patient " + patient.getName() + " | HR spike " + hr + " bpm | Time: " + Instant.now().toString().substring(11, 19),
                        "Context: At rest | No exercise | Previous avg: " + baseline.getHeartRate() + " bpm | Kafka Stream: 12K vitals/sec | Lag: 0.8s | Twin Update: Arrhythmia risk increased to 34%",
                        0.89,
                        "Auto-actions: Notified cardiologist | Scheduled ECG",
                        false, Instant.now().toString(),
                        "telemetry.cardiac.kafka.streams", 0.8, "Atrial Fibrillation"
                    );
                    store.addAlert(alert);
                    triggeredAlerts.add(alert);
                    store.logAudit("KafkaAnomalyDetectionService", "AI_ENGINE", "TRIGGER_ALERT", "Alert", alert.getAlertId(), "SUCCESS");
                } else {
                    sys = 168 + random.nextInt(15);
                    dia = 104 + random.nextInt(8);
                    Alert alert = new Alert(
                        "alt-" + UUID.randomUUID().toString().substring(0, 8),
                        pid, patient.getName(), "warning",
                        "WARNING: Hypertensive Surge (" + sys + "/" + dia + " mmHg)",
                        "Telemetry detected acute elevation exceeding patient baseline by +35 mmHg systolic.",
                        0.84,
                        "Auto-actions: Flagged for antihypertensive titration review",
                        false, Instant.now().toString(),
                        "telemetry.bp.kafka.streams", 0.6, "Hypertensive Crisis"
                    );
                    store.addAlert(alert);
                    triggeredAlerts.add(alert);
                }
            }

            Vitals newReading = new Vitals(
                "vit-" + UUID.randomUUID().toString().substring(0, 8),
                pid, Instant.now().toString(), hr, sys, dia, spo2, 36.8, 16, "Kafka Real-Time Wearable Stream", rhythm
            );
            store.addVitals(pid, newReading);
        }

        return triggeredAlerts;
    }

    public Alert acknowledge(String alertId, String clinicianName) {
        TwinDataStore store = TwinDataStore.getInstance();
        Alert alert = store.getAlert(alertId);
        if (alert != null) {
            alert.setAcknowledged(true);
            alert.setAcknowledgedBy(clinicianName != null ? clinicianName : "Dr. Meera Iyer");
            alert.setAcknowledgedAt(Instant.now().toString());
            store.logAudit(clinicianName, "CLINICIAN", "ACKNOWLEDGE_ALERT", "Alert", alertId, "SUCCESS");
        }
        return alert;
    }

    public List<Double> generateEcgWaveform(String rhythmType, int points) {
        List<Double> wave = new ArrayList<>(points);
        boolean isAfib = "Atrial Fibrillation".equalsIgnoreCase(rhythmType) || "AFib".equalsIgnoreCase(rhythmType);

        for (int i = 0; i < points; i++) {
            double phase = (i % 60) / 60.0;
            double v = 0.0;

            if (isAfib) {
                v += 0.08 * Math.sin(i * 0.4) + 0.05 * Math.sin(i * 1.3);
                int rPos = (i % 45);
                if (rPos == 12) v += 1.8;
                else if (rPos == 11) v -= 0.3;
                else if (rPos == 13) v -= 0.5;
                else if (rPos >= 20 && rPos <= 28) v += 0.25 * Math.sin((rPos - 20) / 8.0 * Math.PI);
            } else {
                if (phase >= 0.10 && phase <= 0.18) {
                    v += 0.20 * Math.sin((phase - 0.10) / 0.08 * Math.PI);
                } else if (phase >= 0.28 && phase <= 0.30) {
                    v -= 0.15;
                } else if (phase >= 0.30 && phase <= 0.34) {
                    v += 1.60 * Math.sin((phase - 0.30) / 0.04 * Math.PI);
                } else if (phase >= 0.34 && phase <= 0.37) {
                    v -= 0.40 * Math.sin((phase - 0.34) / 0.03 * Math.PI);
                } else if (phase >= 0.52 && phase <= 0.68) {
                    v += 0.35 * Math.sin((phase - 0.52) / 0.16 * Math.PI);
                }
                v += (random.nextDouble() - 0.5) * 0.02;
            }
            wave.add(Math.round(v * 1000.0) / 1000.0);
        }
        return wave;
    }
}
