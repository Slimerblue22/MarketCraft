package com.marketcraft.Commands;

import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(Component.text("You don't have permission to run this command.", NamedTextColor.RED));
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft removeshop <playerUUID>", NamedTextColor.RED));
            return false;
        }

        String uuidString = args[1];
        try {
            boolean isRemoved = playerShopManager.removePlayerShopFile(uuidString);
            if (isRemoved) {
                sender.sendMessage(Component.text("Removed shop for UUID: " + uuidString, NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("No shop found for UUID: " + uuidString, NamedTextColor.RED));
            }
            return isRemoved;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid UUID format: " + uuidString, NamedTextColor.RED));
            return false;
        }
    }
}
