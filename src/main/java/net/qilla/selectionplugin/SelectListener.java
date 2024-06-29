package net.qilla.selectionplugin;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.regionselection.settings.PlayerWandSettings;
import net.qilla.selectionplugin.regionselection.wand.WandContainer;
import net.qilla.selectionplugin.regionselection.wand.WandVariant;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


public class SelectListener implements Listener {

    private final SelectionPlugin plugin;

    public SelectListener(SelectionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerWandSettings playerSettings = this.plugin.getSettingsRegistry().getPlayer(player);
        final WandContainer wandContainer = this.plugin.getRegionRegistry().getContainer(player);

        if(player.getInventory().getItemInMainHand().getType() != Material.BREEZE_ROD) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        event.setCancelled(true);

        if(playerSettings.isChangingVariant()) {
            final List<WandVariant> list = Arrays.asList(WandVariant.values());
            int selected = list.indexOf(playerSettings.getVariant());

            if(event.getAction().isLeftClick()) {
                selected = (selected >= list.size() - 1) ? 0 : selected + 1;
            } else if(event.getAction().isRightClick()) {
                selected = (selected <= 0) ? list.size() - 1 : selected - 1;
            }

            WandVariant wandVariant = list.get(selected);
            playerSettings.setVariant(wandVariant);
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<green>Wand variant set to <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + ">"));
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
        } else {
            if(event.getAction().isLeftClick())
                wandContainer.getPersistent().selectRegion(playerSettings.getVariant());
             else if(event.getAction().isRightClick())
                if(wandContainer.hasInstance(playerSettings.getVariant()))
                    wandContainer.getPersistent().clearWand(playerSettings.getVariant());
        }
    }

    @EventHandler
    private void onSwapHand(PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        final PlayerWandSettings playerSettings = this.plugin.getSettingsRegistry().getPlayer(player);

        if(item.getType() != Material.BREEZE_ROD) return;
        event.setCancelled(true);
        playerSettings.toggleVariant();
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Wand variant selection mode " + (playerSettings.isChangingVariant() ? "<green>ENABLED</green>" : "<red>DISABLED</red>") + "!</yellow>"));
    }

    @EventHandler
    private void onPlayerHeldItem(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItem(event.getNewSlot());
        final WandContainer wandContainer = this.plugin.getRegionRegistry().getContainer(player);

        if(item != null && item.getType() == Material.BREEZE_ROD) wandContainer.getPersistent().update();
        else if(this.plugin.getRegionRegistry().hasContainer(player) && wandContainer.getPersistent().activePreview())
            wandContainer.getPersistent().unselect();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.plugin.getSettingsRegistry().createPlayer(player);
    }
}