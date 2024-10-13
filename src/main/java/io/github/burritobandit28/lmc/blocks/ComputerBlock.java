package io.github.burritobandit28.lmc.blocks;

import com.mojang.serialization.MapCodec;
import io.github.burritobandit28.lmc.block_entities.ComputerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public ComputerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(ComputerBlock::new);
    };

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH -> {
                return VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0, 0, 1, 0.375, 0.125),
                        VoxelShapes.cuboid(0, 0, 0.125, 1, 1, 1)
                );
            }
            case EAST -> {
                return VoxelShapes.union(
                        VoxelShapes.cuboid(0.875, 0, 0, 1, 0.375, 1),
                        VoxelShapes.cuboid(0, 0, 0, 0.875, 1, 1)
                );
            }
            case SOUTH -> {
                return VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0, 0.875, 1, 0.375, 1),
                        VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.875)
                );
            }
            case WEST -> {
                return VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0, 0, 0.125, 0.375, 1),
                        VoxelShapes.cuboid(0.125, 0, 0, 1, 1, 1)
                );
            }
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, flip(ctx.getHorizontalPlayerFacing()));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public Direction flip(Direction dir) {
        switch (dir) {
            case NORTH -> {
                return Direction.SOUTH;
            }
            case SOUTH -> {
                return Direction.NORTH;
            }
            case EAST -> {
                return Direction.WEST;
            }
            case WEST -> {
                return Direction.EAST;
            }
        }
        return Direction.NORTH;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComputerBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ComputerBlockEntity.COMPUTER_BLOCK_ENTITY, (world1, pos, state1, be) -> ComputerBlockEntity.tick(world1, pos, state1, (ComputerBlockEntity) be));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ComputerBlockEntity computerBlockEntity) {
                ItemScatterer.spawn(world, pos, computerBlockEntity);

                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ComputerBlockEntity computerBlockEntity) {
            return computerBlockEntity.getAcc();
        }
        else {
            return 0;
        }
    }
}
