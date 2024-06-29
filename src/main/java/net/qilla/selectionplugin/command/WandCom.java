package net.qilla.selectionplugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.qilla.selectionplugin.SelectionPlugin;
import net.qilla.selectionplugin.tools.regionselection.WandVariant;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WandCom {

    private final Commands commands;
    private final SelectionPlugin plugin;

    private final String command = "wand";
    private final List<String> commandAlis = List.of();
    private final String argVariant = "variant";

    public WandCom(Commands commands, SelectionPlugin plugin) {
        this.commands = commands;
        this.plugin = plugin;
    }

    public void register() {
        final LiteralArgumentBuilder<CommandSourceStack> commandNode = Commands
                .literal(command)
                .requires(source -> source.getSender() instanceof Player && source.getSender().hasPermission("selection.tools"))
                .executes(this::usage);

        final ArgumentCommandNode<CommandSourceStack, String> variantNode = Commands
                .argument(argVariant, StringArgumentType.word())
                .suggests((context, builder) -> {
                   final String argument = builder.getRemaining();

                   for(WandVariant variant : WandVariant.values()) {
                       String variantString = variant.toString();
                       if(variantString.regionMatches(true, 0, argument, 0, argument.length()))
                           builder.suggest(variantString);
                   }
                   return builder.buildFuture();
                }).executes(this::setVariant).build();

        commandNode.then(variantNode);

        this.commands.register(commandNode.build(), commandAlis);
    }

    private int usage(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid arguments.</red>"));
        return Command.SINGLE_SUCCESS;
    }

    private int setVariant(CommandContext<CommandSourceStack> context) {
        final Player player = (Player) context.getSource().getSender();
        final String specifiedVariant = context.getArgument(argVariant, String.class).toUpperCase();

        WandVariant variant;
        try {
            variant = WandVariant.valueOf(specifiedVariant);
        } catch(IllegalArgumentException e){
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid variant.</red>"));
            return Command.SINGLE_SUCCESS;
        }

        this.plugin.getSettingsRegistry().getPlayer(player).setVariant(variant);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Wand Variant set to <#" + variant.getHex() + ">" + variant + "</#" + variant.getHex() +">!</green>"));
        return Command.SINGLE_SUCCESS;
    }
}
