/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.locks.ShopLockManager;
import com.marketcraft.locks.VaultLockManager;
import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command handler for the `removeshop` subcommand within the MarketCraft plugin.
 * Utilizes PlayerShopManager for shop removal operations.
 */
public class RemoveShopCommand {
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;
    private final ShopLockManager shopLockManager;
    private final VaultLockManager vaultLockManager;

    public RemoveShopCommand(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager, ShopLockManager shopLockManager, VaultLockManager vaultLockManager) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
        this.shopLockManager = shopLockManager;
        this.vaultLockManager = vaultLockManager;
    }

    /**
     * Handles the 'removeshop' subcommand of the /marketcraft command set.
     * This method allows players to remove their own shops and the associated vaults.
     * The command expects only the shop's name as an argument.
     * It checks if the shop's vault is empty before proceeding with the removal. If the vault is not empty,
     * the shop and the vault are not removed, and the player is notified.
     * The command can only be executed by a player, and it operates on the shop and vault owned by the player who issued the command.
     *
     * @param sender The sender of the command, which must be a player.
     * @param args   The arguments provided with the command, where the first and only argument is the shop's name.
     * @return true if both the shop and the vault are successfully removed, false if there is an error such as the vault not being empty,
     * incorrect usage, or if the shop or the vault does not exist.
     */
    public boolean handleRemoveShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft removeshop <shopName>"));
            return false;
        }
        String shopName = args[1];
        UUID playerUUID = player.getUniqueId();
        String playerUUIDString = playerUUID.toString();
        try {
            if (!playerVaultManager.isPlayerVaultEmpty(playerUUIDString, shopName)) {
                sender.sendMessage(Component.text("Cannot remove shop '" + shopName + "' as its vault is not empty."));
                return false;
            }
            if (vaultLockManager.isLocked(playerUUID, shopName)) {
                sender.sendMessage(Component.text("Cannot remove shop '" + shopName + "' as its vault is currently locked."));
                return false;
            }
            if (shopLockManager.isLocked(playerUUID, shopName)) {
                sender.sendMessage(Component.text("Cannot remove shop '" + shopName + "' as its shop is currently locked."));
                return false;
            }
            boolean shopRemoved = playerShopManager.deletePlayerShop(playerUUIDString, shopName);
            boolean vaultRemoved = playerVaultManager.removePlayerVault(playerUUIDString, shopName);
            if (shopRemoved) {
                sender.sendMessage(Component.text("Removed shop '" + shopName + "'."));
            } else {
                sender.sendMessage(Component.text("No shop '" + shopName + "' found."));
            }
            if (vaultRemoved) {
                sender.sendMessage(Component.text("Removed associated vault for shop '" + shopName + "'."));
            } else {
                sender.sendMessage(Component.text("No vault found."));
            }
            return shopRemoved && vaultRemoved;
        } catch (Exception e) {
            sender.sendMessage(Component.text("Error processing the remove shop command."));
            return false;
        }
    }
}