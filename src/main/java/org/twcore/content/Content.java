package org.twcore.content;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.twcore.registry.TWRegistries;

import java.util.Objects;

/**
 * 内容物类型。
 * <p>
 * 表示一种可以被容器承载的内容物类型，如水、汤、牛奶等。
 * 这是一个纯类型定义，不包含具体状态。
 * </p>
 */
public class Content {
    private final String category;
    private String translationKey;

    /**
     * 创建一个内容物类型实例。
     *
     * @param category 内容物所属分类，不能为null
     */
    public Content(@NotNull String category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null");
    }

    /**
     * 获取内容物类型的分类。
     */
    @NotNull
    public String getCategory() {
        return category;
    }

    /**
     * 获取内容物类型的显示翻译键。
     * <p>
     * 格式："content.{namespace}.{path}"，其中标识符从注册表获取。
     * </p>
     */
    @NotNull
    public String getDisplayTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("content", TWRegistries.CONTENT.get().getKey(this));
        }
        return this.translationKey;
    }

    /**
     * 获取内容物的显示文本。
     */
    public Component getDisplayName() {
        return Component.translatable(getDisplayTranslationKey());
    }

    /**
     * 检查该内容物是否属于某个分组。
     *
     * @param category 目标分组
     * @return 属于返回true，否则返回false
     */
    public boolean isIn(@NotNull String category) {
        return this.category.equals(category);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{category=" + category + "}";
    }
}