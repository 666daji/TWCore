package org.twcore.registry;

import net.minecraft.item.FoodComponents;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.twcore.TWCore;
import org.twcore.content.Content;
import org.twcore.content.FoodContent;
import org.twcore.content.HaveColorContent;

public class Contents {
    public static final String SOUP = "soup";
    public static final String BASE_LIQUID = "base_liquid";
    public static final String SYRUP = "syrup";

    // 汤
    public static final Content MUSHROOM_STEW = registerContent("mushroom_stew",
            new FoodContent(SOUP, FoodComponents.MUSHROOM_STEW));

    public static final Content BEETROOT_SOUP = registerContent("beetroot_soup",
            new FoodContent(SOUP, FoodComponents.BEETROOT_SOUP));

    public static final Content RABBIT_STEW = registerContent("rabbit_stew",
            new FoodContent(SOUP, FoodComponents.RABBIT_STEW));

    // 基础液体
    public static final Content WATER = registerContent("water",
            new HaveColorContent(BASE_LIQUID, 4159204));

    public static final Content MILK = registerContent("milk",
            new HaveColorContent(BASE_LIQUID, 0xFFFAF2ED));

    // 糖浆
    public static final Content HONEY = registerContent("honey", new Content(SYRUP));

    public static Content registerContent(String name, Content content) {
        return Registry.register(TWRegistries.CONTENT, new Identifier(TWCore.MOD_ID, name), content);
    }

    public static void registerAll() {}
}
