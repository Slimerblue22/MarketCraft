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

import java.util.Set;

public class VaultInventoryListener implements Listener {
    private final PlayerVaultManager playerVaultManager;
    private final MarketCraft marketCraft;
    private static final Set<Integer> GUI_SLOTS = Set.of(4, 13, 22, 31, 40, 49);
    private static final int INFO_BOOK_SLOT = 4;

    public VaultInventoryListener(PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.marketCraft = marketCraft;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        boolean isVaultGUI = event.getInventory().getHolder() instanceof Player &&
                event.getView().title().equals(Component.text("Your Vault"));
        // Prevent double-click stacking in the entire "Your Vault" GUI (both top and bottom)
        if (isVaultGUI) {
            preventDoubleClickStacking(event);
            preventShiftClickingInvalidItems(event);
        }
        // Handle menu clicks only if the click is in the top inventory, and it is the vault
        if (isVaultGUI &&
                event.getClickedInventory() != null &&
                event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            handleMenuClicks(event);
        }
    }

    private void preventDoubleClickStacking(InventoryClickEvent event) {
        event.getWhoClicked();
        // Check if the action is a double click
        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            // Cancel the event to prevent double-click stacking
            event.setCancelled(true);
        }
    }

    private void preventShiftClickingInvalidItems(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            ItemStack clickedItem = event.getCurrentItem();
            // Prevents people from losing certain items if they shift click them into the GUI and they stack into an existing menu slot
            if (clickedItem != null && clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        }
    }

    private void handleMenuClicks(InventoryClickEvent event) {
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
        ItemStack infoBook = closedInventory.getItem(INFO_BOOK_SLOT);
        if (infoBook != null && infoBook.hasItemMeta()) {
            ItemMeta meta = infoBook.getItemMeta();
            String shopName = meta.getPersistentDataContainer().get(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING);
            playerVaultManager.savePlayerVault(player, closedInventory, shopName);
        }
    }
}