package org.twcore.container;

import net.minecraft.item.ItemStack;
import org.twcore.content.Content;
import org.twcore.registry.Contents;

public class BucketContainer extends AbstractMappedContainer {
    public BucketContainer(ContainerSettings settings) {
        super(settings);
    }

    @Override
    public boolean matches(ItemStack stack) {
        // 检查是否是空桶或支持的桶装物品
        return stack.isOf(getEmptyItem()) || supportsItem(stack.getItem());
    }

    @Override
    public boolean canContain(Content content) {
        // 桶可以装基础液体类内容物
        return content.isIn(Contents.BASE_LIQUID);
    }
}