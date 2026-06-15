package org.twcore;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwModManager;
import org.twcore.api.forgeevent.TwCoreClientRegisterEvent;
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

    public TWCore(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);
        RegistryInit.init(modEventBus);
        register();
        cubeBlockPileInit();
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        // 调用所有子模组注册方法
        MinecraftForge.EVENT_BUS.post(new TwCoreRegisterEvent());

        // 初始化逻辑
        ConfigManager.loadCommon();
        ContentCategories.init();
        ContainerTypes.initMappings();

        LOGGER.info("TW`s Core is initializing!");
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        // 调用所有子模组注册方法
        MinecraftForge.EVENT_BUS.post(new TwCoreClientRegisterEvent());

        // 完成初始化逻辑
        ConfigManager.loadClient();
    }

    private void register() {
        TwModManager.IMPL.register(MOD_ID, 1);
    }

    private static void cubeBlockPileInit(){
        // 世界加载时恢复多方块数据
        MinecraftForge.EVENT_BUS.addListener(CubeBlockPileManager::onWorldStart);
        // 服务器停止时清理
        MinecraftForge.EVENT_BUS.addListener(CubeBlockPileManager::onServerStopping);
    }
}
