package gh.edu.campushub.model;

import gh.edu.campushub.config.TeamConfig;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/** A single service request routed from a source to a destination location. */
public class ServiceRequest {
    private final String requestId;
    private final String sourceLocationId;
    private final String destinationLocationId;
    private final String category;
    private final int urgency; // 1 (low) .. 5 (critical)
    private final LocalDateTime timeSubmitted;
    private final LocalDateTime deadline;
    private RequestStatus status;

    public ServiceRequest(String requestId, String sourceLocationId, String destinationLocationId,
                           String category, int urgency, LocalDateTime timeSubmitted,
                           LocalDateTime deadline, RequestStatus status) {
        this.requestId = requestId;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.category = category;
        this.urgency = urgency;
        this.timeSubmitted = timeSubmitted;
        this.deadline = deadline;
        this.status = status;
    }

    public String getRequestId() { return requestId; }
    public String getSourceLocationId() { return sourceLocationId; }
    public String getDestinationLocationId() { return destinationLocationId; }
    public String getCategory() { return category; }
    public int getUrgency() { return urgency; }
    public LocalDateTime getTimeSubmitted() { return timeSubmitted; }
    public LocalDateTime getDeadline() { return deadline; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    /**
     * Dispatch priority score used by the heap-based scheduling engine (M5):
     * urgency is the PRIMARY key (a critical request always beats a routine one,
     * no matter how close the routine one's deadline is), with time-to-deadline
     * only breaking ties within the same urgency level. A lower score means
     * "dispatch sooner" (min-heap ordering).
     *
     * <p>Urgency tiers are separated by {@link TeamConfig#PRIORITY_WEIGHT} *
     * 100,000 minutes (~69 days per weight point) — comfortably wider than the
     * dataset's real deadline spread — specifically so time-to-deadline can
     * never leak across a tier boundary and outrank a more urgent request.
     */
    public double dispatchScore(LocalDateTime now) {
        long minutesToDeadline = ChronoUnit.MINUTES.between(now, deadline);
        double urgencyTier = (5 - urgency) * TeamConfig.PRIORITY_WEIGHT * 100_000.0;
        return urgencyTier + minutesToDeadline;
    }

    @Override
    public String toString() {
        return String.format("ServiceRequest{%s, %s->%s, %s, urgency=%d, %s, deadline=%s, %s}",
                requestId, sourceLocationId, destinationLocationId, category, urgency,
                timeSubmitted, deadline, status);
    }
}
