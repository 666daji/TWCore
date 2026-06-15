package org.twcore.container;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.twcore.content.Content;
import org.twcore.registry.Contents;

public class PotionContainer extends AbstractMappedContainer {
    public PotionContainer(ContainerSettings settings) {
        super(settings);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isOf(getEmptyItem()) || supportsItem(stack.getItem())) {
            return true;
        }

        // 水瓶
        return isWaterPotion(stack);
    }

    @Override
    public boolean canContain(Content content) {
        return content.isIn(Contents.BASE_LIQUID) || content.isIn(Contents.SYRUP);
    }

    @Override
    public @Nullable Content extractContent(ItemStack stack) {
        if (isWaterPotion(stack)) {
            return Contents.WATER;
        }

        return super.extractContent(stack);
    }

    @Override
    public @NotNull ItemStack replaceContent(@NotNull ItemStack stack, @Nullable Content content) {
        if (content != null && content.equals(Contents.WATER)) {
            ItemStack result = new ItemStack(Items.POTION, stack.getCount());
            result.applyChanges(stack.getComponentChanges());
            result.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.WATER));

            return result;
        }

        return super.replaceContent(stack, content);
    }

    @Override
    @NotNull
    public ItemStack createItemStack(Content content, int amount) {
        // 特殊处理水瓶
        if (content.equals(Contents.WATER)) {
            ItemStack result = new ItemStack(Items.POTION, amount);
            result.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.WATER));
        }

        return super.createItemStack(content, amount);
    }

    /**
     * 检查物品堆是否为水瓶。
     * @param stack 要检查的物品堆
     * @return 如果物品堆是水瓶则返回true，否则返回false
     */
    public static boolean isWaterPotion(ItemStack stack) {
        if (stack.getItem() instanceof PotionItem) {
            return stack.isOf(Items.POTION) &&
                    stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).matches(Potions.WATER);
        }
        return false;
    }

    /**
     * @apiNote 如果要调用该方法请先使用isWaterPotion方法检查堆栈是否为水瓶
     */
    @Override
    public boolean supportsItem(Item item) {
        return super.supportsItem(item);
    }
}