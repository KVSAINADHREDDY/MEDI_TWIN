package com.medisphere.service;

import com.medisphere.model.AuditLog;
import com.medisphere.repository.TwinDataStore;
import java.util.List;

public class AuditService {
    public List<AuditLog> getAuditLogs() {
        return TwinDataStore.getInstance().getAuditLogs();
    }

    public void logAccess(String actor, String role, String action, String resourceType, String resourceId, String outcome) {
        TwinDataStore.getInstance().logAudit(actor, role, action, resourceType, resourceId, outcome);
    }
}
