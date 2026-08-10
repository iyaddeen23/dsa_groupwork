package gh.edu.campushub.model;

/** A dispatchable resource: a shuttle van, a maintenance rider, an equipment kit, etc. */
public class Resource {
    private final String resourceId;
    private final String resourceType;
    private final String homeLocationId;
    private final int capacity;
    private AvailabilityStatus availabilityStatus;

    public Resource(String resourceId, String resourceType, String homeLocationId,
                     int capacity, AvailabilityStatus availabilityStatus) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.homeLocationId = homeLocationId;
        this.capacity = capacity;
        this.availabilityStatus = availabilityStatus;
    }

    public String getResourceId() { return resourceId; }
    public String getResourceType() { return resourceType; }
    public String getHomeLocationId() { return homeLocationId; }
    public int getCapacity() { return capacity; }
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus status) { this.availabilityStatus = status; }

    @Override
    public String toString() {
        return String.format("Resource{%s, %s, home=%s, cap=%d, %s}",
                resourceId, resourceType, homeLocationId, capacity, availabilityStatus);
    }
}
