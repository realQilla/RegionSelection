package net.qilla.selectionplugin.tools.regionselection;

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.qilla.selectionplugin.tools.regionselection.util.CuboidRegion;
import org.bukkit.craftbukkit.entity.CraftBlockDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class RegionShard {

    private final WandVariant variant;
    private final WandContainer container;
    private final CuboidRegion cuboidRegion;
    private final List<CraftBlockDisplay> outline;

    RegionShard(@NotNull WandVariant variant, @NotNull CuboidRegion cuboidRegion, @NotNull WandContainer container) {
        this.variant = variant;
        this.cuboidRegion = cuboidRegion;
        this.container = container;
        this.outline = new ArrayList<>();
    }

    void setOutline(List<CraftBlockDisplay> regionPreview) {
        this.outline.addAll(regionPreview);
    }

    void clearOutline() {
        if(this.outline.isEmpty()) return;

        ServerPlayer nmsPlayer = ((CraftPlayer) container.getPlayer()).getHandle();
        this.outline.forEach(entity -> {
            nmsPlayer.connection.sendPacket(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
        });
    }

    @NotNull
    public WandVariant getVariant() {
        return variant;
    }

    @NotNull
    public CuboidRegion getRegion() {
        return this.cuboidRegion;
    }

    @NotNull
    public List<CraftBlockDisplay> getOutline() {
        return this.outline;
    }
}