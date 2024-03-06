/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

/**
 * Command handler for the `removeshop` subcommand within the MarketCraft plugin.
 * Utilizes PlayerShopManager for shop removal operations.
 */
public class RemoveShopCommand {
    private final PlayerShopManager playerShopManager;

    public RemoveShopCommand(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
    }

    /**
     * Handles the 'removeshop' subcommand of the /marketcraft command set.
     * This method allows administrators with the appropriate permissions to remove a player's shop
     * based on the player's UUID and the shop's name. The command ensures that only authorized users
     * can perform this action and validates the provided UUID and shop name before attempting to
     * remove the associated shop. It provides feedback to the administrator about the outcome of
     * the action, whether successful or not.
     *
     * @param sender The sender of the command, expected to be an administrator with the required permission.
     * @param args   The arguments provided with the command, including the player's UUID and the shop's name.
     * @return true if the shop is successfully removed, false if there is an error such as lack of permission,
     * incorrect usage, invalid UUID format, or if the shop does not exist.
     */
    public boolean handleRemoveShopCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command."));
            return false;
        }
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /marketcraft removeshop <playerUUID> <shopName>"));
            return false;
        }
        String uuidString = args[1];
        String shopName = args[2];
        try {
            boolean isRemoved = playerShopManager.deletePlayerShop(uuidString, shopName);
            if (isRemoved) {
                sender.sendMessage(Component.text("Removed shop '" + shopName + "' for UUID: " + uuidString));
            } else {
                sender.sendMessage(Component.text("No shop '" + shopName + "' found for UUID: " + uuidString));
            }
            return isRemoved;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid UUID format: " + uuidString));
            return false;
        }
    }
}