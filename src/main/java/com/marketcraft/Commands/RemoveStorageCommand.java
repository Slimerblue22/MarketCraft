package com.marketcraft.Commands;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class RemoveStorageCommand {

    private final PlayerVaultManager playerVaultManager;

    public RemoveStorageCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public boolean handleRemoveStorageCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command.", NamedTextColor.RED));
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft removestorage <playerUUID>", NamedTextColor.RED));
            return false;
        }

        String uuidString = args[1];
        try {
            boolean isRemoved = playerVaultManager.removePlayerVaultFile(uuidString);
            if (isRemoved) {
                sender.sendMessage(Component.text("Removed storage for UUID: " + uuidString, NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("No storage found for UUID: " + uuidString, NamedTextColor.RED));
            }
            return isRemoved;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid UUID format: " + uuidString, NamedTextColor.RED));
            return false;
        }
    }
}
