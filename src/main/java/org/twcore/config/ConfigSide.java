package org.twcore.config;

/**
 * <h1>配置所属的物理端</h1>
 * <p>
 * 用于标记一个配置是双端通用（{@link #COMMON}）还是仅客户端（{@link #CLIENT}）。
 * </p>
 *
 * <h2>加载行为</h2>
 * <ul>
 *     <li>{@code COMMON} —— 由 {@link ConfigManager#loadCommon()} 加载，在双端注册完成后执行。</li>
 *     <li>{@code CLIENT} —— 由 {@link ConfigManager#loadClient()} 加载，仅在物理客户端、
 *         客户端专属注册完成后执行。在服务端该配置会被完全忽略。</li>
 * </ul>
 *
 * <p>在 {@link ConfigType} 中通过 {@code side()} 字段指定，默认为 {@code COMMON}。</p>
 *
 * @see ConfigType
 * @see ConfigManager
 */
public enum ConfigSide {
    /** 双端通用配置，服务端和客户端都会加载 */
    COMMON,
    /** 仅客户端配置，只在物理客户端加载 */
    CLIENT
}