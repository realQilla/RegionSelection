package net.qilla.selectionplugin.tools.regionselection;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.qilla.selectionplugin.SelectionPlugin;
import net.qilla.selectionplugin.tools.regionselection.util.CuboidRegion;
import net.qilla.selectionplugin.tools.regionselection.util.CuboidSpecs;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RegionCore {

    private final Plugin plugin = SelectionPlugin.getInstance();
    private final WandContainer container;
    private final WandSettings settings;

    private BukkitTask tickInfo;
    private BukkitTask tickTask;
    private Block previewPos;
    private List<CraftBlockDisplay> outline;

    private RegionShard cachedShard;

    RegionCore(@NotNull WandContainer container) {
        this.container = container;
        this.settings = this.container.getSettings();
    }

    public void selectRegion() {
        Optional<RegionShard> optRegionShard = this.container.getActiveShard();

        if(optRegionShard.isPresent()) {
            RegionShard regionShard = optRegionShard.get();

            regionShard.getRegion().setEnd(previewPos);
            updateLoc(regionShard.getOutline(), regionShard.getRegion());
            this.container.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region <bold><#" + this.settings.getVariant().getHex() + ">" + this.settings.getVariant() + "</#" + this.settings.getVariant().getHex() + "> <aqua>UPDATED</aqua></bold> successfully. Size of " + NumberFormat.getInstance().format(regionShard.getRegion().getSize()) + " blocks!</yellow>"));
            this.container.getPlayer().playSound(this.container.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
        } else {
            if(this.cachedShard == null) {
                CuboidRegion cuboidRegion = new CuboidRegion(this.previewPos, this.previewPos);
                this.cachedShard = new RegionShard(this.settings.getVariant(), cuboidRegion, this.container);
                this.cachedShard.setOutline(createCuboid(cuboidRegion, this.settings.getVariant()));

                this.container.getPlayer().playSound(this.container.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);
                this.container.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<gold><bold>POSITION A</bold></gold> <yellow>Selected @ " + this.previewPos.getX() + ", " + this.previewPos.getY() + ", " + this.previewPos.getZ() + "!</yellow>"));
            } else {
                this.cachedShard.getRegion().setEnd(this.previewPos);
                updateLoc(this.cachedShard.getOutline(), this.cachedShard.getRegion());
                this.container.setShard(this.cachedShard);

                this.container.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<aqua><bold>POSITION B</bold></aqua> <yellow>Selected @ " + this.previewPos.getX() + ", " + this.previewPos.getY() + ", " + this.previewPos.getZ() + "!</yellow>"));
                this.container.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region <bold><#" + this.settings.getVariant().getHex() + ">" + this.settings.getVariant() + "</#" + this.settings.getVariant().getHex() + "> <green>CREATED</green></bold> successfully. Size of " + NumberFormat.getInstance().format(this.cachedShard.getRegion().getSize()) + " blocks!</yellow>"));
                this.container.getPlayer().playSound(this.container.getPlayer(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);

                this.cachedShard = null;
            }
        }
    }

    public void tickOutline() {
        if(this.outline != null) return;

        ServerPlayer nmsPlayer = ((CraftPlayer) this.container.getPlayer()).getHandle();
        this.previewPos = scanForward();
        this.outline = createCuboid(new CuboidRegion(this.previewPos, this.previewPos), WandVariant.WHITE);
        tickInfo(false);

        this.tickTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            final Block forwardScan = scanForward();

            if(!previewPos.equals(forwardScan)) {
                this.previewPos = scanForward();
                this.outline.forEach(display -> {
                    display.teleport(previewPos.getLocation());
                    nmsPlayer.connection.sendPacket(new ClientboundTeleportEntityPacket(display.getHandle()));
                });

                if(this.cachedShard != null) {
                    this.cachedShard.getRegion().setEnd(this.previewPos);
                    updateLoc(this.cachedShard.getOutline(), this.cachedShard.getRegion());
                }
            }
        }, 0, 1);
    }

    public void removeNonSaved() {
        if(this.outline == null) return;

        if(this.container.getPlayer().isOnline()) {
            ServerPlayer nmsPlayer = ((CraftPlayer) this.container.getPlayer()).getHandle();

            this.tickTask.cancel();
            this.outline.forEach(entity -> {
                nmsPlayer.connection.sendPacket(new ClientboundRemoveEntitiesPacket(entity.getEntityId()));
            });
        }

        this.outline = null;
        clearTickInfo();

        if(this.cachedShard != null) {
            this.cachedShard.clearOutline();
            this.cachedShard = null;
        }
    }

    public void removeSavedShard() {
        if(this.cachedShard != null) {
            this.cachedShard.clearOutline();
            this.cachedShard = null;
            this.container.getPlayer().playSound(this.container.getPlayer(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 1);
            return;
        }

        if(this.container.hasShard(this.settings.getVariant())) {
            this.container.removeShard(this.settings.getVariant());
            this.container.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Region <bold><#" + this.settings.getVariant().getHex() + ">" + this.settings.getVariant() + "</#" + this.settings.getVariant().getHex() + "> <red>REMOVED</red></bold> successfully!</yellow>"));
            this.container.getPlayer().playSound(this.container.getPlayer(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 1);
        }
    }

    public void tickInfo(boolean update) {
        if(update) {
            this.container.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>Selection variant <#" + this.settings.getVariant().getHex() + "><bold>" + this.settings.getVariant() + "</#" + this.settings.getVariant().getHex() + "></yellow>"));
            return;
        }
        if(this.tickInfo != null) return;
        this.tickInfo = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            this.container.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>Selection variant <#" + this.settings.getVariant().getHex() + "><bold>" + this.settings.getVariant() + "</#" + this.settings.getVariant().getHex() + "></yellow>"));
        },0, 40);
    }

    public void clearTickInfo() {
        if(tickInfo == null) return;
        this.tickInfo.cancel();
        this.tickInfo = null;
        this.container.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize(""));
    }

    private Block scanForward() {
        final Location eyeLoc = this.container.getPlayer().getEyeLocation();
        Block selection = null;

        for(int i = 1; i <= settings.wandReach().getValue(); i++) {
            selection = eyeLoc.clone().add(eyeLoc.getDirection().multiply(i)).getBlock();
            if(selection.getType() != Material.AIR) {
                return selection;
            }
        }
        return selection;
    }

    List<CraftBlockDisplay> createCuboid(@NotNull CuboidRegion cuboidRegion, @NotNull WandVariant variant) {
        Level nmsWorld = ((CraftWorld) cuboidRegion.getOrigin().getWorld()).getHandle();
        CraftServer craftServer = nmsWorld.getCraftServer();
        ServerPlayer nmsPlayer = ((CraftPlayer) this.container.getPlayer()).getHandle();

        int minX = Math.min(cuboidRegion.getOrigin().getX(), cuboidRegion.getEnd().getX());
        int minY = Math.min(cuboidRegion.getOrigin().getY(), cuboidRegion.getEnd().getY());
        int minZ = Math.min(cuboidRegion.getOrigin().getZ(), cuboidRegion.getEnd().getZ());
        int maxX = Math.max(cuboidRegion.getOrigin().getX(), cuboidRegion.getEnd().getX());
        int maxY = Math.max(cuboidRegion.getOrigin().getY(), cuboidRegion.getEnd().getY());
        int maxZ = Math.max(cuboidRegion.getOrigin().getZ(), cuboidRegion.getEnd().getZ());

        int xDist = maxX - minX + 1;
        int yDist = maxY - minY + 1;
        int zDist = maxZ - minZ + 1;

        List<CraftBlockDisplay> displayList = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            final Vector3f position = CuboidSpecs.getPositions(xDist, yDist, zDist)[i];
            final Vector3f size = CuboidSpecs.getSizes(xDist, yDist, zDist)[i];

            CraftBlockDisplay display = new CraftBlockDisplay(craftServer, net.minecraft.world.entity.EntityType.BLOCK_DISPLAY.create(nmsWorld));
            display.setGlowing(true);
            display.setGlowColorOverride(variant.getColor());
            display.setBlock(variant.getMainMaterial().createBlockData());
            display.setBrightness(new Display.Brightness(15, 15));
            display.setTransformation(new Transformation(
                    position,
                    new Quaternionf(),
                    size,
                    new Quaternionf()));
            displayList.add(display);

            BlockPos blockPos = new BlockPos(cuboidRegion.getOrigin().getX(), cuboidRegion.getOrigin().getY(), cuboidRegion.getOrigin().getZ());
            nmsPlayer.connection.sendPacket(new ClientboundAddEntityPacket(display.getHandle(), display.getEntityId(), blockPos));
            nmsPlayer.connection.sendPacket(new ClientboundSetEntityDataPacket(display.getHandle().getId(), display.getHandle().getEntityData().packAll()));
        }
        return displayList;
    }

    void updateLoc(@NotNull List<CraftBlockDisplay> displayList, @NotNull CuboidRegion cuboidRegion) {
        ServerPlayer nmsPlayer = ((CraftPlayer) this.container.getPlayer()).getHandle();

        int minX = Math.min(cuboidRegion.getOrigin().getX(), cuboidRegion.getEnd().getX());
        int minY = Math.min(cuboidRegion.getOrigin().getY(), cuboidRegion.getEnd().getY());
        int minZ = Math.min(cuboidRegion.getOrigin().getZ(), cuboidRegion.getEnd().getZ());
        int maxX = Math.max(cuboidRegion.getOrigin().getX(), cuboidRegion.getEnd().getX());
        int maxY = Math.max(cuboidRegion.getOrigin().getY(), cuboidRegion.getEnd().getY());
        int maxZ = Math.max(cuboidRegion.getOrigin().getZ(), cuboidRegion.getEnd().getZ());

        int xDist = maxX - minX + 1;
        int yDist = maxY - minY + 1;
        int zDist = maxZ - minZ + 1;

        for(int i = 0; i < displayList.size(); i++) {
            final Vector3f position = CuboidSpecs.getPositions(xDist, yDist, zDist)[i];
            final Vector3f size = CuboidSpecs.getSizes(xDist, yDist, zDist)[i];
            CraftBlockDisplay display = displayList.get(i);

            display.teleport(new Location(cuboidRegion.getOrigin().getWorld(), minX, minY, minZ));
            display.setTransformation(new Transformation(
                    position,
                    new Quaternionf(),
                    size,
                    new Quaternionf()));

            nmsPlayer.connection.sendPacket(new ClientboundTeleportEntityPacket(display.getHandle()));
            nmsPlayer.connection.sendPacket(new ClientboundSetEntityDataPacket(display.getHandle().getId(), display.getHandle().getEntityData().packAll()));
        }
    }
}