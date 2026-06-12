package org.twcore.container;

import net.minecraft.item.ItemStack;
import org.twcore.content.Content;
import org.twcore.registry.Contents;

/**
 * 碗容器类型。
 * <p>
 * 用于承载汤类内容物，如蘑菇煲、甜菜汤等。
 * </p>
 */
public class BowlContainer extends AbstractMappedContainer {
    public BowlContainer( ContainerSettings settings) {
        super(settings);
    }

    @Override
    public boolean matches(ItemStack stack) {
        // 检查是否是碗或已知的汤类物品
        return stack.isOf(getEmptyItem()) || supportsItem(stack.getItem());
    }

    @Override
    public boolean canContain(Content content) {
        // 碗可以装汤类内容物
        return content.isIn(Contents.SOUP);
    }
}