package com.marketcraft.Shops;

import com.marketcraft.Vaults.PlayerVaultManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.marketcraft.Util.GUIUtils.createNamedItem;

public class ShopTransaction {
    private final PlayerVaultManager playerVaultManager;
    private static final int SELL_SLOT = 11;
    private static final int CHARGE_SLOT = 15;
    public static final int STOCK_INDICATOR_SLOT = 20;

    public ShopTransaction(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

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

    private void updateStockIndicator(Inventory shopInventory, UUID shopOwnerUUID, ItemStack itemBeingSold, String shopName) {
        int newStockCount = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold, shopName);
        ItemStack stockIndicator = createNamedItem(Material.NAME_TAG, "Shop has " + newStockCount + " in stock");
        shopInventory.setItem(STOCK_INDICATOR_SLOT, stockIndicator);
    }

    private boolean shopHasSufficientSpace(Player player, UUID shopOwnerUUID, ItemStack itemCost, String shopName) {
        if (!playerVaultManager.canAddItemToPlayerVault(shopOwnerUUID, itemCost, itemCost.getAmount(), shopName)) {
            player.sendMessage("Shop owner's vault does not have enough space for the transaction.");
            return false;
        }
        return true;
    }

    private boolean shopHasSufficientStock(Player player, UUID shopOwnerUUID, ItemStack itemBeingSold, String shopName) {
        int stockInVault = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold, shopName);
        if (stockInVault < itemBeingSold.getAmount()) {
            player.sendMessage("Insufficient stock in the shop for this purchase.");
            return false;
        }
        return true;
    }

    private boolean buyerHasInventorySpace(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("Your inventory is full. Unable to complete the purchase.");
            return false;
        }
        return true;
    }

    private void giveItemsToBuyer(Player player, ItemStack itemBeingSold) {
        ItemStack item = itemBeingSold.clone();
        player.getInventory().addItem(item);
        player.sendMessage("Purchase successful!");
    }

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