package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase
{


    public BiomeGenDesert(int p_i1977_1_)
    {
        super(p_i1977_1_);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState();
        this.fillerBlock = Blocks.sand.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = 50;
        this.theBiomeDecorator.cactiPerChunk = 10;
        this.spawnableCreatureList.clear();
    }

    @Override
	public void decorate(World worldIn, Random p_180624_2_, BlockPos p_180624_3_)
    {
        super.decorate(worldIn, p_180624_2_, p_180624_3_);

        if (p_180624_2_.nextInt(1000) == 0)
        {
            int var4 = p_180624_2_.nextInt(16) + 8;
            int var5 = p_180624_2_.nextInt(16) + 8;
            BlockPos var6 = worldIn.getHeight(p_180624_3_.add(var4, 0, var5)).up();
            (new WorldGenDesertWells()).generate(worldIn, p_180624_2_, var6);
        }
    }
}
