package net.qilla.selectionplugin.tools.router;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public interface ToolSystem {
    void interact(PlayerInteractEvent event);
    void selectItem(PlayerItemHeldEvent event);
    void unselectItem(PlayerItemHeldEvent event);
    void swapHand(PlayerSwapHandItemsEvent event);
    void dropItem(PlayerDropItemEvent event);

}