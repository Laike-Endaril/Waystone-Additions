package journeymap.client.api.display;

import net.minecraft.util.math.BlockPos;

public class Waypoint extends Displayable
{
    public Waypoint(String modid, String displayName, int dimension, BlockPos position)
    {
        super(modid, displayName);
    }

    @Override
    public int getDisplayOrder()
    {
        return 0;
    }
}
