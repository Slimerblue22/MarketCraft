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

public class OpenShopCommand {
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;
    private final MarketCraft marketCraft;
    private final PlayerOpenShopGUI playerOpenShopGUI;

    public OpenShopCommand(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
        this.marketCraft = marketCraft;
        this.playerOpenShopGUI = new PlayerOpenShopGUI(playerShopManager, playerVaultManager, marketCraft);
    }

    public boolean handleOpenShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft openshop <playerName>"));
            return false;
        }

        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        UUID shopOwnerUUID = offlinePlayer.getUniqueId();

        playerOpenShopGUI.openPlayerShopGUI(player, shopOwnerUUID);
        return true;
    }
}
