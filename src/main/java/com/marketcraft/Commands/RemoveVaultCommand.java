package com.marketcraft.Commands;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class RemoveVaultCommand {

    private final PlayerVaultManager playerVaultManager;

    public RemoveVaultCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public boolean handleRemoveVaultCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command.", NamedTextColor.RED));
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft removevault <playerUUID>", NamedTextColor.RED));
            return false;
        }

        String uuidString = args[1];
        try {
            boolean isRemoved = playerVaultManager.removePlayerVaultFile(uuidString);
            if (isRemoved) {
                sender.sendMessage(Component.text("Removed vault for UUID: " + uuidString, NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("No vault found for UUID: " + uuidString, NamedTextColor.RED));
            }
            return isRemoved;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid UUID format: " + uuidString, NamedTextColor.RED));
            return false;
        }
    }
}
