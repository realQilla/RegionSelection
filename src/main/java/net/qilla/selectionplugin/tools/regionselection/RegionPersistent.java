package net.qilla.selectionplugin.tools.regionselection;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.qilla.selectionplugin.SelectionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftBlockDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RegionPersistent {

    private final Plugin plugin = SelectionPlugin.getInstance();
    private final WandContainer wandContainer;
    private final Player player;
    private WandVariant wandVariant;
    private BukkitTask updateTask;
    private Block previewPos;
    private final List<CraftBlockDisplay> previewCuboid = new ArrayList<>();

    protected RegionPersistent(@NotNull final WandContainer wandContainer) {
        this.wandContainer = wandContainer;
        this.player = wandContainer.getPlayer();
    }

    public void selectRegion(final WandVariant wandVariant) {
        this.wandVariant = wandVariant;
        if(!this.wandContainer.getInstance(wandVariant).hasOrigin()) {
            this.wandContainer.getInstance(wandVariant).regionOrigin(previewPos);
            this.player.playSound(this.player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<gold><bold>POSITION A</bold></gold> <yellow>Selected @ " + this.previewPos.getX() + ", " + this.previewPos.getY() + ", " + this.previewPos.getZ() + "!</yellow>"));
        } else {
            this.wandContainer.getInstance(wandVariant).regionEnd(previewPos);
            this.player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua><bold>POSITION B</bold></aqua> <yellow>Selected @ " + this.previewPos.getX() + ", " + this.previewPos.getY() + ", " + this.previewPos.getZ() + "!</yellow>"));
            this.player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
        }
    }

    public void update() {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        if(!this.previewCuboid.isEmpty()) return;
        this.previewPos = scanForward();
        this.previewCuboid.addAll(createCuboid(this.previewPos, this.previewPos, WandVariant.WHITE));

        this.updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            final Block forwardScan = scanForward();
            if(previewPos != forwardScan) {
                this.previewPos = scanForward();
                this.previewCuboid.forEach(display -> {
                    display.teleport(previewPos.getLocation());
                    nmsPlayer.connection.sendPacket(new ClientboundTeleportEntityPacket(display.getHandle()));
                });
            }

            if(this.wandContainer.hasInstance(this.wandVariant) && !this.wandContainer.getInstance(wandVariant).hasEnd()) {
                updateLoc(this.wandContainer.getInstance(wandVariant).getCuboid(), this.wandContainer.getInstance(wandVariant).getOrigin(), previewPos);
            }
        }, 0, 1);
    }

    public boolean isPreviewActive() {
        return !this.previewCuboid.isEmpty();
    }

    public void unselect() {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        if(!this.previewCuboid.isEmpty()) {
            this.updateTask.cancel();
            this.previewCuboid.forEach(entity -> {
                nmsPlayer.connection.sendPacket(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
            });
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
        this.player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region <bold><#" + this.wandVariant.getHex() + ">" + this.wandVariant + "</#" + this.wandVariant.getHex() + "></bold> has been <red><bold>CLEARED</bold></red>!</yellow>"));
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

    protected List<CraftBlockDisplay> createCuboid(Block origin, Block end, WandVariant variant) {
        Level nmsWorld = ((CraftWorld) origin.getWorld()).getHandle();
        CraftServer craftServer = nmsWorld.getCraftServer();
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        int minX = Math.min(origin.getX(), end.getX());
        int minY = Math.min(origin.getY(), end.getY());
        int minZ = Math.min(origin.getZ(), end.getZ());
        int maxX = Math.max(origin.getX(), end.getX());
        int maxY = Math.max(origin.getY(), end.getY());
        int maxZ = Math.max(origin.getZ(), end.getZ());

        int xDist = maxX - minX + 1;
        int yDist = maxY - minY + 1;
        int zDist = maxZ - minZ + 1;

        List<CraftBlockDisplay> displayList = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            final Vector3f position = getPositions(xDist, yDist, zDist)[i];
            final Vector3f size = getSizes(xDist, yDist, zDist)[i];

            CraftBlockDisplay display = new CraftBlockDisplay(craftServer, net.minecraft.world.entity.EntityType.BLOCK_DISPLAY.create(nmsWorld));
            display.setGlowing(true);
            display.setGlowColorOverride(variant.getColor());
            display.setBlock(variant.getMaterial().createBlockData());
            display.setBrightness(new Display.Brightness(15, 15));
            display.setTransformation(new Transformation(
                    position,
                    new Quaternionf(),
                    size,
                    new Quaternionf()));
            displayList.add(display);

            BlockPos blockPos = new BlockPos(origin.getX(), origin.getY(), origin.getZ());
            nmsPlayer.connection.sendPacket(new ClientboundAddEntityPacket(display.getHandle(), display.getEntityId(), blockPos));
            nmsPlayer.connection.sendPacket(new ClientboundSetEntityDataPacket(display.getHandle().getId(), display.getHandle().getEntityData().packAll()));
        }
        return displayList;
    }

    protected void updateLoc(final List<CraftBlockDisplay> displayList, final Block origin, final Block end) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

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
            CraftBlockDisplay display = displayList.get(i);

            display.teleport(new Location(origin.getWorld(), minX, minY, minZ));
            display.setTransformation(new Transformation(
                    position,
                    new Quaternionf(),
                    size,
                    new Quaternionf()));

            nmsPlayer.connection.sendPacket(new ClientboundTeleportEntityPacket(display.getHandle()));
            nmsPlayer.connection.sendPacket(new ClientboundSetEntityDataPacket(display.getHandle().getId(), display.getHandle().getEntityData().packAll()));
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