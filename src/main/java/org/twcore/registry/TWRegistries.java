package org.twcore.registry;

import net.minecraft.core.Registry;
import org.twcore.container.ContainerType;
import org.twcore.content.Content;

public class TWRegistries {
    public static final Registry<Content> CONTENT = Contents.CONTENT.makeRegistry(builder -> builder.sync(true));
    public static final Registry<ContainerType> CONTAINER_TYPE = ContainerTypes.CONTAINER_TYPE.makeRegistry(builder -> builder.sync(true));

    public static void registerAll() {}
}