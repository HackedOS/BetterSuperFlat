package xyz.hackos.bettersuperflat.gen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import xyz.hackos.bettersuperflat.utils.BetterSuperFlatIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BetterSuperFlatChunkGenerator extends NoiseChunkGenerator {
    private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
    private final Supplier<List<PlacedFeatureIndexer.IndexedFeatures>> indexedFeaturesListSupplier;

    public static final Codec<BetterSuperFlatChunkGenerator> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            NoiseChunkGenerator.createStructureSetRegistryGetter(instance).and(
                                            instance
                                                    .group(
                                                            RegistryOps.createRegistryCodec(Registry.NOISE_KEY).forGetter(generator -> generator.noiseRegistry),
                                                            (BiomeSource.CODEC.fieldOf("biome_source")).forGetter(ChunkGenerator::getBiomeSource),
                                                            (ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings")).forGetter(BetterSuperFlatChunkGenerator::getSettings)))
                                    .apply(instance, instance.stable(BetterSuperFlatChunkGenerator::new)));

    public BetterSuperFlatChunkGenerator(Registry<StructureSet> structureRegistry, Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(structureRegistry, noiseRegistry, biomeSource, settings);
        this.noiseRegistry = noiseRegistry;
        this.indexedFeaturesListSupplier = Suppliers.memoize(() -> PlacedFeatureIndexer.collectIndexedFeatures(List.copyOf(biomeSource.getBiomes()), biomeEntry -> biomeEntry.value().getGenerationSettings().getFeatures(), true));
    }

    public RegistryEntry<ChunkGeneratorSettings> getSettings() {
        return this.settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        BlockPos pos =
                new BlockPos(
                        region.getCenterPos().getStartX(),
                        region.getBottomY(),
                        region.getCenterPos().getStartZ());
        generateChunkFloor(
                region,
                pos,
                new BlockBox(
                        chunk.getPos().getStartX(),
                        region.getBottomY(),
                        chunk.getPos().getStartZ(),
                        chunk.getPos().getStartX() + 15,
                        region.getTopY(),
                        chunk.getPos().getStartZ() + 15));
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(
            Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor accessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess access, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carver) {
    }


    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos pos = new BlockPos(chunkPos.getStartX(), chunk.getBottomY(), chunkPos.getStartZ());
        int startX = chunkPos.getStartX();
        int startZ = chunkPos.getStartZ();
        BlockBox box = new BlockBox(startX, 0, startZ, startX + 15, chunk.getHeight(), startZ + 15);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    protected static void placeRelativeBlockInBox(
            WorldAccess world,
            BlockState block,
            BlockPos referencePos,
            int x,
            int y,
            int z,
            BlockBox box) {
        BlockPos blockPos =
                new BlockPos(referencePos.getX() + x, referencePos.getY() + y, referencePos.getZ() + z);
        if (box.contains(blockPos)) {
            world.setBlockState(blockPos, block, 2);
        }
    }

    protected static void fillRelativeBlockInBox(
            WorldAccess world,
            BlockState block,
            BlockPos referencePos,
            int startX,
            int startY,
            int startZ,
            int endX,
            int endY,
            int endZ,
            BlockBox box) {
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    placeRelativeBlockInBox(world, block, referencePos, x, y, z, box);
                }
            }
        }
    }

    protected static void generateChunkFloor(ServerWorldAccess world, BlockPos pos, BlockBox box) {
        fillRelativeBlockInBox(
                world, Blocks.GRAY_CONCRETE.getDefaultState(), pos, 1,0,1,14,0,14,box);
        fillRelativeBlockInBox(
                world, Blocks.GRAY_WOOL.getDefaultState(), pos, 1,0,0,15,0,0,box);
        fillRelativeBlockInBox(
                world, Blocks.GRAY_WOOL.getDefaultState(), pos, 0,0,0,0,0,15,box);
        fillRelativeBlockInBox(
                world, Blocks.GRAY_WOOL.getDefaultState(), pos, 15,0,1,15,0,15,box);
        fillRelativeBlockInBox(
                world, Blocks.GRAY_WOOL.getDefaultState(), pos, 1,0,15,14,0,15,box);
        fillRelativeBlockInBox(
                world, Blocks.BARRIER.getDefaultState(), pos, 0,1,0,15,1,15,box);

    }

    static {
        Registry.register(Registry.CHUNK_GENERATOR, new BetterSuperFlatIdentifier("bettersuperflat"), BetterSuperFlatChunkGenerator.CODEC);
    }
}
