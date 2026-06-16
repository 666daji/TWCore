package org.twcore.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.twcore.TWCore;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TWCore.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUP_FILL = registerSoundEvent("soup_fill");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TWCore.MOD_ID, name)));
    }

    public static void registerAll(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
