package com.fantasticsource.waystoneadditions;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Random;

public class BlockWaystoneEdit extends BlockWaystone
{
    private static final StatBase WAYSTONE_ACTIVATED = StatList.getOneShotStat("stat.waystones:waystonesActivated");
    private static Field registryNameField;

    static
    {
        try
        {
            registryNameField = ReflectionTool.getField(Impl.class, "registryName");
        }
        catch (Exception e)
        {
            MCTools.crash(e, 200, true);
        }
    }


    public BlockWaystoneEdit() throws IllegalAccessException
    {
        super();

        registryNameField.set(this, new ResourceLocation("waystones", "waystone"));
        setUnlocalizedName(registryName.toString());
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileWaystoneEdit(!getStateFromMeta(metadata).getValue(BASE));
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
    {
        if (!player.capabilities.isCreativeMode && !SyncedConfig.breakable) return -1;
        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileWaystoneEdit tileWaystone = (TileWaystoneEdit) getTileWaystone(world, pos);
        if (tileWaystone == null) return true;

        if (!tileWaystone.isSpawnstone && !world.isRemote && tileWaystone.wasGenerated() && tileWaystone.getOwner() == null)
        {
            if (tileWaystone.isMossy())
            {
                if (SyncedConfig.naturalMossyFinderBecomesOwner)
                {
                    tileWaystone.setOwner(player);
                    WaystoneAdditions.refreshWaystone(tileWaystone);
                }
            }
            else
            {
                if (SyncedConfig.naturalSmoothFinderBecomesOwner)
                {
                    tileWaystone.setOwner(player);
                    WaystoneAdditions.refreshWaystone(tileWaystone);
                }
            }
        }


        if (player.isSneaking() && (player.capabilities.isCreativeMode || !WaystoneConfig.general.creativeModeOnly))
        {
            if (tileWaystone.isGlobal() && !player.capabilities.isCreativeMode && !WaystoneConfig.general.allowEveryoneGlobal)
            {
                player.sendStatusMessage(new TextComponentTranslation("waystones:creativeRequired"), true);
                return true;
            }

            if (WaystoneConfig.general.restrictRenameToOwner && !tileWaystone.isOwner(player))
            {
                player.sendStatusMessage(new TextComponentTranslation("waystoneadditions:notTheOwner"), true);
                return true;
            }

            Waystones.proxy.openWaystoneSettings(player, new WaystoneEntry(tileWaystone.getParent()), false);
            return true;
        }

        WaystoneEntry knownWaystone = world.isRemote ? ClientWaystones.getKnownWaystone(tileWaystone.getWaystoneName()) : null;
        if (knownWaystone == null)
        {
            activateWaystone(player, world, tileWaystone);
        }
        else
        {
            Waystones.proxy.openWaystoneSelection(player, WarpMode.WAYSTONE, EnumHand.MAIN_HAND, knownWaystone);
        }

        return true;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (!SyncedConfig.dropItem) return null;
        return super.getItemDropped(state, rand, fortune);
    }
}
