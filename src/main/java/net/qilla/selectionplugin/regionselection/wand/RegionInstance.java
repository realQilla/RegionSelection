package net.qilla.selectionplugin.regionselection.wand;

import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RegionInstance {

    private final WandContainer wandStorage;
    private final WandVariant wandVariant;
    private final List<BlockDisplay> regionCuboid = new ArrayList<>();
    private Block originPos;
    private Block endPos;

    protected RegionInstance(@NotNull final WandContainer wandContainer, @NotNull final WandVariant wandVariant) {
        this.wandStorage = wandContainer;
        this.wandVariant = wandVariant;
    }

    protected void clear() {
        this.regionCuboid.forEach(Entity::remove);
        this.wandStorage.removeInstance(wandVariant);
    }

    protected void regionOrigin(final Block block) {
        this.originPos = block;
        this.regionCuboid.addAll(this.wandStorage.getPersistent().createCuboid(this.originPos, this.originPos, this.wandVariant));
    }

    protected void regionEnd(final Block endPos) {
        this.endPos = endPos;
        this.wandStorage.getPersistent().updateLoc(this.regionCuboid, this.originPos, this.endPos);
    }

    protected List<BlockDisplay> getCuboid() {
        return regionCuboid;
    }

    protected Block getOrigin() {
        return originPos;
    }

    protected Block getEnd() {
        return endPos;
    }

    protected boolean hasOrigin() {
        return originPos != null;
    }

    protected boolean hasEnd() {
        return endPos != null;
    }
}