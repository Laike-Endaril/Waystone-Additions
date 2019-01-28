package com.fantasticsource.waystoneadditions;

import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.UUID;

public class TileWaystoneEdit extends TileWaystone
{
    private static Field ownerField;

    static
    {
        initReflections();
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
    }

    private static void initReflections()
    {
        try
        {
            ownerField = TileWaystone.class.getDeclaredField("owner");
            ownerField.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            FMLCommonHandler.instance().exitJava(201, true);
            e.printStackTrace();
        }
    }

    public UUID getOwner()
    {
        try
        {
            return (UUID) ownerField.get(this);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(202, false);
        }
        return null;
    }

    @Override
    public boolean isOwner(EntityPlayer player)
    {
        return super.isOwner(player) && (!isSpawnstone || player.capabilities.isCreativeMode);
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
}
