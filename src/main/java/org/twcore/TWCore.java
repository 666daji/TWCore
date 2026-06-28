package org.twcore;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twcore.api.TwModManager;
import org.twcore.api.event.TwCoreRegisterEvent;
import org.twcore.blockpile.CubeBlockPileManager;
import org.twcore.process.playeraction.PlayerActionFactory;
import org.twcore.process.playeraction.impl.AddContentPlayerAction;
import org.twcore.process.playeraction.impl.AddItemPlayerAction;
import org.twcore.registry.ContainerTypes;
import org.twcore.registry.RegistryInit;

@Mod(TWCore.MOD_ID)
public class TWCore {
    public static final String MOD_ID = "tw_core";
    public static final Logger LOGGER = LoggerFactory.getLogger("TW‘s Core");

    public TWCore(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onCommonSetup);
        RegistryInit.init(modEventBus);
        register();
        cubeBlockPileInit();

        // 杂项
        cubeBlockPileInit();
        registerDefaultAction();

        LOGGER.info("TW`s Core is initializing!");
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        // 调用所有子模组注册方法
        ModLoader.postEvent(new TwCoreRegisterEvent());

        ContainerTypes.initDefaultMappings();
    }

    private void register() {
        TwModManager.IMPL.register(MOD_ID, 1);
    }

    // ==================== 其他注册逻辑 ====================

    /**
     * 方块堆事件注册。
     *
     * @see org.twcore.api.blockpile.CubeBlockPile
     */
    private static void cubeBlockPileInit(){
        NeoForge.EVENT_BUS.addListener(CubeBlockPileManager::onWorldStart);
        NeoForge.EVENT_BUS.addListener(CubeBlockPileManager::onServerStopping);
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
}
