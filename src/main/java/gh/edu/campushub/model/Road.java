package gh.edu.campushub.model;

import gh.edu.campushub.config.TeamConfig;

/** A road/path edge between two campus locations. Used to build the graph engine (M7). */
public class Road {
    private final String roadId;
    private final String fromLocationId;
    private final String toLocationId;
    private final double distanceKm;
    private final double travelTimeMin;
    private final double conditionWeight;

    public Road(String roadId, String fromLocationId, String toLocationId,
                double distanceKm, double travelTimeMin, double conditionWeight) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distanceKm = distanceKm;
        this.travelTimeMin = travelTimeMin;
        this.conditionWeight = conditionWeight;
    }

    public String getRoadId() { return roadId; }
    public String getFromLocationId() { return fromLocationId; }
    public String getToLocationId() { return toLocationId; }
    public double getDistanceKm() { return distanceKm; }
    public double getTravelTimeMin() { return travelTimeMin; }
    public double getConditionWeight() { return conditionWeight; }

    /**
     * Edge weight used by the graph algorithms: travel time scaled by the road's
     * condition weight and the team's {@link TeamConfig#ROUTE_PENALTY_FACTOR}, so a
     * poorly rated road (higher conditionWeight) costs more even at the same distance.
     */
    public double routeCost() {
        return travelTimeMin * conditionWeight * TeamConfig.ROUTE_PENALTY_FACTOR;
    }

    @Override
    public String toString() {
        return String.format("Road{%s, %s->%s, %.2fkm, %.1fmin, cond=%.2f}",
                roadId, fromLocationId, toLocationId, distanceKm, travelTimeMin, conditionWeight);
    }
}
