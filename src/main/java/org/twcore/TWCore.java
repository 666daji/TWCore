package org.twcore;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TWCore implements ModInitializer {
    public static String MOD_ID = "tw_core";
    public static Logger LOGGER = LoggerFactory.getLogger("TW`s Core");

    @Override
    public void onInitialize() {
        LOGGER.info("TW`s Core is initializing!");
    }
}
