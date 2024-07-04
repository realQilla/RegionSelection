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
import java.util.*;

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
        savedRegionButton();
        setItem(Material.BARRIER, 53, item -> {
            item.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><red>Remove all regions</red>"));
            });
        });
        wandReachButton();
        cycleVariantButton();
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
        if(event.getCurrentItem() == null) return;
        int slot = event.getSlot();

        switch(slot) {
            case 47 -> selectionReach(event);
            case 49 -> cycleVariant(event);
            case 53 -> clearRegions();
            default -> modifyRegion(event, slot);
        }
    }

    private void selectionReach(InventoryClickEvent event) {
        int amount = event.isShiftClick() ? 5 : 1;

        if(event.isLeftClick()) {
            this.settings.wandReach().increment(amount);
            player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
        } else if(event.isRightClick()) {
            this.settings.wandReach().decrement(amount);
            player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 1.5f);
        }
        wandReachButton();
    }

    private void cycleVariant(InventoryClickEvent event) {
        if(event.getClick().equals(ClickType.LEFT)) {
            this.settings.previousVariant();
            player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
        } else if(event.getClick().equals(ClickType.RIGHT)) {
            this.settings.nextVariant();
            player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 1.5f);
        }
        cycleVariantButton();
        this.container.getCore().tickInfo(true);
    }

    private void clearRegions() {
        if(this.container.getShards().isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>There are no existing regions!</red>"));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
        } else {
            this.container.getShards().forEach(instance -> {
                this.container.removeShard(instance.getVariant());
            });
            savedRegionButton();
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>All regions have been <red><bold>REMOVED</bold></red>!<yellow>"));
            player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 0);
        }
    }

    private void modifyRegion(InventoryClickEvent event, int slot) {
        if(!this.variantMap.containsKey(slot)) return;

        WandVariant wandVariant = this.variantMap.get(slot);
        this.container.getShard(wandVariant).ifPresent(shard -> {
            switch(event.getClick()) {
                case LEFT -> modifyRegionSelect(wandVariant);
                case RIGHT -> modifyRegionRemove(wandVariant, slot);
                case MIDDLE -> modifyRegionTeleport(wandVariant, shard);
            }
        });
    }

    private void modifyRegionSelect(WandVariant wandVariant) {
        if(wandVariant.equals(this.settings.getVariant())) {
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + "> is already selected!</red>"));
            this.player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 2);
        } else {
            this.settings.setVariant(wandVariant);
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region <bold><#" + wandVariant.getHex() + ">" + wandVariant + "</#" + wandVariant.getHex() + "></bold> has been <bold><green>SELECTED</green></bold>!</yellow>"));
            this.container.getCore().tickInfo(true);
            this.player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 2);
        }
        cycleVariantButton();
    }

    private void modifyRegionRemove(WandVariant wandVariant, int slot) {
        this.variantMap.remove(slot);
        this.container.removeShard(wandVariant);
        removeItem(getValidSlots());
        savedRegionButton();
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region" + " <bold><#" + wandVariant.getHex() + ">" + wandVariant + "</#" + wandVariant.getHex() + "> <red>REMOVED</red></bold> successfully!</yellow>"));
        this.player.playSound(player, Sound.BLOCK_LAVA_POP, 1, 1);
    }

    private void modifyRegionTeleport(WandVariant wandVariant, RegionShard shard) {
        this.player.teleport(shard.getRegion().getOrigin().getLocation());
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Teleported to region" + " <#" + wandVariant.getHex() + "><bold>" + wandVariant + "</#" + wandVariant.getHex() + ">!</yellow>"));
        this.player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
    }

    private void wandReachButton() {
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

    private void cycleVariantButton() {
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

    private void savedRegionButton() {
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