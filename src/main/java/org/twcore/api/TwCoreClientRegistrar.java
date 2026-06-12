package org.twcore.api;

/**
 * <h1>TW Core 客户端专属注册入口</h1>
 * <p>
 * 所有基于 TW Core 的子模组若需要注册仅在客户端生效的内容
 * （如客户端配置、按键绑定、渲染层等），<b>必须</b>实现此接口，
 * 并在对应平台的客户端注册点将其提交给 Core。
 * </p>
 *
 * <h2>注册内容</h2>
 * <p>
 * 在 {@link #registerClient()} 方法内，子模组通常需要完成：
 * <ul>
 *     <li>通过 {@code TwConfig.forMod(modId).registerClientConfig()}
 *         注册标记为 {@link org.twcore.config.ConfigSide#CLIENT} 的配置；</li>
 *     <li>通过 {@code TwConfig.forMod(modId).addDefaultOverride()}
 *         为其他模组的客户端配置提供默认值影响器；</li>
 *     <li>其他纯客户端操作（如注册按键绑定、GUI 工厂等）。</li>
 * </ul>
 * </p>
 *
 * <h2>调用时序</h2>
 * <p>
 * Core 保证在所有双端通用注册（{@link TwCoreRegistrar#register()}）
 * <b>全部执行完毕之后</b>，才会在物理客户端调用本接口的方法。
 * 因此，在 {@code registerClient()} 内可以安全地通过
 * {@link TwModManager} 查询其他模组的注册状态和版本等级。
 * </p>
 *
 * <h2>跨模组信息访问的约定</h2>
 * <p>
 * 尽管技术上在客户端注册阶段访问 {@link TwModManager} 是安全的，
 * 但为了保持框架的<b>一致性</b>和代码的可维护性，
 * <b>强烈建议不要在任何注册逻辑中依赖其他模组的注册顺序或状态</b>。
 * 跨模组协作（如为其他模组的配置添加默认值）应始终通过
 * {@code TwConfig.addDefaultOverride()} 提交
 * {@link org.twcore.config.ConfigInfluencer 影响器}，
 * 由目标模组在配置加载时统一处理。这确保了代码在所有注册阶段都健壮且
 * 顺序无关。
 * </p>
 *
 * <h2>平台集成方式</h2>
 * <h3>Fabric</h3>
 * <p>
 * 在 {@code fabric.mod.json} 中声明自定义入口点 {@code "tw-core:register_client"}，
 * 将实现类列在其下。Core 的 {@code ClientModInitializer} 会扫描并调用。
 * </p>
 * <pre>{@code
 * {
 *   "entrypoints": {
 *     "main": ["com.example.MyMod"],
 *     "client": ["com.example.MyModClient"],
 *     "tw-core:register": ["com.example.MyCommonRegistrar"],
 *     "tw-core:register_client": ["com.example.MyClientRegistrar"]
 *   }
 * }
 * }</pre>
 *
 * <h3>Forge / NeoForge</h3>
 * <p>
 * 子模组在自己的客户端事件处理类中监听 {@code TwCoreClientRegisterEvent}，
 * 在该事件处理方法内调用本接口的实现。Core 在 {@code FMLClientSetupEvent}
 * 阶段触发该事件。
 * </p>
 * <pre>{@code
 * @Mod("example_mod")
 * public class ExampleMod {
 *     private final TwCoreClientRegistrar clientRegistrar = new MyClientRegistrar();
 *
 *     @SubscribeEvent
 *     public void onTwCoreClientRegister(TwCoreClientRegisterEvent event) {
 *         clientRegistrar.registerClient();
 *     }
 * }
 * }</pre>
 *
 * <h2>线程安全</h2>
 * <p>
 * 此方法在客户端初始化阶段的主线程上被调用，且每个实现只调用一次。
 * 无需处理并发问题。
 * </p>
 *
 * @see TwCoreRegistrar
 * @see TwModManager
 */
@FunctionalInterface
public interface TwCoreClientRegistrar {

    /**
     * 执行所有纯客户端的注册操作。
     * <p>由 Core 在客户端注册阶段调用一次。</p>
     */
    void registerClient();
}