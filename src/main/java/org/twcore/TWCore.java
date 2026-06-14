package org.twcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwCoreRegistrar;
import org.twcore.api.TwModManager;
import org.twcore.blockpile.CubeBlockPileManager;
import org.twcore.config.ConfigManager;
import org.twcore.content.ContentCategories;
import org.twcore.registry.RegistryInit;

public class TWCore implements ModInitializer {
    public static String MOD_ID = "tw_core";
    public static Logger LOGGER = LoggerFactory.getLogger("TW`s Core");

    @Override
    public void onInitialize() {
        RegistryInit.init();
        register();

        // 调用所有子模组注册方法
        FabricLoader.getInstance()
                .getEntrypointContainers("tw-core:register", TwCoreRegistrar.class)
                .forEach(container -> container.getEntrypoint().register());

        // 初始化逻辑
        ConfigManager.loadCommon();
        ContentCategories.init();
        cubeBlockPileInit();

        LOGGER.info("TW`s Core is initializing!");
    }

    private void register() {
        TwModManager.IMPL.register(MOD_ID, 1);
    }

    private static void cubeBlockPileInit(){
        // 世界加载时恢复多方块数据
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (!world.isClient()) {
                CubeBlockPileManager.loadWorldCubeBlockPiles(world);
            }
        });
        // 服务器停止时清理
        ServerLifecycleEvents.SERVER_STOPPING.register(CubeBlockPileManager::onServerStopping);
    }
}
