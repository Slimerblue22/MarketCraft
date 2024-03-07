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
import org.bukkit.entity.Player;

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
     * This method allows players to remove their own vaults and administrators to remove any player's vault.
     * It is based on the player's name and the vault's name. The command checks if the sender has the required
     * permissions or is the owner of the vault, and validates the provided player name and vault name before
     * attempting to remove the specified vault. It provides feedback about the outcome of the action,
     * whether successful or not.
     *
     * @param sender The sender of the command, expected to be a player or an administrator.
     * @param args   The arguments provided with the command, including the player's name and the vault's name.
     * @return true if the vault is successfully removed, false if there is an error such as lack of permission,
     * incorrect usage, or if the specified vault does not exist.
     */
    public boolean handleRemoveVaultCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /marketcraft removevault <playerName> <vaultName>"));
            return false;
        }
        String playerName = args[1];
        String vaultName = args[2];
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            UUID playerUUID = player.getUniqueId();
            // Check if the sender is not the player and doesn't have admin permission
            if (!sender.hasPermission("marketcraft.admin") && !(sender instanceof Player && ((Player) sender).getUniqueId().equals(playerUUID))) {
                sender.sendMessage(Component.text("You don't have permission to remove this vault."));
                return false;
            }
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