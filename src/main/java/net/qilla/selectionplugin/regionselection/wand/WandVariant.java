package net.qilla.selectionplugin.regionselection.wand;

import org.bukkit.Color;
import org.bukkit.Material;

public enum WandVariant {
    WHITE(Material.WHITE_WOOL, "FFFFFF"),
    LIGHT_GRAY(Material.LIGHT_GRAY_WOOL, "AAAAAA"),
    GRAY(Material.GRAY_WOOL, "555555"),
    BLACK(Material.BLACK_WOOL, "000000"),
    BROWN(Material.BROWN_WOOL, "cd9575"),
    RED(Material.RED_WOOL, "FF5555"),
    ORANGE(Material.ORANGE_WOOL, "FFAA00"),
    YELLOW(Material.YELLOW_WOOL, "FFFF55"),
    LIME(Material.LIME_WOOL, "55FF55"),
    GREEN(Material.GREEN_WOOL, "00AA00"),
    CYAN(Material.CYAN_WOOL, "00AAAA"),
    LIGHT_BLUE(Material.LIGHT_BLUE_WOOL, "55FFFF"),
    BLUE(Material.BLUE_WOOL, "5555FF"),
    PURPLE(Material.PURPLE_WOOL, "AA00AA"),
    MAGENTA(Material.MAGENTA_WOOL, "FF00FF"),
    PINK(Material.PINK_WOOL, "FF55FF");

    private final Material material;
    private final String color;

    WandVariant(Material material, String color) {
        this.material = material;
        this.color = color;
    }

    public Color getColor() {
        int r = Integer.parseInt(this.color.substring(0, 2), 16);
        int g = Integer.parseInt(this.color.substring(2, 4), 16);
        int b = Integer.parseInt(this.color.substring(4, 6), 16);

        return Color.fromRGB(r, g, b);
    }

    public String getHex() {
        return this.color;
    }

    public Material getMaterial() {
        return this.material;
    }
}