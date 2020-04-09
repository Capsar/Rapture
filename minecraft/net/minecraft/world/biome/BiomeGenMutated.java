package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMutated extends BiomeGenBase
{
    protected BiomeGenBase baseBiome;


    public BiomeGenMutated(int p_i45381_1_, BiomeGenBase p_i45381_2_)
    {
        super(p_i45381_1_);
        this.baseBiome = p_i45381_2_;
        this.func_150557_a(p_i45381_2_.color, true);
        this.biomeName = p_i45381_2_.biomeName + " M";
        this.topBlock = p_i45381_2_.topBlock;
        this.fillerBlock = p_i45381_2_.fillerBlock;
        this.fillerBlockMetadata = p_i45381_2_.fillerBlockMetadata;
        this.minHeight = p_i45381_2_.minHeight;
        this.maxHeight = p_i45381_2_.maxHeight;
        this.temperature = p_i45381_2_.temperature;
        this.rainfall = p_i45381_2_.rainfall;
        this.waterColorMultiplier = p_i45381_2_.waterColorMultiplier;
        this.enableSnow = p_i45381_2_.enableSnow;
        this.enableRain = p_i45381_2_.enableRain;
        this.spawnableCreatureList = Lists.newArrayList(p_i45381_2_.spawnableCreatureList);
        this.spawnableMonsterList = Lists.newArrayList(p_i45381_2_.spawnableMonsterList);
        this.spawnableCaveCreatureList = Lists.newArrayList(p_i45381_2_.spawnableCaveCreatureList);
        this.spawnableWaterCreatureList = Lists.newArrayList(p_i45381_2_.spawnableWaterCreatureList);
        this.temperature = p_i45381_2_.temperature;
        this.rainfall = p_i45381_2_.rainfall;
        this.minHeight = p_i45381_2_.minHeight + 0.1F;
        this.maxHeight = p_i45381_2_.maxHeight + 0.2F;
    }

    @Override
	public void decorate(World worldIn, Random p_180624_2_, BlockPos p_180624_3_)
    {
        this.baseBiome.theBiomeDecorator.decorate(worldIn, p_180624_2_, this, p_180624_3_);
    }

    @Override
	public void genTerrainBlocks(World worldIn, Random p_180622_2_, ChunkPrimer p_180622_3_, int p_180622_4_, int p_180622_5_, double p_180622_6_)
    {
        this.baseBiome.genTerrainBlocks(worldIn, p_180622_2_, p_180622_3_, p_180622_4_, p_180622_5_, p_180622_6_);
    }

    /**
     * returns the chance a creature has to spawn.
     */
    @Override
	public float getSpawningChance()
    {
        return this.baseBiome.getSpawningChance();
    }

    @Override
	public WorldGenAbstractTree genBigTreeChance(Random p_150567_1_)
    {
        return this.baseBiome.genBigTreeChance(p_150567_1_);
    }

    @Override
	public int getFoliageColorAtPos(BlockPos p_180625_1_)
    {
        return this.baseBiome.getFoliageColorAtPos(p_180625_1_);
    }

    @Override
	public int getGrassColorAtPos(BlockPos p_180627_1_)
    {
        return this.baseBiome.getGrassColorAtPos(p_180627_1_);
    }

    @Override
	public Class getBiomeClass()
    {
        return this.baseBiome.getBiomeClass();
    }

    /**
     * returns true if the biome specified is equal to this biome
     */
    @Override
	public boolean isEqualTo(BiomeGenBase p_150569_1_)
    {
        return this.baseBiome.isEqualTo(p_150569_1_);
    }

    @Override
	public BiomeGenBase.TempCategory getTempCategory()
    {
        return this.baseBiome.getTempCategory();
    }
}
