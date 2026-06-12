package org.twcore.content;

import net.minecraft.item.FoodComponent;

/**
 * 表示可以食用的内容物
 */
public class FoodContent extends Content{
    private final FoodComponent foodComponent;

    /**
     * 创建一个内容物类型实例。
     *
     * @param id 内容物类型的唯一标识符
     * @param foodComponent 内容物的食物属性
     * @throws NullPointerException 如果id为null
     */
    public FoodContent(String category, FoodComponent foodComponent) {
        super(category);
        this.foodComponent = foodComponent;
    }

    /**
     * 获取内容物对应的食物组件。
     */
    public FoodComponent getFoodComponent() {
        return foodComponent;
    }
}
