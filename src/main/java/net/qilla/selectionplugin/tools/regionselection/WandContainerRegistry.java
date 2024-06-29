package net.qilla.selectionplugin.tools.regionselection;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WandContainerRegistry {

    private static WandContainerRegistry instance;
    private final Map<UUID, WandContainer> playerContainers = new HashMap<>();

    private WandContainerRegistry() {
    }

    public static WandContainerRegistry getContainer() {
        if(instance == null) instance = new WandContainerRegistry();
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