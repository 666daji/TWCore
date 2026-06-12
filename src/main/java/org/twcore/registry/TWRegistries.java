package org.twcore.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.twcore.TWCore;
import org.twcore.container.ContainerType;
import org.twcore.content.Content;

public class TWRegistries {
    public static Registry<Content> CONTENT = of("content");

    public static Registry<ContainerType> CONTAINER_TYPE = of("container_type");

    public static <T> Registry<T> of(String id) {
        RegistryKey<Registry<T>> key = RegistryKey.ofRegistry(new Identifier(TWCore.MOD_ID, id));

        return FabricRegistryBuilder.createSimple(key)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }

    public static void registerAll() {}
}
