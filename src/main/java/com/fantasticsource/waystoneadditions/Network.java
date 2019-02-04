package com.fantasticsource.waystoneadditions;

import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.waystoneadditions.config.WaystoneAdditionsConfig.serverSettings;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(WaystoneAdditions.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ConfigPacketHandler.class, ConfigPacket.class, discriminator++, Side.CLIENT);
    }


    public static class ConfigPacket implements IMessage
    {
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
        @Override
        public IMessage onMessage(ConfigPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
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
