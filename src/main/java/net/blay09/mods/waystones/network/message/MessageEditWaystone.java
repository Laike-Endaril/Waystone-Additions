package net.blay09.mods.waystones.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageEditWaystone implements IMessage {

    private BlockPos pos;
    private String name;
    private boolean isGlobal;
    private boolean fromSelectionGui;

    public MessageEditWaystone() {
    }

    public MessageEditWaystone(BlockPos pos, String name, boolean isGlobal, boolean fromSelectionGui) {
        this.pos = pos;
        this.name = name;
        this.isGlobal = isGlobal;
        this.fromSelectionGui = fromSelectionGui;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        name = ByteBufUtils.readUTF8String(buf);
        isGlobal = buf.readBoolean();
        fromSelectionGui = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBoolean(isGlobal);
        buf.writeBoolean(fromSelectionGui);
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public boolean isFromSelectionGui() {
        return fromSelectionGui;
    }
}
