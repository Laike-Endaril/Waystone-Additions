package com.fantasticsource.waystoneadditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.waystoneadditions.WaystoneAdditionsConfig.serverSettings;

public class Protection
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damage(LivingHurtEvent event)
    {
        Entity source = event.getSource().getTrueSource();
        if (source == null) source = event.getSource().getImmediateSource();
        if (isDamageProtected(event.getEntityLiving(), source)) event.setCanceled(true);
    }

    private static boolean isDamageProtected(EntityLivingBase target, Entity source)
    {
        if (source instanceof EntityPlayer && ((EntityPlayer) source).capabilities.isCreativeMode) return false;

        for (TileEntity tileEntity : target.world.loadedTileEntityList)
        {
            if (tileEntity instanceof TileWaystoneEdit)
            {
                TileWaystoneEdit waystone = (TileWaystoneEdit) tileEntity;
                int radius;

                if (waystone.isSpawnstone) radius = serverSettings.spawnstone.damageProtectionRadius;
                else if (waystone.wasGenerated())
                {
                    if (waystone.isMossy())
                    {
                        if (serverSettings.natural.mossy.ownerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.natural.mossy.damageProtectionRadius;
                    }
                    else
                    {
                        if (serverSettings.natural.smooth.ownerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.natural.smooth.damageProtectionRadius;
                    }
                }
                else
                {
                    if (waystone.isGlobal())
                    {
                        if (serverSettings.placed.global.ownerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.placed.global.damageProtectionRadius;
                    }
                    else
                    {
                        if (serverSettings.placed.nonGlobal.ownerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.placed.nonGlobal.damageProtectionRadius;
                    }
                }

                if (radius < 1) continue;

                BlockPos pos1 = target.getPosition();
                BlockPos pos2 = waystone.getPos();

                if (Math.abs(pos1.getX() - pos2.getX()) <= radius && Math.abs(pos1.getY() - pos2.getY()) <= radius && Math.abs(pos1.getZ() - pos2.getZ()) <= radius) return true;
            }
        }

        return false;
    }
}
