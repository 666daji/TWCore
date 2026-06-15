package org.twcore.api.forgeevent;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * <h1>TW Core 通用注册事件</h1>
 * <p>
 * 该事件由 TW Core 在 {@link FMLCommonSetupEvent} 阶段通过
 * {@code MinecraftForge.EVENT_BUS.post(new TwCoreRegisterEvent())} 触发。
 * </p>
 * <p>
 * 所有 TW 子模组应通过监听本事件来执行通用注册逻辑
 * （即调用 {@link org.twcore.api.TwCoreRegistrar#register()}）。
 * 这保证了所有模组的通用注册都在配置加载前完成，
 * 且与 Fabric 端的 {@code "twcore:register"} 入口点行为一致。
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
 *         registrar.register();
 *     }
 * }
 * }</pre>
 *
 * @see org.twcore.api.TwCoreRegistrar
 */
public class TwCoreRegisterEvent extends Event {

}