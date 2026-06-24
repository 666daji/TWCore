package org.twcore.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.twcore.TWCore;
import org.twcore.container.*;

public class ContainerTypes {
    public static final DeferredRegister<ContainerType> CONTAINER_TYPE =
            DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(TWCore.MOD_ID, "container_type"), TWCore.MOD_ID);

    public static final RegistryObject<ContainerType> BOWL = register("bowl",
            () -> new BowlContainer(new ContainerType.ContainerSettings(() -> Items.BOWL)
                    .setUseSound(ModSounds.SOUP_FILL)));

    public static final RegistryObject<ContainerType> POTION = register("potion",
            () -> new PotionContainer(new ContainerType.ContainerSettings(() -> Items.GLASS_BOTTLE)));

    public static final RegistryObject<ContainerType> BUCKET = register("bucket",
            () -> new BucketContainer(new ContainerType.ContainerSettings(() -> Items.BUCKET)
                    .setBaseCapacity(3)
                    .setUseSound(() -> SoundEvents.BUCKET_EMPTY)));

    private static <T extends ContainerType> RegistryObject<T> register(String name, java.util.function.Supplier<T> supplier) {
        return CONTAINER_TYPE.register(name, supplier);
    }

    public static void registerAll(IEventBus modBus) {
        CONTAINER_TYPE.register(modBus);
    }

    public static void initDefaultMappings() {
        // 碗映射
        BowlContainer bowl = (BowlContainer) BOWL.get();
        bowl.registerContentMapping(Contents.MUSHROOM_STEW.get(), Items.MUSHROOM_STEW);
        bowl.registerContentMapping(Contents.BEETROOT_SOUP.get(), Items.BEETROOT_SOUP);
        bowl.registerContentMapping(Contents.RABBIT_STEW.get(), Items.RABBIT_STEW);

        // 桶映射
        BucketContainer bucket = (BucketContainer) BUCKET.get();
        bucket.registerContentMapping(Contents.WATER.get(), Items.WATER_BUCKET);
        bucket.registerContentMapping(Contents.MILK.get(), Items.MILK_BUCKET);

        // 瓶子映射
        PotionContainer potion = (PotionContainer) POTION.get();
        potion.registerContentMapping(Contents.HONEY.get(), Items.HONEY_BOTTLE);
    }
}