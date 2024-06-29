package net.qilla.selectionplugin.regionselection.wand;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class WandContainer {

    private final Player player;
    private final RegionPersistent persistent;
    private final Map<WandVariant, RegionInstance> instances;
    
    public WandContainer(final Player player) {
        this.player = player;
        this.persistent = new RegionPersistent(this);
        this.instances =  new EnumMap<>(WandVariant.class);
    }

    @NotNull
    public RegionInstance getInstance(final WandVariant wandVariant) {
        return this.instances.computeIfAbsent(wandVariant, k -> new RegionInstance(this, wandVariant));
    }

    public void removeInstance(final WandVariant wandVariant) {
        this.instances.remove(wandVariant);
    }

    public boolean hasInstance(final WandVariant wandVariant) {
        return this.instances.containsKey(wandVariant);
    }

    @NotNull
    public RegionPersistent getPersistent() {
        return this.persistent;
    }

    @NotNull
    protected Player getPlayer() {
        return this.player;
    }
}
