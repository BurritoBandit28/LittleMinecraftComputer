package io.github.burritobandit28.lmc.block_entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IOBlockEntity extends BlockEntity implements InputDevice, OutputDevice {
    public IOBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void handleInput(World world, BlockState state, ServerPlayerEntity player) {

    }
}
