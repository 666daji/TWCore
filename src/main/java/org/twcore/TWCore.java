package org.twcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwCoreRegistrar;
import org.twcore.config.ConfigManager;
import org.twcore.content.ContentCategories;
import org.twcore.registry.RegistryInit;

public class TWCore implements ModInitializer {
    public static String MOD_ID = "tw_core";
    public static Logger LOGGER = LoggerFactory.getLogger("TW`s Core");

    @Override
    public void onInitialize() {
        // 注册初始化
        RegistryInit.init();

        // 调用所有子模组注册方法
        FabricLoader.getInstance()
                .getEntrypointContainers("tw-core:register", TwCoreRegistrar.class)
                .forEach(container -> container.getEntrypoint().register());

        // 完成初始化逻辑
        ConfigManager.loadCommon();
        ContentCategories.init();

        LOGGER.info("TW`s Core is initializing!");
    }
}
