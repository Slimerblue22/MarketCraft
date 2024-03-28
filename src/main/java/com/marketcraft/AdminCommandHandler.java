/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft;

import com.marketcraft.commands.AdminRemoveShopCommand;
import com.marketcraft.locks.ShopLockManager;
import com.marketcraft.locks.VaultLockManager;
import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.vaults.PlayerVaultManager;
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
 * AdminCommandHandler is responsible for handling admin-specific command
 * execution within the MarketCraft plugin. This class manages admin commands
 * and includes permission checks and tab completion support for admin-level
 * operations.
 */
public class AdminCommandHandler implements CommandExecutor, TabCompleter {
    private static final String[] COMMANDS = {"removeshop"};
    private final AdminRemoveShopCommand adminRemoveShopCommand;

    public AdminCommandHandler(PlayerVaultManager playerVaultManager, PlayerShopManager playerShopManager, ShopLockManager shopLockManager, VaultLockManager vaultLockManager) {
        this.adminRemoveShopCommand = new AdminRemoveShopCommand(playerShopManager, playerVaultManager, shopLockManager, vaultLockManager);
    }


    /**
     * Executes the given admin command.
     *
     * @param sender  The sender of the command, typically an admin or console.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments passed with the command.
     * @return true if the command was successfully executed, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage("You do not have permission to execute this command.");
            return false;
        }
        if (args.length == 0) {
            handleUnknownCommand(sender);
            return false;
        }
        String subCommand = args[0].toLowerCase();
        return switch (subCommand) {
            case "removeshop" -> adminRemoveShopCommand.handleAdminRemoveShopCommand(sender, args);
            default -> {
                handleUnknownCommand(sender);
                yield false;
            }
        };
    }

    /**
     * Provides tab completion options for the admin commands.
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
     * Handles cases where an unknown command is entered in the /marketcraftadmin command set.
     * It informs the sender that the command is unrecognized and displays a list of available
     * commands within the /marketcraftadmin command set.
     *
     * @param sender The sender of the command.
     */
    private void handleUnknownCommand(CommandSender sender) {
        sender.sendMessage(Component.text("Unknown command. Use one of the following:"));
        for (String cmd : COMMANDS) {
            sender.sendMessage(Component.text("/marketcraftadmin " + cmd));
        }
    }
}