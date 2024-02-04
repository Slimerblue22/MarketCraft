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
