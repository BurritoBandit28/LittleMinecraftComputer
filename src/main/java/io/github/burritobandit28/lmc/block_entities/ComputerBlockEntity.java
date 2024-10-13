package io.github.burritobandit28.lmc.block_entities;

import io.github.burritobandit28.lmc.LMC;
import io.github.burritobandit28.lmc.blocks.ComputerBlock;
import io.github.burritobandit28.lmc.blocks.ModBlocks;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ComputerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {

    public static BlockEntityType<ComputerBlockEntity> COMPUTER_BLOCK_ENTITY;

    private ArrayList<String> memory;
    // program counter
    private int pc;
    // accumulator
    private int acc;
    // amount of time to keep a tick
    private int tick;
    // clockspeed in ticks
    private int clockSpeed;
    // tick delay
    private int tickDelay;

    protected final PropertyDelegate propertyDelegate;

    private ItemStack currentTape;

    private boolean active;



    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(COMPUTER_BLOCK_ENTITY, pos, state);
        this.memory = new ArrayList<>();
        this.memory = (LMC.translate("""
                START LDA ZERO
                OUT
                LDA ONE
                OUT
                LDA TWO
                OUT
                LDA THREE
                OUT
                BRA START
                
                ZERO    DAT 0
                ONE DAT 1
                TWO DAT 2
                THREE   DAT 3
                """));
        this.pc = 0;
        this.acc = 0;
        this.active = false;
        this.tick=4;
        this.tickDelay = 0;
        this.currentTape = ItemStack.EMPTY;
        this.clockSpeed = 4;

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ComputerBlockEntity.this.pc;
                    case 1 -> ComputerBlockEntity.this.acc;
                    case 2 -> ComputerBlockEntity.this.tick;
                    case 3 -> ComputerBlockEntity.this.clockSpeed;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ComputerBlockEntity.this.pc = value;
                    case 1 -> ComputerBlockEntity.this.acc = value;
                    case 2 -> ComputerBlockEntity.this.tick = value;
                    case 3 -> ComputerBlockEntity.this.clockSpeed = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };



    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        ArrayList<Integer> memAsInt = new ArrayList<>();
        for (String instruction : this.memory ) {
            memAsInt.add(Integer.parseInt(instruction));
        }
        nbt.putIntArray("memory",memAsInt);
        nbt.putBoolean("active", this.active);
        nbt.putInt("tick_delay", this.tickDelay);
        nbt.putInt("accumulator", this.acc);
        nbt.putInt("clockspeed", this.clockSpeed);
        nbt.putInt("output_ticks", this.tick);
        nbt.putInt("pc", this.pc);
        DefaultedList<ItemStack> inventory = DefaultedList.of();
        inventory.add(this.currentTape);
        Inventories.writeNbt(nbt, inventory, registryLookup);

    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        ArrayList<String> memAsStr = new ArrayList<>();
        for (int instruction : nbt.getIntArray("memory") ) {
            memAsStr.add(String.valueOf(instruction));
        }
        this.memory = memAsStr;
        this.active = nbt.getBoolean("active");
        this.tickDelay = nbt.getInt("tick_delay");
        this.acc = nbt.getInt("accumulator");
        this.clockSpeed = nbt.getInt("clockspeed");
        this.tick = nbt.getInt("output_ticks");
        this.pc = nbt.getInt("pc");
        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1,ItemStack.EMPTY);
        Inventories.readNbt(nbt, inventory, registryLookup);
        this.currentTape = inventory.getFirst();

    }

    public int getAcc() {
        return this.acc;
    }
    //                                                     I have used Rust so much now I default to "self"
    public static void tick(World world, BlockPos pos, BlockState state, ComputerBlockEntity self) {
        if (!world.isClient) {
            if (self.tickDelay == self.clockSpeed && self.active) {
                self.tickDelay = 0;

                String command = self.memory.get(self.pc);
                int opcode = Character.getNumericValue(command.charAt(0));
                int address = Integer.parseInt(command.substring(1));

                switch (opcode) {
                    case 1 -> {
                        self.acc += address;
                    }
                    case 2 -> {
                        self.acc -= address;
                    }
                    case 3 -> {
                        self.sta(address);
                    }
                    case 5 -> {
                        self.lda(address, pos);
                    }
                    case 6 -> {
                        self.pc = address;
                        self.tickDelay = self.clockSpeed;
                        return;
                    }
                    case 7 -> {
                        if (self.acc == 0) {
                            self.pc = address;
                            self.tickDelay = self.clockSpeed;
                            return;
                        }
                    }
                    case 8 -> {
                        if (self.acc > -1) {
                            self.pc = address;
                            self.tickDelay = self.clockSpeed;
                            return;
                        }
                    }
                    case 9 -> {
                        if (address == 1) {
                            // input redstone
                        } else if (address == 3) {
                            self.tick = self.acc;
                        } else {
                            // output redstone
                            //LMC.LOGGER.info(String.valueOf(self.acc));
                        }
                    }
                    default -> {
                        self.active = false;
                        self.pc = 0;
                        self.acc = 0;
                        // todo read memory from tape to reset
                        return;
                    }
                }
                self.pc++;
                world.updateComparators(pos, ModBlocks.COMPUTER_BLOCK);
                world.updateNeighbors(pos, ModBlocks.COMPUTER_BLOCK);
            }
            else if (self.active) {
                self.tickDelay++;
            }
        }
    }


    private void lda(int address, BlockPos pos) {
        if (this.memory.size() < address + 1) {
            LMC.LOGGER.warn("No data at memory position {} for LMComputer @ {}, there may be unexpected behaviour", address, pos.toString());
        }
        this.acc = Integer.parseInt(this.memory.get(address));
    }

    private void sta(int address) {
        if (this.memory.size() < (address + 1)) {
            for (int x = 0; x < (address + 1) - memory.size(); x++) {
                memory.add("0");
            }
        }
        memory.add(address, String.valueOf(this.acc));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.computer.containter_name");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.currentTape.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.currentTape;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(List.of(this.currentTape), slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(List.of(this.currentTape), slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.currentTape = stack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player, 4.0f);
    }

    @Override
    public void clear() {
        this.currentTape = ItemStack.EMPTY;
    }

}
