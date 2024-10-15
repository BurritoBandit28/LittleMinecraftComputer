package io.github.burritobandit28.lmc.block_entities;

import com.jcraft.jorbis.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface InputDevice {

    void handleInput(World world, BlockState state, ServerPlayerEntity player);
}
