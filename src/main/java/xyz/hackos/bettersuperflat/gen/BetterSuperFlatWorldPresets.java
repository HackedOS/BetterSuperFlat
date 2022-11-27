package xyz.hackos.bettersuperflat.gen;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import xyz.hackos.bettersuperflat.utils.BetterSuperFlatIdentifier;

import java.util.Map;


public class BetterSuperFlatWorldPresets {
    public static final RegistryKey<WorldPreset> BetterSuperFlat = RegistryKey.of(Registry.WORLD_PRESET_KEY, new BetterSuperFlatIdentifier("bettersuperflat"));

    private static final DimensionOptions OVERWORLD_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.OVERWORLD),
            new BetterSuperFlatChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.OVERWORLD)));
    private static final DimensionOptions NETHER_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.THE_NETHER),
            new BetterSuperFlatChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.NETHER)));
    private static final DimensionOptions END_OPTIONS = new DimensionOptions(
            BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.THE_END),
            new BetterSuperFlatChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.NOISE_PARAMETERS, new TheEndBiomeSource(BuiltinRegistries.BIOME), BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.END)));

    public static void registerAll() {
        BuiltinRegistries.add(BuiltinRegistries.WORLD_PRESET, BetterSuperFlatWorldPresets.BetterSuperFlat, new WorldPreset(Map.of(DimensionOptions.OVERWORLD, OVERWORLD_OPTIONS, DimensionOptions.NETHER, NETHER_OPTIONS, DimensionOptions.END, END_OPTIONS)));
    }
}
