package net.qilla.selectionplugin;

import net.qilla.selectionplugin.tools.regionselection.gui.RegionModification;
import net.qilla.selectionplugin.tools.settings.WandSettings;
import net.qilla.selectionplugin.tools.regionselection.WandContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SelectListener implements Listener {

    private final SelectionPlugin plugin;

    public SelectListener(SelectionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final WandSettings wandSettings = this.plugin.getSettingsRegistry().getPlayer(player);
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);

        if(player.getInventory().getItemInMainHand().getType() != Material.BREEZE_ROD) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        event.setCancelled(true);

        if(event.getAction().isLeftClick())
            wandContainer.getRegionPersistent().selectRegion(wandSettings.getVariant());
        else if(event.getAction().isRightClick())
            if(wandContainer.hasInstance(wandSettings.getVariant()))
                wandContainer.getRegionPersistent().clearWand(wandSettings.getVariant());
    }

    @EventHandler
    private void onSwapHand(PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        final WandSettings wandSettings = this.plugin.getSettingsRegistry().getPlayer(player);
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);

        if(item.getType() != Material.BREEZE_ROD) return;
        event.setCancelled(true);
        new RegionModification(player, wandSettings, wandContainer).openInventory();
    }

    @EventHandler
    private void onPlayerHeldItem(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItem(event.getNewSlot());
        final WandContainer wandContainer = this.plugin.getWandContainerRegistry().getContainer(player);

        if(item != null && item.getType() == Material.BREEZE_ROD) wandContainer.getRegionPersistent().update();
        else if(this.plugin.getWandContainerRegistry().hasContainer(player) && wandContainer.getRegionPersistent().isPreviewActive())
            wandContainer.getRegionPersistent().unselect();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.plugin.getSettingsRegistry().createPlayer(player);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.plugin.getWandContainerRegistry().removeContainer(player);
    }
}