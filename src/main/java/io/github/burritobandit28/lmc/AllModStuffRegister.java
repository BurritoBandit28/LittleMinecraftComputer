package io.github.burritobandit28.lmc;

import io.github.burritobandit28.lmc.items.ModItems;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AllModStuffRegister {


    public static void registerItems() {
        Registry.register(Registries.ITEM_GROUP, ModItems.LMC_GROUP_KEY, ModItems.LMC_ITEMGROUP);
        ModItems.registerMagneticTape();
    }


}
