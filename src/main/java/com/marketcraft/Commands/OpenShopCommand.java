package com.marketcraft.Commands;

import com.marketcraft.MarketCraft;
import com.marketcraft.Shops.GUI.PlayerOpenShopGUI;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Handles the 'openshop' subcommand of the /marketcraft command set.
 * This class is responsible for opening a specified player's shop interface for the command executor.
 * It leverages the PlayerOpenShopGUI for the graphical representation of the shop and utilizes
 * PlayerShopManager and PlayerVaultManager for managing the shop's inventory and stock.
 * The command is designed for player use and requires the player's name and the shop's name as arguments.
 * It validates the existence of the specified shop and the player's identity before opening the shop GUI.
 * This ensures a seamless and intuitive shopping experience within the game, promoting player interaction
 * and commerce within the server's economy.
 */
public class OpenShopCommand {
    private final PlayerOpenShopGUI playerOpenShopGUI;

    public OpenShopCommand(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerOpenShopGUI = new PlayerOpenShopGUI(playerShopManager, playerVaultManager, marketCraft);
    }

    public boolean handleOpenShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }

        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /marketcraft openshop <playerName> <shopName>"));
            return false;
        }

        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        UUID shopOwnerUUID = offlinePlayer.getUniqueId();

        String shopName = args[2];

        playerOpenShopGUI.openPlayerShopGUI(player, shopOwnerUUID, shopName);
        return true;
    }
}
