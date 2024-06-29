package net.qilla.selectionplugin.tools.regionselection.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.gui.InventoryGUI;
import net.qilla.selectionplugin.tools.regionselection.RegionInstance;
import net.qilla.selectionplugin.tools.regionselection.WandContainer;
import net.qilla.selectionplugin.tools.regionselection.WandVariant;
import net.qilla.selectionplugin.tools.settings.WandSettings;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegionModification extends InventoryGUI {

    private final Player player;
    private final WandSettings wandSettings;
    private final WandContainer wandContainer;
    private final Map<Integer, WandVariant> variantMap;

    public RegionModification(@NotNull Player player, @NotNull WandSettings wandSettings, @NotNull WandContainer wandContainer) {
        super(player, 54, MiniMessage.miniMessage().deserialize("Active Regions"));
        this.player = player;
        this.wandSettings = wandSettings;
        this.wandContainer = wandContainer;
        this.variantMap = new HashMap<>();
        loadRegionSlots();
        setItem(Material.BARRIER, 53, item -> {
            item.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><red>Remove all existing region variants</red>"));
            });
        });
        setVariantChange();
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }

    @Override
    public void onClose(@NotNull InventoryOpenEvent event) {

    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if(!(event.getInventory().getHolder() instanceof RegionModification)) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        if(event.getCurrentItem() == null) return;

        switch(slot) {
            case 49: {
                if(event.getClick().equals(ClickType.LEFT)) {
                    this.wandSettings.previousVariant();
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region variant set to <#" + this.wandSettings.getVariant().getHex() + "><bold>" + this.wandSettings.getVariant() + "</#" + this.wandSettings.getVariant().getHex() + "></yellow>"));
                    player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                } else if(event.getClick().equals(ClickType.RIGHT)) {
                    this.wandSettings.nextVariant();
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region variant set to <#" + this.wandSettings.getVariant().getHex() + "><bold>" + this.wandSettings.getVariant() + "</#" + this.wandSettings.getVariant().getHex() + "></yellow>"));
                    player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                }
                setVariantChange();
                break;
            }
            case 53: {
                if(this.wandContainer.getInstances().isEmpty()) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>There are no existing regions!</red>"));
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                    break;
                }
                this.wandContainer.getInstances().forEach(instance -> {
                    this.wandContainer.removeInstance(instance.getWandVariant());
                });
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>All regions have been <red><bold>REMOVED</bold></red>!<yellow>"));
                player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 0);
                break;
            }
            default: {
                if(!this.variantMap.containsKey(slot)) return;
                final WandVariant wandVariant = this.variantMap.get(slot);

                switch(event.getClick()) {
                    case LEFT: {
                        if(wandVariant.equals(this.wandSettings.getVariant())) {
                            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + "> is already selected!</red>"));
                            this.player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        } else {
                            this.wandSettings.setVariant(wandVariant);
                            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region variant has been set to <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + ">!</yellow>"));
                            this.player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                        }
                        setVariantChange();
                        break;
                    }
                    case RIGHT: {
                        this.variantMap.remove(slot);
                        this.wandContainer.removeInstance(wandVariant);
                        removeItem(getValidSlots());
                        loadRegionSlots();
                        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + "> has been <red><bold>REMOVED</bold></red>!</yellow>"));
                        this.player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                        break;
                    }
                    case ClickType.MIDDLE: {
                        this.player.teleport(this.wandContainer.getInstance(wandVariant).getOrigin().getLocation());
                        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Teleported to region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + ">!</yellow>"));
                        this.player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
                    }
                }
            }
        }
    }

    private void setVariantChange() {
        setItem(wandSettings.getVariant().getMaterial(), 49, item -> {
            item.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><green>Change Region Variant</green>"));
                List<Component> lore = new ArrayList<>();
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow>Current Variant: <#" + this.wandSettings.getVariant().getHex() + "><bold>" + this.wandSettings.getVariant() + "</bold></#" + this.wandSettings.getVariant().getHex() + "></yellow>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT CLICK</bold></yellow> to cycle next<gray>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT CLICK</bold></yellow> to cycle previous<gray>"));
                meta.lore(lore);
            });
        });
    }

    private void loadRegionSlots() {
        List<RegionInstance> regionInstances = wandContainer.getInstances();
        for(int i = 0; i < regionInstances.size(); i++) {
            RegionInstance regionInstance = regionInstances.get(i);
            if(!regionInstance.hasEnd()) return;
            int validSlot = getValidSlots().get(i);
            this.variantMap.put(validSlot, regionInstance.getWandVariant());
            setItem(regionInstance.getWandVariant().getMaterial(), validSlot, item -> {
                item.editMeta(meta -> {
                    meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><yellow>Region <#" + regionInstance.getWandVariant().getHex() + "><bold>" + regionInstance.getWandVariant() + "</#" + regionInstance.getWandVariant().getHex() + "></yellow>"));
                    List<Component> lore = new ArrayList<>();
                    lore.add(MiniMessage.miniMessage().deserialize(""));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow><gold><bold>POSITION A</bold></gold> @ " + regionInstance.getOrigin().getX() + ", " + regionInstance.getOrigin().getX() + ", " + regionInstance.getOrigin().getX() + "</yellow>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow><aqua><bold>POSITION B</bold></aqua> @ " + regionInstance.getEnd().getX() + ", " + regionInstance.getEnd().getX() + ", " + regionInstance.getEnd().getX() + "</yellow>"));
                    lore.add(MiniMessage.miniMessage().deserialize(""));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT</bold></yellow> to <green><bold>SELECT</bold></green> this region</gray>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT</bold></yellow> to <red><bold>DELETE</bold></red> this region</gray>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>MIDDLE</bold></yellow> to <light_purple><bold>TELEPORT</bold></light_purple> to this region</gray>"));
                    meta.lore(lore);
                });
            });
        }
    }

    public static List<Integer> getValidSlots() {
        return List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }
}