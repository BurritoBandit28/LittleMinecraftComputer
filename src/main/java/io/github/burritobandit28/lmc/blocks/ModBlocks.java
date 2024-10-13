package io.github.burritobandit28.lmc.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;

public class ModBlocks {

    public static final ComputerBlock COMPUTER_BLOCK = new ComputerBlock(AbstractBlock.Settings.copy(Blocks.QUARTZ_BLOCK));

    public static final IOBlock IO_BLOCK = new IOBlock(AbstractBlock.Settings.copy(Blocks.QUARTZ_BLOCK));

}
