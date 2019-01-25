package com.fantasticsource.waystoneadditions;

import net.blay09.mods.waystones.block.TileWaystone;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.UUID;

public class TileWaystoneEdit extends TileWaystone
{
    private static Field customTileDataField;

    static
    {
        initReflections();
    }


    private boolean isDummy;
    private String waystoneName = "";
    private UUID owner;
    private boolean isGlobal;
    private boolean wasGenerated = true;
    private boolean isMossy;

    public TileWaystoneEdit(boolean isDummy)
    {
        super(isDummy);
        this.isDummy = isDummy;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
        ResourceLocation resourcelocation = new ResourceLocation("waystones", "waystone");

        tagCompound.setString("id", resourcelocation.toString());
        tagCompound.setInteger("x", pos.getX());
        tagCompound.setInteger("y", pos.getY());
        tagCompound.setInteger("z", pos.getZ());

        NBTTagCompound customTileData = null;
        try
        {
            customTileData = (NBTTagCompound) customTileDataField.get(this);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(202, true);
        }
        if (customTileData != null) tagCompound.setTag("ForgeData", customTileData);

        CapabilityDispatcher capabilities = ForgeEventFactory.gatherCapabilities(this);
        if (capabilities != null) tagCompound.setTag("ForgeCaps", capabilities.serializeNBT());

        tagCompound.setBoolean("IsDummy", isDummy);
        if (!isDummy)
        {
            if (!waystoneName.equals("%RANDOM%"))
            {
                tagCompound.setString("WaystoneName", waystoneName);
                tagCompound.setBoolean("WasGenerated", wasGenerated);
            }
            else
            {
                tagCompound.setBoolean("WasGenerated", true);
            }

            if (owner != null)
            {
                tagCompound.setTag("Owner", NBTUtil.createUUIDTag(owner));
            }

            tagCompound.setBoolean("IsGlobal", isGlobal);
            tagCompound.setBoolean("IsMossy", isMossy);
        }
        return tagCompound;
    }


    private static void initReflections()
    {
        try
        {
            customTileDataField = TileEntity.class.getDeclaredField("customTileData");
            customTileDataField.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            FMLCommonHandler.instance().exitJava(201, true);
            e.printStackTrace();
        }
    }
}
