package net.qilla.selectionplugin.tools.router;

import net.qilla.selectionplugin.SelectionPlugin;
import net.qilla.selectionplugin.tools.regionselection.WandContainer;
import net.qilla.selectionplugin.tools.regionselection.WandSettings;
import net.qilla.selectionplugin.tools.regionselection.gui.RegionModification;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class WandTool implements ToolSystem {
    private final Player player;
    private final SelectionPlugin plugin;

    public WandTool(Player player, SelectionPlugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public void interact(PlayerInteractEvent event) {
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(this.player);
        event.setCancelled(true);

        if(event.getAction().isLeftClick()) {
            wandContainer.getCore().selectRegion();
            return;
        }

        if(event.getAction().isRightClick()) {
            wandContainer.getCore().removeSavedShard();
            return;
        }
    }

    @Override
    public void selectItem(PlayerItemHeldEvent event) {
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(this.player);

        wandContainer.getCore().tickOutline();
    }

    @Override
    public void unselectItem(PlayerItemHeldEvent event) {
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(this.player);

        if(wandContainer != null) {
            wandContainer.getCore().removeNonSaved();
        }
    }

    @Override
    public void swapHand(PlayerSwapHandItemsEvent event) {
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(this.player);
        final WandSettings wandSettings = wandContainer.getSettings();
        event.setCancelled(true);

        new RegionModification(this.player, wandSettings, wandContainer).openInventory();
    }

    @Override
    public void dropItem(PlayerDropItemEvent event) {
    }
}