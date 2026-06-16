package org.twcore.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.twcore.api.event.TwCoreClientRegisterEvent;
import org.twcore.api.event.TwCoreRegisterEvent;
import org.twcore.config.ConfigManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private static void coreRegistry(RunArgs args, CallbackInfo ci) {
        TwCoreRegisterEvent.TW_CORE_REGISTRAR.invoker().register();
        TwCoreClientRegisterEvent.TW_CORE_CLIENT_REGISTRAR.invoker().registerClient();

        // 初始化逻辑
        ConfigManager.loadCommon();
        ConfigManager.loadClient();
    }
}
