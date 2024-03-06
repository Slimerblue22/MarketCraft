/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Command handler for the 'listvaults' subcommand within the MarketCraft plugin.
 * Utilizes PlayerVaultManager for listing player vaults.
 */
public class ListVaultsCommand {
    private final PlayerVaultManager playerVaultManager;
    private static final int ENTRIES_PER_PAGE = 10;

    public ListVaultsCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    /**
     * Handles the 'listvaults' subcommand of the /marketcraft command set.
     * This method allows administrators with the appropriate permissions to list all player vaults.
     * It supports pagination to display the vaults in a manageable format, showing a set number
     * of entries per page. The method ensures that only authorized users can view the list of vaults
     * and provides functionality to navigate through the vaults in a paginated manner. This method also
     * handles input validation for the page number, ensuring it's a positive integer and within the range
     * of available pages.
     *
     * @param sender The sender of the command, expected to be an administrator with the required permission.
     * @param args   The arguments provided with the command, optionally including the page number to view.
     * @return true if the command is successfully executed, false if there is an error or unauthorized access.
     */
    public boolean handleListVaultsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command."));
            return false;
        }
        // Default to page 1 if no page number is provided
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    sender.sendMessage(Component.text("Page number must be a positive integer."));
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid page number."));
                return false;
            }
        }
        File[] vaultFiles = playerVaultManager.listAllVaults();
        if (vaultFiles == null || vaultFiles.length == 0) {
            sender.sendMessage(Component.text("No vaults found."));
            return true;
        }
        int totalEntries = vaultFiles.length;
        int totalPages = (int) Math.ceil((double) totalEntries / ENTRIES_PER_PAGE);
        if (page > totalPages) {
            sender.sendMessage(Component.text("Page " + page + " does not exist."));
            return false;
        }
        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, totalEntries);
        sender.sendMessage(Component.text("Vaults (Page " + page + " of " + totalPages + "):"));
        for (int i = start; i < end; i++) {
            sender.sendMessage(Component.text(vaultFiles[i].getName()));
        }
        return true;
    }
}