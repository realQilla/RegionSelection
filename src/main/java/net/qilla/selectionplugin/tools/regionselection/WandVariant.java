package net.qilla.selectionplugin.tools.regionselection;

import org.bukkit.Color;
import org.bukkit.Material;

public enum WandVariant {
    WHITE(Material.WHITE_CONCRETE, Material.WHITE_WOOL, "FFFFFF"),
    SILVER(Material.LIGHT_GRAY_CONCRETE,Material.LIGHT_GRAY_WOOL, "AAAAAA"),
    GRAY(Material.GRAY_CONCRETE,Material.GRAY_WOOL, "555555"),
    BLACK(Material.BLACK_CONCRETE, Material.BLACK_WOOL, "000000"),
    BROWN(Material.BROWN_CONCRETE, Material.BROWN_WOOL, "965A3E"),
    RED(Material.RED_CONCRETE, Material.RED_WOOL, "FF5555"),
    ORANGE(Material.ORANGE_CONCRETE, Material.ORANGE_WOOL, "FFAA00"),
    YELLOW(Material.YELLOW_CONCRETE, Material.YELLOW_WOOL, "FFFF55"),
    LIME(Material.LIME_CONCRETE, Material.LIME_WOOL, "55FF55"),
    GREEN(Material.GREEN_CONCRETE, Material.GREEN_WOOL, "00AA00"),
    CYAN(Material.CYAN_CONCRETE, Material.CYAN_WOOL, "00AAAA"),
    AQUA(Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_WOOL, "55FFFF"),
    BLUE(Material.BLUE_CONCRETE, Material.BLUE_WOOL, "5555FF"),
    PURPLE(Material.PURPLE_CONCRETE, Material.PURPLE_WOOL, "AA00AA"),
    MAGENTA(Material.MAGENTA_CONCRETE, Material.MAGENTA_WOOL, "FF00FF"),
    PINK(Material.PINK_CONCRETE, Material.PINK_WOOL, "FF55FF");

    private final Material mainMaterial;
    private final Material guiMaterial;
    private final String color;

    WandVariant(Material mainMaterial, Material guiMaterial, String color) {
        this.mainMaterial = mainMaterial;
        this.guiMaterial = guiMaterial;
        this.color = color;
    }

    public Color getColor() {
        int r = Integer.parseInt(this.color.substring(0, 2), 16);
        int g = Integer.parseInt(this.color.substring(2, 4), 16);
        int b = Integer.parseInt(this.color.substring(4, 6), 16);

        return Color.fromRGB(r, g, b);
    }

    public Material getMainMaterial() {
        return this.mainMaterial;
    }


    public Material getGUIMaterial() {
        return this.guiMaterial;
    }

    public String getHex() {
        return this.color;
    }
}