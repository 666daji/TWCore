package org.twcore.container;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.twcore.TWCore;
import org.twcore.content.Content;
import org.twcore.registry.TWRegistries;

import java.util.Collections;
import java.util.Set;

/**
 * 基于双向映射的容器类型抽象类。
 * <p>
 * 简化了内容物与物品之间的绑定：每种可装入的内容物都对应一个具体的 {@link Item}（例如“水”对应“水碗”物品）。
 * 子类只需实现 {@link #matches(ItemStack)} 和 {@link #canContain(Content)}，即可自动获得从物品堆栈中提取内容物、
 * 替换内容物以及创建对应物品的能力。
 * </p>
 * <p>
 * 内容物与物品的映射通过 {@link #registerContentMapping(Content, Item)} 添加，底层使用 {@link BiMap} 保证双向唯一性。
 * 空容器物品由父类 {@link ContainerType#getEmptyItem()} 定义，不会出现在映射中。
 * </p>
 *
 * @see ContainerType
 * @see Content
 */
public abstract class AbstractMappedContainer extends ContainerType {
    protected final BiMap<Item, Content> contentBiMap;

    /**
     * 创建映射容器实例。
     *
     * @param settings 容器设置，不能为null
     */
    protected AbstractMappedContainer(ContainerSettings settings) {
        super(settings);
        this.contentBiMap = HashBiMap.create();
    }

    /**
     * 注册一个内容物到物品的映射。
     * <p>
     * 调用前会检查该内容物是否可以被容器容纳（{@link #canContain(Content)}），
     * 以及映射的物品是否与空容器物品相同（避免冲突）。若检查失败则忽略注册并记录警告。
     * </p>
     *
     * @param content 内容物类型
     * @param item    对应的物品
     */
    public void registerContentMapping(Content content, Item item) {
        if (!canContain(content)) {
            TWCore.LOGGER.warn("Attempted to register invalid content to {} container: {}",
                    TWRegistries.CONTAINER_TYPE.getKey(this),
                    TWRegistries.CONTENT.getKey(content));
            return;
        }

        if (item == getEmptyItem()) {
            TWCore.LOGGER.error("Cannot register empty container item as content mapping: {}", item);
            return;
        }

        // forcePut 确保双向唯一性（若已存在映射则替换）
        contentBiMap.forcePut(item, content);
    }

    /**
     * 判断物品堆栈是否为当前容器的空容器形态。
     * <p>
     * 默认实现基于父类的空容器物品 {@link #getEmptyItem()} 进行匹配。
     * 子类如有更复杂的空容器判断逻辑（例如检查NBT）可覆盖此方法。
     * </p>
     *
     * @param stack 物品堆栈
     * @return 如果是空容器则返回true
     */
    protected boolean isEmptyContainer(ItemStack stack) {
        return stack.is(getEmptyItem());
    }

    /**
     * 获取所有已注册映射的内容物类型。
     *
     * @return 不可修改的内容物集合
     */
    public Set<Content> getSupportedContents() {
        return Collections.unmodifiableSet(contentBiMap.values());
    }

    /**
     * 获取所有已注册映射的物品（不包含空容器物品）。
     *
     * @return 不可修改的物品集合
     */
    public Set<Item> getSupportedItems() {
        return Collections.unmodifiableSet(contentBiMap.keySet());
    }

    /**
     * 检查是否支持指定的物品（已注册映射）。
     */
    public boolean supportsItem(Item item) {
        return contentBiMap.containsKey(item);
    }

    /**
     * 检查是否支持指定的内容物（已注册映射）。
     */
    public boolean supportsContent(Content content) {
        return contentBiMap.containsValue(content);
    }

    /**
     * 通过内容物查找对应的物品。
     *
     * @param content 内容物
     * @return 对应的物品，若未映射则返回null
     */
    @Nullable
    public Item getItemForContent(Content content) {
        return contentBiMap.inverse().get(content);
    }

    /**
     * 通过物品查找对应的内容物。
     *
     * @param item 物品
     * @return 对应的内容物，若未映射则返回null
     */
    @Nullable
    public Content getContentForItem(Item item) {
        return contentBiMap.get(item);
    }

    /**
     * 移除指定物品的映射。
     *
     * @param item 物品
     * @return 被移除的内容物，若不存在映射则返回null
     */
    @Nullable
    public Content removeMappingByItem(Item item) {
        return contentBiMap.remove(item);
    }

    /**
     * 移除指定内容物的映射。
     *
     * @param content 内容物
     * @return 被移除的物品，若不存在映射则返回null
     */
    @Nullable
    public Item removeMappingByContent(Content content) {
        return contentBiMap.inverse().remove(content);
    }

    @Override
    @Nullable
    public Content extractContent(ItemStack stack) {
        if (stack.isEmpty() || !matches(stack)) {
            return null;
        }

        if (isEmptyContainer(stack)) {
            return null;
        }

        return contentBiMap.get(stack.getItem());
    }

    @Override
    @NotNull
    public ItemStack replaceContent(@NotNull ItemStack stack, @Nullable Content content) {
        validateReplace(stack, content); // 统一合法性检查

        if (content == null) {
            // 清空容器：用空容器物品替换，保留物品组件
            ItemStack result = new ItemStack(getEmptyItem(), stack.getCount());
            result.applyComponentsAndValidate(stack.getComponentsPatch());

            return result;
        }

        // 查找映射物品
        Item mappedItem = contentBiMap.inverse().get(content);
        if (mappedItem == null) {
            TWCore.LOGGER.warn("No item mapping found for content: {} in container {}",
                    TWRegistries.CONTENT.getKey(content),
                    TWRegistries.CONTAINER_TYPE.getKey(this));
            return stack.copy();
        }

        ItemStack result = new ItemStack(mappedItem, stack.getCount());
        result.applyComponentsAndValidate(stack.getComponentsPatch());

        return result;
    }

    /**
     * 创建装有指定内容物的物品堆栈，不依赖现有堆栈。
     * <p>
     * 该方法重写了父类的通用实现，直接利用映射表创建对应物品的堆栈，无需先构造空容器再替换。
     * </p>
     *
     * @param content 内容物
     * @param amount  数量
     * @return 新物品堆栈
     * @throws IllegalArgumentException 如果内容物不被支持或没有对应物品映射
     */
    @Override
    @NotNull
    public ItemStack createItemStack(Content content, int amount) {
        if (!canContain(content)) {
            throw new IllegalArgumentException("Container cannot contain content: " +
                    TWRegistries.CONTENT.getKey(content));
        }

        Item item = contentBiMap.inverse().get(content);
        if (item == null) {
            throw new IllegalArgumentException("No item mapping found for content: " +
                    TWRegistries.CONTENT.getKey(content));
        }

        return new ItemStack(item, amount);
    }
}