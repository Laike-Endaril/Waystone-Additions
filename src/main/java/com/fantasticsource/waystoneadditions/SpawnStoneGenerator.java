package com.fantasticsource.waystoneadditions;

import com.fantasticsource.waystoneadditions.config.SyncedConfig;
import net.blay09.mods.waystones.GlobalWaystones;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import static com.fantasticsource.waystoneadditions.WaystoneAdditions.spawnstone;

public class SpawnStoneGenerator implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!SyncedConfig.spawnstoneEnabled || world.provider.getDimension() != 0) return;

        BlockPos pos = world.getTopSolidOrLiquidBlock(world.getSpawnPoint()).north();
        BlockPos pos2 = world.getTopSolidOrLiquidBlock(pos);

        Chunk chunk = world.getChunkFromBlockCoords(pos);
        if (world.getChunkFromChunkCoords(chunkX, chunkZ) != chunk) return;

        world.setBlockState(pos, Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, true).withProperty(BlockWaystone.FACING, EnumFacing.SOUTH), 2);
        world.setBlockState(pos.up(), Waystones.blockWaystone.getDefaultState().withProperty(BlockWaystone.BASE, false).withProperty(BlockWaystone.FACING, EnumFacing.SOUTH), 2);
        TileWaystoneEdit tileWaystone = (TileWaystoneEdit) world.getTileEntity(pos); //Get TE before we change the value of pos

        world.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
        pos = pos.down().down();
        while (pos.getY() > pos2.getY())
        {
            world.setBlockState(pos, Blocks.STONE.getDefaultState());
            pos = pos.down();
        }


        //Set global
        tileWaystone.isSpawnstone = true;
        spawnstone = tileWaystone;
        spawnstone.setWorld(world);
        tileWaystone.setGlobal(true);
        tileWaystone.setWaystoneName(SyncedConfig.spawnstoneName);

        WaystoneEntry waystoneEntry = new WaystoneEntry(tileWaystone);
        waystoneEntry.setGlobal(true);

        GlobalWaystones.get(world).addGlobalWaystone(waystoneEntry);
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            WaystoneManager.sendPlayerWaystones(player);
        }
    }
}
