package org.twcore.config;

/**
 * <h1>配置默认值影响器</h1>
 * <p>
 * 由某个模组提供，用于修改另一个模组配置的最终默认值。
 * 携带一个类型安全的 {@code payload}，其类型 {@code T} 必须是
 * 双方模组都能访问到的类（如原版 Minecraft 类型或 TW Core API 中的类）。
 * </p>
 *
 * @param <T> 影响器携带的数据类型，目标模组可以安全地将其转换为该类型使用
 */
public interface ConfigInfluencer<T> {
    /**
     * @return 影响器来源模组的 ID
     */
    String sourceModId();

    /**
     * @return 来源模组的版本等级（用于累加计算配置文件版本号）
     */
    int sourceModVersion();

    /**
     * @return 类型安全的载荷数据，由来源模组提供，目标模组自行解析
     */
    T payload();
}