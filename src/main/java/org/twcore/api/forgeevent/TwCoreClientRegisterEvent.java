package org.twcore.api.forgeevent;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * <h1>TW Core 客户端专属注册事件</h1>
 * <p>
 * 该事件由 TW Core 在 {@link FMLClientSetupEvent} 阶段（仅物理客户端）
 * 通过 {@code MinecraftForge.EVENT_BUS.post(new TwCoreClientRegisterEvent())} 触发。
 * </p>
 * <p>
 * 所有 TW 子模组应通过监听本事件来执行客户端专属注册逻辑
 * （即调用 {@link org.twcore.api.TwCoreClientRegistrar#registerClient()}）。
 * 这保证了所有客户端注册都在客户端初始化阶段完成，
 * 且与 Fabric 端的 {@code "twcore:register_client"} 入口点行为一致。
 * </p>
 *
 * <h2>典型用法</h2>
 * <pre>{@code
 * @Mod("example_mod")
 * public class ExampleMod {
 *     private final TwCoreClientRegistrar clientRegistrar = new MyClientRegistrar();
 *
 *     public ExampleMod() {
 *         // 客户端代码必须在仅客户端类中注册监听器
 *     }
 *
 *     // 在专门的客户端事件处理类中
 *     @SubscribeEvent
 *     public void onTwCoreClientRegister(TwCoreClientRegisterEvent event) {
 *         clientRegistrar.registerClient();
 *     }
 * }
 * }</pre>
 *
 * @see org.twcore.api.TwCoreClientRegistrar
 */
public class TwCoreClientRegisterEvent extends Event {

}