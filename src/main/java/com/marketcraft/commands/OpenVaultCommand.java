/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.MarketCraft;
import com.marketcraft.gui.PlayerVaultGUI;
import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command handler for the 'openvault' subcommand within the MarketCraft plugin.
 * Utilizes PlayerVaultManager, PlayerVaultGUI, and PlayerShopManager for vault access and management.
 */
public class OpenVaultCommand {
    private final PlayerVaultManager playerVaultManager;
    private final PlayerVaultGUI playerVaultGUI;
    private final PlayerShopManager playerShopManager;

    public OpenVaultCommand(PlayerVaultManager playerVaultManager, PlayerShopManager playerShopManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.playerVaultGUI = new PlayerVaultGUI(playerVaultManager, marketCraft);
        this.playerShopManager = playerShopManager;
    }

    /**
     * Handles the 'openvault' subcommand of the /marketcraft command set.
     * This method is responsible for opening a player's vault, creating a new vault if one doesn't exist.
     * It leverages the PlayerVaultManager for checking and managing vault existence and PlayerVaultGUI
     * for displaying the vault's contents to the player. This command is intended for player use and
     * not for console. It ensures the specified shop exists for the player before attempting to open
     * or create a vault, thereby providing a secure and controlled way of managing player's vaults
     * associated with their shops.
     *
     * @param sender The sender of the command, expected to be a player.
     * @param args   The arguments provided with the command, including the shop name associated with the vault.
     * @return true if the vault is successfully opened or created for the specified shop, false if there is
     * an error such as the command not being used by a player, incorrect usage, or if the shop does not exist.
     */
    public boolean handleOpenVaultCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft openvault <shopName>"));
            return false;
        }
        String shopName = args[1];
        UUID playerUUID = player.getUniqueId();
        if (playerShopManager.doesPlayerShopExist(playerUUID, shopName)) {
            if (playerVaultManager.doesPlayerVaultExist(playerUUID)) {
                sender.sendMessage(Component.text("Opening your existing vault for shop: " + shopName));
                playerVaultGUI.openVault(player, shopName);
            } else {
                sender.sendMessage(Component.text("Creating and opening a new vault for shop: " + shopName));
                playerVaultManager.createPlayerVaultFile(player, shopName);
                playerVaultGUI.openVault(player, shopName);
            }
        } else {
            sender.sendMessage(Component.text("The shop '" + shopName + "' does not exist, create a shop first."));
            return false;
        }
        return true;
    }
}