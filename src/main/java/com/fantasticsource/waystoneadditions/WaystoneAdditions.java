package com.fantasticsource.waystoneadditions;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = WaystoneAdditions.MODID, name = WaystoneAdditions.NAME, version = WaystoneAdditions.VERSION, dependencies = "required-after:waystones@[4.0.67,)", acceptableRemoteVersions = "*")
public class WaystoneAdditions
{
    public static final String MODID = "waystoneadditions";
    public static final String NAME = "Waystone Additions";
    public static final String VERSION = "1.12.2.001";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }
}
