/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.listeners;

import com.marketcraft.MarketCraft;
import com.marketcraft.locks.VaultLockManager;
import com.marketcraft.shops.ShopTransaction;
import com.marketcraft.vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

/**
 * Listener class for handling inventory interactions within the MarketCraft plugin's shop interface.
 * This class is responsible for processing player interactions with the shop GUI, such as purchase confirmations and cancellations.
 */
public class OpenShopListener implements Listener {
    private final ShopTransaction shopTransaction;
    private final VaultLockManager vaultLockManager;
    private final MarketCraft marketCraft;
    private static final int CONFIRM_SLOT = 16;
    private static final int CANCEL_SLOT = 22;
    private static final int OWNER_HEAD_SLOT = 4;

    public OpenShopListener(PlayerVaultManager playerVaultManager, VaultLockManager vaultLockManager, MarketCraft marketCraft) {
        this.shopTransaction = new ShopTransaction(playerVaultManager);
        this.vaultLockManager = vaultLockManager;
        this.marketCraft = marketCraft;
    }

    /**
     * Handles click events within the shop inventory interface.
     * This method is triggered when a player interacts with the shop GUI. It checks for specific interactions,
     * such as confirming a purchase or closing the shop, and processes these actions accordingly.
     * It retrieves the shop owner's UUID and shop name from the inventory metadata to facilitate the transaction
     * process when a purchase is confirmed.
     *
     * @param event The inventory click event that contains details about the player's interaction with the shop inventory.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player && event.getView().title().equals(Component.text("Shop"))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getRawSlot();
            if (clickedSlot == CONFIRM_SLOT) { // Buy button slot
                // Retrieve the shop owner's UUID from the inventory
                // This is used within the shop transaction logic to add and remove items from the shop owner's vault
                ItemStack ownerIdentifier = event.getInventory().getItem(OWNER_HEAD_SLOT);
                ItemMeta meta = Objects.requireNonNull(ownerIdentifier).getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                NamespacedKey ownerKey = new NamespacedKey(marketCraft, "shopOwnerUUID");
                String uuidString = dataContainer.get(ownerKey, PersistentDataType.STRING);
                UUID shopOwnerUUID = UUID.fromString(Objects.requireNonNull(uuidString));
                NamespacedKey shopNameKey = new NamespacedKey(marketCraft, "shopName");
                String shopName = dataContainer.get(shopNameKey, PersistentDataType.STRING);
                // Sending the necessary information to the transaction handler
                shopTransaction.processTransaction(player, event.getInventory(), shopOwnerUUID, shopName);
            } else if (clickedSlot == CANCEL_SLOT) { // Close button slot
                player.closeInventory();
            }
        }
    }

    /**
     * Handles the event when a player closes the shop inventory.
     * This method is invoked whenever a player who opened the shop interface closes it.
     * It ensures the proper unlocking of the vault associated with the shop, maintaining the integrity and security
     * of the vault content. The method retrieves the necessary shop and player information from the inventory metadata
     * to correctly identify which vault needs to be unlocked.
     *
     * @param event The inventory close event that contains details about the player closing the shop inventory.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player player && event.getView().title().equals(Component.text("Shop"))) {
            // Retrieve the shop owner's UUID and shop name from the inventory
            ItemStack ownerIdentifier = event.getInventory().getItem(OWNER_HEAD_SLOT);
            if (ownerIdentifier != null && ownerIdentifier.hasItemMeta()) {
                ItemMeta meta = ownerIdentifier.getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                NamespacedKey ownerKey = new NamespacedKey(marketCraft, "shopOwnerUUID");
                String uuidString = dataContainer.get(ownerKey, PersistentDataType.STRING);
                UUID shopOwnerUUID = UUID.fromString(Objects.requireNonNull(uuidString));
                NamespacedKey shopNameKey = new NamespacedKey(marketCraft, "shopName");
                String shopName = dataContainer.get(shopNameKey, PersistentDataType.STRING);
                // Unlock the vault
                vaultLockManager.unlockVault(shopOwnerUUID, shopName, player.getUniqueId());
            }
        }
    }
}