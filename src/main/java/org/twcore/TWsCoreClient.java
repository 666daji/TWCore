package org.twcore;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.twcore.api.event.TwCoreClientRegisterEvent;
import org.twcore.config.ConfigManager;

@Mod(value = TWCore.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TWCore.MOD_ID, value = Dist.CLIENT)
public class TWsCoreClient {
    public TWsCoreClient(ModContainer container) {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 调用所有子模组注册方法
        ModLoader.postEvent(new TwCoreClientRegisterEvent());

        // 完成初始化逻辑
        ConfigManager.loadClient();
    }
}
