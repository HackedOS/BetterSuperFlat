package xyz.hackos.bettersuperflat.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BetterSuperFlatChunkGenerator extends NoiseChunkGenerator {
    public static final Codec<BetterSuperFlatChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(BetterSuperFlatChunkGenerator::getBiomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC
                            .fieldOf("settings")
                            .forGetter(BetterSuperFlatChunkGenerator::getSettings))
            .apply(instance, instance.stable(BetterSuperFlatChunkGenerator::new)));

    public BetterSuperFlatChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
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
}
