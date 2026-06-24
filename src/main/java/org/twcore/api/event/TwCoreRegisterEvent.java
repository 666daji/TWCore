package org.twcore.api.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * <h1>TW Core 通用注册事件（Forge 端）</h1>
 * <p>
 * 该事件由 TW Core 在 {@link FMLCommonSetupEvent} 阶段通过
 * {@code MinecraftForge.EVENT_BUS.post(new TwCoreRegisterEvent())} 触发。
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
 *         MinecraftForge.EVENT_BUS.register(this);
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
public class TwCoreRegisterEvent extends Event {

}