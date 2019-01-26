package com.fantasticsource.waystoneadditions;

import net.blay09.mods.waystones.block.BlockWaystone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class BlockWaystoneEdit extends BlockWaystone
{
    private static final StatBase WAYSTONE_ACTIVATED = StatList.getOneShotStat("stat.waystones:waystonesActivated");
    private static Field registryNameField;

    static
    {
        initReflections();
    }


    public BlockWaystoneEdit() throws IllegalAccessException
    {
        super();

        registryNameField.set(this, new ResourceLocation("waystones", "waystone"));
        setUnlocalizedName(registryName.toString());
    }

    private static void initReflections()
    {
        try
        {
            registryNameField = Impl.class.getDeclaredField("registryName");
            registryNameField.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(200, true);
        }
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileWaystoneEdit(!getStateFromMeta(metadata).getValue(BASE), !world.isRemote);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }
}
