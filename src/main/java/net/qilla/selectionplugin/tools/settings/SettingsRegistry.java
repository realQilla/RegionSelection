package net.qilla.selectionplugin.tools.settings;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsRegistry {
    private final Map<UUID, WandSettings> settingRegistry = new HashMap<>();
    private static SettingsRegistry instance = null;

    private SettingsRegistry() {
    }

    public static SettingsRegistry getInstance() {
        if(instance == null) instance = new SettingsRegistry();
        return instance;
    }

    public WandSettings getPlayer(Player player) {
        return settingRegistry.computeIfAbsent(player.getUniqueId(), k -> new WandSettings());
    }

    public void createPlayer(Player player) {
        this.settingRegistry.put(player.getUniqueId(), new WandSettings());
    }

    public void removePlayer(Player player) {
        this.settingRegistry.remove(player.getUniqueId());
    }
}