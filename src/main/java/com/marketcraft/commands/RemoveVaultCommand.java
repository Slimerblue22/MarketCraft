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

/**
 * Command handler for the 'removevault' subcommand within the MarketCraft plugin.
 * Utilizes PlayerVaultManager for vault removal operations.
 */
public class RemoveVaultCommand {
    private final PlayerVaultManager playerVaultManager;

    public RemoveVaultCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    /**
     * Handles the 'removevault' subcommand of the /marketcraft command set.
     * This method allows administrators with the appropriate permissions to remove a player's vault
     * based on the player's UUID. The command ensures that only authorized users can perform this
     * action and validates the provided UUID before attempting to remove the associated vault.
     * It provides feedback to the administrator about the outcome of the action, whether successful or not.
     *
     * @param sender The sender of the command, expected to be an administrator with the required permission.
     * @param args   The arguments provided with the command, including the player's UUID.
     * @return true if the vault is successfully removed, false if there is an error such as lack of permission,
     * incorrect usage, invalid UUID format, or if the vault does not exist.
     */
    public boolean handleRemoveVaultCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft removevault <playerUUID>"));
            return false;
        }
        String uuidString = args[1];
        try {
            boolean isRemoved = playerVaultManager.removePlayerVaultFile(uuidString);
            if (isRemoved) {
                sender.sendMessage(Component.text("Removed vault for UUID: " + uuidString));
            } else {
                sender.sendMessage(Component.text("No vault found for UUID: " + uuidString));
            }
            return isRemoved;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid UUID format: " + uuidString));
            return false;
        }
    }
}