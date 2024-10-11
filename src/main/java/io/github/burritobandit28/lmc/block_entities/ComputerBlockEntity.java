package io.github.burritobandit28.lmc.block_entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ComputerBlockEntity extends BlockEntity {

    private ArrayList<String> memory;
    // program counter
    private int pc;
    // accumulator
    private int acc;

    private boolean active;

    public ComputerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.memory = new ArrayList<>();
        this.memory.add("0");
        this.pc = 0;
        this.acc = 0;
        this.active = false;
    }
}
