package com.fantasticsource.waystoneadditions;

import com.fantasticsource.waystoneadditions.config.WaystoneAdditionsConfig;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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
        return new TileWaystoneEdit(!getStateFromMeta(metadata).getValue(BASE));
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
                if (WaystoneAdditionsConfig.serverSettings.natural.mossy.finderBecomesOwner)
                {
                    tileWaystone.setOwner(player);
                    WaystoneAdditions.refreshWaystone(tileWaystone);
                }
            }
            else
            {
                if (WaystoneAdditionsConfig.serverSettings.natural.smooth.finderBecomesOwner)
                {
                    tileWaystone.setOwner(player);
                    WaystoneAdditions.refreshWaystone(tileWaystone);
                }
            }
        }


        if (player.isSneaking() && (player.capabilities.isCreativeMode || !WaystoneConfig.general.creativeModeOnly))
        {
            if (!world.isRemote) //TODO see if removing this line disables gui when sneak-right clicking unowned waystone
            {
                if (WaystoneConfig.general.restrictRenameToOwner && !tileWaystone.isOwner(player))
                {
                    player.sendStatusMessage(new TextComponentTranslation("waystones:notTheOwner"), true);
                    return true;
                }

                if (tileWaystone.isGlobal() && !player.capabilities.isCreativeMode && !WaystoneConfig.general.allowEveryoneGlobal)
                {
                    player.sendStatusMessage(new TextComponentTranslation("waystones:creativeRequired"), true);
                    return true;
                }
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
}
