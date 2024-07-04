package net.qilla.selectionplugin.assets;

import net.qilla.selectionplugin.SelectionPlugin;
import org.bukkit.NamespacedKey;

public class MetaKey {

    private static final SelectionPlugin PLUGIN = SelectionPlugin.getInstance();

    public static final NamespacedKey ITEM_TOOL = new NamespacedKey(PLUGIN, "item_tool");
}
