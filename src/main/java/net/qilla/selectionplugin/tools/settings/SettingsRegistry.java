package net.qilla.selectionplugin.tools.settings;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsRegistry {
    private final Map<UUID, PlayerWandSettings> settingRegistry = new HashMap<>();
    private static SettingsRegistry instance = null;

    private SettingsRegistry() {
    }

    public static SettingsRegistry getInstance() {
        if(instance == null) instance = new SettingsRegistry();
        return instance;
    }

    public PlayerWandSettings getPlayer(Player player) {
        return settingRegistry.computeIfAbsent(player.getUniqueId(), k -> new PlayerWandSettings());
    }

    public void createPlayer(Player player) {
        this.settingRegistry.put(player.getUniqueId(), new PlayerWandSettings());
    }
}