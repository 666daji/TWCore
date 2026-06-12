package org.twcore.config;

/**
 * 配置默认值影响器，由某个模组提供，用于修改另一个模组配置的最终默认值。
 */
public interface ConfigInfluencer {
    /**
     * @return 影响器来源模组的 ID
     */
    String sourceModId();

    /**
     * @return 来源模组的版本等级（用于累加计算配置文件版本号）
     */
    int sourceModVersion();

    /**
     * @return 不透明的数据载体，由目标配置的所有者模组自行解析
     */
    Object payload();
}