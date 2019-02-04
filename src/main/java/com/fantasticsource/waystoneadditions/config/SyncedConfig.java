package com.fantasticsource.waystoneadditions.config;

import com.fantasticsource.waystoneadditions.Network;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import static com.fantasticsource.waystoneadditions.WaystoneAdditions.MODID;
import static com.fantasticsource.waystoneadditions.config.WaystoneAdditionsConfig.serverSettings;

public class SyncedConfig
{
    public static int configChanged = 0;


    public static int spawnstoneBlockProtectionRadius = serverSettings.spawnstone.blockProtectionRadius;
    public static int spawnstoneDamageProtectionRadius = serverSettings.spawnstone.damageProtectionRadius;
    public static boolean spawnstoneEnabled = serverSettings.spawnstone.enabled;
    public static String spawnstoneName = serverSettings.spawnstone.spawnstoneName;

    public static int placedGlobalBlockProtectionRadius = serverSettings.placed.global.blockProtectionRadius;
    public static int placedGlobalDamageProtectionRadius = serverSettings.placed.global.damageProtectionRadius;
    public static boolean placedGlobalOwnerCanBuild = serverSettings.placed.global.ownerCanBuild;
    public static boolean placedGlobalOwnerCanKill = serverSettings.placed.global.ownerCanKill;

    public static int placedNonGlobalBlockProtectionRadius = serverSettings.placed.nonGlobal.blockProtectionRadius;
    public static int placedNonGlobalDamageProtectionRadius = serverSettings.placed.nonGlobal.damageProtectionRadius;
    public static boolean placedNonGlobalOwnerCanBuild = serverSettings.placed.nonGlobal.ownerCanBuild;
    public static boolean placedNonGlobalOwnerCanKill = serverSettings.placed.nonGlobal.ownerCanKill;

    public static int naturalMossyBlockProtectionRadius = serverSettings.natural.mossy.blockProtectionRadius;
    public static int naturalMossyDamageProtectionRadius = serverSettings.natural.mossy.damageProtectionRadius;
    public static boolean naturalMossyFinderBecomesOwner = serverSettings.natural.mossy.finderBecomesOwner;
    public static boolean naturalMossyOwnerCanBuild = serverSettings.natural.mossy.ownerCanBuild;
    public static boolean naturalMossyOwnerCanKill = serverSettings.natural.mossy.ownerCanKill;

    public static int naturalSmoothBlockProtectionRadius = serverSettings.natural.smooth.blockProtectionRadius;
    public static int naturalSmoothDamageProtectionRadius = serverSettings.natural.smooth.damageProtectionRadius;
    public static boolean naturalSmoothFinderBecomesOwner = serverSettings.natural.smooth.finderBecomesOwner;
    public static boolean naturalSmoothOwnerCanBuild = serverSettings.natural.smooth.ownerCanBuild;
    public static boolean naturalSmoothOwnerCanKill = serverSettings.natural.smooth.ownerCanKill;


    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
        configChanged = 2;
    }

    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP && !entity.world.isRemote)
        {
            sendConfig((EntityPlayerMP) entity);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event)
    {
        if (configChanged > 0) configChanged--;
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;
        if (configChanged > 0 && !world.isRemote)
        {
            reset();
            for (EntityPlayer player : world.playerEntities)
            {
                sendConfig((EntityPlayerMP) player);
            }
        }
    }

    @SubscribeEvent
    public static void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        reset();
    }

    private static void sendConfig(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new Network.ConfigPacket(), player);
    }

    private static void reset()
    {
        spawnstoneBlockProtectionRadius = serverSettings.spawnstone.blockProtectionRadius;
        spawnstoneDamageProtectionRadius = serverSettings.spawnstone.damageProtectionRadius;
        spawnstoneEnabled = serverSettings.spawnstone.enabled;
        spawnstoneName = serverSettings.spawnstone.spawnstoneName;

        placedGlobalBlockProtectionRadius = serverSettings.placed.global.blockProtectionRadius;
        placedGlobalDamageProtectionRadius = serverSettings.placed.global.damageProtectionRadius;
        placedGlobalOwnerCanBuild = serverSettings.placed.global.ownerCanBuild;
        placedGlobalOwnerCanKill = serverSettings.placed.global.ownerCanKill;

        placedNonGlobalBlockProtectionRadius = serverSettings.placed.nonGlobal.blockProtectionRadius;
        placedNonGlobalDamageProtectionRadius = serverSettings.placed.nonGlobal.damageProtectionRadius;
        placedNonGlobalOwnerCanBuild = serverSettings.placed.nonGlobal.ownerCanBuild;
        placedNonGlobalOwnerCanKill = serverSettings.placed.nonGlobal.ownerCanKill;

        naturalMossyBlockProtectionRadius = serverSettings.natural.mossy.blockProtectionRadius;
        naturalMossyDamageProtectionRadius = serverSettings.natural.mossy.damageProtectionRadius;
        naturalMossyFinderBecomesOwner = serverSettings.natural.mossy.finderBecomesOwner;
        naturalMossyOwnerCanBuild = serverSettings.natural.mossy.ownerCanBuild;
        naturalMossyOwnerCanKill = serverSettings.natural.mossy.ownerCanKill;

        naturalSmoothBlockProtectionRadius = serverSettings.natural.smooth.blockProtectionRadius;
        naturalSmoothDamageProtectionRadius = serverSettings.natural.smooth.damageProtectionRadius;
        naturalSmoothFinderBecomesOwner = serverSettings.natural.smooth.finderBecomesOwner;
        naturalSmoothOwnerCanBuild = serverSettings.natural.smooth.ownerCanBuild;
        naturalSmoothOwnerCanKill = serverSettings.natural.smooth.ownerCanKill;
    }
}
