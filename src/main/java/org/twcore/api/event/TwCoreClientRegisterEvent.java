package org.twcore.api.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * <h1>TW Core 客户端专属注册事件（模组总线）</h1>
 * <p>
 * 该事件由 TW Core 在 {@link FMLClientSetupEvent} 阶段（仅物理客户端）
 * 于模组总线上通过 {@code ModLoader.get().postEvent(new TwCoreClientRegisterEvent())} 触发。
 * 本事件实现了 {@link IModBusEvent}，因此只能在模组总线上监听。
 * </p>
 * <p>
 * 所有 TW 子模组应通过监听本事件来执行客户端专属注册逻辑
 * （即调用 {@link org.twcore.api.TwCoreClientRegistrar#registerClient()}）。
 * Core 在所有回调执行完毕后调用 {@code ConfigManager.loadClient()} 加载客户端配置。
 * </p>
 *
 * <h2>典型用法</h2>
 * <pre>{@code
 * @Mod("example_mod")
 * public class ExampleMod {
 *
 *     public ExampleMod() {
 *         IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
 *         modEventBus.register(new ClientEventHandler());
 *     }
 * }
 *
 * // 在专门的客户端事件处理类中
 * class ClientEventHandler {
 *     @SubscribeEvent
 *     public void onTwCoreClientRegister(TwCoreClientRegisterEvent event) {
 *         TwConfig config = TwConfig.forMod("my_mod");
 *         config.registerClientConfig(...);
 *     }
 * }
 * }</pre>
 *
 * @see org.twcore.api.TwCoreClientRegistrar
 */
public class TwCoreClientRegisterEvent extends Event implements IModBusEvent {

}
