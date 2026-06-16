package org.twcore.registry;

import net.neoforged.bus.api.IEventBus;

public class RegistryInit {

    public static void init(IEventBus modEventBus) {
        TWRegistries.registerAll();
        Contents.registerAll(modEventBus);
        ContainerTypes.registerAll(modEventBus);
        ModSounds.registerAll(modEventBus);
    }
}
