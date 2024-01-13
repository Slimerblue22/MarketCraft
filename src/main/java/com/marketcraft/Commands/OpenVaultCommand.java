package com.marketcraft.Commands;

import com.marketcraft.Vaults.GUI.PlayerVaultGUI;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the 'openvault' subcommand of the /marketcraft command set.
 * This class is responsible for opening a player's vault, creating a new vault if one doesn't exist.
 * It leverages the PlayerVaultManager for checking and managing vault existence and PlayerVaultGUI
 * for displaying the vault's contents to the player. This command is intended for player use and
 * not for console.
 */
public class OpenVaultCommand {

    private final PlayerVaultManager playerVaultManager;
    private final PlayerVaultGUI playerVaultGUI;

    public OpenVaultCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
        this.playerVaultGUI = new PlayerVaultGUI(playerVaultManager);
    }

    public boolean handleOpenVaultCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return false;
        }

        if (playerVaultManager.doesPlayerVaultExist(player.getUniqueId())) {
            sender.sendMessage(Component.text("Opening your existing vault...", NamedTextColor.GOLD));
            playerVaultGUI.openVault(player);
        } else {
            sender.sendMessage(Component.text("Creating and opening a new vault...", NamedTextColor.GOLD));
            playerVaultManager.createPlayerVaultFile(player);
            playerVaultGUI.openVault(player);
        }
        return true;
    }
}