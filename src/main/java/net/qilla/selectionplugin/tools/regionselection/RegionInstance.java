package net.qilla.selectionplugin.tools.regionselection;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBlockDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class RegionInstance {

    private final WandContainer container;
    private final WandVariant variant;
    private final List<CraftBlockDisplay> regionCuboid = new ArrayList<>();
    private Block originPos;
    private Block endPos;

    RegionInstance(@NotNull WandContainer container, @NotNull WandVariant variant) {
        this.container = container;
        this.variant = variant;
    }

    void clear() {
        ServerPlayer nmsPlayer = ((CraftPlayer) container.getPlayer()).getHandle();
        this.regionCuboid.forEach(entity -> {
            nmsPlayer.connection.sendPacket(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
        });
    }

    void regionOrigin(Block block) {
        this.originPos = block;
        this.regionCuboid.addAll(this.container.getPersistent().createCuboid(this.originPos, this.originPos, this.variant));
    }

    void regionEnd(Block endPos) {
        this.endPos = endPos;
        this.container.getPersistent().updateLoc(this.regionCuboid, this.originPos, this.endPos);
    }

    List<CraftBlockDisplay> getCuboid() {
        return regionCuboid;
    }

    public WandVariant getVariant() {
        return variant;
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

    public int getRegionSize() {
        if(!hasOrigin() || !hasEnd()) return -1;
        int x = Math.abs(this.originPos.getX() - this.endPos.getX()) + 1;
        int y = Math.abs(this.originPos.getY() - this.endPos.getY()) + 1;
        int z = Math.abs(this.originPos.getZ() - this.endPos.getZ()) + 1;
        return x * y * z;
    }
}