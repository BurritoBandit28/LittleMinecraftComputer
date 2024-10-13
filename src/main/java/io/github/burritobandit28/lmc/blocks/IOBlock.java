package io.github.burritobandit28.lmc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class IOBlock extends Block {

    public static final EnumProperty<InpOut> INPUT_OUTPUT = EnumProperty.of("io_action", InpOut.class, InpOut.INPUT, InpOut.OUTPUT);

    public IOBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(INPUT_OUTPUT, InpOut.INPUT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INPUT_OUTPUT);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        switch (world.getBlockState(pos).get(INPUT_OUTPUT)) {
            case INPUT -> {
                world.setBlockState(pos, state.with(INPUT_OUTPUT, InpOut.OUTPUT));
            }
            case OUTPUT -> {
                world.setBlockState(pos, state.with(INPUT_OUTPUT, InpOut.INPUT));
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(INPUT_OUTPUT)) {
            case INPUT -> {
                return VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0, 0, 1, 0.25, 1),
                        VoxelShapes.cuboid(0.125, 0.25, 0.125, 0.875, 1, 0.875),
                        VoxelShapes.cuboid(0, 0.6875, 0, 1, 1, 1)
                );
            }
            case OUTPUT -> {
                return VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0, 0, 1, 0.25, 1),
                        VoxelShapes.cuboid(0.125, 0.25, 0.125, 0.875, 1, 0.875),
                        VoxelShapes.cuboid(0, 0.375, 0, 1, 0.6875, 1)
                );
            }
        }
        return VoxelShapes.fullCube();
    }

    public enum InpOut implements StringIdentifiable {
        INPUT("input"),
        OUTPUT("output");

        @Override
        public String asString() {
            return this.asStr;
        }

        private final String asStr;

        InpOut(String asStr) {
            this.asStr = asStr;
        }
    }

}
