package me.truswo.miningLimits.config;

import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import net.minecraft.util.Identifier;

import static me.truswo.miningLimits.MiningLimits.MOD_ID;

public class ModConfig extends Config {
    public ModConfig() {
        super(Identifier.of(MOD_ID, "config"));
    }

    public boolean shouldRun = true;
    public boolean hasLowHeight = false;
    public boolean hasHighHeight = false;
    public boolean isChunkLimited = false;

    @ConfigGroup.Pop
    public double lowHeight;
    public double highHeight;
    public int limitedChunkX;
    public int limitedChunkZ;

    @ConfigGroup.Pop
    public boolean showOutline = true;
    public ValidatedColor outlineColor = new ValidatedColor(255, 0, 0, 64);
}
