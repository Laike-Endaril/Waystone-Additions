package com.fantasticsource.waystoneadditions;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.waystoneadditions.compat.Compat;
import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import com.fantasticsource.waystoneadditions.network.HandlerEditWaystoneEdit;
import com.fantasticsource.waystoneadditions.network.Network;
import com.fantasticsource.waystoneadditions.protection.BlockPistonBaseEdit;
import com.fantasticsource.waystoneadditions.protection.Protection;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.ArrayList;

@Mod(modid = WaystoneAdditions.MODID, name = WaystoneAdditions.NAME, version = WaystoneAdditions.VERSION, dependencies = "required-after:waystones@[4.0.67,);required-after:fantasticlib@[1.12.2.001,)")
public class WaystoneAdditions
{
    public static final String MODID = "waystoneadditions";
    public static final String NAME = "Waystone Additions";
    public static final String VERSION = "1.12.2.001";
    public static ArrayList<TileWaystone> waystones = new ArrayList<>();
    public static TileWaystoneEdit spawnstone;


    public WaystoneAdditions()
    {
        Network.init();

        MinecraftForge.EVENT_BUS.register(WaystoneAdditions.class);
        MinecraftForge.EVENT_BUS.register(Protection.class);
        MinecraftForge.EVENT_BUS.register(SyncedConfig.class);
        MinecraftForge.EVENT_BUS.register(BlockPistonBaseEdit.class);
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlocks(RegistryEvent.Register<Block> event) throws IllegalAccessException
    {
        event.getRegistry().registerAll(new BlockWaystoneEdit());
    }

    public static void refreshWaystone(TileWaystoneEdit waystone)
    {
        for (EntityPlayer player : waystone.getWorld().playerEntities)
        {
            Network.WRAPPER.sendTo(new Network.WaystonePacket(waystone), (EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void playerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP && !entity.world.isRemote && spawnstone != null)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            Network.WRAPPER.sendTo(new Network.WaystoneWaypointPacket(spawnstone), player);
        }
    }

    @SubscribeEvent
    public static void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        spawnstone = null;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerTileEntity(TileWaystoneEdit.class, new ResourceLocation("waystones", "waystone"));
        GameRegistry.registerWorldGenerator(new SpawnStoneGenerator(), 0);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //Compat init
        if (Loader.isModLoaded("journeymap")) Compat.journeymap = true;


        //Replace handler for waystone editing packets
        try
        {
            Field f = ReflectionTool.getField(NetworkHandler.class, "channel");
            SimpleNetworkWrapper wrapper = (SimpleNetworkWrapper) f.get(null);
            wrapper.registerMessage(HandlerEditWaystoneEdit.class, MessageEditWaystone.class, 3, Side.SERVER);
        }
        catch (Exception e)
        {
            MCTools.crash(e, 205, true);
        }
    }
}
