package com.marketcraft;

import com.marketcraft.Commands.OpenStorageCommand;
import com.marketcraft.Commands.HelpCommand;
import com.marketcraft.Commands.RemoveStorageCommand;
import com.marketcraft.Commands.VersionCommand;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * CommandHandler is responsible for routing and handling command execution
 * within the MarketCraft plugin. It serves as the primary entry point for
 * processing commands issued by players or the console.
 * <p>
 * This class handles the selection of appropriate subcommands based on the
 * user input, manages command permission checks, and provides tab completion
 * support. It also includes logic for handling unknown or invalid commands,
 * guiding users towards valid command usage.
 * <p>
 * To maintain a clean and organized code structure, individual command
 * logics are encapsulated in separate classes. This separation allows for
 * easier management and extension of command functionalities. Each specific
 * command, such as 'help' and 'version', is handled by its own dedicated
 * class (e.g., {@link HelpCommand}, {@link VersionCommand}).
 * <p>
 * The CommandHandler delegates the actual execution of commands to these
 * individual command classes, thus decoupling the command routing logic
 * from the command execution logic. This architecture allows for scalable
 * and organized handling of a growing number of commands within the plugin.
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    private final HelpCommand helpCommand;
    private final VersionCommand versionCommand;
    private final OpenStorageCommand openStorageCommand;
    private final RemoveStorageCommand removeStorageCommand;

    public CommandHandler(PlayerVaultManager playerVaultManager) {
        this.removeStorageCommand = new RemoveStorageCommand(playerVaultManager);
        this.helpCommand = new HelpCommand();
        this.versionCommand = new VersionCommand();
        this.openStorageCommand = new OpenStorageCommand(playerVaultManager);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("marketcraft.wip")) {
            sender.sendMessage(Component.text("You don't have permission to run this command.", NamedTextColor.RED));
            return false;
        }

        if (args.length == 0) {
            handleUnknownCommand(sender);
            return false;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "help" -> helpCommand.handleHelpCommand(sender);
            case "version" -> versionCommand.handleVersionCommand(sender);
            case "openstorage" -> openStorageCommand.handleOpenStorageCommand(sender);
            case "removestorage" -> removeStorageCommand.handleRemoveStorageCommand(sender, args);
            default -> {
                handleUnknownCommand(sender);
                yield false;
            }
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String[] commands = {"help", "version", "openstorage"};
            for (String cmd : commands) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    completions.add(cmd);
                }
            }
        }

        return completions;
    }

    private void handleUnknownCommand(CommandSender sender) {
        sender.sendMessage(Component.text("Unknown command. Use one of the following:", NamedTextColor.RED));
        String[] commands = {"help", "version"};
        for (String cmd : commands) {
            sender.sendMessage(Component.text("/marketcraft " + cmd, NamedTextColor.GRAY));
        }
    }
}