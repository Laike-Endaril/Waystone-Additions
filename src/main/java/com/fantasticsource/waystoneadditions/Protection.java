package com.fantasticsource.waystoneadditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.waystoneadditions.WaystoneAdditionsConfig.serverSettings;

public class Protection
{
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void damage(LivingHurtEvent event)
    {
        Entity source = event.getSource().getTrueSource();
        if (source == null) source = event.getSource().getImmediateSource();
        if (isDamageProtected(event.getEntityLiving(), source))
        {
            event.setAmount(0);
            event.setCanceled(true);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockBreakSpeedOverride(PlayerEvent.BreakSpeed event)
    {
        //This prevents the player from making progress on breaking a block instead of preventing the breaking itself; looks and acts much cleaner this way
        //It also catches instant breaking, at least in the case of punching flowers and whatnot.  Must simply use a high break speed by default
        //TODO Some mod items may bypass this.  Exchangers and psi tools come to mind
        if (isBuildProtected(event.getPos(), event.getEntity()))
        {
            event.setCanceled(true);
            event.setNewSpeed(0);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        //This prevents the player from using an item.  Might not catch everything but when it works, it works nicely (it doesn't cause inventory desync)
        //TODO For some reason, this does not block buckets' interactions with fluids!
        if (isBuildProtected(new BlockPos(event.getHitVec()), event.getEntity()))
        {
            System.out.println("Blocked in filter 1?");
            event.setUseItem(Event.Result.DENY);
            event.setUseBlock(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockPlace(BlockEvent.PlaceEvent event)
    {
        //Might catch some things RightClickBlock doesn't catch, but not as nice for when blocks are placed from inventory, because it causes inventory desync
        if (isBuildProtected(event.getPos(), event.getPlayer()))
        {
            System.out.println("Blocked in filter 2");
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockMultiPlace(BlockEvent.MultiPlaceEvent event)
    {
        //I imagine this is similar to PlaceEvent, but for when multiple blocks are placed at once.  Might catch doors and beds in vanilla?
        for (BlockSnapshot snapshot : event.getReplacedBlockSnapshots())
        {
            if (isBuildProtected(snapshot.getPos(), event.getPlayer()))
            {
                System.out.println("Blocked in filter 3");
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
                break;
            }
        }
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

    private static boolean isBuildProtected(BlockPos pos, Entity entity)
    {
        if (entity == null) return true;
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) return false;

        for (TileEntity tileEntity : entity.world.loadedTileEntityList)
        {
            if (tileEntity instanceof TileWaystoneEdit)
            {
                TileWaystoneEdit waystone = (TileWaystoneEdit) tileEntity;
                int radius;

                if (waystone.isSpawnstone) radius = serverSettings.spawnstone.blockProtectionRadius;
                else if (waystone.wasGenerated())
                {
                    if (waystone.isMossy())
                    {
                        if (serverSettings.natural.mossy.ownerCanKill && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.natural.mossy.blockProtectionRadius;
                    }
                    else
                    {
                        if (serverSettings.natural.smooth.ownerCanKill && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.natural.smooth.blockProtectionRadius;
                    }
                }
                else
                {
                    if (waystone.isGlobal())
                    {
                        if (serverSettings.placed.global.ownerCanKill && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.placed.global.blockProtectionRadius;
                    }
                    else
                    {
                        if (serverSettings.placed.nonGlobal.ownerCanKill && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = serverSettings.placed.nonGlobal.blockProtectionRadius;
                    }
                }

                if (radius < 1) continue;

                BlockPos pos2 = waystone.getPos();

                if (Math.abs(pos.getX() - pos2.getX()) <= radius && Math.abs(pos.getY() - pos2.getY()) <= radius && Math.abs(pos.getZ() - pos2.getZ()) <= radius) return true;
            }
        }

        return false;
    }
}
