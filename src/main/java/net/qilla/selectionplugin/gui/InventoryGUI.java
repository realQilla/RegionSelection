package net.qilla.selectionplugin.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.qilla.selectionplugin.SelectionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public abstract class InventoryGUI implements InventoryHolder {
    private final Player player;
    private final Inventory inventory;

    public InventoryGUI(@NotNull Player player, int size, @NotNull Component title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public InventoryGUI setItem(@NotNull Material material, int slot) {
        inventory.setItem(slot, new ItemStack(material));
        return this;
    }

    public InventoryGUI setItem(@NotNull ItemStack item, int slot) {
        inventory.setItem(slot, item);
        return this;
    }

    public InventoryGUI setItem(Material material, int slot, @NotNull Consumer<ItemStack> consumer) {
        ItemStack item = new ItemStack(material);
        consumer.accept(item);
        inventory.setItem(slot, item);
        return this;
    }

    public ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }

    public void removeItem(int slot) {
        this.inventory.clear(slot);
    }

    public void removeItem(List<Integer> slots) {
        slots.forEach(slot -> {
            if(inventory.getItem(slot) != null) inventory.clear(slot);
        });
    }

    public void clearInventory() {
        inventory.clear();
    }

    public void openInventory() {
        this.player.openInventory(this.inventory);
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public abstract void onOpen(@NotNull InventoryOpenEvent event);

    public abstract void onClose(@NotNull InventoryOpenEvent event);

    public abstract void onClick(@NotNull InventoryClickEvent event);
}