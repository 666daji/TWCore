package org.twcore.api;

import org.twcore.config.ConfigInfluencer;
import org.twcore.config.ConfigType;
import org.twcore.config.ConfigManager;

/**
 * 子模组用于注册配置和默认值影响器的 API 入口。
 * 通过 {@link #forMod(String)} 获取与指定模组关联的配置构造器。
 */
public final class TwConfig {
    private final String modId;

    private TwConfig(String modId) {
        this.modId = modId;
    }

    /**
     * 获取指定模组的配置构造器。
     * @param modId 模组 ID，必须已通过 {@link TwModManager} 注册
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
     * 注册一个属于当前模组的配置类型。
     *
     * @param type 配置类型
     */
    public <T> void registerConfig(ConfigType<T> type) {
        ConfigManager.registerConfig(modId, type);
    }

    /**
     * 向指定目标模组的配置添加一个默认值影响器。
     * @param targetModId    目标模组 ID
     * @param configName     目标配置名称
     * @param influencer     影响器
     */
    public void addDefaultOverride(String targetModId, String configName, ConfigInfluencer influencer) {
        ConfigManager.addInfluencer(targetModId, configName, influencer);
    }
}