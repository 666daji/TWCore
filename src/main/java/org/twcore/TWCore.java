package org.twcore;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwModManager;
import org.twcore.api.event.TwCoreClientRegisterEvent;
import org.twcore.api.event.TwCoreRegisterEvent;
import org.twcore.blockpile.CubeBlockPileManager;
import org.twcore.config.ConfigManager;
import org.twcore.process.playeraction.PlayerActionFactory;
import org.twcore.process.playeraction.impl.AddContentPlayerAction;
import org.twcore.process.playeraction.impl.AddItemPlayerAction;
import org.twcore.registry.ContainerTypes;
import org.twcore.registry.RegistryInit;

@Mod(TWCore.MOD_ID)
public class TWCore {
    public static final String MOD_ID = "tw_core";
    public static final Logger LOGGER = LoggerFactory.getLogger("TW's Core");

    public TWCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);

        RegistryInit.init(modEventBus);
        register();
        cubeBlockPileInit();

        // 杂项
        cubeBlockPileInit();
        registerDefaultAction();

        LOGGER.info("TW`s Core is initializing!");
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        ModLoader.get().postEvent(new TwCoreRegisterEvent());

        // 初始化逻辑
        ConfigManager.loadCommon();
        ContainerTypes.initDefaultMappings();
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        ModLoader.get().postEvent(new TwCoreClientRegisterEvent());

        // 初始化逻辑
        ConfigManager.loadClient();
    }

    /**
     * TW`s Core对自己注册。
     *
     * @see TwModManager
     */
    private void register() {
        TwModManager.IMPL.register(MOD_ID, 2);
    }

    // ==================== 其他注册逻辑 ====================

    /**
     * 方块堆事件注册。
     *
     * @see org.twcore.api.blockpile.CubeBlockPile
     */
    private static void cubeBlockPileInit(){
        MinecraftForge.EVENT_BUS.addListener(CubeBlockPileManager::onWorldStart);
        MinecraftForge.EVENT_BUS.addListener(CubeBlockPileManager::onServerStopping);
    }

    /**
     * 玩家操作类型注册。
     *
     * @see org.twcore.api.process.PlayerAction
     */
    public static void registerDefaultAction() {
        PlayerActionFactory.register(
                AddItemPlayerAction.TYPE,
                AddItemPlayerAction::fromParams,
                context -> AddItemPlayerAction.fromContext(context).orElse(null)
        );
        PlayerActionFactory.register(
                AddContentPlayerAction.TYPE,
                AddContentPlayerAction::fromParams,
                context -> AddContentPlayerAction.fromContext(context).orElse(null)
        );
    }

    public static ResourceLocation createResourceLocation(String nameSpace, String path) {
        return new ResourceLocation(nameSpace, path);
    }
}
