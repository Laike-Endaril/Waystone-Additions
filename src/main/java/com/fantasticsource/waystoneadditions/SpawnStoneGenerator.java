package com.fantasticsource.waystoneadditions;

import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class SpawnStoneGenerator implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!WaystoneAdditionsConfig.serverSettings.spawnstone.enabled || world.provider.getDimension() != 0) return;

        BlockPos pos = world.getTopSolidOrLiquidBlock(world.getSpawnPoint()).north();
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        if (world.getChunkFromChunkCoords(chunkX, chunkZ) != chunk) return;

        BlockPos posUp = pos.up();

        world.setBlockState(pos, Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, true).withProperty(BlockWaystone.FACING, EnumFacing.SOUTH), 2);
        world.setBlockState(posUp, Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, false).withProperty(BlockWaystone.FACING, EnumFacing.SOUTH), 2);

        TileWaystoneEdit tileWaystone = (TileWaystoneEdit) world.getTileEntity(pos);
        WaystoneEntry waystoneEntry = new WaystoneEntry(tileWaystone);

        //Set global
        tileWaystone.setGlobal(true);
        waystoneEntry.setGlobal(true);
        GlobalWaystones.get(world).addGlobalWaystone(waystoneEntry);
        for (Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            WaystoneManager.sendPlayerWaystones((EntityPlayer) obj);
        }

        tileWaystone.setWaystoneName(WaystoneAdditionsConfig.serverSettings.spawnstone.spawnstoneName);
        tileWaystone.isSpawnstone = true;
    }


}
