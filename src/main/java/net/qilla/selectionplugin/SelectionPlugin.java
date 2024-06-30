package net.qilla.selectionplugin;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.qilla.selectionplugin.command.ToolsCom;
import net.qilla.selectionplugin.command.WandCom;
import net.qilla.selectionplugin.gui.GUIListener;
import net.qilla.selectionplugin.tools.regionselection.WandContainerRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SelectionPlugin extends JavaPlugin {

    private final LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
    private final WandContainerRegistry wandContainerRegistry = WandContainerRegistry.getContainer();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SelectListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        this.manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            new ToolsCom(commands).register();
            new WandCom(commands, this).register();
        });
    }

    public static SelectionPlugin getInstance() {
        return getPlugin(SelectionPlugin.class);
    }

    public WandContainerRegistry getWandContainerRegistry() {
        return this.wandContainerRegistry;
    }
}
