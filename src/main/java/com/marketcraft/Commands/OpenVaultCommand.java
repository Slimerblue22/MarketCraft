package com.marketcraft.Commands;

import com.marketcraft.MarketCraft;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.GUI.PlayerVaultGUI;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
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
    private final PlayerShopManager playerShopManager;

    public OpenVaultCommand(PlayerVaultManager playerVaultManager, PlayerShopManager playerShopManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.playerVaultGUI = new PlayerVaultGUI(playerVaultManager, marketCraft);
        this.playerShopManager = playerShopManager;
    }

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
        if (playerShopManager.doesPlayerShopExist(player, shopName)) {
            if (playerVaultManager.doesPlayerVaultExist(player.getUniqueId())) {
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