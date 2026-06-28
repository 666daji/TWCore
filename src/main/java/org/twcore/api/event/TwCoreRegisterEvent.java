package org.twcore.api.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * <h1>TW Core 通用注册事件（模组总线）</h1>
 * <p>
 * 该事件由 TW Core 在 {@link FMLCommonSetupEvent} 阶段
 * 于模组总线上通过 {@code ModLoader.get().postEvent(new TwCoreRegisterEvent())} 触发。
 * 本事件实现了 {@link IModBusEvent}，因此只能在模组总线上监听。
 * </p>
 * <p>
 * 所有 TW 子模组应通过监听本事件来执行通用注册逻辑。
 * 在该事件中集中完成模组注册、配置注册、影响器提交等操作。
 * Core 会在所有回调执行完毕后调用 {@code ConfigManager.loadCommon()} 加载双端配置。
 * </p>
 *
 * <h2>典型用法</h2>
 * <pre>{@code
 * @Mod("example_mod")
 * public class ExampleMod {
 *     private final TwCoreRegistrar registrar = new MyRegistrar();
 *
 *     public ExampleMod() {
 *         IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
 *         modEventBus.register(this);
 *     }
 *
 *     @SubscribeEvent
 *     public void onTwCoreRegister(TwCoreRegisterEvent event) {
 *         TwModManager.IMPL.register("my_mod", 2);
 *         TwConfig config = TwConfig.forMod("my_mod");
 *         config.registerConfig(...);
 *     }
 * }
 * }</pre>
 *
 * @see org.twcore.api.TwCoreRegistrar
 */
public class TwCoreRegisterEvent extends Event implements IModBusEvent {

}
