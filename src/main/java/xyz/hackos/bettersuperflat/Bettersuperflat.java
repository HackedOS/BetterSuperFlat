package xyz.hackos.bettersuperflat;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import xyz.hackos.bettersuperflat.gen.BetterSuperFlatChunkGenerator;
import xyz.hackos.bettersuperflat.utils.BetterSuperFlatIdentifier;

public class Bettersuperflat implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registries.CHUNK_GENERATOR, new BetterSuperFlatIdentifier("bettersuperflat"), BetterSuperFlatChunkGenerator.CODEC);
        RegistryKey.of(RegistryKeys.WORLD_PRESET, new BetterSuperFlatIdentifier("bettersuperflat"));
    }
}
