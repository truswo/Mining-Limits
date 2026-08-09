package me.truswo.miningLimits.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.truswo.miningLimits.MiningLimits;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    boolean msg1 = false;
    @Unique
    boolean msg3 = false;
    @Unique
    boolean msg4 = false;

    @Inject(
            method = "isBlockBreakingRestricted",
            at = @At("HEAD"), cancellable = true
    )
    private void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> CIR) {
        var config = MiningLimits.CONFIG;

        var plrEntity = ((PlayerEntity)(Object)this);

        var posY = pos.getY();
        var block = world.getBlockState(pos).getBlock();

        var limitedChunk = world.getWorldChunk(BlockPos.fromLong(BlockPos.asLong(config.limitedChunkX, 0, config.limitedChunkZ)));
        var posChunk = world.getWorldChunk(pos);

        if (config.shouldRun && gameMode.isSurvivalLike() && (limitedChunk.getPos().x != 0 || posChunk.getPos().x != 0)) {
            if (
                    ((config.hasHighHeight && posY > config.highHeight || config.hasLowHeight && posY < config.lowHeight)
                            || (config.isChunkLimited && !posChunk.equals(limitedChunk) || (config.isBlockLimited && !config.allowedBlocks.contains(block.getTranslationKey()))))
                ) {
                if (!config.shiftBypass || (config.shiftBypass && !plrEntity.isSneaking())
                ) {
                    if (config.debugMode && !msg1) {
                        msg1 = true;
                        MiningLimits.LOGGER.info("[MiningLimits] Mining limits bypassed");
                    }

                    CIR.setReturnValue(true);
                    if (config.hasHighHeight && posY > config.highHeight) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.highHeight"), true);
                    } else if (config.hasLowHeight && posY < config.lowHeight) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.lowHeight"), true);
                    } else if (config.isChunkLimited && !posChunk.equals(limitedChunk)) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.limitedChunk"), true);
                    } else if (config.isBlockLimited && !config.allowedBlocks.contains(block.getTranslationKey())) {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.notAllowedBlock"), true);
                    } else {
                        plrEntity.sendMessage(Text.translatable("mining-limits.message.other"), true);
                    }
                }
            }
        } else if (!gameMode.isSurvivalLike() && config.debugMode) {
            MiningLimits.LOGGER.info("[MiningLimits] Breaking was not bypassed (player is in Creative/Spectator Mode, automatic bypass)");
        }
    }

    @ModifyReturnValue(
            method = "getBlockBreakingSpeed",
            at = @At("RETURN")
    )
    private float getBlockBreakingSpeed(float original, BlockState block) {
        var config = MiningLimits.CONFIG;

        var plrEntity = ((PlayerEntity)(Object)this);
        float f = plrEntity.getInventory().getSelectedStack().getMiningSpeedMultiplier(block);

        if (config.carefulBreaking && config.shouldRun) {
            if (
                    plrEntity.isSneaking()
                    && plrEntity.isOnGround()
                    && !plrEntity.isSubmergedInWater()
            ) {
                if (
                        original >= f
                        || (original * config.carefulBreakingSpeed >= f)
                ) {
                    if (config.debugMode && !msg3) {
                        msg4 = false;
                        msg3 = true;
                        MiningLimits.LOGGER.info("[MiningLimits] Reduced breaking speed");
                    }
                    return f * config.carefulBreakingSpeed;
                }
            }
        }
        if (config.debugMode && !msg4) {
            msg3 = false;
            msg4 = true;
            MiningLimits.LOGGER.info("[MiningLimits] Normal breaking speed");
        }
        return original;
    }
}
