package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.wire.WireConnection;
import net.locallupo.goldenfrontier.wire.WireRoutePlanner;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class WireRouteService {
    private final Map<WireConnection, List<Vec3>> cache = new HashMap<>();
    private ClientLevel cachedLevel;
    private long cachedTickBucket = Long.MIN_VALUE;

    void beginFrame(ClientLevel level) {
        // Wires are allowed to follow changed terrain, so don't keep routes for too long.
        long tickBucket = level.getGameTime() / 5L;
        if (cachedLevel != level || cachedTickBucket != tickBucket) {
            cache.clear();
            cachedLevel = level;
            cachedTickBucket = tickBucket;
        }
    }

    List<Vec3> route(ClientLevel level, WireConnection connection) {
        List<Vec3> cached = cache.get(connection);
        if (cached != null) return cached;

        WireRoutePlanner.RouteResult result = WireRoutePlanner.findRouteWithDiagnostics(
                new ClientWireTerrain(level), connection.first(), connection.second());
        List<Vec3> route = result.points().size() >= 2 ? result.points() : List.of();
        cache.put(connection, route);
        if (route.isEmpty()) {
            GoldenFrontier.LOGGER.debug("Wire route rejected {} -> {}: {}",
                    connection.first(), connection.second(), result.diagnostic());
        }
        return route;
    }
}
