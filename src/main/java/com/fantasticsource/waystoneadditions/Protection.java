package com.fantasticsource.waystoneadditions;

import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

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
    public static void blockBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        //This prevents the player from making progress on breaking a block instead of preventing the breaking itself; looks and acts much cleaner this way
        //It also catches instant breaking, at least in the case of punching flowers and whatnot.  Must simply use a high break speed by default
        if (isBuildProtected(event.getPos(), event.getEntity()))
        {
            event.setNewSpeed(0);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        //This prevents the player from using an item.  Might not catch everything but when it works, it works nicely (it doesn't cause inventory desync)
        //This does not detect buckets!  Buckets are handled in the FillBucketEvent (which should be named UseBucketEvent)
        if (isBuildProtected(new BlockPos(event.getHitVec()), event.getEntity()))
        {
            event.setUseItem(Event.Result.DENY);
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void bucketUse(FillBucketEvent event)
    {
        //Prevents bucket usage in the protected zones
        //This event is not just for filling buckets; it is also for emptying them
        RayTraceResult rayTrace = event.getTarget();
        if (rayTrace == null || isBuildProtected(new BlockPos(rayTrace.hitVec), event.getEntity()))
        {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        //This prevents the player from using an item.  Might not catch everything but when it works, it works nicely (it doesn't cause inventory desync)
        //This does not detect buckets!  Buckets are handled in the FillBucketEvent (which should be named UseBucketEvent)
        if (isBuildProtected(new BlockPos(event.getHitVec()), event.getEntity()))
        {
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
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockMultiPlace(BlockEvent.MultiPlaceEvent event)
    {
        //I imagine this is similar to PlaceEvent, but for when multiple blocks are placed at once
        for (BlockSnapshot snapshot : event.getReplacedBlockSnapshots())
        {
            if (isBuildProtected(snapshot.getPos(), event.getPlayer()))
            {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
                break;
            }
        }
    }


    private static boolean isDamageProtected(EntityLivingBase target, Entity source)
    {
        if (source instanceof EntityPlayer && ((EntityPlayer) source).capabilities.isCreativeMode) return false;

        List<TileEntity> list = target.world.loadedTileEntityList;
        for (TileEntity tileEntity : list.toArray(new TileEntity[list.size()]))
        {
            if (tileEntity instanceof TileWaystoneEdit)
            {
                TileWaystoneEdit waystone = (TileWaystoneEdit) tileEntity;
                int radius;

                if (waystone.isSpawnstone) radius = SyncedConfig.spawnstoneDamageProtectionRadius;
                else if (waystone.wasGenerated())
                {
                    if (waystone.isMossy())
                    {
                        if (SyncedConfig.naturalMossyOwnerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.naturalMossyDamageProtectionRadius;
                    }
                    else
                    {
                        if (SyncedConfig.naturalSmoothOwnerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.naturalSmoothDamageProtectionRadius;
                    }
                }
                else
                {
                    if (waystone.isGlobal())
                    {
                        if (SyncedConfig.placedGlobalOwnerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.placedGlobalDamageProtectionRadius;
                    }
                    else
                    {
                        if (SyncedConfig.placedNonGlobalOwnerCanKill && source != null && source.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.placedNonGlobalDamageProtectionRadius;
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

        int radius;
        List<TileEntity> list = entity.world.loadedTileEntityList;
        for (TileEntity tileEntity : list.toArray(new TileEntity[list.size()]))
        {
            if (tileEntity instanceof TileWaystoneEdit)
            {
                TileWaystoneEdit waystone = (TileWaystoneEdit) tileEntity;

                if (waystone.isSpawnstone) radius = SyncedConfig.spawnstoneBlockProtectionRadius;
                else if (waystone.wasGenerated())
                {
                    if (waystone.isMossy())
                    {
                        if (SyncedConfig.naturalMossyOwnerCanBuild && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.naturalMossyBlockProtectionRadius;
                    }
                    else
                    {
                        if (SyncedConfig.naturalSmoothOwnerCanBuild && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.naturalSmoothBlockProtectionRadius;
                    }
                }
                else
                {
                    if (waystone.isGlobal())
                    {
                        if (SyncedConfig.placedGlobalOwnerCanBuild && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.placedGlobalBlockProtectionRadius;
                    }
                    else
                    {
                        if (SyncedConfig.placedNonGlobalOwnerCanBuild && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.placedNonGlobalBlockProtectionRadius;
                    }
                }

                if (radius < 1) continue;

                BlockPos pos2 = waystone.getPos();

                if (Math.abs(pos.getX() - pos2.getX()) <= radius && Math.abs(pos.getY() - pos2.getY()) <= radius && Math.abs(pos.getZ() - pos2.getZ()) <= radius)
                {
                    System.out.println(entity.world.isRemote ? "Client" : "Server");
                    System.out.println(waystone.getPos());
                    System.out.println(entity.getUniqueID().equals(waystone.getOwner()));
                    System.out.println(entity.getUniqueID().equals(waystone.getParent().getOwner()));
                    System.out.println(radius);
                    System.out.println(SyncedConfig.naturalMossyOwnerCanBuild);
                    System.out.println("===============================");

                    return true;
                }
            }
        }

        return false;
    }
}
