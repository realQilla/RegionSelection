package net.qilla.selectionplugin.tools.regionselection;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WandContainer {

    private final Player player;
    private final WandSettings settings;
    private final RegionCore core;
    private final Map<WandVariant, RegionShard> regionShards;

    public WandContainer(Player player) {
        this.player = player;
        this.settings = new WandSettings();
        this.core = new RegionCore(this);
        this.regionShards = new EnumMap<>(WandVariant.class);
    }

    public void setShard(@NotNull RegionShard regionShard) {
        this.regionShards.put(regionShard.getVariant(), regionShard);
    }

    @NotNull
    Player getPlayer() {
        return this.player;
    }

    @NotNull
    public WandSettings getSettings() {
        return this.settings;
    }

    @NotNull
    public RegionCore getCore() {
        return this.core;
    }

    @Nullable
    public RegionShard getShard(@NotNull WandVariant wandVariant) {
        return this.regionShards.get(wandVariant);
    }

    @NotNull
    public List<RegionShard> getShards() {
        return this.regionShards.values().stream().toList();
    }


    public void removeShard(@NotNull WandVariant wandVariant) {
        if(!this.regionShards.containsKey(wandVariant)) return;

        this.regionShards.get(wandVariant).clearOutline();
        this.regionShards.remove(wandVariant);
    }

    public boolean hasShard(@NotNull WandVariant wandVariant) {
        return this.regionShards.containsKey(wandVariant);
    }
}