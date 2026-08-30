package net.locallupo.goldenfrontier.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.locallupo.goldenfrontier.blocks.ModBlocks;
import net.locallupo.goldenfrontier.wire.WireConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class WireClientRenderer {
    // Reuse Minecraft's built-in leash pipeline. Iris maps this exact pipeline
    // object to its LEASH shader key; custom pipelines are not shader-pack safe.
    private static final RenderType WIRE_RENDER_TYPE = RenderTypes.leash();
    private static final WireRouteService ROUTES = new WireRouteService();

    private WireClientRenderer() {}

    public static void initialize() {
        LevelRenderEvents.COLLECT_SUBMITS.register(WireClientRenderer::render);
    }

    private static void render(LevelRenderContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || context.levelState().cameraRenderState == null) return;
        ROUTES.beginFrame(level);

        PoseStack poseStack = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        Set<WireConnection> rendered = new HashSet<>();
        for (WireConnection connection : WireClientState.connections()) {
            if (!WireClientState.isHidden(connection)
                    && isEndpoint(level, connection.first()) && isEndpoint(level, connection.second())) {
                renderConnection(context, poseStack, level, connection);
                rendered.add(connection);
            }
        }
        WireClientState.ignition().ifPresent(ignition -> {
            ignition.connections().forEach(connection -> {
                if (WireClientState.connections().contains(connection)
                        && rendered.add(connection)) {
                    renderConnection(context, poseStack, level, connection);
                }
            });
            if (WireIgnitionAnimator.expired(System.nanoTime())) WireClientState.clearIgnition();
        });

        WireClientState.selection().ifPresent(pos -> {
            if (isEndpoint(level, pos)) {
                Vec3 start = Vec3.atCenterOf(pos);
                WireTubeRenderer.submit(context, poseStack, level, WIRE_RENDER_TYPE,
                        List.of(start, start.add(0.0, 0.75, 0.0)));
            }
        });
        poseStack.popPose();
    }

    private static void renderConnection(LevelRenderContext context, PoseStack poseStack,
                                         ClientLevel level, WireConnection connection) {
        List<Vec3> points = ROUTES.route(level, connection);
        if (points.size() < 2) return;

        Optional<WireClientState.Ignition> ignitionState = WireClientState.ignition();
        boolean isIgnitionConnection = ignitionState.isPresent()
                && ignitionState.get().connections().contains(connection);
        Optional<WireIgnitionAnimator.Progress> ignition = WireIgnitionAnimator.progress(connection, points);
        if (isIgnitionConnection && ignition.isEmpty()) return;
        List<Vec3> visible = ignition.map(progress -> WireIgnitionAnimator.unburned(points, progress)).orElse(points);
        if (visible.size() >= 2) WireTubeRenderer.submit(context, poseStack, level, WIRE_RENDER_TYPE, visible);

        ignition.ifPresent(progress -> {
            List<Vec3> pulse = WireIgnitionAnimator.pulse(points, progress);
            if (pulse.size() < 2) return;
            boolean bright = ((long) (System.nanoTime() / 65_000_000L) & 1L) == 0L;
            WireTubeRenderer.submit(context, poseStack, level, WIRE_RENDER_TYPE, pulse,
                    1.0f, bright ? 0.34f : 0.12f, bright ? 0.03f : 0.0f, 0.062);
        });
    }

    private static boolean isEndpoint(ClientLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.DETONATOR)
                || level.getBlockState(pos).is(ModBlocks.DYNAMITE);
    }
}
