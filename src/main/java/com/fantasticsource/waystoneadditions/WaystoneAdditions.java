package com.fantasticsource.waystoneadditions;

import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod(modid = WaystoneAdditions.MODID, name = WaystoneAdditions.NAME, version = WaystoneAdditions.VERSION, dependencies = "required-after:waystones@[4.0.67,)")
public class WaystoneAdditions
{


    public static final String MODID = "waystoneadditions";
    public static final String NAME = "Waystone Additions";
    public static final String VERSION = "1.12.2.001";
    public static ArrayList<TileWaystone> waystones = new ArrayList<>();
    private static Logger logger;


    public WaystoneAdditions()
    {
        Network.init();

        MinecraftForge.EVENT_BUS.register(WaystoneAdditions.class);
        MinecraftForge.EVENT_BUS.register(Protection.class);
        MinecraftForge.EVENT_BUS.register(SyncedConfig.class);
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
            WaystoneManager.sendPlayerWaystones(player);
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        GameRegistry.registerTileEntity(TileWaystoneEdit.class, new ResourceLocation("waystones", "waystone"));
        GameRegistry.registerWorldGenerator(new SpawnStoneGenerator(), 0);
    }
}
