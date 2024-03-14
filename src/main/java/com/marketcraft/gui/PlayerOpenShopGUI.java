/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.gui;

import com.marketcraft.MarketCraft;
import com.marketcraft.locks.VaultLockManager;
import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.locks.ShopLockManager;
import com.marketcraft.vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static com.marketcraft.util.GUIUtils.createNamedItem;
import static com.marketcraft.util.GUIUtils.createPlayerHead;

/**
 * Manages the graphical user interface (GUI) for players to open and interact with shops in the MarketCraft plugin.
 * This class is responsible for creating and displaying the shop inventory interface, enabling players to view items for sale, understand their costs, and check available stock.
 * <p>
 * Utilizes PlayerShopManager to retrieve information about items in a specific player's shop, including details such as item types and prices.
 * Employs PlayerVaultManager to manage and display the stock availability of items in the shop.
 * Leverages GUIUtils for creating custom inventory items and layouts in the GUI.
 * Interacts with MarketCraft for plugin-specific context and information.
 * It also uses both ShopLockManager and VaultLockManager for locking operations.
 */
public class PlayerOpenShopGUI {
    private static final int INVENTORY_SIZE = 27;
    private static final int SELL_TAG_SLOT = 10;
    private static final int CHARGE_TAG_SLOT = 14;
    private static final int CANCEL_SLOT = 22;
    private static final int CONFIRM_SLOT = 16;
    private static final int SELL_SLOT = 11;
    private static final int CHARGE_SLOT = 15;
    public static final int STOCK_INDICATOR_SLOT = 20;
    public static final int OWNER_HEAD_SLOT = 4;
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;
    private final ShopLockManager shopLockManager;
    private final VaultLockManager vaultLockManager;
    private final MarketCraft marketCraft;

    public PlayerOpenShopGUI(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager, ShopLockManager shopLockManager, VaultLockManager vaultLockManager, MarketCraft marketCraft) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
        this.shopLockManager = shopLockManager;
        this.vaultLockManager = vaultLockManager;
        this.marketCraft = marketCraft;
    }

    /**
     * Opens the shop GUI for the specified player, showing items for sale, their costs, and stock availability.
     * The method sets up the shop inventory based on the shop owner's UUID, displaying items for sale and purchase options.
     *
     * @param player        The player for whom the shop GUI is to be opened.
     * @param shopOwnerUUID The UUID of the shop owner whose items are being displayed in the shop.
     * @param shopName      The name of the shop that the player is accessing.
     */
    public void openPlayerShopGUI(Player player, UUID shopOwnerUUID, String shopName) {
        // Check if the shop is locked
        if (shopLockManager.isLocked(shopOwnerUUID, shopName)) {
            player.sendMessage(Component.text("This shop is currently being modified. Please try again later."));
            return;
        }
        // If the method returns null, we can assume the shop does not exist or is invalid
        ItemStack[] shopItems = playerShopManager.getPlayerShopItems(shopOwnerUUID, shopName);
        if (shopItems == null) {
            player.sendMessage("Shop does not exist or is invalid");
            return;
        }
        Inventory shopInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop"));
        ItemStack itemBeingSoldTag = createNamedItem(Material.NAME_TAG, "Selling");
        ItemStack itemCostTag = createNamedItem(Material.NAME_TAG, "Cost");
        ItemStack confirmSelection = createNamedItem(Material.LIME_STAINED_GLASS_PANE, "Buy");
        ItemStack cancelSelection = createNamedItem(Material.RED_STAINED_GLASS_PANE, "Close shop");
        // Create the customized items
        ItemStack itemBeingSold = shopItems[0];
        ItemStack itemCost = shopItems[1];
        int stockCount = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold, shopName);
        ItemStack stockIndicator = createNamedItem(Material.NAME_TAG, "Shop has " + stockCount + " in stock");
        ItemStack ownerIdentifier = createPlayerHead(shopOwnerUUID);
        ItemMeta meta = ownerIdentifier.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(marketCraft, "shopOwnerUUID"), PersistentDataType.STRING, shopOwnerUUID.toString());
        meta.getPersistentDataContainer().set(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING, shopName);
        ownerIdentifier.setItemMeta(meta);
        // Fill the entire inventory with the background
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            shopInventory.setItem(i, createNamedItem(Material.GRAY_STAINED_GLASS_PANE, "")); // Empty name
        }
        // Replacing certain slots with the menu items
        shopInventory.setItem(SELL_TAG_SLOT, itemBeingSoldTag);
        shopInventory.setItem(CHARGE_TAG_SLOT, itemCostTag);
        shopInventory.setItem(CANCEL_SLOT, cancelSelection);
        shopInventory.setItem(CONFIRM_SLOT, confirmSelection);
        shopInventory.setItem(STOCK_INDICATOR_SLOT, stockIndicator);
        shopInventory.setItem(SELL_SLOT, itemBeingSold);
        shopInventory.setItem(CHARGE_SLOT, itemCost);
        shopInventory.setItem(OWNER_HEAD_SLOT, ownerIdentifier);
        // Setup is done, create the inventory
        player.openInventory(shopInventory);
        // After setting up the shop GUI, lock the vault for the shop
        vaultLockManager.lockVault(shopOwnerUUID, shopName, player.getUniqueId());
    }
}