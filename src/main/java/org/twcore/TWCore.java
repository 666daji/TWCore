package org.twcore;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwModManager;
import org.twcore.api.forgeevent.TwCoreRegisterEvent;
import org.twcore.blockpile.CubeBlockPileManager;
import org.twcore.config.ConfigManager;
import org.twcore.content.ContentCategories;
import org.twcore.registry.ContainerTypes;
import org.twcore.registry.RegistryInit;

@Mod(TWCore.MOD_ID)
public class TWCore {
    public static final String MOD_ID = "tw_core";
    public static final Logger LOGGER = LoggerFactory.getLogger("TW`s Core");

    public TWCore(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onCommonSetup);
        RegistryInit.init(modEventBus);
        register();
        cubeBlockPileInit();
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        // 调用所有子模组注册方法
        NeoForge.EVENT_BUS.post(new TwCoreRegisterEvent());

        // 初始化逻辑
        ConfigManager.loadCommon();
        ContentCategories.init();
        ContainerTypes.initMappings();

        LOGGER.info("TW`s Core is initializing!");
    }

    private void register() {
        TwModManager.IMPL.register(MOD_ID, 1);
    }

    private static void cubeBlockPileInit(){
        // 世界加载时恢复多方块数据
        NeoForge.EVENT_BUS.addListener(CubeBlockPileManager::onWorldStart);
        // 服务器停止时清理
        NeoForge.EVENT_BUS.addListener(CubeBlockPileManager::onServerStopping);
    }
}
