package me.truswo.miningLimits.mixin.client;

import me.truswo.miningLimits.MiningLimits;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(
            method = "isBlockBreakingRestricted",
            at = @At("HEAD"), cancellable = true
    )
    private void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> CIR) {
        var config = MiningLimits.CONFIG;

        var plrEntity = ((PlayerEntity)(Object)this);

        var posY = pos.getY();

        var limitedChunk = world.getWorldChunk(BlockPos.fromLong(BlockPos.asLong(config.limitedChunkX, 0, config.limitedChunkZ)));
        var posChunk = world.getWorldChunk(pos);

        if (config.shouldRun && gameMode.isSurvivalLike() && (limitedChunk.getPos().x != 0 || posChunk.getPos().x != 0)) {
//            MiningLimits.LOGGER.info("1 " + limitedChunk.getPos());
//            MiningLimits.LOGGER.info("2 " + posChunk.getPos());
            if (
                ((config.hasHighHeight && posY > config.highHeight || config.hasLowHeight && posY < config.lowHeight)
                || (config.isChunkLimited && !posChunk.equals(limitedChunk)))
            ) {
                if (!config.shiftBypass || (config.shiftBypass && !plrEntity.isSneaking())
                ) {
                    CIR.setReturnValue(true);
                    if (config.hasHighHeight && posY > config.highHeight) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.highHeight"), true);
                    } else if (config.hasLowHeight && posY < config.lowHeight) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.lowHeight"), true);
                    } else if (config.isChunkLimited && !posChunk.equals(limitedChunk)) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.limitedChunk"), true);
                    } else {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.other"), true);
                    }
                }
            }
        }
    }
}