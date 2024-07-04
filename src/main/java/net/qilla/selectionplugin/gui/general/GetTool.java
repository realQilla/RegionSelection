package net.qilla.selectionplugin.gui.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.assets.MetaKey;
import net.qilla.selectionplugin.gui.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GetTool extends InventoryGUI {

    private final Player player;

    public GetTool(@NotNull Player player) {
        super(player, 54, MiniMessage.miniMessage().deserialize("Tools"));

        this.player = player;
        setItem(selection(), 22);
        setItem(fill(), 40);
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }

    @Override
    public void onClose(@NotNull InventoryOpenEvent event) {

    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if(!(event.getInventory().getHolder() instanceof GetTool)) return;
        event.setCancelled(true);

        int slot = event.getSlot();

        if(getInventory().getItem(slot) == null) return;

        switch(slot) {
            case 22: {
                this.player.getInventory().addItem(selection());
                break;
            }
            case 40: {
                this.player.getInventory().addItem(fill());
            }
        }
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    private static ItemStack fill() {
        ItemStack item = new ItemStack(Material.BUCKET);
        item.editMeta(meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><yellow>Fill Tool</yellow>"));

            List<Component> lore = new ArrayList<>();
            lore.add(MiniMessage.miniMessage().deserialize(""));

            meta.lore(lore);
            meta.setMaxStackSize(1);
            meta.setEnchantmentGlintOverride(true);
            meta.getPersistentDataContainer().set(MetaKey.ITEM_TOOL, PersistentDataType.STRING, "item_fill_tool");
        }) ;
        return item;
    }

    private static ItemStack selection() {
        ItemStack item = new ItemStack(Material.BREEZE_ROD);
        item.editMeta(meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><aqua>Selection Wand</aqua>"));

            List<Component> lore = new ArrayList<>();
            lore.add(MiniMessage.miniMessage().deserialize(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT</yellow> to create a selection</gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT</yellow> to remove your current region/selection</gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>F</yellow> to open the modification menu</gray>"));
            lore.add(MiniMessage.miniMessage().deserialize(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>A wand radiating a magical aura. This powerful wand allows its</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>holder to create selections which can then turn into \"regions\".</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>These so called \"regions\" and their blocks within can be modified</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>in a number of different ways, at will. What could go wrong?</dark_gray>"));

            meta.lore(lore);
            meta.setMaxStackSize(1);
            meta.setEnchantmentGlintOverride(true);
            meta.getPersistentDataContainer().set(MetaKey.ITEM_TOOL, PersistentDataType.STRING, "item_selection_tool");
        }) ;
        return item;
    }
}
