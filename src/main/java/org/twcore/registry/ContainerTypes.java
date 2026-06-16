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

    public static void registerAll() {}

    /**
     * 映射容器注册。
     *
     * @see AbstractMappedContainer
     */
    public static void initDefaultMappings() {
        // 碗映射
        BowlContainer bowl = (BowlContainer) BOWL;
        bowl.registerContentMapping(Contents.MUSHROOM_STEW, Items.MUSHROOM_STEW);
        bowl.registerContentMapping(Contents.BEETROOT_SOUP, Items.BEETROOT_SOUP);
        bowl.registerContentMapping(Contents.RABBIT_STEW, Items.RABBIT_STEW);

        // 桶映射
        BucketContainer bucket = (BucketContainer) BUCKET;
        bucket.registerContentMapping(Contents.WATER, Items.WATER_BUCKET);
        bucket.registerContentMapping(Contents.MILK, Items.MILK_BUCKET);

        // 瓶子映射
        PotionContainer potion = (PotionContainer) POTION;
        potion.registerContentMapping(Contents.HONEY, Items.HONEY_BOTTLE);
    }
}
