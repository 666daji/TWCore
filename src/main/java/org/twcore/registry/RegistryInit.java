package org.twcore.registry;

public class RegistryInit {

    public static void init() {
        TWRegistries.registerAll();
        Contents.registerAll();
        ContainerTypes.registerAll();
        ModSounds.registerAll();
    }
}
