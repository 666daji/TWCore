package org.twcore.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.twcore.TWCore;

public class ModSounds {
    public static final SoundEvent SOUP_FILL = registerSoundEvent("soup_fill");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(TWCore.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerAll() {}
}
