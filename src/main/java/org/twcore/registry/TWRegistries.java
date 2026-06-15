package org.twcore.registry;

import net.minecraftforge.registries.*;
import org.twcore.content.Content;
import org.twcore.container.ContainerType;
import java.util.function.Supplier;

public class TWRegistries {
    public static final Supplier<IForgeRegistry<Content>> CONTENT =
            Contents.CONTENT.makeRegistry(() -> new RegistryBuilder<Content>().hasTags());

    public static final Supplier<IForgeRegistry<ContainerType>> CONTAINER_TYPE =
            ContainerTypes.CONTAINER_TYPE.makeRegistry(() -> new RegistryBuilder<ContainerType>().hasTags());

    public static void registerAll() {}
}