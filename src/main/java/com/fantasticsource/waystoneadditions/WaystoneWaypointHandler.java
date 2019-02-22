package com.fantasticsource.waystoneadditions;

import com.fantasticsource.mctools.MCTools;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.waystones.Waystones;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ClientPlugin
public class WaystoneWaypointHandler implements IClientPlugin
{
    private static IClientAPI api;

    @SubscribeEvent
    public static void makeWaystoneWaypoint(String name, int dimension, BlockPos pos)
    {
        try
        {
            api.show(new Waypoint(Waystones.MOD_ID, name, dimension, pos));
        }
        catch (Exception e)
        {
            MCTools.crash(e, 204, false);
        }
    }

    @Override
    public void initialize(IClientAPI iClientAPI)
    {
        api = iClientAPI;
    }

    @Override
    public String getModId()
    {
        return WaystoneAdditions.MODID;
    }

    @Override
    public void onEvent(ClientEvent clientEvent)
    {
    }
}
