package net.qilla.selectionplugin.tools.regionselection.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.gui.InventoryGUI;
import net.qilla.selectionplugin.tools.regionselection.RegionShard;
import net.qilla.selectionplugin.tools.regionselection.WandContainer;
import net.qilla.selectionplugin.tools.regionselection.WandVariant;
import net.qilla.selectionplugin.tools.regionselection.WandSettings;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegionModification extends InventoryGUI {

    private final Player player;
    private final WandSettings settings;
    private final WandContainer container;
    private final Map<Integer, WandVariant> variantMap;

    public RegionModification(@NotNull Player player, @NotNull WandSettings settings, @NotNull WandContainer container) {
        super(player, 54, MiniMessage.miniMessage().deserialize("Saved Regions"));
        this.player = player;
        this.settings = settings;
        this.container = container;
        this.variantMap = new HashMap<>();
        loadRegionSlots();
        setItem(Material.BARRIER, 53, item -> {
            item.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><red>Remove all regions</red>"));
            });
        });
        setWandReach();
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
            case 47: {
                int amount = 1;
                if(event.isShiftClick()) {
                    amount = 5;
                }
                if(event.isLeftClick()) {
                    this.settings.wandReach().increment(amount);
                    player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                } else if(event.isRightClick()) {
                    this.settings.wandReach().decrement(amount);
                    player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                }
                setWandReach();
                break;
            }
            case 49: {
                if(event.getClick().equals(ClickType.LEFT)) {
                    this.settings.previousVariant();
                    player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                } else if(event.getClick().equals(ClickType.RIGHT)) {
                    this.settings.nextVariant();
                    player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                }
                setVariantChange();
                this.container.getCore().tickInfo(true);
                break;
            }
            case 53: {
                if(this.container.getShards().isEmpty()) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>There are no existing regions!</red>"));
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                    break;
                }
                this.container.getShards().forEach(instance -> {
                    this.container.removeShard(instance.getVariant());
                });
                loadRegionSlots();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>All regions have been <red><bold>REMOVED</bold></red>!<yellow>"));
                player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 0);
                break;
            }
            default: {
                if(!this.variantMap.containsKey(slot)) return;
                final WandVariant wandVariant = this.variantMap.get(slot);

                switch(event.getClick()) {
                    case LEFT: {
                        if(wandVariant.equals(this.settings.getVariant())) {
                            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + "> is already selected!</red>"));
                            this.player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        } else {
                            this.settings.setVariant(wandVariant);
                            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region <bold><#" + wandVariant.getHex() + ">" + wandVariant + "</#" + wandVariant.getHex() + "></bold> has been <bold><green>SELECTED</green></bold>!</yellow>"));
                            this.container.getCore().tickInfo(true);
                            this.player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                        }
                        setVariantChange();
                        break;
                    }
                    case RIGHT: {
                        this.variantMap.remove(slot);
                        this.container.removeShard(wandVariant);
                        removeItem(getValidSlots());
                        loadRegionSlots();
                        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region" + " <bold><#" + wandVariant.getHex() + ">" + wandVariant + "</#" + wandVariant.getHex() + "> <red>REMOVED</red></bold> successfully!</yellow>"));
                        this.player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
                        break;
                    }
                    case ClickType.MIDDLE: {
                        this.player.teleport(this.container.getShard(wandVariant).getRegion().getOrigin().getLocation());
                        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Teleported to region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + ">!</yellow>"));
                        this.player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
                    }
                }
            }
        }
    }

    private void setWandReach() {
        setItem(Material.SPYGLASS, 47, item -> {
            item.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><green>Change Selection Reach</green>"));
                List<Component> lore = new ArrayList<>();
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow>Current Reach: " + this.settings.wandReach().getValue() + "</yellow>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT</bold></yellow> to add reach<gray>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT</bold></yellow> to subtract reach<gray>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>SHIFT</bold></yellow> for a bigger value<gray>"));
                meta.lore(lore);
            });
        });
    }

    private void setVariantChange() {
        setItem(settings.getVariant().getGUIMaterial(), 49, item -> {
            item.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><green>Change Region Variant</green>"));
                List<Component> lore = new ArrayList<>();
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow>Current Variant: <#" + this.settings.getVariant().getHex() + "><bold>" + this.settings.getVariant() + "</bold></#" + this.settings.getVariant().getHex() + "></yellow>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT</bold></yellow> to cycle next<gray>"));
                lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT</bold></yellow> to cycle previous<gray>"));
                meta.lore(lore);
            });
        });
    }

    private void loadRegionSlots() {
        List<RegionShard> regionShards = container.getShards();
        List<Integer> validSlots = new ArrayList<>(getValidSlots());
        for(RegionShard regionShard : regionShards) {
            int validSlot = validSlots.removeFirst();
            this.variantMap.put(validSlot, regionShard.getVariant());
            setItem(regionShard.getVariant().getGUIMaterial(), validSlot, item -> {
                item.editMeta(meta -> {
                    meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><yellow>Region <#" + regionShard.getVariant().getHex() + "><bold>" + regionShard.getVariant() + "</#" + regionShard.getVariant().getHex() + "></yellow>"));
                    List<Component> lore = new ArrayList<>();
                    lore.add(MiniMessage.miniMessage().deserialize(""));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow><gold><bold>POSITION A</bold></gold> @ " + regionShard.getRegion().getOrigin().getX() + ", " + regionShard.getRegion().getOrigin().getY() + ", " + regionShard.getRegion().getOrigin().getZ() + "</yellow>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow><aqua><bold>POSITION B</bold></aqua> @ " + regionShard.getRegion().getEnd().getX() + ", " + regionShard.getRegion().getEnd().getY() + ", " + regionShard.getRegion().getEnd().getZ() + "</yellow>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><yellow><green><bold>TOTAL SIZE</bold></green> " + NumberFormat.getInstance().format(regionShard.getRegion().getSize()) + "</yellow>"));
                    lore.add(MiniMessage.miniMessage().deserialize(""));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>LEFT</bold></yellow> to <green><bold>SELECT</bold></green> this region</gray>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>RIGHT</bold></yellow> to <red><bold>DELETE</bold></red> this region</gray>"));
                    lore.add(MiniMessage.miniMessage().deserialize("<!italic><gray><yellow><bold>MIDDLE</bold></yellow> to <light_purple><bold>TELEPORT</bold></light_purple> to this region</gray>"));
                    meta.lore(lore);
                });
            });
        }
        validSlots.forEach(slot -> {
            setItem(Material.BLACK_STAINED_GLASS_PANE, slot, item -> {
                item.editMeta(meta -> {
                    meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><gray>Empty region</gray>"));
                });
            });
        });
    }

    public List<Integer> getValidSlots() {
        return List.copyOf(List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 30, 31, 32));
    }
}