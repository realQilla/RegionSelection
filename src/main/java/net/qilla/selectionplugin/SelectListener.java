package net.qilla.selectionplugin;

import net.qilla.selectionplugin.tools.regionselection.gui.RegionModification;
import net.qilla.selectionplugin.tools.regionselection.WandSettings;
import net.qilla.selectionplugin.tools.regionselection.WandContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class SelectListener implements Listener {

    private final SelectionPlugin plugin;

    public SelectListener(SelectionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);
        final WandSettings wandSettings = wandContainer.getSettings();

        if(player.getInventory().getItemInMainHand().getType() != Material.BREEZE_ROD) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        event.setCancelled(true);

        if(event.getAction().isLeftClick())
            wandContainer.getPersistent().selectRegion();
        else if(event.getAction().isRightClick())
            if(wandContainer.hasInstance(wandSettings.getVariant()))
                wandContainer.getPersistent().clearWand();
    }

    @EventHandler
    private void onSwapHand(PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);
        final WandSettings wandSettings = wandContainer.getSettings();
        final ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() != Material.BREEZE_ROD) return;
        event.setCancelled(true);
        new RegionModification(player, wandSettings, wandContainer).openInventory();
    }

    @EventHandler
    private void onPlayerHeldItem(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);
        final ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if(item != null && item.getType() == Material.BREEZE_ROD) wandContainer.getPersistent().update();
        else if(this.plugin.getWandContainerRegistry().hasContainer(player) && wandContainer.getPersistent().isPreviewActive())
            wandContainer.getPersistent().unselect();
    }
}