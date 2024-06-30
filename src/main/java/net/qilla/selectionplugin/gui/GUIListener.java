package net.qilla.selectionplugin.gui;

import net.qilla.selectionplugin.tools.regionselection.gui.RegionModification;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public final class GUIListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if(!(holder instanceof InventoryGUI)) return;

        if(holder instanceof RegionModification) {
            ((InventoryGUI) holder).onClick(event);
        }
    }
}