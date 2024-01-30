package com.marketcraft.Shops;

import com.marketcraft.Vaults.PlayerVaultManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.UUID;

import static com.marketcraft.Util.GUIUtils.createNamedItem;

public class ShopTransaction {
    private final PlayerVaultManager playerVaultManager;

    public ShopTransaction(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public void processTransaction(Player player, Inventory shopInventory, UUID shopOwnerUUID) {
        ItemStack itemBeingSold = shopInventory.getItem(13);
        ItemStack itemCost = shopInventory.getItem(40);

        if (shopHasSufficientStock(player, shopOwnerUUID, itemBeingSold) && shopHasSufficientSpace(player, shopOwnerUUID, itemCost)) {
            if (buyerHasEnoughItems(player, itemCost) && buyerHasInventorySpace(player)) {
                removeItemsFromBuyer(player, itemCost);
                giveItemsToBuyer(player, itemBeingSold);
                playerVaultManager.removeItemsFromPlayerVault(shopOwnerUUID, itemBeingSold, itemBeingSold.getAmount());
                playerVaultManager.addItemsToPlayerVault(shopOwnerUUID, itemCost, itemCost.getAmount());
                updateStockIndicator(shopInventory, shopOwnerUUID, itemBeingSold);
            }
        }
    }

    private void updateStockIndicator(Inventory shopInventory, UUID shopOwnerUUID, ItemStack itemBeingSold) {
        int newStockCount = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold);
        ItemStack stockIndicator = createNamedItem(Material.NAME_TAG, "Shop has " + newStockCount + " in stock");
        shopInventory.setItem(14, stockIndicator);
    }

    private boolean shopHasSufficientSpace(Player player, UUID shopOwnerUUID, ItemStack itemCost) {
        if (!playerVaultManager.canAddItemToPlayerVault(shopOwnerUUID, itemCost, itemCost.getAmount())) {
            player.sendMessage("Shop owner's vault does not have enough space for the transaction.");
            return false;
        }
        return true;
    }

    private boolean shopHasSufficientStock(Player player, UUID shopOwnerUUID, ItemStack itemBeingSold) {
        int stockInVault = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold);
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