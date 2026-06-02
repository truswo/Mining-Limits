package me.truswo.miningLimits.mixin.client;

import me.truswo.miningLimits.MiningLimits;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private @Nullable ClientWorld world;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
            method = "drawBlockOutline",
            at = @At("HEAD"), cancellable = true
    )
    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, double x, double y, double z, OutlineRenderState state, int color, float lineWidth, CallbackInfo CIR) {
        var config = MiningLimits.CONFIG;

        assert world != null;
        assert client.player != null;

        if (config.showOutline && !client.player.isInCreativeMode()) {
            BlockPos blockPos = state.pos();

            var limitedChunk = world.getWorldChunk(BlockPos.fromLong(BlockPos.asLong(config.limitedChunkX, 0, config.limitedChunkZ)));
            var posChunk = world.getWorldChunk(blockPos);

            if ((config.shouldRun && !config.shiftBypass) || (config.shouldRun && !client.player.isSneaking())) {
                if (
                        (config.hasHighHeight && blockPos.getY() > config.highHeight || config.hasLowHeight && blockPos.getY() < config.lowHeight)
                                || (config.isChunkLimited && !posChunk.equals(limitedChunk))
                ) {
                    //MiningLimits.LOGGER.info("red outline");
                    VertexRendering.drawOutline(matrices, vertexConsumer, state.shape(), blockPos.getX() - x, blockPos.getY() - y, blockPos.getZ() - z, config.outlineColor.toInt(), 1.5f);
                    CIR.cancel();
                }
            }
        }
    }
}
