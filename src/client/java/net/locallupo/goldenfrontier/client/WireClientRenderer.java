package net.locallupo.goldenfrontier.client;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.locallupo.goldenfrontier.GoldenFrontier;
import net.locallupo.goldenfrontier.wire.WireConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.PriorityQueue;

public final class WireClientRenderer {
    private static final double GROUND_CLEARANCE = 0.08;
    private static final Map<WireConnection, List<Vec3>> ROUTE_CACHE = new HashMap<>();
    private static ClientLevel cachedLevel;
    private static long cachedTickBucket = Long.MIN_VALUE;

    private WireClientRenderer() {
    }

    public static void initialize() {
        LevelRenderEvents.COLLECT_SUBMITS.register(WireClientRenderer::render);
    }

    private static void render(LevelRenderContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || context.levelState().cameraRenderState == null) {
            return;
        }

        long tickBucket = level.getGameTime() / 5L;
        if (cachedLevel != level || cachedTickBucket != tickBucket) {
            ROUTE_CACHE.clear();
            cachedLevel = level;
            cachedTickBucket = tickBucket;
        }

        PoseStack poseStack = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        for (WireConnection connection : WireClientState.connections()) {
            if (isEndpoint(level, connection.first()) && isEndpoint(level, connection.second())) {
                renderConnection(context, poseStack, level, connection);
            }
        }

        WireClientState.selection().ifPresent(pos -> {
            if (isEndpoint(level, pos)) {
                Vec3 start = endpointPosition(pos);
                submitLeashGeometry(context, poseStack, level, start, start.add(0.0, 0.75, 0.0), false);
            }
        });

        poseStack.popPose();
    }

    private static void renderConnection(LevelRenderContext context, PoseStack poseStack, ClientLevel level,
                                         WireConnection connection) {
        List<Vec3> points = connectionPoints(level, connection);
        if (points.size() < 2) {
            return;
        }
        for (int i = 0; i < points.size() - 1; i++) {
            submitLeashGeometry(context, poseStack, level, points.get(i), points.get(i + 1), false);
        }
    }

    private static List<Vec3> connectionPoints(ClientLevel level, WireConnection connection) {
        List<Vec3> cached = ROUTE_CACHE.get(connection);
        if (cached != null) {
            return cached;
        }

        Vec3 start = endpointPosition(connection.first());
        Vec3 end = endpointPosition(connection.second());
        List<Vec3> groundRoute = findGroundRoute(level, connection.first(), connection.second(), start, end);
        if (groundRoute.size() >= 2) {
            ROUTE_CACHE.put(connection, groundRoute);
            return groundRoute;
        }
        ROUTE_CACHE.put(connection, List.of());
        return List.of();
    }

    private static List<Vec3> findGroundRoute(ClientLevel level, BlockPos first, BlockPos second,
                                               Vec3 start, Vec3 end) {
        int endpointLayer = Math.min(first.getY(), second.getY());
        int searchCeiling = level.dimensionType().minY() + level.dimensionType().height() - 1;
        Optional<SurfacePoint> startSurface = findGroundSurface(level, first.getX(), first.getZ(), searchCeiling,
                endpointLayer, first, second);
        Optional<SurfacePoint> endSurface = findGroundSurface(level, second.getX(), second.getZ(), searchCeiling,
                endpointLayer, first, second);
        if (startSurface.isEmpty() || endSurface.isEmpty()) {
            logGroundFailure(first, second, "no endpoint surface", startSurface, endSurface, 0);
            return List.of();
        }

        List<SurfacePoint> surfacePath = findGroundSurfacePath(level, startSurface.get(), endSurface.get(),
                searchCeiling, endpointLayer, first, second);
        if (surfacePath.isEmpty()
                && groundSurfaceTransitionClear(level, startSurface.get().position(), endSurface.get().position(),
                first, second)) {
            surfacePath = List.of(startSurface.get(), endSurface.get());
        }
        if (surfacePath.isEmpty()
                || !endpointTransitionClear(level, start, startSurface.get().position(), first)
                || !endpointTransitionClear(level, endSurface.get().position(), end, second)) {
            logGroundFailure(first, second, "surface path or endpoint transition", startSurface, endSurface, surfacePath.size());
            return List.of();
        }

        List<Vec3> route = new ArrayList<>(surfacePath.size() + 2);
        route.add(start);
        for (int i = 0; i < surfacePath.size(); i++) {
            SurfacePoint surface = surfacePath.get(i);
            if (i > 0) {
                addGroundTransitionPoints(route, surfacePath.get(i - 1).position(), surface.position());
            } else {
                addRoutePoint(route, surface.position());
            }
        }
        addRoutePoint(route, end);
        if (!validateRoute(level, route, first, second)) {
            logGroundFailure(first, second, "route collision validation", startSurface, endSurface, surfacePath.size());
            return List.of();
        }
        GoldenFrontier.LOGGER.info("Ground route {} -> {}: {} points, y-values {}",
                first, second, route.size(), route.stream().map(point -> String.format("%.2f", point.y)).toList());
        return route;
    }

    private static void logGroundFailure(BlockPos first, BlockPos second, String reason,
                                         Optional<SurfacePoint> startSurface,
                                         Optional<SurfacePoint> endSurface,
                                         int pathSize) {
        GoldenFrontier.LOGGER.info("Ground route rejected {} -> {}: {}, startSurface={}, endSurface={}, pathPoints={}",
                first, second, reason, startSurface.map(SurfacePoint::position).orElse(null),
                endSurface.map(SurfacePoint::position).orElse(null), pathSize);
    }

    private static Optional<SurfacePoint> findGroundSurface(ClientLevel level, int x, int z, int searchCeiling,
                                                            int preferredLayer, BlockPos firstEndpoint,
                                                            BlockPos secondEndpoint) {
        int lowStart = Math.min(preferredLayer, searchCeiling);
        for (int y = lowStart; y >= level.dimensionType().minY(); y--) {
            Optional<SurfacePoint> surface = surfaceAt(level, x, z, y, firstEndpoint, secondEndpoint);
            if (surface.isPresent()) {
                return surface;
            }
        }
        for (int y = preferredLayer + 1; y <= searchCeiling; y++) {
            Optional<SurfacePoint> surface = surfaceAt(level, x, z, y, firstEndpoint, secondEndpoint);
            if (surface.isPresent()) {
                return surface;
            }
        }
        return Optional.empty();
    }

    private static Optional<SurfacePoint> surfaceAt(ClientLevel level, int x, int z, int y,
                                                     BlockPos firstEndpoint, BlockPos secondEndpoint) {
            BlockPos support = new BlockPos(x, y, z);
            if (support.equals(firstEndpoint) || support.equals(secondEndpoint)) {
                return Optional.empty();
            }
            VoxelShape shape = level.getBlockState(support).getCollisionShape(level, support);
            if (shape.isEmpty()) {
                return Optional.empty();
            }
            double surfaceY = y + shape.max(Direction.Axis.Y) + GROUND_CLEARANCE;
            BlockPos space = new BlockPos(x, (int) Math.floor(surfaceY), z);
            if (!space.equals(firstEndpoint) && !space.equals(secondEndpoint)) {
                VoxelShape spaceShape = level.getBlockState(space).getCollisionShape(level, space);
                if (!spaceShape.isEmpty()
                        && space.getY() + spaceShape.max(Direction.Axis.Y) > surfaceY - GROUND_CLEARANCE) {
                    return Optional.empty();
                }
            }
            return Optional.of(new SurfacePoint(
                    new Vec3(x + 0.5, surfaceY, z + 0.5), support, Direction.UP));
    }

    private static boolean columnClearAbove(ClientLevel level, int x, int z, int fromY, int toY,
                                            BlockPos firstEndpoint, BlockPos secondEndpoint) {
        for (int y = fromY; y <= toY; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (pos.equals(firstEndpoint) || pos.equals(secondEndpoint)) {
                continue;
            }
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<SurfacePoint> findGroundSurfacePath(ClientLevel level, SurfacePoint start,
                                                             SurfacePoint goal, int searchCeiling, int endpointLayer,
                                                             BlockPos firstEndpoint, BlockPos secondEndpoint) {
        int margin = 16;
        int minX = Math.min(start.supportBlock().getX(), goal.supportBlock().getX()) - margin;
        int maxX = Math.max(start.supportBlock().getX(), goal.supportBlock().getX()) + margin;
        int minZ = Math.min(start.supportBlock().getZ(), goal.supportBlock().getZ()) - margin;
        int maxZ = Math.max(start.supportBlock().getZ(), goal.supportBlock().getZ()) + margin;
        BlockPos startKey = new BlockPos(start.supportBlock().getX(), 0, start.supportBlock().getZ());
        BlockPos goalKey = new BlockPos(goal.supportBlock().getX(), 0, goal.supportBlock().getZ());
        Map<BlockPos, SurfacePoint> surfaces = new HashMap<>();
        Map<BlockPos, Double> scores = new HashMap<>();
        Map<BlockPos, BlockPos> previous = new HashMap<>();
        Set<BlockPos> closed = new HashSet<>();
        PriorityQueue<GroundSearchNode> open = new PriorityQueue<>(Comparator.comparingDouble(GroundSearchNode::priority));
        surfaces.put(startKey, start);
        surfaces.put(goalKey, goal);
        scores.put(startKey, 0.0);
        open.add(new GroundSearchNode(startKey, groundHeuristic(startKey, goalKey)));

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!open.isEmpty()) {
            BlockPos current = open.poll().position();
            if (!closed.add(current)) {
                continue;
            }
            if (current.equals(goalKey)) {
                List<BlockPos> keys = reconstructPath(previous, current);
                List<SurfacePoint> result = new ArrayList<>(keys.size());
                for (BlockPos key : keys) {
                    result.add(surfaces.get(key));
                }
                return result;
            }

            SurfacePoint currentSurface = surfaces.get(current);
            for (int[] direction : directions) {
                BlockPos next = new BlockPos(current.getX() + direction[0], 0, current.getZ() + direction[1]);
                if (next.getX() < minX || next.getX() > maxX || next.getZ() < minZ || next.getZ() > maxZ
                        || closed.contains(next)) {
                    continue;
                }
                Optional<SurfacePoint> candidate = next.equals(goalKey)
                        ? Optional.of(goal)
                        : findGroundSurface(level, next.getX(), next.getZ(), searchCeiling, endpointLayer,
                        firstEndpoint, secondEndpoint);
                if (candidate.isEmpty()) {
                    continue;
                }
                SurfacePoint nextSurface = candidate.get();
                if (!groundSurfaceTransitionClear(level, currentSurface.position(), nextSurface.position(),
                        firstEndpoint, secondEndpoint)) {
                    continue;
                }
                surfaces.put(next, nextSurface);
                double heightCost = Math.abs(currentSurface.position().y - nextSurface.position().y) * 3.0;
                double candidateScore = scores.get(current) + 1.0 + heightCost;
                if (candidateScore < scores.getOrDefault(next, Double.POSITIVE_INFINITY)) {
                    previous.put(next, current);
                    scores.put(next, candidateScore);
                    open.add(new GroundSearchNode(next, candidateScore + groundHeuristic(next, goalKey)));
                }
            }
        }
        return Collections.emptyList();
    }

    private static double groundHeuristic(BlockPos from, BlockPos to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getZ() - to.getZ());
    }

    private static boolean endpointTransitionClear(ClientLevel level, Vec3 start, Vec3 end, BlockPos endpoint) {
        Vec3 delta = end.subtract(start);
        if (Math.hypot(delta.x, delta.z) > 0.08) {
            return lineIsClear(level, start, end);
        }
        int minY = (int) Math.floor(Math.min(start.y, end.y));
        int maxY = (int) Math.floor(Math.max(start.y, end.y));
        for (int y = minY; y <= maxY; y++) {
            BlockPos pos = new BlockPos(endpoint.getX(), y, endpoint.getZ());
            if (pos.equals(endpoint)) {
                continue;
            }
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateRoute(ClientLevel level, List<Vec3> route, BlockPos first, BlockPos second) {
        if (route.size() < 2) {
            return false;
        }
        for (int i = 0; i < route.size() - 1; i++) {
            if (i == 0 && !endpointTransitionClear(level, route.get(i), route.get(i + 1), first)) {
                return false;
            }
            if (i == route.size() - 2 && !endpointTransitionClear(level, route.get(i), route.get(i + 1), second)) {
                return false;
            }
            if (i > 0 && i < route.size() - 2
                    && !groundSurfaceTransitionClear(level, route.get(i), route.get(i + 1), first, second)) {
                return false;
            }
        }
        return true;
    }

    private static List<BlockPos> findPath(ClientLevel level, BlockPos start, BlockPos goal) {
        int margin = 8;
        int minX = Math.min(start.getX(), goal.getX()) - margin;
        int maxX = Math.max(start.getX(), goal.getX()) + margin;
        int minZ = Math.min(start.getZ(), goal.getZ()) - margin;
        int maxZ = Math.max(start.getZ(), goal.getZ()) + margin;
        int minY = Math.min(start.getY(), goal.getY()) - 2;
        int maxY = Math.max(start.getY(), goal.getY()) + 16;

        PriorityQueue<SearchNode> open = new PriorityQueue<>(Comparator.comparingDouble(SearchNode::priority));
        Map<BlockPos, Double> scores = new HashMap<>();
        Map<BlockPos, BlockPos> previous = new HashMap<>();
        Set<BlockPos> closed = new HashSet<>();
        scores.put(start, 0.0);
        open.add(new SearchNode(start, heuristic(start, goal)));

        int[][] directions = {
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
                {0, 1, 0}, {0, -1, 0}
        };

        while (!open.isEmpty()) {
            BlockPos current = open.poll().position();
            if (!closed.add(current)) {
                continue;
            }
            if (current.equals(goal)) {
                return reconstructPath(previous, current);
            }

            for (int[] direction : directions) {
                if (current.equals(start) && direction[1] != 0) {
                    continue;
                }
                BlockPos next = current.offset(direction[0], direction[1], direction[2]);
                if (next.getX() < minX || next.getX() > maxX
                        || next.getY() < minY || next.getY() > maxY
                        || next.getZ() < minZ || next.getZ() > maxZ
                        || (next.equals(goal) && direction[1] != 0)
                        || !isPassable(level, next) || closed.contains(next)) {
                    continue;
                }

                // Strongly prefer a ground-level detour. Climbing is reserved for
                // cases where the obstacle cannot be reasonably bypassed.
                double verticalCost = direction[1] == 0 ? 1.0 : 6.0;
                double candidateScore = scores.get(current) + verticalCost;
                if (candidateScore < scores.getOrDefault(next, Double.POSITIVE_INFINITY)) {
                    previous.put(next, current);
                    scores.put(next, candidateScore);
                    open.add(new SearchNode(next, candidateScore + heuristic(next, goal)));
                }
            }
        }
        return Collections.emptyList();
    }

    private static double heuristic(BlockPos from, BlockPos to) {
        return Math.abs(from.getX() - to.getX())
                + Math.abs(from.getZ() - to.getZ())
                + Math.abs(from.getY() - to.getY()) * 6.0;
    }

    private static List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> previous, BlockPos current) {
        List<BlockPos> path = new ArrayList<>();
        path.add(current);
        while (previous.containsKey(current)) {
            current = previous.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

    private static boolean isPassable(ClientLevel level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    private static Vec3 cellCenter(BlockPos cell) {
        return new Vec3(cell.getX() + 0.5, cell.getY() + 0.05, cell.getZ() + 0.5);
    }

    private static Optional<Vec3> supportAnchor(ClientLevel level, BlockPos cell) {
        Vec3 cellCenter = cellCenter(cell);
        List<Vec3> candidates = new ArrayList<>();

        BlockPos below = cell.below();
        VoxelShape belowShape = level.getBlockState(below).getCollisionShape(level, below);
        if (!belowShape.isEmpty()) {
            double top = below.getY() + belowShape.max(Direction.Axis.Y) + 0.02;
            candidates.add(new Vec3(cellCenter.x, top, cellCenter.z));
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos beside = cell.relative(direction);
            VoxelShape besideShape = level.getBlockState(beside).getCollisionShape(level, beside);
            if (!besideShape.isEmpty()) {
                candidates.add(new Vec3(
                        cellCenter.x + direction.getStepX() * 0.52,
                        cell.getY() + 0.5,
                        cellCenter.z + direction.getStepZ() * 0.52
                ));
            }
        }

        return candidates.stream().min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(cellCenter)));
    }

    private static List<Vec3> simplifySupportedPath(ClientLevel level, List<Vec3> supported) {
        return simplifyPath(level, supported);
    }

    private static boolean isSupportedRoute(ClientLevel level, List<Vec3> route) {
        if (route.size() < 2) {
            return false;
        }
        for (int i = 0; i < route.size() - 1; i++) {
            if (!lineIsClear(level, route.get(i), route.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    private static List<Vec3> simplifyPath(ClientLevel level, List<Vec3> raw) {
        List<Vec3> simplified = new ArrayList<>();
        int current = 0;
        simplified.add(raw.get(0));
        while (current < raw.size() - 1) {
            int furthest = current + 1;
            if (current != 0 && current != raw.size() - 2) {
                for (int candidate = raw.size() - 1; candidate > furthest; candidate--) {
                    Vec3 from = raw.get(current);
                    Vec3 to = raw.get(candidate);
                    double horizontalDelta = Math.hypot(to.x - from.x, to.z - from.z);
                    boolean sameHeight = Math.abs(to.y - from.y) < 0.08;
                    boolean sameColumn = horizontalDelta < 0.08;
                    if ((sameHeight || sameColumn) && lineIsClear(level, from, to)) {
                        furthest = candidate;
                        break;
                    }
                }
            }
            simplified.add(raw.get(furthest));
            current = furthest;
        }
        return simplified;
    }

    private static boolean lineIsClear(ClientLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < 0.04) {
            return true;
        }
        Vec3 inset = delta.normalize().scale(0.02);
        return level.clip(new ClipContext(start.add(inset), end.subtract(inset), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                        CollisionContext.empty()))
                .getType() == HitResult.Type.MISS;
    }

    private static boolean lineIsClearIgnoringEndpoints(ClientLevel level, Vec3 start, Vec3 end,
                                                         BlockPos firstEndpoint, BlockPos secondEndpoint) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < 0.04) {
            return true;
        }

        Vec3 direction = delta.scale(1.0 / length);
        Vec3 adjustedStart = start;
        Vec3 adjustedEnd = end;
        BlockPos startBlock = BlockPos.containing(start);
        BlockPos endBlock = BlockPos.containing(end);
        if (startBlock.equals(firstEndpoint) || startBlock.equals(secondEndpoint)) {
            adjustedStart = start.add(direction.scale(Math.min(0.55, length * 0.45)));
        }
        if (endBlock.equals(firstEndpoint) || endBlock.equals(secondEndpoint)) {
            adjustedEnd = end.subtract(direction.scale(Math.min(0.55, length * 0.45)));
        }
        return lineIsClear(level, adjustedStart, adjustedEnd);
    }

    private static boolean groundSurfaceTransitionClear(ClientLevel level, Vec3 start, Vec3 end,
                                                         BlockPos firstEndpoint, BlockPos secondEndpoint) {
        if (Math.abs(start.y - end.y) < 0.05) {
            return true;
        }
        Vec3 step;
        if (end.y < start.y) {
            step = new Vec3(end.x, start.y, end.z);
            return lineIsClearIgnoringEndpoints(level, start, step, firstEndpoint, secondEndpoint)
                    && verticalDropClear(level, end, start.y, firstEndpoint, secondEndpoint);
        }
        step = new Vec3((start.x + end.x) * 0.5, end.y, (start.z + end.z) * 0.5);
        return lineIsClearIgnoringEndpoints(level, start, step, firstEndpoint, secondEndpoint)
                && lineIsClearIgnoringEndpoints(level, step, end, firstEndpoint, secondEndpoint);
    }

    private static void addGroundTransitionPoints(List<Vec3> route, Vec3 start, Vec3 end) {
        if (Math.abs(start.y - end.y) < 0.05) {
            addRoutePoint(route, end);
            return;
        }
        Vec3 step = end.y > start.y
                ? new Vec3((start.x + end.x) * 0.5, end.y, (start.z + end.z) * 0.5)
                : new Vec3(end.x, start.y, end.z);
        addRoutePoint(route, step);
        addRoutePoint(route, end);
    }

    private static boolean verticalDropClear(ClientLevel level, Vec3 lowPoint, double highY,
                                              BlockPos firstEndpoint, BlockPos secondEndpoint) {
        int x = BlockPos.containing(lowPoint).getX();
        int z = BlockPos.containing(lowPoint).getZ();
        int lowY = (int) Math.floor(lowPoint.y) + 1;
        int highBlockY = (int) Math.floor(highY);
        for (int y = lowY; y <= highBlockY; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (pos.equals(firstEndpoint) || pos.equals(secondEndpoint)) {
                continue;
            }
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<Vec3> applyGravity(ClientLevel level, List<Vec3> route) {
        if (route.size() < 3) {
            return route;
        }

        List<Vec3> grounded = new ArrayList<>(route);
        for (int i = 1; i < grounded.size() - 1; i++) {
            Vec3 point = grounded.get(i);
            Vec3 ground = groundPoint(level, point.x, point.z);
            if (ground.y >= point.y - 0.02 || !lineIsClear(level, point, ground)) {
                continue;
            }

            Vec3 previous = grounded.get(i - 1);
            Vec3 next = grounded.get(i + 1);
            if (lineIsClear(level, previous, ground) && lineIsClear(level, ground, next)) {
                grounded.set(i, ground);
            }
        }
        return grounded;
    }

    private static List<Vec3> addGroundSupports(ClientLevel level, List<Vec3> route, int endpointLayer) {
        if (route.size() < 2) {
            return route;
        }
        List<Vec3> supported = new ArrayList<>();
        for (int i = 0; i < route.size() - 1; i++) {
            Vec3 start = route.get(i);
            Vec3 end = route.get(i + 1);
            addRoutePoint(supported, start);
            double horizontalDistance = Math.hypot(end.x - start.x, end.z - start.z);
            int supportCount = Math.max(0, (int) Math.ceil(horizontalDistance / 3.0) - 1);
            for (int support = 1; support <= supportCount; support++) {
                double fraction = support / (double) (supportCount + 1);
                Vec3 candidate = groundPointBelowEndpointLayer(level,
                        start.x + (end.x - start.x) * fraction,
                        start.z + (end.z - start.z) * fraction,
                        endpointLayer);
                if (lineIsClear(level, start, candidate)
                        && lineIsClear(level, candidate, end)) {
                    addRoutePoint(supported, candidate);
                }
            }
        }
        addRoutePoint(supported, route.get(route.size() - 1));
        return supported;
    }

    private static Vec3 groundPoint(ClientLevel level, double x, double z) {
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        double y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ) + 0.02;
        return new Vec3(x, y, z);
    }

    private static Vec3 groundPointBelowEndpointLayer(ClientLevel level, double x, double z, int endpointLayer) {
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        for (int y = endpointLayer - 1; y >= level.dimensionType().minY(); y--) {
            BlockPos block = new BlockPos(blockX, y, blockZ);
            VoxelShape shape = level.getBlockState(block).getCollisionShape(level, block);
            if (!shape.isEmpty()) {
                return new Vec3(x, y + shape.max(Direction.Axis.Y) + 0.02, z);
            }
        }
        double heightmapY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ) + 0.02;
        return new Vec3(x, Math.min(heightmapY, endpointLayer + 0.02), z);
    }

    private static void addRoutePoint(List<Vec3> route, Vec3 point) {
        if (route.isEmpty() || route.get(route.size() - 1).distanceToSqr(point) > 0.0001) {
            route.add(point);
        }
    }

    private static void submitLeashGeometry(LevelRenderContext context, PoseStack poseStack, ClientLevel level,
                                            Vec3 start, Vec3 end, boolean slack) {
        context.submitNodeCollector().submitCustomGeometry(
                poseStack,
                RenderTypes.leash(),
                (pose, consumer) -> renderLeashTube(pose.pose(), consumer, level, start, end, slack)
        );
    }

    private static void renderLeashTube(org.joml.Matrix4fc matrix, VertexConsumer consumer, ClientLevel level,
                                        Vec3 start, Vec3 end, boolean slack) {
        Vec3 direction = end.subtract(start).normalize();
        Vec3 firstAxis = direction.cross(new Vec3(0.0, 1.0, 0.0));
        if (firstAxis.lengthSqr() < 0.0001) {
            firstAxis = direction.cross(new Vec3(1.0, 0.0, 0.0));
        }
        firstAxis = firstAxis.normalize().scale(0.045);
        Vec3 secondAxis = direction.cross(firstAxis).normalize().scale(0.045);

        int segments = 48;
        int sides = 8;
        for (int segment = 0; segment < segments; segment++) {
            // Keep neighboring sections edge-to-edge. Overlapping the quads causes
            // coplanar surfaces to z-fight, especially where the wire bends.
            float overlap = 0.0f;
            float startFraction = Math.max(0.0f, (segment / (float) segments) - overlap);
            float endFraction = Math.min(1.0f, ((segment + 1) / (float) segments) + overlap);
            Vec3 segmentStart = ropePoint(start, end, startFraction, slack);
            Vec3 segmentEnd = ropePoint(start, end, endFraction, slack);
            float shade = segment % 2 == 0 ? 1.0f : 0.85f;

            for (int side = 0; side < sides; side++) {
                double startAngle = side * Math.PI * 2.0 / sides;
                double endAngle = (side + 1) * Math.PI * 2.0 / sides;
                Vec3 startOffset = firstAxis.scale(Math.cos(startAngle)).add(secondAxis.scale(Math.sin(startAngle)));
                Vec3 endOffset = firstAxis.scale(Math.cos(endAngle)).add(secondAxis.scale(Math.sin(endAngle)));

                addVertex(consumer, matrix, level, segmentStart.add(startOffset), shade);
                addVertex(consumer, matrix, level, segmentStart.add(endOffset), shade);
                addVertex(consumer, matrix, level, segmentEnd.add(endOffset), shade);
                addVertex(consumer, matrix, level, segmentEnd.add(startOffset), shade);
            }
        }
    }

    private static Vec3 ropePoint(Vec3 start, Vec3 end, float fraction, boolean slack) {
        Vec3 point = start.lerp(end, fraction);
        if (slack) {
            point = point.subtract(0.0, Math.sin(Math.PI * fraction) * 0.15, 0.0);
        }
        return point;
    }

    private static void addVertex(VertexConsumer consumer, org.joml.Matrix4fc matrix, ClientLevel level,
                                  Vec3 position, float shade) {
        BlockPos lightPos = BlockPos.containing(position);
        int blockLight = level.getBrightness(LightLayer.BLOCK, lightPos);
        int skyLight = level.getBrightness(LightLayer.SKY, lightPos);
        int light = (blockLight << 4) | (skyLight << 20);
        consumer.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .setColor(0.48f * shade, 0.50f * shade, 0.52f * shade, 1.0f)
                .setLight(light);
    }

    private static Vec3 endpointPosition(BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    private static boolean isEndpoint(ClientLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.DETONATOR)
                || level.getBlockState(pos).is(ModBlocks.DYNAMITE);
    }

    private record SearchNode(BlockPos position, double priority) {
    }

    private record GroundSearchNode(BlockPos position, double priority) {
    }

    private record SurfacePoint(Vec3 position, BlockPos supportBlock, Direction supportFace) {
    }
}
