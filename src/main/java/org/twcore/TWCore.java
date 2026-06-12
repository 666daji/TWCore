package org.twcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwCoreRegistrar;
import org.twcore.config.ConfigManager;

public class TWCore implements ModInitializer {
    public static String MOD_ID = "tw_core";
    public static Logger LOGGER = LoggerFactory.getLogger("TW`s Core");

    @Override
    public void onInitialize() {
        // 调用所有注册方法
        FabricLoader.getInstance()
                .getEntrypointContainers("tw-core:register", TwCoreRegistrar.class)
                .forEach(container -> container.getEntrypoint().register());

        // 完成初始化逻辑
        ConfigManager.loadAll();

        LOGGER.info("TW`s Core is initializing!");
    }
}
