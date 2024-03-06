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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

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
     * This method allows administrators with the appropriate permissions to remove a specific vault
     * for a player, identified by the player's name and the vault's name. The command ensures that
     * only authorized users can perform this action and validates the provided player name and vault name
     * before attempting to remove the specified vault. It provides feedback to the administrator about
     * the outcome of the action, whether successful or not.
     *
     * @param sender The sender of the command, expected to be an administrator with the required permission.
     * @param args   The arguments provided with the command, including the player's name and the vault's name.
     * @return true if the vault is successfully removed, false if there is an error such as lack of permission,
     *         incorrect usage, or if the specified vault does not exist.
     */
    public boolean handleRemoveVaultCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command."));
            return false;
        }
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /marketcraft removevault <playerName> <vaultName>"));
            return false;
        }
        String playerName = args[1];
        String vaultName = args[2];
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            UUID playerUUID = player.getUniqueId();
            boolean isRemoved = playerVaultManager.removePlayerVault(playerUUID.toString(), vaultName);
            if (isRemoved) {
                sender.sendMessage(Component.text("Removed vault '" + vaultName + "' for player: " + playerName));
            } else {
                sender.sendMessage(Component.text("No vault '" + vaultName + "' found for player: " + playerName));
            }
            return isRemoved;
        } catch (Exception e) {
            sender.sendMessage(Component.text("Error processing the command for player: " + playerName));
            return false;
        }
    }
}