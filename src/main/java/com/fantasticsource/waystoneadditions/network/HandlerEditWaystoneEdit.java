package com.fantasticsource.waystoneadditions.network;

import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageEditWaystone;
import net.blay09.mods.waystones.network.message.MessageOpenWaystoneSelection;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.blay09.mods.waystones.worldgen.NameGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class HandlerEditWaystoneEdit implements IMessageHandler<MessageEditWaystone, IMessage>
{
    @Override
    @Nullable
    public IMessage onMessage(final MessageEditWaystone message, final MessageContext ctx)
    {
        NetworkHandler.getThreadListener(ctx).addScheduledTask(() ->
        {
            //Filter creative only
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (WaystoneConfig.general.creativeModeOnly && !player.capabilities.isCreativeMode) return;

            //Filter distance
            World world = player.getEntityWorld();
            BlockPos pos = message.getPos();
            if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) > 100) return;

            //Filter null and non-waystone TEs
            TileEntity tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof TileWaystone)) return;

            //Filter based on allowEveryoneGlobal
            TileWaystone waystone = ((TileWaystone) tileEntity).getParent();
            if (waystone.isGlobal() && !player.capabilities.isCreativeMode && !WaystoneConfig.general.allowEveryoneGlobal)
            {
                ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("waystones:creativeRequired"));
                return;
            }

            //Filter based on globalIsPermanent; I'd rather cancel the entire edit if they try to make it non-global than allow the name change
            if (!player.capabilities.isCreativeMode && SyncedConfig.globalIsPermanent && waystone.isGlobal() && !message.isGlobal())
            {
                ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("waystoneadditions:globalIsPermanent"));
                return;
            }

            //Filter based on ownership
            if (WaystoneConfig.general.restrictRenameToOwner && !waystone.isOwner(player))
            {
                ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("waystoneadditions:notTheOwner"));
                return;
            }

            //Generate random name if keyword is used
            GlobalWaystones globalWaystones = GlobalWaystones.get(player.world);
            String newName = message.getName();
            if (newName.equals("%RANDOM%"))
            {
                newName = NameGenerator.get(world).getName(world.getBiome(pos), world.rand);
                while (globalWaystones.getGlobalWaystone(newName) != null) newName = NameGenerator.get(world).getName(world.getBiome(pos), world.rand);
            }

            //Filter if it would be global and would match the name of an existing global...unless a creative-mode player wants to do it
            if (!(waystone.getWaystoneName().equals(newName) && waystone.isGlobal()) && message.isGlobal() && globalWaystones.getGlobalWaystone(newName) != null && !player.capabilities.isCreativeMode)
            {
                ctx.getServerHandler().player.sendMessage(new TextComponentTranslation("waystones:nameOccupied", newName));
                return;
            }


            //If it was global, remove old global entry for it
            //If it wasn't global, remove it from all players
            WaystoneEntry oldWaystoneEntry = new WaystoneEntry(waystone);
            if (waystone.isGlobal()) globalWaystones.removeGlobalWaystone(oldWaystoneEntry);
            else
            {

            }

            waystone.setWaystoneName(newName);

            WaystoneEntry newWaystone = new WaystoneEntry(waystone);
            if (message.isGlobal() && (player.capabilities.isCreativeMode || WaystoneConfig.general.allowEveryoneGlobal))
            {
                waystone.setGlobal(true);
                newWaystone.setGlobal(true);
                globalWaystones.addGlobalWaystone(newWaystone);
                for (Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                {
                    WaystoneManager.sendPlayerWaystones((EntityPlayer) obj);
                }
            }

            if (!newWaystone.isGlobal())
            {
                WaystoneManager.removePlayerWaystone(player, oldWaystoneEntry);
                WaystoneManager.addPlayerWaystone(player, newWaystone);
                WaystoneManager.sendPlayerWaystones(player);
            }

            if (message.isFromSelectionGui())
            {
                NetworkHandler.channel.sendTo(new MessageOpenWaystoneSelection(WarpMode.WAYSTONE, EnumHand.MAIN_HAND, newWaystone), player);
            }
        });

        return null;
    }
}
