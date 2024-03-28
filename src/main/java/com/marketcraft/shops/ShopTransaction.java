/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.shops;

import com.marketcraft.vaults.PlayerVaultManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.marketcraft.util.GUIUtils.createNamedItem;

/**
 * Handles transactions for player shops in the MarketCraft plugin.
 * This class manages the logic for buying and selling items in a player's shop,
 * including checking stock availability, updating inventory, and managing item exchange between buyer and shop.
 */
public class ShopTransaction {
    private final PlayerVaultManager playerVaultManager;
    private static final int SELL_SLOT = 11;
    private static final int CHARGE_SLOT = 15;
    public static final int STOCK_INDICATOR_SLOT = 20;

    public ShopTransaction(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    /**
     * Processes a transaction for an item purchase in a player's shop.
     * Checks if the shop has enough stock, if the buyer has sufficient items and inventory space,
     * then carries out the transaction by updating both the player's and the shop owner's inventories.
     *
     * @param player        The player making the purchase.
     * @param shopInventory The inventory of the shop where the transaction is occurring.
     * @param shopOwnerUUID The UUID of the shop owner.
     * @param shopName      The name of the shop.
     */
    public void processTransaction(Player player, Inventory shopInventory, UUID shopOwnerUUID, String shopName) {
        ItemStack itemBeingSold = shopInventory.getItem(SELL_SLOT);
        ItemStack itemCost = shopInventory.getItem(CHARGE_SLOT);
        if (shopHasSufficientStock(player, shopOwnerUUID, itemBeingSold, shopName) && shopHasSufficientSpace(player, shopOwnerUUID, itemCost, shopName)) {
            if (buyerHasEnoughItems(player, Objects.requireNonNull(itemCost)) && buyerHasInventorySpace(player)) {
                removeItemsFromBuyer(player, itemCost);
                giveItemsToBuyer(player, Objects.requireNonNull(itemBeingSold));
                playerVaultManager.removeItemsFromPlayerVault(shopOwnerUUID, itemBeingSold, itemBeingSold.getAmount(), shopName);
                playerVaultManager.addItemsToPlayerVault(shopOwnerUUID, itemCost, itemCost.getAmount(), shopName);
                updateStockIndicator(shopInventory, shopOwnerUUID, itemBeingSold, shopName);
            }
        }
    }

    /**
     * Updates the stock indicator in the shop's inventory.
     *
     * @param shopInventory The shop inventory.
     * @param shopOwnerUUID UUID of the shop owner.
     * @param itemBeingSold The item being sold.
     * @param shopName      The name of the shop.
     */
    private void updateStockIndicator(Inventory shopInventory, UUID shopOwnerUUID, ItemStack itemBeingSold, String shopName) {
        int newStockCount = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold, shopName);
        ItemStack stockIndicator = createNamedItem(Material.NAME_TAG, "Shop has " + newStockCount + " in stock");
        shopInventory.setItem(STOCK_INDICATOR_SLOT, stockIndicator);
    }

    /**
     * Checks if the shop owner's vault has enough space for the transaction.
     *
     * @param player        The player attempting the purchase.
     * @param shopOwnerUUID UUID of the shop owner.
     * @param itemCost      The item being charged.
     * @param shopName      The name of the shop.
     * @return True if there is sufficient space, false otherwise.
     */
    private boolean shopHasSufficientSpace(Player player, UUID shopOwnerUUID, ItemStack itemCost, String shopName) {
        if (!playerVaultManager.canAddItemToPlayerVault(shopOwnerUUID, itemCost, itemCost.getAmount(), shopName)) {
            player.sendMessage("Shop owner's vault does not have enough space for the transaction.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the shop has enough stock for the purchase.
     *
     * @param player        The player making the purchase.
     * @param shopOwnerUUID UUID of the shop owner.
     * @param itemBeingSold The item being sold.
     * @param shopName      The name of the shop.
     * @return True if there is sufficient stock, false otherwise.
     */
    private boolean shopHasSufficientStock(Player player, UUID shopOwnerUUID, ItemStack itemBeingSold, String shopName) {
        int stockInVault = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold, shopName);
        if (stockInVault < itemBeingSold.getAmount()) {
            player.sendMessage("Insufficient stock in the shop for this purchase.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the buyer has enough inventory space.
     *
     * @param player The player making the purchase.
     * @return True if there is enough space, false otherwise.
     */
    private boolean buyerHasInventorySpace(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("Your inventory is full. Unable to complete the purchase.");
            return false;
        }
        return true;
    }

    /**
     * Gives the purchased item to the buyer.
     *
     * @param player        The player buying the item.
     * @param itemBeingSold The item being purchased.
     */
    private void giveItemsToBuyer(Player player, ItemStack itemBeingSold) {
        ItemStack item = itemBeingSold.clone();
        player.getInventory().addItem(item);
        player.sendMessage("Purchase successful!");
    }

    /**
     * Checks if the buyer has enough items for the cost.
     *
     * @param player The player buying the item.
     * @param cost   The cost of the item.
     * @return True if the buyer has enough items, false otherwise.
     */
    private boolean buyerHasEnoughItems(Player player, ItemStack cost) {
        Inventory playerInventory = player.getInventory();
        HashMap<Integer, ? extends ItemStack> allItems = playerInventory.all(cost.getType());
        int totalAmount = allItems.values().stream().mapToInt(ItemStack::getAmount).sum();
        if (totalAmount < cost.getAmount()) {
            player.sendMessage("You do not have enough items to make this purchase.");
            return false;
        }
        return true;
    }

    /**
     * Removes the cost items from the buyer's inventory.
     *
     * @param player The player buying the item.
     * @param cost   The cost of the item.
     */
    private void removeItemsFromBuyer(Player player, ItemStack cost) {
        int amountToRemove = cost.getAmount();
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == cost.getType()) {
                int removed = Math.min(itemStack.getAmount(), amountToRemove);
                itemStack.setAmount(itemStack.getAmount() - removed);
                amountToRemove -= removed;
                if (amountToRemove <= 0) break;
            }
        }
    }
}