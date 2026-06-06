package me.truswo.miningLimits.mixin.client;

import me.truswo.miningLimits.MiningLimits;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Unique
    boolean msg1 = false;
    @Unique
    boolean msg2 = false;

    @Shadow
    private @Nullable ClientWorld world;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(
            method = "drawBlockOutline",
            at = @At("HEAD")
    )
    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        var config = MiningLimits.CONFIG;

        assert world != null;
        assert client.player != null;

        if (config.showOutline && !client.player.isInCreativeMode()) {
            var limitedChunk = world.getWorldChunk(BlockPos.fromLong(BlockPos.asLong(config.limitedChunkX, 0, config.limitedChunkZ)));
            var posChunk = world.getWorldChunk(pos);

            if ((config.shouldRun && !config.shiftBypass) || (config.shouldRun && !client.player.isSneaking())) {
                if (
                        (config.hasHighHeight && pos.getY() > config.highHeight || config.hasLowHeight && pos.getY() < config.lowHeight)
                                || (config.isChunkLimited && !posChunk.equals(limitedChunk))
                ) {
                    if (config.debugMode && !msg1) {
                        msg2 = false;
                        msg1 = true;
                        MiningLimits.LOGGER.info("[MiningLimits] Rendering highlight");
                    }

                    drawCuboidShapeOutline(
                        matrices,
                        vertexConsumer,
                        state.getOutlineShape(this.world, pos, ShapeContext.of(entity)),
                        pos.getX() - cameraX,
                        pos.getY() - cameraY,
                        pos.getZ() - cameraZ,
                        (float) config.outlineColor.r() /255,
                        (float) config.outlineColor.g() /255,
                        (float) config.outlineColor.b() /255,
                        (float) config.outlineColor.a() /255
                    );
                }
            }
        } else if (client.player.isInCreativeMode() && config.debugMode && !msg2) {
            msg1 = false;
            msg2 = true;
            MiningLimits.LOGGER.info("[MiningLimits] Outline not rendered (player is in Creative/Spectator Mode)");
        }
    }
}
