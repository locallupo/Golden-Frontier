package net.locallupo.goldenfrontier.client;

import net.locallupo.goldenfrontier.wire.WireConnection;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class WireIgnitionAnimator {
    private static final double MAX_IGNITION_SECONDS = 0.65;

    private WireIgnitionAnimator() {}

    static Optional<Progress> progress(WireConnection connection, List<Vec3> points) {
        return progress(connection, points, System.nanoTime());
    }

    static Optional<Progress> progress(WireConnection connection, List<Vec3> points, long nowNanos) {
        Optional<WireClientState.Ignition> ignition = WireClientState.ignition();
        if (ignition.isEmpty() || !ignition.get().connections().contains(connection)) return Optional.empty();

        boolean fromFirst;
        if (connection.first().equals(ignition.get().detonator())) fromFirst = true;
        else if (connection.second().equals(ignition.get().detonator())) fromFirst = false;
        else return Optional.empty();

        double elapsed = (nowNanos - ignition.get().startedAtNanos()) / 1_000_000_000.0;
        if (elapsed > MAX_IGNITION_SECONDS) {
            // Keep the shared ignition state intact until the renderer has
            // processed every connection in the batch. Returning empty here
            // would make the renderer draw the complete wire as a fallback.
            return Optional.of(new Progress(1.0, fromFirst, length(points)));
        }
        double length = length(points);
        double duration = Math.max(0.16, Math.min(0.42, length / 24.0));
        return Optional.of(new Progress(elapsed / duration, fromFirst, length));
    }

    static boolean expired(long nowNanos) {
        return WireClientState.ignition().map(ignition ->
                (nowNanos - ignition.startedAtNanos()) / 1_000_000_000.0 > MAX_IGNITION_SECONDS
        ).orElse(false);
    }

    static List<Vec3> unburned(List<Vec3> points, Progress progress) {
        if (progress.progress() <= 0.0) return points;
        if (progress.progress() >= 1.0) return List.of();
        double burned = progress.progress() * progress.length();
        return progress.fromFirst() ? slice(points, burned, progress.length())
                : slice(points, 0.0, progress.length() - burned);
    }

    static List<Vec3> pulse(List<Vec3> points, Progress progress) {
        if (progress.progress() <= 0.0 || progress.progress() >= 1.0) return List.of();
        double head = Math.min(progress.length(), Math.max(0.0, progress.progress() * progress.length()));
        double tail = Math.max(0.0, head - 0.42);
        return progress.fromFirst() ? slice(points, tail, head)
                : slice(points, progress.length() - head, progress.length() - tail);
    }

    static double length(List<Vec3> points) {
        double result = 0.0;
        for (int i = 1; i < points.size(); i++) result += points.get(i - 1).distanceTo(points.get(i));
        return result;
    }

    static List<Vec3> slice(List<Vec3> points, double from, double to) {
        List<Vec3> result = new ArrayList<>();
        double travelled = 0.0;
        for (int i = 1; i < points.size() && travelled < to; i++) {
            Vec3 start = points.get(i - 1);
            Vec3 end = points.get(i);
            double segmentLength = start.distanceTo(end);
            if (segmentLength < 0.0001) continue;
            double segmentStart = Math.max(from, travelled);
            double segmentEnd = Math.min(to, travelled + segmentLength);
            if (segmentStart < segmentEnd) {
                addPoint(result, start.lerp(end, (segmentStart - travelled) / segmentLength));
                addPoint(result, start.lerp(end, (segmentEnd - travelled) / segmentLength));
            }
            travelled += segmentLength;
        }
        return result;
    }

    private static void addPoint(List<Vec3> points, Vec3 point) {
        if (points.isEmpty() || points.getLast().distanceToSqr(point) > 0.0001) points.add(point);
    }

    record Progress(double progress, boolean fromFirst, double length) {}
}
