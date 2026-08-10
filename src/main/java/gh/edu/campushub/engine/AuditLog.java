package gh.edu.campushub.engine;

import gh.edu.campushub.db.AuditEventDao;
import gh.edu.campushub.model.AuditEvent;
import gh.edu.campushub.structures.Stack;

import java.time.LocalDateTime;

/**
 * Stack-based undo/audit log (M3 stack evidence): every mutating console
 * action is pushed here and persisted to {@code audit_events}. Undo pops the
 * most recent event, deletes its DB row, and hands it back to the caller so
 * the caller can restore {@code beforeState} on the actual entity — this
 * class only owns the LIFO bookkeeping, not domain-specific revert logic.
 */
public class AuditLog {

    private final AuditEventDao dao;
    private final Stack<AuditEvent> undoStack = new Stack<>();

    public AuditLog(AuditEventDao dao) {
        this.dao = dao;
        for (AuditEvent event : dao.findAll()) {
            undoStack.push(event); // DB rows come back id-ascending, so the last push is the most recent event
        }
    }

    public AuditEvent record(String eventType, String entityTable, String entityId,
                              String beforeState, String afterState) {
        AuditEvent event = new AuditEvent(null, eventType, entityTable, entityId,
                beforeState, afterState, LocalDateTime.now());
        AuditEvent saved = dao.insert(event);
        undoStack.push(saved);
        return saved;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public AuditEvent peekLast() {
        return undoStack.peek();
    }

    /** Pops and deletes the most recent event, returning it so the caller can apply {@code beforeState}. */
    public AuditEvent undoLast() {
        AuditEvent event = undoStack.pop();
        dao.deleteById(event.getEventId());
        return event;
    }

    public int size() {
        return undoStack.size();
    }
}
