package com.fantasticsource.waystoneadditions;

import net.blay09.mods.waystones.block.TileWaystone;
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


    public TileWaystoneEdit()
    {
        super();
    }

    public TileWaystoneEdit(boolean isDummy)
    {
        super(isDummy);
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
}
