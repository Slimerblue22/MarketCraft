package com.marketcraft.Vaults.GUI;

import com.marketcraft.MarketCraft;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

public class VaultInventoryListener implements Listener {
    private final PlayerVaultManager playerVaultManager;
    private final MarketCraft marketCraft;

    public VaultInventoryListener(PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.marketCraft = marketCraft;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        // Check if the closed inventory is a vault
        if (closedInventory.getHolder() instanceof Player && Component.text("Your Vault").equals(player.getOpenInventory().title())) {
            ItemStack dummyBook = closedInventory.getItem(26); // Dummy book is in slot 26
            if (dummyBook != null && dummyBook.hasItemMeta() && dummyBook.getItemMeta() instanceof BookMeta bookMeta) {
                String shopName = bookMeta.getPersistentDataContainer().get(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING);
                if (shopName != null) {
                    playerVaultManager.savePlayerVault(player, closedInventory, shopName);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Prevent moving the dummy book
        if (event.getInventory().getHolder() instanceof Player && event.getSlot() == 26) {
            event.setCancelled(true);
        }
    }
}