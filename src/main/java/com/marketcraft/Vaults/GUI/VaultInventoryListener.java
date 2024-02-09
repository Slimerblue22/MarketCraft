package com.marketcraft.Vaults.GUI;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class VaultInventoryListener implements Listener {
    private final PlayerVaultManager playerVaultManager;

    public VaultInventoryListener(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        // Check if the closed inventory is the player's vault
        if (closedInventory.getHolder() instanceof Player && Component.text("Your Vault").equals(player.getOpenInventory().title())) {
            playerVaultManager.savePlayerVault(player, closedInventory);
        }
    }
}