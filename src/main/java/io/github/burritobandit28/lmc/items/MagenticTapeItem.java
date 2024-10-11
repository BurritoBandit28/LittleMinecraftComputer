package io.github.burritobandit28.lmc.items;

import net.minecraft.item.Item;

public class MagenticTapeItem extends Item {

    private final TapeColour colour;

    public MagenticTapeItem(TapeColour colour) {
        super(new Settings().maxCount(1));
        this.colour = colour;
    }

    public TapeColour getColour() {
        return this.colour;
    }

    // probably won't even use this
    public enum TapeColour {
        WHITE("white"),
        LIGHT_GRAY("light_gray"),
        GRAY("gray"),
        BLACK("black"),
        BROWN("brown"),
        RED("red"),
        ORANGE("orange"),
        YELLOW("yellow"),
        LIME("lime"),
        GREEN("green"),
        CYAN("cyan"),
        LIGHT_BLUE("light_blue"),
        BLUE("blue"),
        PURPLE("purple"),
        MAGENTA("magenta"),
        PINK("pink");

        private final String colour;
        TapeColour(String colour) {
            this.colour = colour;
        }
        public String getColour() {
            return this.colour;
        }
    }

}

