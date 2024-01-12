package com.marketcraft.Commands;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenStorageCommand {

    private final PlayerVaultManager playerVaultManager;

    public OpenStorageCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public boolean handleOpenStorageCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return false;
        }

        if (playerVaultManager.doesPlayerVaultExist(player.getUniqueId())) {
            sender.sendMessage(Component.text("Opening your existing shop storage...", NamedTextColor.GOLD));
            // Logic to open the existing vault will be here
            // Also need to account for what to do if the vault fails to open
        } else {
            sender.sendMessage(Component.text("Creating and opening a new shop storage...", NamedTextColor.GOLD));
            playerVaultManager.createPlayerVaultFile(player);
            // Logic to open the new vault after creation will be here
            // Also need to account for what to do if vault creation fails
        }

        // Placeholder message until the actual inventory opening logic is implemented
        sender.sendMessage(Component.text("Your storage has been opened.", NamedTextColor.GREEN));
        return true;
    }
}