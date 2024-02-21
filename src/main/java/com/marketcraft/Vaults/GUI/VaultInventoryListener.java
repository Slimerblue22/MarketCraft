/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.Vaults.GUI;

import com.marketcraft.MarketCraft;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Handles inventory interactions within the player's vault in the MarketCraft plugin.
 * This class is responsible for managing the functionality related to a player's vault inventory,
 * including adding and removing items, and ensuring that only appropriate items are placed in specific slots.
 * It also manages interactions with the inventory GUI, ensuring valid actions and preventing invalid ones.
 * <p>
 * The class contains methods for:
 * - Handling clicks in the inventory, both in the player's own inventory and in the vault inventory.
 * - Moving items into and out of the vault, considering both the type of item (selling or buying) and available space.
 * - Returning invalid items to the player's inventory when the vault is closed.
 * - Saving the contents of the vault upon closing.
 * - Preventing certain global and menu-specific actions within the inventory to maintain consistency and prevent errors.
 * <p>
 * It utilizes sets of predefined slots for selling and buying items to maintain order in the vault inventory.
 * The class works closely with the PlayerVaultManager and PlayerShopManager to handle vault-specific and shop-specific data.
 */
public class VaultInventoryListener implements Listener {
    private final PlayerVaultManager playerVaultManager;
    private final PlayerShopManager playerShopManager;
    private final MarketCraft marketCraft;
    private static final Set<Integer> GUI_SLOTS = Set.of(4, 13, 22, 31, 40, 49);
    private static final Set<Integer> SELLING_SLOTS = Set.of(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);
    private static final Set<Integer> BUYING_SLOTS = Set.of(5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53);
    private static final int INFO_BOOK_SLOT = 4;

    public VaultInventoryListener(PlayerVaultManager playerVaultManager, PlayerShopManager playerShopManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.playerShopManager = playerShopManager;
        this.marketCraft = marketCraft;
    }

    private String getShopNameFromItem(Inventory inventory) {
        ItemStack item = inventory.getItem(VaultInventoryListener.INFO_BOOK_SLOT);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return meta.getPersistentDataContainer().get(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING);
            }
        }
        return null; // Return null if the item doesn't exist or doesn't have the metadata
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        // Check if the player is in the menu in general, doesn't matter if it's their inventory or the vault
        boolean isVaultGUI = event.getInventory().getHolder() instanceof Player
                && event.getView().title().equals(Component.text("Your Vault"));
        // Check if the player is specifically interacting with the vault
        boolean isTopInventory = Objects.equals(event.getClickedInventory(),
                player.getOpenInventory().getTopInventory());
        // Check if the player is specifically interacting with their own inventory
        boolean isBottomInventory = Objects.equals(event.getClickedInventory(),
                player.getOpenInventory().getBottomInventory());
        // If not in the Vault GUI, no need to proceed further
        if (!isVaultGUI) {
            return;
        }
        preventInvalidGlobalActions(event);
        if (event.isCancelled()) {
            return;
        }
        if (isTopInventory) {
            preventInvalidMenuSpecificActions(event);
            if (event.isCancelled()) {
                return;
            }
            moveItemOutOfVault(event);
        } else if (isBottomInventory) {
            String shopName = getShopNameFromItem(player.getOpenInventory().getTopInventory());
            moveItemIntoVault(event, shopName);
        }
    }

    private void moveItemOutOfVault(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        Inventory playerInventory = player.getInventory();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return; // No item was clicked or the slot is empty
        }
        ItemStack copyOfClickedItem = clickedItem.clone();
        HashMap<Integer, ItemStack> remainingItems = playerInventory.addItem(copyOfClickedItem);
        if (!remainingItems.isEmpty()) {
            // Not all items could be added, adjust the item amount in the vault
            ItemStack remaining = remainingItems.values().iterator().next();
            clickedItem.setAmount(remaining.getAmount());
        } else {
            // All items were added, remove from the vault
            event.setCurrentItem(new ItemStack(Material.AIR));
        }
    }

    private void moveItemIntoVault(InventoryClickEvent event, String shopName) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory vaultInventory = event.getInventory();
        ItemStack[] shopItems = playerShopManager.getPlayerShopItems(player.getUniqueId(), shopName);
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return; // No item was clicked or the slot is empty
        }
        // Determine if the clicked item is a selling or buying item
        ItemStack sellingItem = shopItems[0];
        ItemStack buyingItem = shopItems[1];
        boolean isSellingItem = sellingItem != null && sellingItem.isSimilar(clickedItem);
        boolean isBuyingItem = buyingItem != null && buyingItem.isSimilar(clickedItem);
        Set<Integer> appropriateSlots = isSellingItem ? SELLING_SLOTS : (isBuyingItem ? BUYING_SLOTS : Collections.emptySet());
        if (appropriateSlots.isEmpty()) {
            player.sendMessage(Component.text("This item cannot be placed in the vault."));
            return;
        }
        ItemStack copyOfClickedItem = clickedItem.clone();
        int amountToAdd = copyOfClickedItem.getAmount();
        for (int slot : appropriateSlots) {
            ItemStack itemInSlot = vaultInventory.getItem(slot);
            if (itemInSlot != null && itemInSlot.isSimilar(copyOfClickedItem) && itemInSlot.getAmount() < itemInSlot.getMaxStackSize()) {
                int amountCanAdd = itemInSlot.getMaxStackSize() - itemInSlot.getAmount();
                int amountWillAdd = Math.min(amountToAdd, amountCanAdd);
                itemInSlot.setAmount(itemInSlot.getAmount() + amountWillAdd);
                amountToAdd -= amountWillAdd;
                if (amountToAdd == 0) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    return; // All items have been added
                }
            }
        }
        if (amountToAdd > 0) {
            Optional<Integer> emptySlotIndex = appropriateSlots.stream()
                    .filter(slot -> vaultInventory.getItem(slot) == null || Objects.requireNonNull(vaultInventory.getItem(slot)).getType() == Material.AIR)
                    .findFirst();
            if (emptySlotIndex.isPresent()) {
                copyOfClickedItem.setAmount(amountToAdd);
                vaultInventory.setItem(emptySlotIndex.get(), copyOfClickedItem);
                event.setCurrentItem(new ItemStack(Material.AIR));
            } else {
                player.sendMessage(Component.text("There is no available space in the vault for this item."));
                clickedItem.setAmount(amountToAdd); // Adjust the amount on the original item to reflect what couldn't be added
            }
        }
    }

    private void preventInvalidGlobalActions(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SHIFT_LEFT
                || event.getClick() == ClickType.SHIFT_RIGHT
                || event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
        }
    }

    private void preventInvalidMenuSpecificActions(InventoryClickEvent event) {
        // These slots are menu items and should not be interacted with
        if (GUI_SLOTS.contains(event.getSlot())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        // Check if the closed inventory is the top inventory and is the vault
        if (closedInventory.getHolder() instanceof Player &&
                closedInventory.equals(player.getOpenInventory().getTopInventory()) &&
                Component.text("Your Vault").equals(player.getOpenInventory().title())) {
            saveVaultContents(closedInventory, player);
        }
    }

    private void saveVaultContents(Inventory closedInventory, Player player) {
        String shopName = getShopNameFromItem(closedInventory);
        // Return items that are not part of the shops stock to the player before continuing
        // This should never occur since the player is normally prevented from putting such items
        // into the GUI, but it remains here as a fail-safe
        returnInvalidItems(player, closedInventory, shopName);
        // Save the vault inventory
        playerVaultManager.savePlayerVault(player, closedInventory, shopName);
    }

    private void returnInvalidItems(Player player, Inventory inventory, String shopName) {
        ItemStack[] shopItems = playerShopManager.getPlayerShopItems(player.getUniqueId(), shopName);
        // Iterate over the inventory slots
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!GUI_SLOTS.contains(i)) {
                ItemStack item = inventory.getItem(i);
                // Check if the slot is not empty
                if (item != null && item.getType() != Material.AIR) {
                    // Check if the item is a shop item
                    boolean isShopItem = false;
                    for (ItemStack shopItem : shopItems) {
                        if (shopItem != null && shopItem.isSimilar(item)) {
                            isShopItem = true;
                            break;
                        }
                    }
                    // If the item is not a shop item, return it to the player
                    if (!isShopItem) {
                        HashMap<Integer, ItemStack> unfittedItems = player.getInventory().addItem(item.clone());
                        if (!unfittedItems.isEmpty()) {
                            // Drop items on the ground if the player's inventory is full
                            unfittedItems.values().forEach(unfittedItem -> player.getWorld().dropItem(player.getLocation(), unfittedItem));
                        }
                        inventory.setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }
}