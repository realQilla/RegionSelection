package net.qilla.selectionplugin.gui.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.assets.MetaKey;
import net.qilla.selectionplugin.gui.InventoryGUI;
import net.qilla.selectionplugin.tools.regionselection.gui.RegionModification;
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
        setItem(wand(), 22);
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

        switch(event.getSlot()) {
            case 22: {
                player.getInventory().addItem(wand());
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                break;
            }
        }
    }

    private static ItemStack wand() {
        ItemStack item = new ItemStack(Material.BREEZE_ROD);
        item.editMeta(meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><aqua>Selection Wand</aqua>"));

            List<Component> lore = new ArrayList<>();
            lore.add(MiniMessage.miniMessage().deserialize(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT</yellow> to create a selection</gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT</yellow> to remove your current region/selection</gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>F</yellow> to open the modification menu</gray>"));
            lore.add(MiniMessage.miniMessage().deserialize(""));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>A wand radiating a magical aura. This powerful wand</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>allows its user to make selections which then turn into</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>\"regions\". These so called \"regions\" can then be modified</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>in a number of different ways allowing its user to fully modify</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>the properties of blocks they so choose.</dark_gray>"));
            lore.add(MiniMessage.miniMessage().deserialize("<!italic><dark_gray>What could go wrong?</dark_gray>"));

            meta.lore(lore);
            meta.setMaxStackSize(1);
            meta.setEnchantmentGlintOverride(true);
            meta.getPersistentDataContainer().set(MetaKey.ITEM_WAND_TOOL, PersistentDataType.STRING, MetaKey.ITEM_WAND_TOOL.value());
        }) ;
        return item;
    }
}
