package net.qilla.selectionplugin.regionselection.wand;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionRegistry {

    private static RegionRegistry instance;
    private final Map<UUID, WandContainer> playerContainers = new HashMap<>();

    private RegionRegistry() {
    }

    public static RegionRegistry getContainer() {
        if(instance == null) instance = new RegionRegistry();
        return instance;
    }

    public WandContainer getContainer(final Player player) {
        return this.playerContainers.computeIfAbsent(player.getUniqueId(), k -> new WandContainer(player));
    }

    public void removeContainer(final Player player) {
        this.playerContainers.remove(player.getUniqueId());
    }

    public boolean hasContainer(final Player player) {
        return playerContainers.containsKey(player.getUniqueId());
    }
}