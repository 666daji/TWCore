package org.twcore.api.config;

import org.twcore.api.TwCoreClientRegistrar;
import org.twcore.api.TwModManager;
import org.twcore.config.ConfigInfluencer;
import org.twcore.config.ConfigManager;
import org.twcore.config.ConfigSide;
import org.twcore.config.ConfigType;

/**
 * <h1>TW Core 配置注册入口</h1>
 * <p>
 * 子模组通过本类完成与配置相关的所有注册操作。
 * 使用 {@link #forMod(String)} 获取与指定模组关联的构造器实例，
 * 然后调用以下方法：
 * <ul>
 *     <li>{@link #registerConfig(ConfigType)} —— 注册双端通用配置
 *         （{@link ConfigSide#COMMON}）。</li>
 *     <li>{@link #registerClientConfig(ConfigType)} —— 注册仅客户端配置
 *         （{@link ConfigSide#CLIENT}）。</li>
 *     <li>{@link #addDefaultOverride(String, String, Object)}
 *         —— 向任意已注册或尚未注册的目标配置提供默认值影响器。</li>
 * </ul>
 * </p>
 *
 * <h2>使用前提</h2>
 * <p>
 * 调用 {@link #forMod(String)} 前，指定的模组 ID 必须已通过
 * {@link TwModManager#register(String, int)} 完成注册，否则会抛出
 * {@link IllegalStateException}。这确保配置系统与模组管理器保持数据一致。
 * </p>
 *
 * <h2>跨模组默认值叠加</h2>
 * <p>
 * 通过 {@link #addDefaultOverride(String, String, Object)}
 * 方法，模组可以为其他模组的配置添加影响器。注册阶段仅收集数据，
 * 不检查目标是否存在。所有检查与合并将在配置最终加载时由
 * {@link ConfigManager} 统一执行。无法匹配到已注册配置的影响器
 * 会被静默丢弃，不会产生任何日志或异常。
 * </p>
 *
 * <h2>典型用法</h2>
 * <pre>{@code
 * // 注册自身
 * TwModManager.IMPL.register("my_mod", 2);
 * TwConfig config = TwConfig.forMod("my_mod");
 *
 * // 注册一个双端通用配置
 * config.registerConfig(ConfigType.of(
 *     "my_settings",
 *     MySettings.CODEC,
 *     influencers -> MySettings.createDefault(),
 *     null
 * ));
 *
 * // 注册一个客户端专属配置
 * config.registerClientConfig(ConfigType.of(
 *     "my_ui",
 *     MyUiConfig.CODEC,
 *     influencers -> MyUiConfig.createDefault(),
 *     null,
 *     ConfigSide.CLIENT
 * ));
 *
 * // 为其他模组的配置添加默认值
 * config.addDefaultOverride("other_mod", "ore_list",
 *     ConfigInfluencer.create("my_mod", 2, new ExtraOre("ruby")));
 * }</pre>
 *
 * @see ConfigManager
 * @see ConfigType
 * @see ConfigInfluencer
 */
public final class TwConfig {
    private final String modId;

    private TwConfig(String modId) {
        this.modId = modId;
    }

    /**
     * 获取指定模组的配置构造器。
     *
     * @param modId 模组 ID，必须已通过 {@link TwModManager} 注册
     * @return 该模组的配置构造器
     * @throws IllegalStateException 如果指定模组尚未在 {@link TwModManager} 中注册
     */
    public static TwConfig forMod(String modId) {
        if (!TwModManager.IMPL.isRegistered(modId)) {
            throw new IllegalStateException(
                    "Mod '" + modId + "' is not registered in TW Mod Manager. " +
                            "Please call TwModManager.IMPL.register() first."
            );
        }
        return new TwConfig(modId);
    }

    /**
     * 注册一个双端通用配置。
     * 配置将在通用注册完成后由 {@link ConfigManager#loadCommon()} 加载。
     *
     * @param type 配置元信息，其 {@code side} 必须为 {@link ConfigSide#COMMON}
     * @param <T>  配置数据类型
     * @throws IllegalArgumentException 如果配置类型不是 {@code COMMON}
     */
    public <T> void registerConfig(ConfigType<T> type) {
        if (type.side() != ConfigSide.COMMON) {
            throw new IllegalArgumentException(
                    "Config '" + type.name() + "' is marked as " + type.side() +
                            ". Use registerClientConfig() for CLIENT configs."
            );
        }
        ConfigManager.registerConfig(modId, type);
    }

    /**
     * 注册一个客户端专属配置。
     * 仅在物理客户端生效，将由 {@link ConfigManager#loadClient()} 加载。
     * <p>此方法应在 {@link TwCoreClientRegistrar#registerClient()} 中调用。</p>
     *
     * @param type 配置元信息，其 {@code side} 必须为 {@link ConfigSide#CLIENT}
     * @param <T>  配置数据类型
     * @throws IllegalArgumentException 如果配置类型不是 {@code CLIENT}
     */
    public <T> void registerClientConfig(ConfigType<T> type) {
        if (type.side() != ConfigSide.CLIENT) {
            throw new IllegalArgumentException(
                    "Config '" + type.name() + "' is marked as " + type.side() +
                            ". Use registerConfig() for COMMON configs."
            );
        }
        ConfigManager.registerConfig(modId, type);
    }

    /**
     * 为目标配置添加一个默认值影响器。
     * <p>
     * 传入任意类型的 payload 数据，本方法会自动封装来源模组信息
     * （当前 {@code modId} 及从 {@link TwModManager} 获取的版本等级）
     * 为一个 {@link ConfigInfluencer} 并提交。
     * </p>
     * <p>
     * 注意：{@code payload} 的类型必须是两个模组都能访问到的公共类型
     * （例如原版 Minecraft 的类或 TW Core API 中定义的类型），
     * 目标模组在默认值工厂中通过 {@code instanceof} 安全提取。
     * </p>
     *
     * @param targetModId 目标配置所属模组的 ID
     * @param configName  目标配置的名称
     * @param payload     影响器携带的数据，类型自动推断
     * @param <T>         载荷类型
     */
    public <T> void addDefaultOverride(String targetModId, String configName, T payload) {
        int version = TwModManager.IMPL.getRegisteredVersion(modId);
        ConfigInfluencer<T> influencer = new ConfigInfluencer<>() {
            @Override
            public String sourceModId() {
                return modId;
            }

            @Override
            public int sourceModVersion() {
                return version;
            }

            @Override
            public T payload() {
                return payload;
            }
        };
        ConfigManager.addInfluencer(targetModId, configName, influencer);
    }
}