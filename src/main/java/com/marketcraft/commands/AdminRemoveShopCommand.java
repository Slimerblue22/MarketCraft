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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Command handler for the `removeshop` subcommand within the MarketCraft plugin for the admin command set.
 */
public class AdminRemoveShopCommand {
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;
    private final ShopLockManager shopLockManager;
    private final VaultLockManager vaultLockManager;

    public AdminRemoveShopCommand(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager, ShopLockManager shopLockManager, VaultLockManager vaultLockManager) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
        this.shopLockManager = shopLockManager;
        this.vaultLockManager = vaultLockManager;
    }

    /**
     * Handles the 'removeshop' subcommand of the /marketcraftadmin command set.
     * This method allows administrators to forcibly remove any shop along with its associated vault,
     * regardless of the shop owner's presence or consent.
     * <p>
     * The command requires the shop's name and the owner's name as arguments. It performs checks
     * to ensure that the shop and vault are not locked before proceeding with the removal in order to prevent errors.
     * <p>
     * It is intended for administrative use only, and proper permissions should be set to restrict
     * access to authorized users.
     *
     * @param sender The sender of the command; expected to be an administrator with the appropriate permissions.
     * @param args   The arguments provided with the command, where args[1] is the shop's name and
     *               args[2] is the shop owner's name.
     * @return true if both the shop and the vault are successfully removed, false if there is an error such
     * as the shop or the vault being locked, incorrect usage, or if the shop or the vault does not exist.
     */
    public boolean handleAdminRemoveShopCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /marketcraftadmin removeshop <shopName> <shopOwnerName>"));
            return false;
        }
        String shopName = args[1];
        String shopOwnerName = args[2];
        OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shopOwnerName);
        UUID playerUUID = shopOwner.getUniqueId();
        String playerUUIDString = playerUUID.toString();
        try {
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
            sender.sendMessage(Component.text("Error processing the admin remove shop command."));
            return false;
        }
    }
}