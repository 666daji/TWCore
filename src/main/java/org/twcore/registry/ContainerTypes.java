package org.twcore.registry;

import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.twcore.TWCore;
import org.twcore.container.*;

public class ContainerTypes {
    // 碗
    public static final ContainerType BOWL = registerContainerType("bowl",
            new BowlContainer(new ContainerType.ContainerSettings(Items.BOWL)
                    .setUseSound(ModSounds.SOUP_FILL)));
    // 瓶子
    public static final ContainerType POTION = registerContainerType("potion",
            new PotionContainer(new ContainerType.ContainerSettings(Items.GLASS_BOTTLE)));
    // 桶
    public static final ContainerType BUCKET = registerContainerType("bucket",
            new BucketContainer(new ContainerType.ContainerSettings(Items.BUCKET)
                    .setBaseCapacity(3)
                    .setUseSound(SoundEvents.ITEM_BUCKET_EMPTY)));

    public static ContainerType registerContainerType(String name, ContainerType containerType) {
        return Registry.register(TWRegistries.CONTAINER_TYPE, new Identifier(TWCore.MOD_ID, name), containerType);
    }

    public static void registerAll() {
        initializeBowl();
        initializeBucket();
        initializePotion();
    }

    /**
     * 初始化汤映射
     */
    private static void initializeBowl() {
        // 蘑菇煲
        ((AbstractMappedContainer) BOWL).registerContentMapping(Contents.MUSHROOM_STEW, Items.MUSHROOM_STEW);

        // 甜菜汤
        ((AbstractMappedContainer) BOWL).registerContentMapping(Contents.BEETROOT_SOUP, Items.BEETROOT_SOUP);

        // 兔肉煲
        ((AbstractMappedContainer) BOWL).registerContentMapping(Contents.RABBIT_STEW, Items.RABBIT_STEW);
    }

    /**
     * 初始化桶的映射。
     */
    private static void initializeBucket() {
        // 水桶
        ((AbstractMappedContainer) BUCKET).registerContentMapping(Contents.WATER, Items.WATER_BUCKET);

        // 牛奶桶
        ((AbstractMappedContainer) BUCKET).registerContentMapping(Contents.MILK, Items.MILK_BUCKET);
    }

    /**
     * 初始化瓶子映射。
     */
    private static void initializePotion() {
        // 蜂蜜瓶
        ((AbstractMappedContainer) POTION).registerContentMapping(Contents.HONEY, Items.HONEY_BOTTLE);
    }
}
