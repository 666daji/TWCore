package org.twcore.container;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.twcore.content.Content;

import java.util.Objects;

/**
 * 容器类型抽象基类。
 * <p>
 * 表示一种可以承载内容物的容器类型，如碗、瓶、桶等。
 * </p>
 */
public abstract class ContainerType {
    private final Item emptyItem;
    private final int baseCapacity;
    private final SoundEvent useSound;

    /**
     * 创建一个容器类型实例。
     *
     * @param settings 容器设置，不能为null
     */
    protected ContainerType(ContainerSettings settings) {
        Objects.requireNonNull(settings, "Container settings cannot be null");
        this.emptyItem = Objects.requireNonNull(settings.emptyItem, "Empty container item cannot be null");
        this.baseCapacity = settings.baseCapacity;
        this.useSound = Objects.requireNonNull(settings.useSound, "Use sound cannot be null");
    }

    /**
     * 容器设置类。
     */
    public static class ContainerSettings {
        private final Item emptyItem;
        private int baseCapacity = 1;
        private SoundEvent useSound = SoundEvents.ITEM_BOTTLE_FILL;

        public ContainerSettings(Item emptyItem) {
            this.emptyItem = Objects.requireNonNull(emptyItem, "Empty container item cannot be null");
        }

        public ContainerSettings setBaseCapacity(int baseCapacity) {
            if (baseCapacity <= 0) {
                throw new IllegalArgumentException("Base capacity must be greater than 0");
            }
            this.baseCapacity = baseCapacity;
            return this;
        }

        public ContainerSettings setUseSound(@NotNull SoundEvent useSound) {
            this.useSound = Objects.requireNonNull(useSound, "Use sound cannot be null");
            return this;
        }
    }

    @NotNull
    public Item getEmptyItem() {
        return emptyItem;
    }

    public int getBaseCapacity() {
        return baseCapacity;
    }

    @NotNull
    public SoundEvent getUseSound() {
        return useSound;
    }

    /**
     * 判断一个物品堆栈是否属于该容器类型。
     * <p>
     * 子类必须实现此方法，根据物品的ID、NBT数据等判断。
     * </p>
     *
     * @param stack 要检查的物品堆栈
     * @return 如果物品堆栈属于该容器类型则返回true
     */
    public abstract boolean matches(ItemStack stack);

    /**
     * 判断该容器类型是否可以装入指定的内容物类型。
     * <p>
     * 子类必须实现此方法，定义容器可以承载哪些内容物。
     * </p>
     *
     * @param content 要检查的内容物类型
     * @return 如果容器可以装入该内容物则返回true
     */
    public abstract boolean canContain(Content content);

    /**
     * 从物品堆栈中提取内容物类型。
     *
     * @param stack 物品堆栈（必须已通过 {@link #matches} 验证）
     * @return 内容物类型，若为空则返回null
     */
    @Nullable
    public abstract Content extractContent(ItemStack stack);

    /**
     * 替换容器中的内容物，构建并返回一个新的 ItemStack，不修改传入的堆栈。
     * <p>
     * 子类实现必须保证原堆栈不被改动，并返回新创建的堆栈。
     * </p>
     *
     * @param stack   容器物品堆栈（必须已通过 {@link #matches} 验证）
     * @param content 新的内容物类型，为null表示清空容器
     * @return 替换内容物后的新物品堆栈
     * @throws IllegalArgumentException 如果容器不匹配或无法装入该内容物
     */
    @NotNull
    public abstract ItemStack replaceContent(@NotNull ItemStack stack, @Nullable Content content);

    /**
     * 对传入的容器物品堆栈和内容物进行合法性检查。
     * 通常应在 {@link #replaceContent} 实现的开头调用。
     *
     * @param stack   待检查的容器物品堆栈
     * @param content 待装入的内容物，可为null（表示清空，不检查内容物兼容性）
     * @throws IllegalArgumentException 如果堆栈不匹配本容器类型，或content不为null且容器不能装入该内容物
     */
    public void validateReplace(@NotNull ItemStack stack, @Nullable Content content) {
        if (!matches(stack)) {
            throw new IllegalArgumentException("Item stack is not a valid container of type " + this);
        }
        if (content != null && !canContain(content)) {
            throw new IllegalArgumentException("Container cannot contain content: " + content);
        }
    }

    /**
     * 创建一个空的该容器类型的物品堆栈。
     *
     * @param amount 物品数量
     * @return 空的容器物品堆栈
     */
    @NotNull
    public ItemStack createEmptyItemStack(int amount) {
        return new ItemStack(emptyItem, amount);
    }

    /**
     * 创建装有指定内容物的物品堆栈。
     * <p>默认实现：创建一个空容器，然后用指定内容物填充</p>
     *
     * @param content 要装入的内容物类型
     * @param amount 物品数量
     * @return 装有内容物的物品堆栈
     */
    @NotNull
    public ItemStack createItemStack(Content content, int amount) {
        ItemStack empty = createEmptyItemStack(amount);
        return replaceContent(empty, content);
    }

    /**
     * 获取容器的使用剩余
     * @return 使用剩余，默认为对应的空容器
     */
    public ItemStack remainder() {
        return createEmptyItemStack(1);
    }
}