package com.fantasticsource.waystoneadditions;

import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
        MinecraftForge.EVENT_BUS.register(WaystoneAdditions.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlocks(RegistryEvent.Register<Block> event) throws IllegalAccessException
    {
        event.getRegistry().registerAll(new BlockWaystoneEdit());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damage(LivingHurtEvent event)
    {
        Entity source = event.getSource().getTrueSource();
        if (source == null) source = event.getSource().getImmediateSource();
        if (source != null)
        {
            if (byPassesDamageProtection(source)) return;
            if (isDamageProtected(source))
            {
                event.setCanceled(true);
                return;
            }
        }

        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase != null && isDamageProtected(livingBase)) event.setCanceled(true);
    }

    public static boolean isDamageProtected(Entity entity)
    {
        return true;
    }

    public static boolean byPassesDamageProtection(Entity entity)
    {
        return false;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }
}
