package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock
{


    public ItemBanner()
    {
        super(Blocks.standing_banner);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side == EnumFacing.DOWN)
        {
            return false;
        }
        else if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            pos = pos.offset(side);

            if (!playerIn.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (!Blocks.standing_banner.canPlaceBlockAt(worldIn, pos))
            {
                return false;
            }
            else if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                if (side == EnumFacing.UP)
                {
                    int var9 = MathHelper.floor_double((double)((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    worldIn.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(var9)), 3);
                }
                else
                {
                    worldIn.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
                }

                --stack.stackSize;
                TileEntity var10 = worldIn.getTileEntity(pos);

                if (var10 instanceof TileEntityBanner)
                {
                    ((TileEntityBanner)var10).setItemValues(stack);
                }

                return true;
            }
        }
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        String var2 = "item.banner.";
        EnumDyeColor var3 = this.getBaseColor(stack);
        var2 = var2 + var3.getUnlocalizedName() + ".name";
        return StatCollector.translateToLocal(var2);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     *  
     * @param tooltip All lines to display in the Item's tooltip. This is a List of Strings.
     * @param advanced Whether the setting "Advanced tooltips" is enabled
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
    {
        NBTTagCompound var5 = stack.getSubCompound("BlockEntityTag", false);

        if (var5 != null && var5.hasKey("Patterns"))
        {
            NBTTagList var6 = var5.getTagList("Patterns", 10);

            for (int var7 = 0; var7 < var6.tagCount() && var7 < 6; ++var7)
            {
                NBTTagCompound var8 = var6.getCompoundTagAt(var7);
                EnumDyeColor var9 = EnumDyeColor.byDyeDamage(var8.getInteger("Color"));
                TileEntityBanner.EnumBannerPattern var10 = TileEntityBanner.EnumBannerPattern.getPatternByID(var8.getString("Pattern"));

                if (var10 != null)
                {
                    tooltip.add(StatCollector.translateToLocal("item.banner." + var10.getPatternName() + "." + var9.getUnlocalizedName()));
                }
            }
        }
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass == 0)
        {
            return 16777215;
        }
        else
        {
            EnumDyeColor var3 = this.getBaseColor(stack);
            return var3.getMapColor().colorValue;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *  
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
        EnumDyeColor[] var4 = EnumDyeColor.values();
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            EnumDyeColor var7 = var4[var6];
            subItems.add(new ItemStack(itemIn, 1, var7.getDyeDamage()));
        }
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return CreativeTabs.tabDecorations;
    }

    private EnumDyeColor getBaseColor(ItemStack stack)
    {
        NBTTagCompound var2 = stack.getSubCompound("BlockEntityTag", false);
        EnumDyeColor var3 = null;

        if (var2 != null && var2.hasKey("Base"))
        {
            var3 = EnumDyeColor.byDyeDamage(var2.getInteger("Base"));
        }
        else
        {
            var3 = EnumDyeColor.byDyeDamage(stack.getMetadata());
        }

        return var3;
    }
}
