package net.minecraft.server.waypoints;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.Sets.SetView;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.waypoints.WaypointManager;
import net.minecraft.world.waypoints.WaypointTransmitter;

public class ServerWaypointManager implements WaypointManager<WaypointTransmitter> {
    private final Set<WaypointTransmitter> waypoints = new HashSet<>();
    private final Set<ServerPlayer> players = new HashSet<>();
    private final Table<ServerPlayer, WaypointTransmitter, WaypointTransmitter.Connection> connections = HashBasedTable.create();

    public void trackWaypoint(WaypointTransmitter p_415909_) {
        this.waypoints.add(p_415909_);

        for (ServerPlayer serverplayer : this.players) {
            this.createConnection(serverplayer, p_415909_);
        }
    }

    public void updateWaypoint(WaypointTransmitter p_415954_) {
        if (this.waypoints.contains(p_415954_)) {
            Map<ServerPlayer, WaypointTransmitter.Connection> map = Tables.transpose(this.connections).row(p_415954_);
            SetView<ServerPlayer> setview = Sets.difference(this.players, map.keySet());

            for (Entry<ServerPlayer, WaypointTransmitter.Connection> entry : ImmutableSet.copyOf(map.entrySet())) {
                this.updateConnection(entry.getKey(), p_415954_, entry.getValue());
            }

            for (ServerPlayer serverplayer : setview) {
                this.createConnection(serverplayer, p_415954_);
            }
        }
    }

    public void untrackWaypoint(WaypointTransmitter p_416348_) {
        this.connections.column(p_416348_).forEach((p_415970_, p_416453_) -> p_416453_.disconnect());
        Tables.transpose(this.connections).row(p_416348_).clear();
        this.waypoints.remove(p_416348_);
    }

    public void addPlayer(ServerPlayer p_416227_) {
        this.players.add(p_416227_);

        for (WaypointTransmitter waypointtransmitter : this.waypoints) {
            this.createConnection(p_416227_, waypointtransmitter);
        }

        if (p_416227_.isTransmittingWaypoint()) {
            this.trackWaypoint((WaypointTransmitter)p_416227_);
        }
    }

    public void updatePlayer(ServerPlayer p_415814_) {
        Map<WaypointTransmitter, WaypointTransmitter.Connection> map = this.connections.row(p_415814_);
        SetView<WaypointTransmitter> setview = Sets.difference(this.waypoints, map.keySet());

        for (Entry<WaypointTransmitter, WaypointTransmitter.Connection> entry : ImmutableSet.copyOf(map.entrySet())) {
            this.updateConnection(p_415814_, entry.getKey(), entry.getValue());
        }

        for (WaypointTransmitter waypointtransmitter : setview) {
            this.createConnection(p_415814_, waypointtransmitter);
        }
    }

    public void removePlayer(ServerPlayer p_416554_) {
        this.connections.row(p_416554_).values().removeIf(p_415833_ -> {
            p_415833_.disconnect();
            return true;
        });
        this.untrackWaypoint((WaypointTransmitter)p_416554_);
        this.players.remove(p_416554_);
    }

    public void breakAllConnections() {
        this.connections.values().forEach(WaypointTransmitter.Connection::disconnect);
        this.connections.clear();
    }

    public void remakeConnections(WaypointTransmitter p_415728_) {
        for (ServerPlayer serverplayer : this.players) {
            this.createConnection(serverplayer, p_415728_);
        }
    }

    public Set<WaypointTransmitter> transmitters() {
        return this.waypoints;
    }

    private static boolean isLocatorBarEnabledFor(ServerPlayer p_423601_) {
        return p_423601_.level().getGameRules().get(GameRules.LOCATOR_BAR);
    }

    private void createConnection(ServerPlayer p_416685_, WaypointTransmitter p_416130_) {
        if (p_416685_ != p_416130_) {
            if (isLocatorBarEnabledFor(p_416685_)) {
                p_416130_.makeWaypointConnectionWith(p_416685_).ifPresentOrElse(p_416381_ -> {
                    this.connections.put(p_416685_, p_416130_, p_416381_);
                    p_416381_.connect();
                }, () -> {
                    WaypointTransmitter.Connection waypointtransmitter$connection = this.connections.remove(p_416685_, p_416130_);
                    if (waypointtransmitter$connection != null) {
                        waypointtransmitter$connection.disconnect();
                    }
                });
            }
        }
    }

    private void updateConnection(ServerPlayer p_415809_, WaypointTransmitter p_416073_, WaypointTransmitter.Connection p_415562_) {
        if (p_415809_ != p_416073_) {
            if (isLocatorBarEnabledFor(p_415809_)) {
                if (!p_415562_.isBroken()) {
                    p_415562_.update();
                } else {
                    p_416073_.makeWaypointConnectionWith(p_415809_).ifPresentOrElse(p_416199_ -> {
                        p_416199_.connect();
                        this.connections.put(p_415809_, p_416073_, p_416199_);
                    }, () -> {
                        p_415562_.disconnect();
                        this.connections.remove(p_415809_, p_416073_);
                    });
                }
            }
        }
    }
}
