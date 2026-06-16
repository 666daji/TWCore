package org.twcore.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.Foods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.twcore.TWCore;
import org.twcore.content.Content;
import org.twcore.content.FoodContent;
import org.twcore.content.HaveColorContent;

public class Contents {
    public static final DeferredRegister<Content> CONTENT =
            DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(TWCore.MOD_ID, "content"), TWCore.MOD_ID);

    public static final String SOUP = "soup";
    public static final String BASE_LIQUID = "base_liquid";
    public static final String SYRUP = "syrup";

    // 汤
    public static final DeferredHolder<Content, Content> MUSHROOM_STEW = registerContent("mushroom_stew",
            new FoodContent(SOUP, Foods.MUSHROOM_STEW));

    public static final DeferredHolder<Content, Content> BEETROOT_SOUP = registerContent("beetroot_soup",
            new FoodContent(SOUP, Foods.BEETROOT_SOUP));

    public static final DeferredHolder<Content, Content> RABBIT_STEW = registerContent("rabbit_stew",
            new FoodContent(SOUP, Foods.RABBIT_STEW));

    // 基础液体
    public static final DeferredHolder<Content, Content> WATER = registerContent("water",
            new HaveColorContent(BASE_LIQUID, 4159204));

    public static final DeferredHolder<Content, Content> MILK = registerContent("milk",
            new HaveColorContent(BASE_LIQUID, 0xFFFAF2ED));

    // 糖浆
    public static final DeferredHolder<Content, Content> HONEY = registerContent("honey", new Content(SYRUP));

    public static DeferredHolder<Content, Content> registerContent(String name, Content content) {
        return CONTENT.register(name, () -> content);
    }

    public static void registerAll(IEventBus modEventBus) {
        CONTENT.register(modEventBus);
    }
}
