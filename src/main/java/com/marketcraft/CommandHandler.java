package com.marketcraft;

import com.marketcraft.Commands.*;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
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
    private final OpenVaultCommand openVaultCommand;
    private final RemoveVaultCommand removeVaultCommand;
    private final MarketCraft marketCraft;
    private final ListVaultsCommand listVaultsCommand;
    private final CreateShopCommand createShopCommand;
    private final RemoveShopCommand removeShopCommand;
    private final ListShopsCommand listShopsCommand;
    private final OpenShopCommand openShopCommand;
    private static final String[] COMMANDS = {"help", "version", "openvault", "removevault", "listvaults", "createshop", "removeshop", "listshops", "openshop"};

    public CommandHandler(PlayerVaultManager playerVaultManager, PlayerShopManager playerShopManager, MarketCraft marketCraft) {
        this.removeVaultCommand = new RemoveVaultCommand(playerVaultManager);
        this.marketCraft = marketCraft;
        this.helpCommand = new HelpCommand();
        this.versionCommand = new VersionCommand();
        this.openVaultCommand = new OpenVaultCommand(playerVaultManager);
        this.listVaultsCommand = new ListVaultsCommand(playerVaultManager);
        this.createShopCommand = new CreateShopCommand();
        this.removeShopCommand = new RemoveShopCommand(playerShopManager);
        this.listShopsCommand = new ListShopsCommand(playerShopManager);
        this.openShopCommand = new OpenShopCommand(playerShopManager, playerVaultManager, marketCraft);
    }

    /**
     * Executes the given /marketcraft command.
     * This method handles the primary command logic for the /marketcraft command set.
     * All commands require the 'marketcraft.use' permission. Some specific subcommands
     * require additional permissions, such as 'marketcraft.admin'.
     *
     * @param sender  The sender of the command, typically a player or the console.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments passed with the command.
     * @return true if the command was successfully executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("marketcraft.use")) {
            sender.sendMessage(Component.text("You don't have permission to run this command."));
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
            case "openvault" -> openVaultCommand.handleOpenVaultCommand(sender);
            case "createshop" -> createShopCommand.handleCreateShopCommand(sender, args);
            case "openshop" -> openShopCommand.handleOpenShopCommand(sender, args);
            case "removevault" -> removeVaultCommand.handleRemoveVaultCommand(sender, args); // Needs marketcraft.admin
            case "removeshop" -> removeShopCommand.handleRemoveShopCommand(sender, args); // Needs marketcraft.admin
            case "listvaults" -> listVaultsCommand.handleListVaultsCommand(sender, args); // Needs marketcraft.admin
            case "listshops" -> listShopsCommand.handleListShopsCommand(sender, args); // Needs marketcraft.admin
            default -> {
                handleUnknownCommand(sender);
                yield false;
            }
        };
    }

    /**
     * Provides tab completion options for the /marketcraft command.
     * This method is triggered whenever a player starts typing a /marketcraft command
     * and hits the tab key. It offers a list of possible completions based on the current
     * input.
     *
     * @param sender  The sender of the command.
     * @param command The command being executed.
     * @param alias   The alias used for the command.
     * @param args    The arguments provided with the command.
     * @return A List of Strings containing possible tab completions.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String cmd : COMMANDS) {
                if (cmd.startsWith(args[0].toLowerCase())) {
                    completions.add(cmd);
                }
            }
        }

        return completions;
    }

    /**
     * Handles cases where an unknown command is entered in the /marketcraft command set.
     * It informs the sender that the command is unrecognized and displays a list of available
     * commands within the /marketcraft command set.
     *
     * @param sender The sender of the command.
     */
    private void handleUnknownCommand(CommandSender sender) {
        sender.sendMessage(Component.text("Unknown command. Use one of the following:"));
        for (String cmd : COMMANDS) {
            sender.sendMessage(Component.text("/marketcraft " + cmd));
        }
    }
}