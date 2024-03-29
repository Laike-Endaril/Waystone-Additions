package com.fantasticsource.waystoneadditions.network;

import com.fantasticsource.waystoneadditions.TileWaystoneEdit;
import com.fantasticsource.waystoneadditions.WaystoneAdditions;
import com.fantasticsource.waystoneadditions.WaystoneWaypointHandler;
import com.fantasticsource.waystoneadditions.compat.Compat;
import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import io.netty.buffer.ByteBuf;
import net.blay09.mods.waystones.WaystoneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

import static com.fantasticsource.waystoneadditions.config.WaystoneAdditionsConfig.serverSettings;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(WaystoneAdditions.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ConfigPacketHandler.class, ConfigPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(WaystonePacketHandler.class, WaystonePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(WaystoneWaypointPacketHandler.class, WaystoneWaypointPacket.class, discriminator++, Side.CLIENT);
    }


    public static class WaystoneWaypointPacket implements IMessage
    {
        private String name;
        private int dimension;
        private BlockPos pos;

        public WaystoneWaypointPacket()
        {

        }

        public WaystoneWaypointPacket(TileWaystoneEdit waystone)
        {
            name = waystone.getWaystoneName();
            waystone.getWorld().provider.getDimension();
            pos = waystone.getPos();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, name);
            buf.writeInt(dimension);
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            name = ByteBufUtils.readUTF8String(buf);
            dimension = buf.readInt();
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        }
    }

    public static class WaystoneWaypointPacketHandler implements IMessageHandler<WaystoneWaypointPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(WaystoneWaypointPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    if (Compat.journeymap && WaystoneConfig.compat.createJourneyMapWaypoint)
                    {
                        WaystoneWaypointHandler.makeWaystoneWaypoint(packet.name, packet.dimension, packet.pos);
                    }
                });
            }

            return null;
        }
    }


    public static class WaystonePacket implements IMessage
    {
        private TileWaystoneEdit waystone;

        private UUID owner;
        private BlockPos pos;

        public WaystonePacket()
        {

        }

        public WaystonePacket(TileWaystoneEdit waystone)
        {
            this.waystone = waystone;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            BlockPos pos = waystone.getPos();
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());

            UUID owner = waystone.getOwner();
            buf.writeBoolean(owner != null);
            if (owner != null)
            {
                buf.writeLong(owner.getMostSignificantBits());
                buf.writeLong(owner.getLeastSignificantBits());
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
            if (buf.readBoolean()) owner = new UUID(buf.readLong(), buf.readLong());
            else owner = null;
        }
    }

    public static class WaystonePacketHandler implements IMessageHandler<WaystonePacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(WaystonePacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    World world = player.world;
                    TileWaystoneEdit waystone = (TileWaystoneEdit) world.getTileEntity(packet.pos);
                    if (waystone != null)
                    {
                        waystone.setOwnerDirect(packet.owner);
                    }
                });
            }

            return null;
        }
    }


    public static class ConfigPacket implements IMessage
    {
        private boolean breakable;
        private boolean dropItem;
        private boolean globalIsPermanent;

        private int spawnstoneBlockProtectionRadius;
        private int spawnstoneDamageProtectionRadius;
        private boolean spawnstoneEnabled;
        private String spawnstoneName;

        private int placedGlobalBlockProtectionRadius;
        private int placedGlobalDamageProtectionRadius;
        private boolean placedGlobalOwnerCanBuild;
        private boolean placedGlobalOwnerCanKill;

        private int placedNonGlobalBlockProtectionRadius;
        private int placedNonGlobalDamageProtectionRadius;
        private boolean placedNonGlobalOwnerCanBuild;
        private boolean placedNonGlobalOwnerCanKill;

        private int naturalMossyBlockProtectionRadius;
        private int naturalMossyDamageProtectionRadius;
        private boolean naturalMossyFinderBecomesOwner;
        private boolean naturalMossyOwnerCanBuild;
        private boolean naturalMossyOwnerCanKill;

        private int naturalSmoothBlockProtectionRadius;
        private int naturalSmoothDamageProtectionRadius;
        private boolean naturalSmoothFinderBecomesOwner;
        private boolean naturalSmoothOwnerCanBuild;
        private boolean naturalSmoothOwnerCanKill;

        public ConfigPacket() //Required; probably for when the packet is received
        {
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(serverSettings.breakable);
            buf.writeBoolean(serverSettings.dropItem);
            buf.writeBoolean(serverSettings.globalIsPermanent);

            buf.writeInt(serverSettings.spawnstone.blockProtectionRadius);
            buf.writeInt(serverSettings.spawnstone.damageProtectionRadius);
            buf.writeBoolean(serverSettings.spawnstone.enabled);
            ByteBufUtils.writeUTF8String(buf, serverSettings.spawnstone.spawnstoneName);

            buf.writeInt(serverSettings.placed.global.blockProtectionRadius);
            buf.writeInt(serverSettings.placed.global.damageProtectionRadius);
            buf.writeBoolean(serverSettings.placed.global.ownerCanBuild);
            buf.writeBoolean(serverSettings.placed.global.ownerCanKill);

            buf.writeInt(serverSettings.placed.nonGlobal.blockProtectionRadius);
            buf.writeInt(serverSettings.placed.nonGlobal.damageProtectionRadius);
            buf.writeBoolean(serverSettings.placed.nonGlobal.ownerCanBuild);
            buf.writeBoolean(serverSettings.placed.nonGlobal.ownerCanKill);

            buf.writeInt(serverSettings.natural.mossy.blockProtectionRadius);
            buf.writeInt(serverSettings.natural.mossy.damageProtectionRadius);
            buf.writeBoolean(serverSettings.natural.mossy.finderBecomesOwner);
            buf.writeBoolean(serverSettings.natural.mossy.ownerCanBuild);
            buf.writeBoolean(serverSettings.natural.mossy.ownerCanKill);

            buf.writeInt(serverSettings.natural.smooth.blockProtectionRadius);
            buf.writeInt(serverSettings.natural.smooth.damageProtectionRadius);
            buf.writeBoolean(serverSettings.natural.smooth.finderBecomesOwner);
            buf.writeBoolean(serverSettings.natural.smooth.ownerCanBuild);
            buf.writeBoolean(serverSettings.natural.smooth.ownerCanKill);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            breakable = buf.readBoolean();
            dropItem = buf.readBoolean();
            globalIsPermanent = buf.readBoolean();

            spawnstoneBlockProtectionRadius = buf.readInt();
            spawnstoneDamageProtectionRadius = buf.readInt();
            spawnstoneEnabled = buf.readBoolean();
            spawnstoneName = ByteBufUtils.readUTF8String(buf);

            placedGlobalBlockProtectionRadius = buf.readInt();
            placedGlobalDamageProtectionRadius = buf.readInt();
            placedGlobalOwnerCanBuild = buf.readBoolean();
            placedGlobalOwnerCanKill = buf.readBoolean();

            placedNonGlobalBlockProtectionRadius = buf.readInt();
            placedNonGlobalDamageProtectionRadius = buf.readInt();
            placedNonGlobalOwnerCanBuild = buf.readBoolean();
            placedNonGlobalOwnerCanKill = buf.readBoolean();

            naturalMossyBlockProtectionRadius = buf.readInt();
            naturalMossyDamageProtectionRadius = buf.readInt();
            naturalMossyFinderBecomesOwner = buf.readBoolean();
            naturalMossyOwnerCanBuild = buf.readBoolean();
            naturalMossyOwnerCanKill = buf.readBoolean();

            naturalSmoothBlockProtectionRadius = buf.readInt();
            naturalSmoothDamageProtectionRadius = buf.readInt();
            naturalSmoothFinderBecomesOwner = buf.readBoolean();
            naturalSmoothOwnerCanBuild = buf.readBoolean();
            naturalSmoothOwnerCanKill = buf.readBoolean();
        }
    }

    public static class ConfigPacketHandler implements IMessageHandler<ConfigPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(ConfigPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    SyncedConfig.breakable = packet.breakable;
                    SyncedConfig.dropItem = packet.dropItem;
                    SyncedConfig.globalIsPermanent = packet.globalIsPermanent;

                    SyncedConfig.spawnstoneBlockProtectionRadius = packet.spawnstoneBlockProtectionRadius;
                    SyncedConfig.spawnstoneDamageProtectionRadius = packet.spawnstoneDamageProtectionRadius;
                    SyncedConfig.spawnstoneEnabled = packet.spawnstoneEnabled;
                    SyncedConfig.spawnstoneName = packet.spawnstoneName;

                    SyncedConfig.placedGlobalBlockProtectionRadius = packet.placedGlobalBlockProtectionRadius;
                    SyncedConfig.placedGlobalDamageProtectionRadius = packet.placedGlobalDamageProtectionRadius;
                    SyncedConfig.placedGlobalOwnerCanBuild = packet.placedGlobalOwnerCanBuild;
                    SyncedConfig.placedGlobalOwnerCanKill = packet.placedGlobalOwnerCanKill;

                    SyncedConfig.placedNonGlobalBlockProtectionRadius = packet.placedNonGlobalBlockProtectionRadius;
                    SyncedConfig.placedNonGlobalDamageProtectionRadius = packet.placedNonGlobalDamageProtectionRadius;
                    SyncedConfig.placedNonGlobalOwnerCanBuild = packet.placedNonGlobalOwnerCanBuild;
                    SyncedConfig.placedNonGlobalOwnerCanKill = packet.placedNonGlobalOwnerCanKill;

                    SyncedConfig.naturalMossyBlockProtectionRadius = packet.naturalMossyBlockProtectionRadius;
                    SyncedConfig.naturalMossyDamageProtectionRadius = packet.naturalMossyDamageProtectionRadius;
                    SyncedConfig.naturalMossyFinderBecomesOwner = packet.naturalMossyFinderBecomesOwner;
                    SyncedConfig.naturalMossyOwnerCanBuild = packet.naturalMossyOwnerCanBuild;
                    SyncedConfig.naturalMossyOwnerCanKill = packet.naturalMossyOwnerCanKill;

                    SyncedConfig.naturalSmoothBlockProtectionRadius = packet.naturalSmoothBlockProtectionRadius;
                    SyncedConfig.naturalSmoothDamageProtectionRadius = packet.naturalSmoothDamageProtectionRadius;
                    SyncedConfig.naturalSmoothFinderBecomesOwner = packet.naturalSmoothFinderBecomesOwner;
                    SyncedConfig.naturalSmoothOwnerCanBuild = packet.naturalSmoothOwnerCanBuild;
                    SyncedConfig.naturalSmoothOwnerCanKill = packet.naturalSmoothOwnerCanKill;
                });
            }

            return null;
        }
    }
}
