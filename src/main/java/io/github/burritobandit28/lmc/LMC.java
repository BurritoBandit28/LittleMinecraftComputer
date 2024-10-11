package io.github.burritobandit28.lmc;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LMC implements ModInitializer {

    public static final Block temp_cb = new Block(AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK));

    
    public static Identifier ID(String path) {
        return Identifier.of("lmc", path);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Little Minecraft Computer");

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, Identifier.of("lmc","computer"), temp_cb);
        AllModStuffRegister.registerItems();
    }

    public ArrayList<String> translate(String lmc) {
        String file = lmc;

        file = file.replaceAll("\\bLDA\\b", "5");
        file = file.replaceAll("\\bSTA\\b", "3");
        file = file.replaceAll("\\bADD\\b", "1");
        file = file.replaceAll("\\bSUB\\b", "2");
        file = file.replaceAll("\\bINP\\b", "901");
        file = file.replaceAll("\\bOUT\\b", "902");
        file = file.replaceAll("\\bHLT\\b", "000");
        file = file.replaceAll("\\bBRZ\\b", "7");
        file = file.replaceAll("\\bBRP\\b", "8");
        file = file.replaceAll("\\bBRA\\b", "6");

        ArrayList<String> buffer_thing = new ArrayList<>();

        // wow java doesn't have tuples? or am I just stupid
        HashMap<String, Pair<String, String>> dat_key = new HashMap<>();
        HashMap<String, String> branch_key = new HashMap<>();
        int line_count = 0;

        for (String line : file.lines().toList()) {
            line = line.replaceFirst("^\\s+", "");
            ArrayList<String> line_as_list = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            boolean is_dat_line = false;
            String ret_string = StringUtils.join(line_as_list,"");

            if (line.isEmpty() || line_as_list.isEmpty()) {
                continue;
            }
            if (line.matches(".*\\bDAT\\b.*")) {
                if (line_as_list.size() < 3) {
                    line_as_list.add("0");
                }
                dat_key.put(line_as_list.get(0), new Pair<>(Integer.toString(line_count), line_as_list.get(2)));
                is_dat_line = true;
            }
            else if (Character.isAlphabetic(ret_string.charAt(0))) {
                branch_key.put(line_as_list.getFirst(), Integer.toString(line_count));
                line_as_list.removeFirst();
                ret_string = StringUtils.join(line_as_list, "");
            }
            if (!is_dat_line) {
                buffer_thing.add(ret_string);
            }
            line_count+=1;
        }

        file = StringUtils.join(buffer_thing, "\n");
        buffer_thing = new ArrayList<>();

        for (String line : file.lines().toList()) {
            StringBuilder before = new StringBuilder();
            StringBuilder after = new StringBuilder();
            int indx = 0;
            char_loop : for (int i = 0; i < line.length(); i++){
                char c = line.charAt(i);
                if (Character.isAlphabetic(c)) {
                    break char_loop;
                }
                before.append(c);
                indx+=1;
            }
            for (int x = indx; x < line.length(); x++) {
                after.append(line.charAt(x));
            }
            checks : {
                for (Map.Entry<String, Pair<String, String>> data : dat_key.entrySet()) {
                    if (data.getKey().contentEquals(after)) {
                        after = new StringBuilder(data.getValue().getLeft());
                        break checks;
                    }
                }
                for (Map.Entry<String, String> data : branch_key.entrySet()) {
                    if (data.getKey().contentEquals(after)) {
                        after = new StringBuilder(data.getValue());
                        break checks;
                    }
                }
            }
            buffer_thing.add(String.format("%s%s", before, after));
        }
        file = StringUtils.join(buffer_thing, "\n");

        ArrayList<String> memory = new ArrayList<>(file.lines().toList());

        for (Map.Entry<String, Pair<String,String>> data : dat_key.entrySet()) {
            if (memory.size() < Integer.parseInt(data.getValue().getLeft()) + 1) {
                for (int x = 0; x < (Integer.parseInt(data.getValue().getLeft()) + 1) - memory.size(); x++) {
                    memory.add("0");
                }
            }
            memory.add(Integer.parseInt(data.getValue().getLeft()), data.getValue().getRight());
        }
        return memory;
    }
}
