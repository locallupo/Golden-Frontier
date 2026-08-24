package net.locallupo.goldenfrontier.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Deterministic, gravity-aware wire routing.  This class deliberately knows
 * nothing about a client level, so the routing rules can be unit tested.
 */
public final class WireRoutePlanner {
    public static final double CLEARANCE = 0.08;
    private static final double LEDGE_FACE_CLEARANCE = 0.025;
    public static final int MAX_UNSUPPORTED_BRIDGE = 2;
    private static final int MAX_EXPANDED_NODES = 50_000;
    // Start beyond the former 16-block ceiling so the first successful route is
    // already allowed to choose a meaningful detour, then grow on demand.
    private static final int[] GROWTH_STEPS = {32, 64, 128, 256, 512, 1024};
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    private WireRoutePlanner() {
    }

    public interface Terrain {
        int minY();
        int maxY();

        /** All exposed support surfaces in this column (surface and cave floors). */
        List<Surface> surfacesAt(int x, int z, BlockPos firstEndpoint, BlockPos secondEndpoint);

        /** True when the line segment is clear of collision shapes. */
        boolean lineClear(Vec3 start, Vec3 end, BlockPos firstEndpoint, BlockPos secondEndpoint);
    }

    public record Surface(Vec3 position, BlockPos supportBlock) {
    }

    public static List<Vec3> findRoute(Terrain terrain, BlockPos first, BlockPos second) {
        return findRouteWithDiagnostics(terrain, first, second).points();
    }

    public static RouteResult findRouteWithDiagnostics(Terrain terrain, BlockPos first, BlockPos second) {
        Vec3 start = Vec3.atCenterOf(first);
        Vec3 end = Vec3.atCenterOf(second);
        List<Surface> starts = reachableEndpointSurfaces(terrain, first, start, first, second);
        List<Surface> goals = reachableEndpointSurfaces(terrain, second, end, first, second);
        if (starts.isEmpty() || goals.isEmpty()) {
            return new RouteResult(List.of(), "no reachable endpoint surface (start=" + starts.size()
                    + ", end=" + goals.size() + ")");
        }

        SearchResult lastResult = null;
        for (int margin : GROWTH_STEPS) {
            SearchResult result = search(terrain, starts, goals, first, second, margin);
            lastResult = result;
            if (result.route() != null) {
                List<Vec3> route = new ArrayList<>();
                addPoint(route, start);
                for (Vec3 point : result.route()) {
                    addPoint(route, point);
                }
                addPoint(route, end);
                // Every non-attachment segment was collision-checked while it was
                // expanded.  Rechecking this flattened list would incorrectly test
                // the direct start-surface -> goal-surface transition when the
                // endpoints are adjacent; that transition is an attachment edge at
                // both ends and may legitimately intersect either endpoint shape.
                return new RouteResult(List.copyOf(route), "route found (margin=" + margin
                        + ", startSurfaces=" + starts.size() + ", endSurfaces=" + goals.size() + ")");
            }
            if (result.budgetExhausted()) {
                return new RouteResult(List.of(), "search work budget exhausted (margin=" + margin
                        + ", limit=" + MAX_EXPANDED_NODES + ", " + result.statistics() + ")");
            }
        }
        return new RouteResult(List.of(), "no valid route within the expanded search bounds (maxMargin="
                + GROWTH_STEPS[GROWTH_STEPS.length - 1] + ", starts=" + starts + ", goals=" + goals
                + ", " + lastResult.statistics() + ")");
    }

    public record RouteResult(List<Vec3> points, String diagnostic) {
        public boolean found() {
            return points.size() >= 2;
        }
    }

    private static List<Surface> reachableEndpointSurfaces(Terrain terrain, BlockPos endpoint, Vec3 point,
                                                             BlockPos first, BlockPos second) {
        // A column can contain several cave floors. Only the surface directly
        // associated with an endpoint is a valid zero-cost start/goal; all
        // other heights must be reached through normal, costed transitions.
        return terrain.surfacesAt(endpoint.getX(), endpoint.getZ(), first, second).stream()
                .min(Comparator.comparingDouble(surface -> Math.abs(surface.position().y - point.y)))
                .stream().toList();
    }

    private static SearchResult search(Terrain terrain, List<Surface> starts, List<Surface> goals,
                                       BlockPos first, BlockPos second, int margin) {
        int minX = Math.min(first.getX(), second.getX()) - margin;
        int maxX = Math.max(first.getX(), second.getX()) + margin;
        int minZ = Math.min(first.getZ(), second.getZ()) - margin;
        int maxZ = Math.max(first.getZ(), second.getZ()) + margin;
        Set<Node> goalNodes = new HashSet<>();
        for (Surface goal : goals) goalNodes.add(Node.of(goal));
        Set<Node> startNodes = new HashSet<>();

        Map<Node, Surface> surfaces = new HashMap<>();
        Map<Node, Cost> scores = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<QueueNode> open = new PriorityQueue<>();
        for (Surface start : starts) {
            Node node = Node.of(start);
            startNodes.add(node);
            surfaces.put(node, start);
            Cost cost = Cost.ZERO;
            if (cost.compareTo(scores.get(node)) < 0) {
                scores.put(node, cost);
                open.add(new QueueNode(node, cost, heuristic(node, goals)));
            }
        }

        int expanded = 0;
        int candidates = 0;
        int collisionRejected = 0;
        while (!open.isEmpty()) {
            QueueNode queued = open.poll();
            if (!queued.cost().equals(scores.get(queued.node()))) continue;
            if (++expanded > MAX_EXPANDED_NODES) return new SearchResult(null, true, expanded, candidates, collisionRejected);
            if (goalNodes.contains(queued.node())) return new SearchResult(buildRoute(previous, surfaces, queued.node(), terrain, first, second), false, expanded, candidates, collisionRejected);

            Surface current = surfaces.get(queued.node());
            for (int[] direction : DIRECTIONS) {
                int nextX = queued.node().x + direction[0];
                int nextZ = queued.node().z + direction[1];
                if (inside(nextX, nextZ, minX, maxX, minZ, maxZ)) {
                    for (Surface next : terrain.surfacesAt(nextX, nextZ, first, second)) {
                        candidates++;
                        if (!relax(terrain, current, next, queued.node(), startNodes, goalNodes, surfaces, scores, previous, open, goals, first, second)) collisionRejected++;
                    }
                }
                // A wire may cross a small unsupported hole, but never free-span farther.
                for (int span = 2; span <= MAX_UNSUPPORTED_BRIDGE + 1; span++) {
                    int bridgeX = queued.node().x + direction[0] * span;
                    int bridgeZ = queued.node().z + direction[1] * span;
                    if (!inside(bridgeX, bridgeZ, minX, maxX, minZ, maxZ) || hasSurface(terrain, queued.node(), direction, span, first, second)) break;
                    for (Surface next : terrain.surfacesAt(bridgeX, bridgeZ, first, second)) {
                        if (Math.abs(next.position().y - current.position().y) < 0.05) {
                            candidates++;
                            if (!relax(terrain, current, next, queued.node(), startNodes, goalNodes, surfaces, scores, previous, open, goals, first, second)) collisionRejected++;
                        }
                    }
                }
            }
        }
        return new SearchResult(null, false, expanded, candidates, collisionRejected);
    }

    private static boolean hasSurface(Terrain terrain, Node from, int[] direction, int span, BlockPos first, BlockPos second) {
        for (int step = 1; step < span; step++) {
            if (!terrain.surfacesAt(from.x + direction[0] * step, from.z + direction[1] * step, first, second).isEmpty()) return true;
        }
        return false;
    }

    private static boolean relax(Terrain terrain, Surface current, Surface next, Node currentNode,
                                 Set<Node> startNodes, Set<Node> goalNodes,
                                 Map<Node, Surface> surfaces, Map<Node, Cost> scores, Map<Node, Node> previous,
                                 PriorityQueue<QueueNode> open, List<Surface> goals, BlockPos first, BlockPos second) {
        Node nextNode = Node.of(next);
        List<Vec3> transition = transition(current.position(), next.position());
        if (!startNodes.contains(currentNode) && !goalNodes.contains(nextNode)) {
            Vec3 last = current.position();
            for (Vec3 point : transition) {
                if (!terrain.lineClear(last, point, first, second)) return false;
                last = point;
            }
        }
        Cost candidate = scores.get(currentNode).step(current.position(), next.position());
        Cost old = scores.get(nextNode);
        if (old == null || candidate.compareTo(old) < 0) {
            surfaces.put(nextNode, next);
            scores.put(nextNode, candidate);
            previous.put(nextNode, currentNode);
            open.add(new QueueNode(nextNode, candidate, heuristic(nextNode, goals)));
        }
        return true;
    }

    private static List<Vec3> buildRoute(Map<Node, Node> previous, Map<Node, Surface> surfaces, Node goal,
                                         Terrain terrain, BlockPos first, BlockPos second) {
        List<Surface> chain = new ArrayList<>();
        for (Node node = goal; node != null; node = previous.get(node)) chain.add(surfaces.get(node));
        java.util.Collections.reverse(chain);
        List<Vec3> points = new ArrayList<>();
        addPoint(points, chain.getFirst().position());
        for (int i = 1; i < chain.size(); i++) for (Vec3 point : transition(chain.get(i - 1).position(), chain.get(i).position())) addPoint(points, point);
        return points;
    }

    private static List<Vec3> transition(Vec3 start, Vec3 end) {
        if (Math.abs(start.y - end.y) < 0.05) return List.of(end);
        // Changes in height happen on the shared edge between two columns,
        // rather than after moving to the centre of the next block. This
        // makes a wire drop from a ledge, or climb a step, where a player
        // would expect it to in Minecraft.
        Vec3 horizontal = new Vec3(end.x - start.x, 0.0, end.z - start.z).normalize();
        // Put the vertical leg just inside the lower column's open side of the
        // ledge. Rendering exactly on the shared block face causes z-fighting.
        Vec3 lowerSide = end.y < start.y ? horizontal : horizontal.scale(-1.0);
        Vec3 edgeAtStartHeight = new Vec3((start.x + end.x) * 0.5 + lowerSide.x * LEDGE_FACE_CLEARANCE,
                start.y, (start.z + end.z) * 0.5 + lowerSide.z * LEDGE_FACE_CLEARANCE);
        Vec3 edgeAtEndHeight = new Vec3(edgeAtStartHeight.x, end.y, edgeAtStartHeight.z);
        return List.of(edgeAtStartHeight, edgeAtEndHeight, end);
    }

    private static boolean inside(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    private static double heuristic(Node node, List<Surface> goals) {
        return goals.stream().mapToDouble(goal -> Math.abs(node.x - goal.supportBlock().getX()) + Math.abs(node.z - goal.supportBlock().getZ())).min().orElse(0);
    }

    private static void addPoint(List<Vec3> route, Vec3 point) {
        if (route.isEmpty() || route.getLast().distanceToSqr(point) > 0.0001) route.add(point);
    }


    private record Node(int x, int y, int z) {
        static Node of(Surface surface) { return new Node(surface.supportBlock().getX(), surface.supportBlock().getY(), surface.supportBlock().getZ()); }
    }

    private record Cost(double distance, double elevation, int turns) implements Comparable<Cost> {
        static final Cost ZERO = new Cost(0, 0, 0);
        Cost step(Vec3 from, Vec3 to) { return new Cost(distance + from.distanceTo(to), elevation + Math.abs(from.y - to.y), turns + (Math.abs(from.y - to.y) > 0.05 ? 1 : 0)); }
        @Override public int compareTo(Cost other) {
            if (other == null) return -1;
            int result = Double.compare(distance, other.distance);
            if (result != 0) return result;
            result = Double.compare(elevation, other.elevation);
            return result != 0 ? result : Integer.compare(turns, other.turns);
        }
    }

    private record QueueNode(Node node, Cost cost, double heuristic) implements Comparable<QueueNode> {
        @Override public int compareTo(QueueNode other) {
            int result = Double.compare(cost.distance + heuristic, other.cost.distance + other.heuristic);
            if (result != 0) return result;
            result = cost.compareTo(other.cost);
            if (result != 0) return result;
            result = Integer.compare(node.x, other.node.x);
            if (result != 0) return result;
            result = Integer.compare(node.z, other.node.z);
            return Integer.compare(node.y, other.node.y);
        }
    }

    private record SearchResult(List<Vec3> route, boolean budgetExhausted, int expanded, int candidates,
                                int collisionRejected) {
        String statistics() {
            return "expanded=" + expanded + ", neighbourSurfaces=" + candidates
                    + ", collisionRejected=" + collisionRejected;
        }
    }
}
