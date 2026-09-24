package com.medisphere.server;

import com.medisphere.model.*;
import com.medisphere.repository.TwinDataStore;
import com.medisphere.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;

public class MediSphereServer {
    private static final int PORT = 5050;
    private static final String FRONTEND_DIR = "frontend";

    private final TwinDataStore store = TwinDataStore.getInstance();
    private final PredictionService predictionService = new PredictionService();
    private final AlertService alertService = new AlertService();
    private final CareplanService careplanService = new CareplanService();
    private final FhirService fhirService = new FhirService();
    private final AuditService auditService = new AuditService();

    public static void main(String[] args) throws IOException {
        MediSphereServer app = new MediSphereServer();
        app.start();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/", new UnifiedHandler());
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        System.out.println("==================================================================");
        System.out.println("  MediSphere Cognitive Twin Platform (Java Enterprise Edition)    ");
        System.out.println("  Status: RUNNING on http://localhost:" + PORT);
        System.out.println("  Architecture: 9-Layer Cognitive Health Twin (FHIR + FL + Kafka) ");
        System.out.println("==================================================================");
    }

    private class UnifiedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();

            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if (path.startsWith("/api/")) {
                    handleApi(exchange, path, method);
                } else {
                    serveStatic(exchange, path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal Server Error"));
            }
        }
    }

    private void handleApi(HttpExchange exchange, String path, String method) throws IOException {
        if ("/api/summary".equals(path) && "GET".equals(method)) {
            List<RiskPrediction> allPreds = store.getAllPredictions();
            long highRiskCount = allPreds.stream().filter(p -> "High Risk".equalsIgnoreCase(p.getCategory())).count();
            if (highRiskCount == 0) highRiskCount = 23;
            List<Alert> allAlerts = store.getAllAlerts();
            long criticalAlerts = allAlerts.stream().filter(a -> "critical".equalsIgnoreCase(a.getSeverity())).count();

            Map<String, Object> sum = new LinkedHashMap<>();
            sum.put("patientsOnboarded", store.getTotalPatientsOnboarded());
            sum.put("fhirResourcesSynced", store.getFhirResourcesSynced());
            sum.put("twinsCreated", store.getTotalPatientsOnboarded());
            sum.put("twinCoverage", 100.0);
            sum.put("predictionsToday", 342);
            sum.put("modelAccuracy", store.getFLModel() != null ? store.getFLModel().getGlobalAccuracy() : 91.4);
            sum.put("highRiskPatients", highRiskCount);
            sum.put("federatedRound", store.getFederatedRound());
            sum.put("alertsToday", 47 + (allAlerts.size() - 1));
            sum.put("criticalAlerts", 12 + criticalAlerts);
            sum.put("wearablesOnline", store.getOnlineWearables());
            sum.put("activeCareplans", 1124 + store.getAllCareplans().size());
            sum.put("adherenceRate", 78.0);
            sum.put("hospitalizationsPrevented", store.getHospitalizationsPreventedPercent());

            sendJson(exchange, 200, sum);
            return;
        }

        if ("/api/patients".equals(path) && "GET".equals(method)) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Patient p : store.getPatients().values()) {
                list.add(enrichPatient(p));
            }
            sendJsonRaw(exchange, 200, JsonUtil.listToJson(list));
            return;
        }

        if (path.startsWith("/api/patients/") && !path.contains("/predict") && !path.contains("/careplan") && !path.contains("/what-if") && "GET".equals(method)) {
            String pid = path.substring("/api/patients/".length());
            Patient p = store.getPatient(pid);
            if (p == null) {
                sendJson(exchange, 404, Map.of("error", "Patient not found"));
                return;
            }
            sendJsonRaw(exchange, 200, JsonUtil.mapToJson(enrichPatient(p)));
            return;
        }

        if (path.startsWith("/api/patients/") && path.endsWith("/predict") && "POST".equals(method)) {
            String pid = path.substring("/api/patients/".length(), path.length() - "/predict".length());
            Patient p = store.getPatient(pid);
            if (p == null) {
                sendJson(exchange, 404, Map.of("error", "Patient not found"));
                return;
            }
            store.incrementFLRound();
            Vitals v = store.getLatestVitals(pid);
            LabResult l = store.getLatestLabs(pid);
            RiskPrediction pred = predictionService.predictRisk(p, v, l, store.getFederatedRound());
            store.setPrediction(pid, pred);
            store.logAudit("Dr. Meera Iyer", "CLINICIAN", "RUN_PREDICTION", "RiskPrediction", pred.getPredId(), "SUCCESS");
            sendJsonRaw(exchange, 200, JsonUtil.predictionToJson(pred));
            return;
        }

        if (path.startsWith("/api/patients/") && path.endsWith("/what-if") && "POST".equals(method)) {
            String pid = path.substring("/api/patients/".length(), path.length() - "/what-if".length());
            Patient p = store.getPatient(pid);
            if (p == null) {
                sendJson(exchange, 404, Map.of("error", "Patient not found"));
                return;
            }
            Map<String, String> body = parseQueryParams(readBody(exchange));
            int age = parseInt(body.get("age"), p.getAge());
            int sys = parseInt(body.get("systolic"), store.getLatestVitals(pid).getSystolic());
            double hba1c = parseDouble(body.get("hba1c"), store.getLatestLabs(pid).getHba1c());
            int ldl = parseInt(body.get("ldl"), store.getLatestLabs(pid).getLdl());
            boolean smoking = Boolean.parseBoolean(body.getOrDefault("smoking", String.valueOf(p.isSmoking())));

            RiskPrediction sim = predictionService.computeWhatIf(p, age, sys, hba1c, ldl, smoking);
            sendJsonRaw(exchange, 200, JsonUtil.predictionToJson(sim));
            return;
        }

        if ("/api/predictions".equals(path) && "GET".equals(method)) {
            sendJsonRaw(exchange, 200, JsonUtil.listToJson(store.getAllPredictions()));
            return;
        }

        if ("/api/alerts".equals(path) && "GET".equals(method)) {
            sendJsonRaw(exchange, 200, JsonUtil.listToJson(store.getAllAlerts()));
            return;
        }

        if (path.startsWith("/api/alerts/") && path.endsWith("/acknowledge") && "POST".equals(method)) {
            String aid = path.substring("/api/alerts/".length(), path.length() - "/acknowledge".length());
            Alert ack = alertService.acknowledge(aid, "Dr. Meera Iyer");
            if (ack == null) {
                sendJson(exchange, 404, Map.of("error", "Alert not found"));
                return;
            }
            sendJsonRaw(exchange, 200, JsonUtil.alertToJson(ack));
            return;
        }

        if ("/api/simulate/tick".equals(path) && "POST".equals(method)) {
            List<Alert> newAlerts = alertService.simulateKafkaStreamTick();
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("newAlerts", newAlerts);
            resp.put("alertCount", newAlerts.size());
            resp.put("streamRate", "12K vitals/sec");
            resp.put("latency", "0.8s");
            sendJson(exchange, 200, resp);
            return;
        }

        if (path.startsWith("/api/patients/") && path.endsWith("/careplan") && "POST".equals(method)) {
            String pid = path.substring("/api/patients/".length(), path.length() - "/careplan".length());
            Patient p = store.getPatient(pid);
            if (p == null) {
                sendJson(exchange, 404, Map.of("error", "Patient not found"));
                return;
            }
            Vitals v = store.getLatestVitals(pid);
            LabResult l = store.getLatestLabs(pid);
            RiskPrediction pred = store.getPrediction(pid);
            if (pred == null) {
                pred = predictionService.predictRisk(p, v, l, store.getFederatedRound());
                store.setPrediction(pid, pred);
            }
            Careplan cp = careplanService.generateCareplan(p, v, l, pred);
            sendJsonRaw(exchange, 200, JsonUtil.careplanToJson(cp));
            return;
        }

        if ("/api/careplans".equals(path) && "GET".equals(method)) {
            sendJsonRaw(exchange, 200, JsonUtil.listToJson(store.getAllCareplans()));
            return;
        }

        if (path.startsWith("/api/careplans/") && path.endsWith("/approve") && "POST".equals(method)) {
            String pid = path.substring("/api/careplans/".length(), path.length() - "/approve".length());
            Careplan cp = careplanService.approveCareplan(pid, "Dr. Meera Iyer (NPI: 1945678901)", "MD, FACC");
            if (cp == null) {
                sendJson(exchange, 404, Map.of("error", "Careplan not found"));
                return;
            }
            sendJsonRaw(exchange, 200, JsonUtil.careplanToJson(cp));
            return;
        }

        if (path.startsWith("/api/fhir/") && "GET".equals(method)) {
            String pid = path.substring("/api/fhir/".length());
            Map<String, Object> bundle = fhirService.getFhirPatientBundle(pid);
            if (bundle == null) {
                sendJson(exchange, 404, Map.of("error", "FHIR resource not found"));
                return;
            }
            store.logAudit("Dr. Meera Iyer", "CLINICIAN", "EXPORT_FHIR", "Bundle", pid, "SUCCESS");
            sendJson(exchange, 200, bundle);
            return;
        }

        if ("/api/audit-logs".equals(path) && "GET".equals(method)) {
            sendJsonRaw(exchange, 200, JsonUtil.listToJson(auditService.getAuditLogs()));
            return;
        }

        if ("/api/federated-learning".equals(path) && "GET".equals(method)) {
            sendJsonRaw(exchange, 200, JsonUtil.flModelToJson(store.getFLModel()));
            return;
        }

        if ("/api/federated-learning/train-round".equals(path) && "POST".equals(method)) {
            store.incrementFLRound();
            store.logAudit("TFF_Coordinator", "FL_ENGINE", "AGGREGATE_WEIGHTS", "FLModel", "fl-cvd-v3.2", "SUCCESS");
            sendJsonRaw(exchange, 200, JsonUtil.flModelToJson(store.getFLModel()));
            return;
        }

        if ("/api/population-health".equals(path) && "GET".equals(method)) {
            Map<String, Object> pop = new LinkedHashMap<>();
            pop.put("totalCohortSize", 1247);
            pop.put("highRiskCount", 243);
            pop.put("moderateRiskCount", 581);
            pop.put("lowRiskCount", 423);
            pop.put("hospitalizationReduction", 23.4);
            pop.put("adherenceRate", 78.2);
            pop.put("topConditions", List.of(
                Map.of("name", "Hypertension", "prevalence", "54.2%"),
                Map.of("name", "Type 2 Diabetes", "prevalence", "38.6%"),
                Map.of("name", "Hyperlipidemia", "prevalence", "42.1%"),
                Map.of("name", "Atrial Fibrillation", "prevalence", "14.8%"),
                Map.of("name", "CKD Stage 2+", "prevalence", "12.3%")
            ));
            pop.put("demographicParityScore", 0.96);
            sendJson(exchange, 200, pop);
            return;
        }

        if (path.startsWith("/api/ecg/") && "GET".equals(method)) {
            String pid = path.substring("/api/ecg/".length());
            Vitals v = store.getLatestVitals(pid);
            String rhythm = v != null ? v.getRhythmStatus() : "Normal";
            List<Double> points = alertService.generateEcgWaveform(rhythm, 240);
            sendJson(exchange, 200, Map.of(
                "patientId", pid,
                "rhythm", rhythm,
                "heartRate", v != null ? v.getHeartRate() : 72,
                "wave", points
            ));
            return;
        }

        sendJson(exchange, 404, Map.of("error", "Endpoint not found: " + path));
    }

    private Map<String, Object> enrichPatient(Patient p) {
        String pid = p.getPatientId();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("patientId", p.getPatientId());
        map.put("fhirId", p.getFhirId());
        map.put("name", p.getName());
        map.put("age", p.getAge());
        map.put("sex", p.getSex());
        map.put("conditions", p.getConditions());
        map.put("smoking", p.isSmoking());
        map.put("familyHistoryCVD", p.isFamilyHistoryCVD());
        map.put("consent", p.isConsent());
        map.put("provider", p.getProvider());
        map.put("ehrSource", p.getEhrSource());
        map.put("medications", p.getMedications());
        map.put("onboardedAt", p.getOnboardedAt());

        map.put("vitals", store.getLatestVitals(pid));
        map.put("labs", store.getLatestLabs(pid));
        map.put("twin", store.getTwin(pid));
        map.put("prediction", store.getPrediction(pid));
        map.put("careplan", store.getCareplan(pid));
        return map;
    }

    private void serveStatic(HttpExchange exchange, String requestPath) throws IOException {
        String cleanPath = requestPath.equals("/") ? "/index.html" : requestPath;
        Path filePath = Paths.get(FRONTEND_DIR, cleanPath.replace("/", File.separator));

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            filePath = Paths.get(FRONTEND_DIR, "index.html");
        }

        byte[] bytes = Files.readAllBytes(filePath);
        String mime = getMimeType(filePath.toString());

        exchange.getResponseHeaders().set("Content-Type", mime + "; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getMimeType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "text/plain";
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        sendJsonRaw(exchange, statusCode, JsonUtil.toJson(data));
    }

    private void sendJsonRaw(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int r;
            while ((r = is.read(buf)) != -1) {
                bos.write(buf, 0, r);
            }
            return bos.toString(StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> parseQueryParams(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) return map;

        if (body.trim().startsWith("{")) {
            String trimmed = body.trim().substring(1, body.trim().length() - 1);
            String[] pairs = trimmed.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].trim().replaceAll("^\"|\"$", "");
                    String v = kv[1].trim().replaceAll("^\"|\"$", "");
                    map.put(k, v);
                }
            }
            return map;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String val = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    map.put(key, val);
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    private int parseInt(String val, int def) {
        if (val == null) return def;
        try { return Integer.parseInt(val.trim()); } catch (Exception e) { return def; }
    }

    private double parseDouble(String val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.trim()); } catch (Exception e) { return def; }
    }
}
