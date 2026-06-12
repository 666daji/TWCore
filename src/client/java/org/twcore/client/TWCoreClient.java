package org.twcore.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.twcore.api.TwCoreClientRegistrar;
import org.twcore.config.ConfigManager;

public class TWCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 调用所有子模组注册方法
        FabricLoader.getInstance()
                .getEntrypointContainers("tw-core:register_client", TwCoreClientRegistrar.class)
                .forEach(container -> container.getEntrypoint().registerClient());

        // 完成初始化逻辑
        ConfigManager.loadClient();
    }
}
