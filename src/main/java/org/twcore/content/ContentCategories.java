package org.twcore.content;

import org.jetbrains.annotations.NotNull;
import org.twcore.registry.TWRegistries;

import java.util.*;

public class ContentCategories {
    private static final Map<String, Set<Content>> CATEGORY_MAP = new HashMap<>();

    private ContentCategories() {}

    /**
     * 初始化分类映射，遍历注册表中的所有内容物。
     * @apiNote 此方法应当仅调用一次。
     */
    public static void init() {
        TWRegistries.CONTENT.get().forEach(content ->
                CATEGORY_MAP.computeIfAbsent(content.getCategory(),
                k -> new LinkedHashSet<>()).add(content));
    }

    /**
     * 获取指定分类下的所有内容物。
     *
     * @param category 分类名称
     * @return 不可修改的内容物集合，如果分类不存在则返回空集合
     */
    @NotNull
    public static Set<Content> getByCategory(@NotNull String category) {
        Set<Content> contents = CATEGORY_MAP.get(category);
        if (contents == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(contents);
    }

    /**
     * 获取所有已知的分类。
     *
     * @return 不可修改的分类集合
     */
    @NotNull
    public static Set<String> getCategories() {
        return Collections.unmodifiableSet(CATEGORY_MAP.keySet());
    }
}
