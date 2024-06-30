package net.qilla.selectionplugin.tools.regionselection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class WandContainer {

    private final Player player;
    private final WandSettings settings;
    private final RegionPersistent persistent;
    private final Map<WandVariant, RegionInstance> instances;
    
    public WandContainer(Player player) {
        this.player = player;
        this.settings = new WandSettings();
        this.persistent = new RegionPersistent(this);
        this.instances =  new EnumMap<>(WandVariant.class);
    }

    @NotNull
    public RegionPersistent getPersistent() {
        return this.persistent;
    }

    @NotNull
    public RegionInstance getInstance(WandVariant wandVariant) {
        return this.instances.computeIfAbsent(wandVariant, k -> new RegionInstance(this, wandVariant));
    }

    @NotNull
    public List<RegionInstance> getInstances() {
        return this.instances.values().stream().toList();
    }

    @NotNull
    public WandSettings getSettings() {
        return this.settings;
    }

    public void removeInstance(WandVariant wandVariant) {
        if(!this.instances.containsKey(wandVariant)) return;
        this.instances.get(wandVariant).clear();
        this.instances.remove(wandVariant);
    }

    public boolean hasInstance(WandVariant wandVariant) {
        return this.instances.containsKey(wandVariant);
    }

    @NotNull
    Player getPlayer() {
        return this.player;
    }
}