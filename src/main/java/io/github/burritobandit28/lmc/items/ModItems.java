package io.github.burritobandit28.lmc.items;

import io.github.burritobandit28.lmc.LMC;
import io.github.burritobandit28.lmc.blocks.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

import java.util.HashMap;

public class ModItems {


    // Tapes
    public static HashMap<MagenticTapeItem.TapeColour, MagenticTapeItem> MagneticTapes = HashMap.newHashMap(16);

    // Item Group
    public static final RegistryKey<ItemGroup> LMC_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), LMC.ID("lmc_group"));
    public static final ItemGroup LMC_ITEMGROUP = FabricItemGroup.builder()
            .icon(() -> MagneticTapes.get(MagenticTapeItem.TapeColour.LIGHT_BLUE).getDefaultStack())
            .displayName(Text.translatable("item_group.lmc"))
            .build();

    // computer block item
    public static final BlockItem COMPUTER_BLOCK_ITEM = new BlockItem(ModBlocks.COMPUTER_BLOCK, new Item.Settings());

    // io block
    public static final BlockItem IO_BLOCK_ITEM = new BlockItem(ModBlocks.IO_BLOCK, new Item.Settings());



    public static void registerMagneticTape() {
        for (MagenticTapeItem.TapeColour colour : MagenticTapeItem.TapeColour.values()) {
            MagenticTapeItem tapeItem = new MagenticTapeItem(colour);
            MagneticTapes.put(colour, tapeItem);
            Registry.register(Registries.ITEM, LMC.ID(String.format("%s_magnetic_tape", colour.getColour())), tapeItem);
            ItemGroupEvents.modifyEntriesEvent(LMC_GROUP_KEY).register(itemGroup -> {
                itemGroup.add(tapeItem);
            });
        }
    }

}
