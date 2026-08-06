package me.truswo.miningLimits.config;

import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static me.truswo.miningLimits.MiningLimits.MOD_ID;
import me.truswo.miningLimits.list.blockBreakingList;

public class ModConfig extends Config {
    public ModConfig() {
        super(Identifier.of(MOD_ID, "config"));
    }

    public boolean shouldRun = true;
    public boolean hasLowHeight = false;
    public boolean hasHighHeight = false;
    public boolean isChunkLimited = false;
    public boolean isBlockLimited = false;

    @ConfigGroup.Pop
    public double lowHeight;
    public double highHeight;
    public int limitedChunkX;
    public int limitedChunkZ;

    @ConfigGroup.Pop
    public boolean shiftBypass = true;
    public boolean carefulBreaking = true;
    @ValidatedFloat.Restrict(min = 0f, max = 1f)
    public float carefulBreakingSpeed = 1f;
    public List<String> allowedBlocks = new ArrayList<>(List.of(blockBreakingList.allowedBlocks));
    public boolean showOutline = true;
    public ValidatedColor outlineColor = new ValidatedColor(255, 0, 0, 64);
    public boolean debugMode = false;
}
