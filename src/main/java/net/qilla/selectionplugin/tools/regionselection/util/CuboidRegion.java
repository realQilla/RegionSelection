package net.qilla.selectionplugin.tools.regionselection.util;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public final class CuboidRegion {

    private Block origin;
    private Block end;

    public CuboidRegion(@NotNull Block origin, @NotNull Block end) {
        this.origin = origin;
        this.end = end;
    }

    @NotNull
    public Block setOrigin(@NotNull Block origin) {
        return this.origin = origin;
    }

    @NotNull
    public Block setEnd(@NotNull Block end) {
        return this.end = end;
    }

    @NotNull
    public Block getOrigin() {
        return this.origin;
    }

    @NotNull
    public Block getEnd() {
        return this.end;
    }

    public int getSize() {
        int x = Math.abs(this.origin.getX() - this.end.getX()) + 1;
        int y = Math.abs(this.origin.getY() - this.end.getY()) + 1;
        int z = Math.abs(this.origin.getZ() - this.end.getZ()) + 1;
        return x * y * z;
    }
}