package io.github.burritobandit28.lmc;

import io.github.burritobandit28.lmc.block_entities.ComputerBlockEntity;
import io.github.burritobandit28.lmc.blocks.ModBlocks;
import io.github.burritobandit28.lmc.items.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AllModStuffRegister {


    public static void registerItems() {
        // item group
        Registry.register(Registries.ITEM_GROUP, ModItems.LMC_GROUP_KEY, ModItems.LMC_ITEMGROUP);

        // computer block item
        Registry.register(Registries.ITEM, LMC.ID("computer"), ModItems.COMPUTER_BLOCK_ITEM);
        ItemGroupEvents.modifyEntriesEvent(ModItems.LMC_GROUP_KEY).register(itemGroup -> {
            itemGroup.add(ModItems.COMPUTER_BLOCK_ITEM);
        });

        // tapes
        ModItems.registerMagneticTape();
    }

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK, Identifier.of("lmc","computer"), ModBlocks.COMPUTER_BLOCK);
    }

    public static void registerBlockEntities() {
        ComputerBlockEntity.COMPUTER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, LMC.ID("computer_block_entity"),
                BlockEntityType.Builder.create(ComputerBlockEntity::new, ModBlocks.COMPUTER_BLOCK).build());
    }


}
