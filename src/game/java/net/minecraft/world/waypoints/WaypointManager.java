package net.minecraft.world.waypoints;

public interface WaypointManager<T extends Waypoint> {
    void trackWaypoint(T p_416095_);

    void updateWaypoint(T p_415971_);

    void untrackWaypoint(T p_416193_);
}
