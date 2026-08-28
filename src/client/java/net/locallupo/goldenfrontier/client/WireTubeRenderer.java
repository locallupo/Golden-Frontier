package net.locallupo.goldenfrontier.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

final class WireTubeRenderer {
    private static final int SIDES = 8;
    private static final double DEFAULT_RADIUS = 0.045;

    private WireTubeRenderer() {}

    static void submit(LevelRenderContext context, PoseStack poseStack, ClientLevel level,
                       RenderType renderType, List<Vec3> points) {
        submit(context, poseStack, level, renderType, points, 0.23f, 0.23f, 0.23f, DEFAULT_RADIUS);
    }

    static void submit(LevelRenderContext context, PoseStack poseStack, ClientLevel level,
                       RenderType renderType, List<Vec3> points,
                       float red, float green, float blue, double radius) {
        context.submitNodeCollector().submitCustomGeometry(poseStack, renderType,
                (pose, consumer) -> render(pose.pose(), consumer, level, points, red, green, blue, radius));
    }

    private static void render(org.joml.Matrix4fc matrix, VertexConsumer consumer, ClientLevel level,
                                List<Vec3> points, float red, float green, float blue, double radius) {
        if (points.size() < 2) return;
        List<TubeFrame> frames = new ArrayList<>(points.size());
        for (int i = 0; i < points.size(); i++) frames.add(frame(points, i, radius));
        for (int segment = 0; segment < points.size() - 1; segment++) {
            for (int side = 0; side < SIDES; side++) {
                double first = side * Math.PI * 2.0 / SIDES;
                double second = (side + 1) * Math.PI * 2.0 / SIDES;
                vertex(consumer, matrix, level, points.get(segment), frames.get(segment), first, red, green, blue);
                vertex(consumer, matrix, level, points.get(segment), frames.get(segment), second, red, green, blue);
                vertex(consumer, matrix, level, points.get(segment + 1), frames.get(segment + 1), second, red, green, blue);
                vertex(consumer, matrix, level, points.get(segment + 1), frames.get(segment + 1), first, red, green, blue);
            }
        }
    }

    private static TubeFrame frame(List<Vec3> points, int index, double radius) {
        Vec3 tangent = index == 0 ? points.get(1).subtract(points.get(0))
                : index == points.size() - 1 ? points.getLast().subtract(points.get(index - 1))
                : points.get(index + 1).subtract(points.get(index - 1));
        if (tangent.lengthSqr() < 0.0001 && index + 1 < points.size()) {
            tangent = points.get(index + 1).subtract(points.get(index));
        }
        tangent = tangent.normalize();
        Vec3 first = tangent.cross(new Vec3(0.0, 1.0, 0.0));
        if (first.lengthSqr() < 0.0001) first = tangent.cross(new Vec3(1.0, 0.0, 0.0));
        first = first.normalize().scale(radius);
        return new TubeFrame(first, tangent.cross(first).normalize().scale(radius));
    }

    private static void vertex(VertexConsumer consumer, org.joml.Matrix4fc matrix, ClientLevel level,
                               Vec3 center, TubeFrame frame, double angle,
                               float red, float green, float blue) {
        Vec3 position = center.add(frame.first().scale(Math.cos(angle)))
                .add(frame.second().scale(Math.sin(angle)));
        BlockPos lightPos = BlockPos.containing(position);
        int blockLight = level.getBrightness(LightLayer.BLOCK, lightPos);
        int skyLight = level.getBrightness(LightLayer.SKY, lightPos);
        int light = (blockLight << 4) | (skyLight << 20);
        consumer.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .setColor(red, green, blue, 1.0f).setLight(light);
    }

    private record TubeFrame(Vec3 first, Vec3 second) {}
}
