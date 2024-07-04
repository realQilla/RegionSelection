package net.qilla.selectionplugin.tools.router;

import net.qilla.selectionplugin.SelectionPlugin;
import net.qilla.selectionplugin.assets.MetaKey;
import net.qilla.selectionplugin.tools.regionselection.WandContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ToolListener implements Listener {

    private final SelectionPlugin plugin;

    public ToolListener(SelectionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(item == null || !item.hasItemMeta()) return;

        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        if(!dataContainer.has(MetaKey.ITEM_TOOL)) return;
        String key = dataContainer.get(MetaKey.ITEM_TOOL, PersistentDataType.STRING);

        switch(key) {
            case "item_selection_tool": {
                new WandTool(player, this.plugin).interact(event);
                break;
            }
            case "item_fill_tool": {
                break;
            }
        }
    }

    @EventHandler
    private void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getOffHandItem();

        if(!item.hasItemMeta()) return;

        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        if(!dataContainer.has(MetaKey.ITEM_TOOL)) return;
        String key = dataContainer.get(MetaKey.ITEM_TOOL, PersistentDataType.STRING);

        switch(key) {
            case "item_selection_tool": {
                new WandTool(player, this.plugin).swapHand(event);
                break;
            }
        }
    }

    @EventHandler
    private void onPlayerHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if(item == null || !item.hasItemMeta()) {
            new WandTool(player, this.plugin).unselectItem(event);
            return;
        }

        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        if(!dataContainer.has(MetaKey.ITEM_TOOL)) return;
        String key = dataContainer.get(MetaKey.ITEM_TOOL, PersistentDataType.STRING);

        switch(key) {
            case "item_selection_tool": {
                new WandTool(player, this.plugin).selectItem(event);
                break;
            }
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);

        wandContainer.getCore().removeNonSaved();
        this.plugin.getWandContainerRegistry().removeContainer(player);
    }
}