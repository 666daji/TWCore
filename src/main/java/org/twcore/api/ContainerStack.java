package org.twcore.api;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.twcore.container.ContainerType;
import org.twcore.content.Content;

import java.util.Objects;

/**
 * 容器-内容物-物品堆栈绑定对象。
 * <p>
 * 表示一个物品堆栈被识别为特定的容器类型和内容物类型的组合，
 * 同时保留原始的 ItemStack 引用。
 * 提供一系列实例方法来查询和操作该容器。
 * </p>
 */
public final class ContainerStack {
    private final ContainerType container;
    @Nullable
    private final Content content;
    private final ItemStack originalStack;

    /**
     * @param container    容器类型，不能为null
     * @param content      内容物类型，可以为null（空容器）
     * @param originalStack 原始物品堆栈，不能为null
     */
    public ContainerStack(@NotNull ContainerType container,
                          @Nullable Content content,
                          @NotNull ItemStack originalStack) {
        this.container = Objects.requireNonNull(container, "Container cannot be null");
        this.content = content;
        this.originalStack = Objects.requireNonNull(originalStack, "Original stack cannot be null");
    }

    @NotNull
    public ContainerType getContainer() {
        return container;
    }

    @Nullable
    public Content getContent() {
        return content;
    }

    @NotNull
    public ItemStack getOriginalStack() {
        return originalStack;
    }

    public boolean isEmpty() {
        return content == null;
    }

    /**
     * 检查是否装有指定的内容物。
     */
    public boolean contains(@NotNull Content content) {
        return !isEmpty() && this.content.equals(content);
    }

    /**
     * 检查此容器物品堆栈是否提供指定的内容物。
     */
    public boolean providesContent(@NotNull Content content) {
        Objects.requireNonNull(content);
        return !isEmpty() && this.content.equals(content);
    }

    /**
     * 检查是否为空容器。
     */
    public boolean isEmptyContainer() {
        return isEmpty();
    }

    /**
     * 检查此容器是否可以装入指定的内容物。
     */
    public boolean canContain(@NotNull Content content) {
        return isEmpty() && container.canContain(content);
    }

    /**
     * 替换此容器中的内容物，返回一个新的 ItemStack。
     * 不修改原始堆栈。
     */
    @NotNull
    public ItemStack replaceContent(@Nullable Content newContent) {
        return container.replaceContent(originalStack, newContent);
    }

    /**
     * 获取一个装有特定内容物的新物品堆栈。
     */
    @NotNull
    public ItemStack createFilledStack(@NotNull Content content, int amount) {
        return container.createItemStack(content, amount);
    }

    /**
     * 获取一个空容器的物品堆栈。
     */
    @NotNull
    public ItemStack createEmptyStack(int amount) {
        return container.createEmptyItemStack(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerStack that)) return false;
        return container.equals(that.container) &&
                Objects.equals(content, that.content) &&
                ItemStack.areItemsEqual(originalStack, that.originalStack);
    }

    @Override
    public String toString() {
        return "ContainerStack{" +
                "container=" + container +
                ", content=" + (content != null ? content : "null") +
                '}';
    }
}