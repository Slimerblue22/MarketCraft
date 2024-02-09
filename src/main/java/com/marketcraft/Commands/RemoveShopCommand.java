package com.marketcraft.Commands;

import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

/**
 * Handles the 'removeshop' subcommand of the /marketcraft command set.
 * This class allows administrators with the appropriate permissions to remove a player's shop
 * based on the player's UUID. The command ensures that only authorized users can perform this
 * action and validates the provided UUID before attempting to remove the associated shop.
 */
public class RemoveShopCommand {
    private final PlayerShopManager playerShopManager;

    public RemoveShopCommand(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
    }

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
