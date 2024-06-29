package net.qilla.selectionplugin.tools.regionselection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class WandContainer {

    private final Player player;
    private final RegionPersistent regionPersistent;
    private final Map<WandVariant, RegionInstance> regionInstances;
    private WandVariant activeInstance;
    
    public WandContainer(Player player) {
        this.player = player;
        this.regionPersistent = new RegionPersistent(this);
        this.regionInstances =  new EnumMap<>(WandVariant.class);
    }

    @NotNull
    public RegionInstance getInstance(WandVariant wandVariant) {
        this.activeInstance = wandVariant;
        return this.regionInstances.computeIfAbsent(wandVariant, k -> new RegionInstance(this, wandVariant));
    }

    @NotNull
    public List<RegionInstance> getInstances() {
        return this.regionInstances.values().stream().toList();
    }

    public boolean isInstanceAlive() {
        return this.regionInstances.containsKey(this.activeInstance);
    }

    public void removeInstance(WandVariant wandVariant) {
        if(!this.regionInstances.containsKey(wandVariant)) return;
        this.regionInstances.get(wandVariant).clear();
        this.regionInstances.remove(wandVariant);
    }

    public boolean hasInstance(WandVariant wandVariant) {
        return this.regionInstances.containsKey(wandVariant);
    }

    @NotNull
    public RegionPersistent getRegionPersistent() {
        return this.regionPersistent;
    }

    @NotNull
    Player getPlayer() {
        return this.player;
    }
}