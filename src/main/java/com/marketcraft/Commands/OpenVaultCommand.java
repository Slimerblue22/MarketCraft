package com.marketcraft.Commands;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenVaultCommand {

    private final PlayerVaultManager playerVaultManager;

    public OpenVaultCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public boolean handleOpenVaultCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return false;
        }

        if (playerVaultManager.doesPlayerVaultExist(player.getUniqueId())) {
            sender.sendMessage(Component.text("Opening your existing vault...", NamedTextColor.GOLD));
            // Logic to open the existing vault will be here
            // Also need to account for what to do if the vault fails to open
        } else {
            sender.sendMessage(Component.text("Creating and opening a new vault...", NamedTextColor.GOLD));
            playerVaultManager.createPlayerVaultFile(player);
            // Logic to open the new vault after creation will be here
            // Also need to account for what to do if vault creation fails
        }

        // Placeholder message until the actual inventory opening logic is implemented
        sender.sendMessage(Component.text("Your vault has been opened.", NamedTextColor.GREEN));
        return true;
    }
}