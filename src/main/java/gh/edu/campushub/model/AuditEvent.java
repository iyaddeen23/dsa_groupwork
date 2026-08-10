package gh.edu.campushub.model;

import java.time.LocalDateTime;

/** One entry in the stack-based undo/audit log (M3 stack evidence, `audit_events` table). */
public class AuditEvent {
    private final Integer eventId; // null until persisted (auto-increment)
    private final String eventType;
    private final String entityTable;
    private final String entityId;
    private final String beforeState;
    private final String afterState;
    private final LocalDateTime createdAt;

    public AuditEvent(Integer eventId, String eventType, String entityTable, String entityId,
                       String beforeState, String afterState, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.entityTable = entityTable;
        this.entityId = entityId;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.createdAt = createdAt;
    }

    public Integer getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getEntityTable() { return entityTable; }
    public String getEntityId() { return entityId; }
    public String getBeforeState() { return beforeState; }
    public String getAfterState() { return afterState; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("AuditEvent{%s, %s on %s/%s, at=%s}",
                eventId, eventType, entityTable, entityId, createdAt);
    }
}
