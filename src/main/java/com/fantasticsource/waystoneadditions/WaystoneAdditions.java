package com.fantasticsource.waystoneadditions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = WaystoneAdditions.MODID, name = WaystoneAdditions.NAME, version = WaystoneAdditions.VERSION, dependencies = "required-after:waystones@[4.0.67,)", acceptableRemoteVersions = "*")
public class WaystoneAdditions
{
    public static final String MODID = "waystoneadditions";
    public static final String NAME = "Waystone Additions";
    public static final String VERSION = "1.12.2.001";

    private static Logger logger;

    public WaystoneAdditions()
    {
        MinecraftForge.EVENT_BUS.register(WaystoneAdditions.class);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
