package net.qilla.selectionplugin.tools.regionselection;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBlockDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class RegionInstance {

    private final WandContainer wandContainer;
    private final WandVariant wandVariant;
    private final List<CraftBlockDisplay> regionCuboid = new ArrayList<>();
    private Block originPos;
    private Block endPos;

    RegionInstance(@NotNull WandContainer wandContainer, @NotNull WandVariant wandVariant) {
        this.wandContainer = wandContainer;
        this.wandVariant = wandVariant;
    }

    void clear() {
        ServerPlayer nmsPlayer = ((CraftPlayer) wandContainer.getPlayer()).getHandle();
        this.regionCuboid.forEach(entity -> {
            nmsPlayer.connection.sendPacket(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
        });
    }

    void regionOrigin(Block block) {
        this.originPos = block;
        this.regionCuboid.addAll(this.wandContainer.getRegionPersistent().createCuboid(this.originPos, this.originPos, this.wandVariant));
    }

    void regionEnd(Block endPos) {
        this.endPos = endPos;
        this.wandContainer.getRegionPersistent().updateLoc(this.regionCuboid, this.originPos, this.endPos);
    }

    List<CraftBlockDisplay> getCuboid() {
        return regionCuboid;
    }

    public WandVariant getWandVariant() {
        return wandVariant;
    }

    public Block getOrigin() {
        return originPos;
    }

    public Block getEnd() {
        return endPos;
    }

    public boolean hasOrigin() {
        return originPos != null;
    }

    public boolean hasEnd() {
        return endPos != null;
    }
}