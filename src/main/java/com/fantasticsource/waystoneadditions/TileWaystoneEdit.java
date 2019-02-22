package com.fantasticsource.waystoneadditions;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.waystoneadditions.compat.Compat;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.util.UUID;

public class TileWaystoneEdit extends TileWaystone
{
    private static Field ownerField;

    static
    {
        try
        {
            ownerField = ReflectionTool.getField(TileWaystone.class, "owner");
        }
        catch (Exception e)
        {
            MCTools.crash(e, 201, true);
        }
    }


    public boolean isSpawnstone = false;

    public TileWaystoneEdit()
    {
        super();
    }

    public TileWaystoneEdit(boolean isDummy)
    {
        super(isDummy);
    }

    public TileWaystoneEdit(boolean isDummy, boolean isSpawnstone)
    {
        super(isDummy);
        this.isSpawnstone = isSpawnstone;
        if (isSpawnstone && !isDummy)
        {
            WaystoneAdditions.spawnstone = this;

            if (world.isRemote && Compat.journeymap && WaystoneConfig.compat.createJourneyMapWaypoint)
            {
                WaystoneWaypointHandler.makeWaystoneWaypoint(getWaystoneName(), world.provider.getDimension(), pos);
            }
        }
    }

    public UUID getOwner()
    {
        try
        {
            return (UUID) (isDummy() ? ownerField.get(getParent()) : ownerField.get(this));
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 202, false);
        }
        return null;
    }

    @Override
    public void setOwner(EntityPlayer owner)
    {
        super.setOwner(owner);
    }

    public void setOwnerDirect(UUID owner)
    {
        try
        {
            ownerField.set(this, owner);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 203, false);
        }
    }

    @Override
    public boolean isOwner(EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode) return true;
        UUID owner = getOwner();
        return !isSpawnstone && (owner == null || owner.equals(player.getUniqueID()));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setBoolean("spawnstone", isSpawnstone);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        isSpawnstone = compound.getBoolean("spawnstone");
    }

    public boolean isDummy()
    {
        return getParent() != this;
    }

    @Override
    public boolean isGlobal()
    {
        return isDummy() ? getParent().isGlobal() : super.isGlobal();
    }

    @Override
    public boolean isMossy()
    {
        return isDummy() ? getParent().isMossy() : super.isMossy();
    }

    @Override
    public boolean wasGenerated()
    {
        return isDummy() ? getParent().wasGenerated() : super.wasGenerated();
    }

    @Override
    public TileWaystoneEdit getParent()
    {
        return (TileWaystoneEdit) super.getParent();
    }
}
