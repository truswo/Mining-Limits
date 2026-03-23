package me.truswo.miningLimits;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.truswo.miningLimits.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiningLimits implements ModInitializer {
    public static final String MOD_ID = "mining-limits";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ModConfig CONFIG = ConfigApiJava.registerAndLoadConfig(ModConfig::new);

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_ID + " finished loading");
    }
}
