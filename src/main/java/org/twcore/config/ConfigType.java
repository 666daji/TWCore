package org.twcore.config;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * 配置类型元信息。
 * @param name            配置名称（用作文件名，不含 .json）
 * @param codec           当前版本的 Codec
 * @param defaultFactory  默认值工厂，接收所有已收集的 {@link ConfigInfluencer}，返回最终默认值
 * @param migrator        迁移器，可为 null
 * @param <T>             配置数据类型
 */
public record ConfigType<T>(
        String name,
        Codec<T> codec,
        Function<List<ConfigInfluencer>, T> defaultFactory,
        @Nullable ConfigMigrator<T> migrator
) {
    public static <T> ConfigType<T> of(String name, Codec<T> codec,
                                       Function<List<ConfigInfluencer>, T> defaultFactory,
                                       @Nullable ConfigMigrator<T> migrator) {
        return new ConfigType<>(name, codec, defaultFactory, migrator);
    }
}