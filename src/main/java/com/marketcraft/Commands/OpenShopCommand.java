package com.marketcraft.Commands;

import com.marketcraft.Shops.GUI.PlayerOpenShopGUI;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenShopCommand {
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;
    private final PlayerOpenShopGUI playerOpenShopGUI;

    public OpenShopCommand(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
        this.playerOpenShopGUI = new PlayerOpenShopGUI(playerShopManager);
    }

    public boolean handleOpenShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft openshop <playerUUID>"));
            return false;
        }

        String shopOwnerUUIDString = args[1];
        try {
            playerOpenShopGUI.openPlayerShopGUI(player, shopOwnerUUIDString);
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid UUID format: " + shopOwnerUUIDString));
            return false;
        }
    }
}
