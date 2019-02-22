package com.fantasticsource.waystoneadditions.protection;

import com.fantasticsource.waystoneadditions.TileWaystoneEdit;
import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protection
{
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void damage(LivingHurtEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;

            Entity source = event.getSource().getTrueSource();
            if (source == null) source = event.getSource().getImmediateSource();

            if (isDamageProtected(player, source))
            {
                event.setAmount(0);
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        //This prevents the player from making progress on breaking a block instead of preventing the breaking itself; looks and acts much cleaner this way
        //It also catches instant breaking, at least in the case of punching flowers and whatnot.  Must simply use a high break speed by default
        Entity entity = event.getEntity();
        if (isBuildProtected(entity.world, event.getPos(), entity))
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
        Vec3d vec = event.getHitVec();
        if (vec == null || isBuildProtected(event.getWorld(), new BlockPos(event.getHitVec()), event.getEntity()))
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
        if (rayTrace == null || isBuildProtected(event.getWorld(), new BlockPos(rayTrace.hitVec), event.getEntity()))
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
        Vec3d vec = event.getHitVec();
        if (vec == null || isBuildProtected(event.getWorld(), new BlockPos(event.getHitVec()), event.getEntity()))
        {
            event.setUseItem(Event.Result.DENY);
            event.setUseBlock(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockPlace(BlockEvent.PlaceEvent event)
    {
        //Might catch some things RightClickBlock doesn't catch, but not as nice for when blocks are placed from inventory, because it causes inventory desync
        if (isBuildProtected(event.getWorld(), event.getPos(), event.getPlayer()))
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
            if (isBuildProtected(snapshot.getWorld(), snapshot.getPos(), event.getPlayer()))
            {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
                break;
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDetonation(ExplosionEvent.Detonate event)
    {
        World world = event.getWorld();
        List<BlockPos> list = event.getAffectedBlocks();
        for(BlockPos pos : list.toArray(new BlockPos[list.size()]))
        {
            if (isBuildProtected(world, pos, null)) list.remove(pos);
        }
    }


    public static boolean isDamageProtected(EntityPlayer target, Entity source)
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

    public static boolean buildProtectionOwnersMatch(World world, BlockPos pos1, BlockPos pos2)
    {
        ArrayList<UUID> owners1 = buildProtectionOwners(world, pos1), owners2 = buildProtectionOwners(world, pos2);
        if (owners1.size() != owners2.size()) return false;

        for(UUID id : owners1)
        {
            if (!owners2.contains(id)) return false;
        }

        return true;
    }

    public static ArrayList<UUID> buildProtectionOwners(World world, BlockPos pos)
    {
        ArrayList<UUID> result = new ArrayList<>();

        UUID owner;
        for(TileWaystoneEdit waystone : buildProtectingWaystones(world, pos))
        {
            owner = waystone.getOwner();
            if (!result.contains(owner)) result.add(owner);
        }

        return result;
    }

    public static ArrayList<TileWaystoneEdit> buildProtectingWaystones(World world, BlockPos pos)
    {
        ArrayList<TileWaystoneEdit> result = new ArrayList<>();

        int radius;
        List<TileEntity> list = world.loadedTileEntityList;
        for (TileEntity tileEntity : list.toArray(new TileEntity[list.size()]))
        {
            if (tileEntity instanceof TileWaystoneEdit)
            {
                TileWaystoneEdit waystone = (TileWaystoneEdit) tileEntity;

                if (waystone.isSpawnstone) radius = SyncedConfig.spawnstoneBlockProtectionRadius;
                else if (waystone.wasGenerated())
                {
                    if (waystone.isMossy()) radius = SyncedConfig.naturalMossyBlockProtectionRadius;
                    else radius = SyncedConfig.naturalSmoothBlockProtectionRadius;
                }
                else
                {
                    if (waystone.isGlobal()) radius = SyncedConfig.placedGlobalBlockProtectionRadius;
                    else radius = SyncedConfig.placedNonGlobalBlockProtectionRadius;
                }

                if (radius < 1) continue;

                BlockPos pos2 = waystone.getPos();

                if (Math.abs(pos.getX() - pos2.getX()) <= radius && Math.abs(pos.getY() - pos2.getY()) <= radius && Math.abs(pos.getZ() - pos2.getZ()) <= radius) result.add(waystone);
            }
        }

        return result;
    }

    public static boolean isBuildProtected(World world, BlockPos pos, Entity entity)
    {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) return false;

        int radius;
        List<TileEntity> list = world.loadedTileEntityList;
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
                        if (SyncedConfig.naturalMossyOwnerCanBuild && entity != null && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.naturalMossyBlockProtectionRadius;
                    }
                    else
                    {
                        if (SyncedConfig.naturalSmoothOwnerCanBuild && entity != null && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.naturalSmoothBlockProtectionRadius;
                    }
                }
                else
                {
                    if (waystone.isGlobal())
                    {
                        if (SyncedConfig.placedGlobalOwnerCanBuild && entity != null && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.placedGlobalBlockProtectionRadius;
                    }
                    else
                    {
                        if (SyncedConfig.placedNonGlobalOwnerCanBuild && entity != null && entity.getUniqueID().equals(waystone.getOwner())) radius = -1;
                        else radius = SyncedConfig.placedNonGlobalBlockProtectionRadius;
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
