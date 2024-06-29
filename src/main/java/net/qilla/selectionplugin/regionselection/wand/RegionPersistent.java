package net.qilla.selectionplugin.regionselection.wand;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.SelectionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.List;

public class RegionPersistent {

    private final Plugin plugin = SelectionPlugin.getInstance();
    private final WandContainer wandContainer;
    private final Player player;
    private WandVariant wandVariant;
    private BukkitTask updateTask;
    private Block previewPos;
    private final List<BlockDisplay> previewCuboid = new ArrayList<>();

    protected RegionPersistent(@NotNull final WandContainer wandContainer) {
        this.wandContainer = wandContainer;
        this.player = wandContainer.getPlayer();
    }

    public void selectRegion(final WandVariant wandVariant) {
        this.wandVariant = wandVariant;
        if(!this.wandContainer.getInstance(wandVariant).hasOrigin()) {
            this.wandContainer.getInstance(wandVariant).regionOrigin(previewPos);
            this.player.playSound(this.player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<gold><bold>SELECTED POINT A</bold> <yellow>" + this.previewPos.getX() + ", " + this.previewPos.getY() + ", " + this.previewPos.getZ() + "</yellow>!</gold>"));
        } else {
            this.wandContainer.getInstance(wandVariant).regionEnd(previewPos);
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua><bold>SELECTED POINT B</bold> <yellow>" + this.previewPos.getX() + ", " + this.previewPos.getY() + ", " + this.previewPos.getZ() + "</yellow>!</aqua>"));
            this.player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
        }
    }

    public void update() {
        if(!this.previewCuboid.isEmpty()) return;
        this.previewPos = scanForward();
        this.previewCuboid.addAll(createCuboid(this.previewPos, this.previewPos, WandVariant.WHITE));

        this.updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            final Block forwardScan = scanForward();
            if(previewPos != forwardScan) {
                this.previewPos = scanForward();
                this.previewCuboid.forEach(display -> {
                    display.teleport(previewPos.getLocation());
                });
            }

            if(this.wandContainer.hasInstance(this.wandVariant) && !this.wandContainer.getInstance(wandVariant).hasEnd()) {
                updateLoc(this.wandContainer.getInstance(wandVariant).getCuboid(), this.wandContainer.getInstance(wandVariant).getOrigin(), previewPos);
            }
        }, 0, 1);
    }

    public boolean activePreview() {
        return !this.previewCuboid.isEmpty();
    }

    public void unselect() {
        if(!this.previewCuboid.isEmpty()) {
            this.updateTask.cancel();
            this.previewCuboid.forEach(Entity::remove);
            this.previewCuboid.clear();
            this.previewPos = null;
        }

        if(!this.wandContainer.getInstance(wandVariant).hasEnd()) {
            this.wandContainer.getInstance(wandVariant).clear();
        }
    }

    public void clearWand(final WandVariant wandVariant) {
        this.wandVariant = wandVariant;
        this.wandContainer.getInstance(wandVariant).clear();
        this.player.playSound(this.player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 1);
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red><bold>SELECTION <#" + this.wandVariant.getHex() + ">" + this.wandVariant + "</#" + this.wandVariant.getHex() + "></bold> has been cleared!</red>"));
    }

    private Block scanForward() {
        final Location eyeLoc = this.player.getEyeLocation();
        Block selection = null;

        for(int i = 1; i <= 5; i++) {
            selection = eyeLoc.clone().add(eyeLoc.getDirection().multiply(i)).getBlock();
            if(selection.getType() != Material.AIR) {
                return selection;
            }
        }
        return selection;
    }

    protected List<BlockDisplay> createCuboid(final Block origin, final Block end, final WandVariant variant) {
        int minX = Math.min(origin.getX(), end.getX());
        int minY = Math.min(origin.getY(), end.getY());
        int minZ = Math.min(origin.getZ(), end.getZ());
        int maxX = Math.max(origin.getX(), end.getX());
        int maxY = Math.max(origin.getY(), end.getY());
        int maxZ = Math.max(origin.getZ(), end.getZ());

        int xDist = maxX - minX + 1;
        int yDist = maxY - minY + 1;
        int zDist = maxZ - minZ + 1;

        List<BlockDisplay> blockDisplay = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            final Vector3f position = getPositions(xDist, yDist, zDist)[i];
            final Vector3f size = getSizes(xDist, yDist, zDist)[i];

            blockDisplay.add(origin.getWorld().spawn(origin.getLocation(), BlockDisplay.class, display -> {
                display.setGlowColorOverride(variant.getColor());
                display.setBlock(variant.getMaterial().createBlockData());
                display.setGlowing(true);
                display.setBrightness(new Display.Brightness(15, 15));
                display.setTransformation(new Transformation(
                        position,
                        new Quaternionf(),
                        size,
                        new Quaternionf()));
            }));
        }
        return blockDisplay;
    }

    protected void updateLoc(final List<BlockDisplay> display, final Block origin, final Block end) {
        int minX = Math.min(origin.getX(), end.getX());
        int minY = Math.min(origin.getY(), end.getY());
        int minZ = Math.min(origin.getZ(), end.getZ());
        int maxX = Math.max(origin.getX(), end.getX());
        int maxY = Math.max(origin.getY(), end.getY());
        int maxZ = Math.max(origin.getZ(), end.getZ());

        int xDist = maxX - minX + 1;
        int yDist = maxY - minY + 1;
        int zDist = maxZ - minZ + 1;

        for(int i = 0; i < 12; i++) {
            final Vector3f position = getPositions(xDist, yDist, zDist)[i];
            final Vector3f size = getSizes(xDist, yDist, zDist)[i];

            display.get(i).teleport(new Location(origin.getWorld(), minX, minY, minZ));
            display.get(i).setTransformation(new Transformation(
                    position,
                    new Quaternionf(),
                    size,
                    new Quaternionf()));
        }
    }

    private Vector3f[] getPositions(int xDistance, int yDistance, int zDistance) {
        return new Vector3f[]{new Vector3f(0.05f, yDistance - 0.05f, 0), // Top Left Front
                new Vector3f(0, yDistance - 0.05f, 0.05f), // Top Right Front
                new Vector3f(xDistance - 0.05f, yDistance - 0.05f, 0.05f), // Top Left Back
                new Vector3f(0.05f, yDistance - 0.05f, zDistance - 0.05f), // Top Right Back

                new Vector3f(0.05f, 0, 0), // Bottom Left Front
                new Vector3f(0, 0, 0.05f), // Bottom Right Front
                new Vector3f(xDistance - 0.05f, 0, 0.05f), // Bottom Left Back
                new Vector3f(0.05f, 0, zDistance - 0.05f), // Bottom Right Back

                new Vector3f(xDistance - 0.05f, 0.05f, 0), //Left Edge
                new Vector3f(0, 0.05f, zDistance - 0.05f), // Right Edge
                new Vector3f(0, 0.05f, 0), // Front Edge
                new Vector3f(xDistance - 0.05f, 0.05f, zDistance - 0.05f) // Back Edge
        };
    }

    private Vector3f[] getSizes(int xDistance, int yDistance, int zDistance) {
        float edgeSize = 0.05f;

        return new Vector3f[]{new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Top Left Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Top Right Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Top Left Back
                new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Top Right Back

                new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Bottom Left Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Bottom Right Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Bottom Right Back
                new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Bottom Right Back

                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Left Edge
                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Right Edge
                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Front Edge
                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Back Edge
        };
    }
}