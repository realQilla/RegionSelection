package net.qilla.selectionplugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.qilla.selectionplugin.gui.general.GetTool;
import org.bukkit.entity.Player;

public final class ToolsCom {

    private final Commands commands;

    private final String command = "tools";

    public ToolsCom(Commands commands) {
        this.commands = commands;
    }

    public void register() {
        final LiteralArgumentBuilder<CommandSourceStack> commandNode = Commands
                .literal(command)
                .requires(source -> source.getSender() instanceof Player && source.getSender().hasPermission("selection.tools"))
                .executes(this::open);

        this.commands.register(commandNode.build());
    }

    private int open(CommandContext<CommandSourceStack> context) {
        final Player player = (Player) context.getSource().getSender();

        new GetTool(player).openInventory();
        return Command.SINGLE_SUCCESS;
    }
}
