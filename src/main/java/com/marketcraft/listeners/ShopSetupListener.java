/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.listeners;

import com.marketcraft.shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Listener for handling the shop setup interface in the MarketCraft plugin.
 * This class processes player interactions within the shop setup inventory,
 * managing the configuration and confirmation of new shops.
 * It leverages PlayerShopManager to save and manage the shop details as configured by the player.
 * <p>
 * The class handles the retrieval of items from the setup interface upon inventory closure
 * and processes various inventory click events to ensure proper setup actions are taken.
 */
public class ShopSetupListener implements Listener {
    private final PlayerShopManager playerShopManager;
    private static final int SELL_SLOT = 11;
    private static final int CHARGE_SLOT = 16;
    private static final int CANCEL_SLOT = 21;
    private static final int CONFIRM_SLOT = 23;
    private static final int SHOP_NAME_TAG_SLOT = 4;

    public ShopSetupListener(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
    }

    /**
     * Handles the closure of the shop setup inventory.
     * This method is triggered when a player closes the shop setup interface. It retrieves
     * any items placed in the setup inventory by the player and returns them to the player's
     * inventory or drops them at the player's location if the inventory is full.
     *
     * @param event The inventory close event containing details about the inventory being closed.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player player && event.getView().title().equals(Component.text("Shop Setup"))) {
            Inventory shopSetupInventory = event.getInventory();
            List<ItemStack> itemsToReturn = new ArrayList<>();
            ItemStack itemToSell = shopSetupInventory.getItem(SELL_SLOT);
            ItemStack itemToCharge = shopSetupInventory.getItem(CHARGE_SLOT);
            if (itemToSell != null && itemToSell.getType() != Material.AIR) {
                itemsToReturn.add(itemToSell);
            }
            if (itemToCharge != null && itemToCharge.getType() != Material.AIR) {
                itemsToReturn.add(itemToCharge);
            }
            returnItemsToPlayer(player, itemsToReturn);
        }
    }

    /**
     * Returns items to the player's inventory or drops them near the player if the inventory is full.
     * This method is used to ensure that players do not lose items during shop setup when the inventory is closed.
     *
     * @param player The player to return items to.
     * @param items  The list of items to be returned.
     */
    private void returnItemsToPlayer(Player player, List<ItemStack> items) {
        boolean itemsDropped = false;
        for (ItemStack item : items) {
            Map<Integer, ItemStack> unaddedItems = player.getInventory().addItem(item);
            if (!unaddedItems.isEmpty()) {
                unaddedItems.values().forEach(it -> player.getWorld().dropItem(player.getLocation(), it));
                itemsDropped = true;
            }
        }
        // If boolean was changed to true, one or more items were dropped, notify the player
        if (itemsDropped) {
            player.sendMessage(Component.text("Your inventory is full. Items have been dropped at your location."));
        }
    }

    /**
     * Handles click events within the shop setup inventory.
     * This method manages player interactions with the setup inventory, including item placement,
     * cancellation of setup, and confirmation of shop settings. It ensures appropriate handling
     * of click events to facilitate the shop setup process.
     *
     * @param event The inventory click event that includes details about the player's interaction with the inventory.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player player && event.getView().title().equals(Component.text("Shop Setup"))) {
            boolean isTopInventory = event.getView().getTopInventory().getType() == InventoryType.CHEST &&
                    event.getRawSlot() < event.getView().getTopInventory().getSize();
            handleShiftClick(event, player, isTopInventory);
            if (isTopInventory) {
                handleItemClick(event, player);
            }
        }
    }

    /**
     * Handles shift-click actions within the shop setup inventory.
     * Prevents unwanted interactions such as quick-moving items in or out of the inventory.
     *
     * @param event          The inventory click event.
     * @param player         The player interacting with the inventory.
     * @param isTopInventory Boolean indicating if the action is performed in the top inventory.
     */
    private void handleShiftClick(InventoryClickEvent event, Player player, boolean isTopInventory) {
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            if (isTopInventory || event.getClickedInventory() == player.getInventory()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Manages item click events within the shop setup inventory.
     * Processes interactions with specific inventory slots, like cancel or confirm buttons.
     *
     * @param event  The inventory click event.
     * @param player The player interacting with the inventory.
     */
    private void handleItemClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();
        switch (slot) {
            case SELL_SLOT:
            case CHARGE_SLOT:
                // Allow interactions with these slots
                return;
            case CANCEL_SLOT:
                handleCancelSelectionClick(player, event);
                break;
            case CONFIRM_SLOT:
                handleConfirmSelectionClick(player, event);
                break;
            default:
                // Cancel all other interactions within the top inventory
                event.setCancelled(true);
                break;
        }
    }

    /**
     * Processes a click event on the cancel selection in the shop setup.
     * Informs the player that the shop creation process has been canceled and closes the inventory.
     *
     * @param player The player interacting with the cancel option.
     * @param event  The inventory click event.
     */
    private void handleCancelSelectionClick(Player player, InventoryClickEvent event) {
        player.sendMessage(Component.text("Shop creation canceled!"));
        event.setCancelled(true);
        player.closeInventory();
    }

    /**
     * Handles the confirmation of shop setup by the player.
     * Validates the setup, saves the shop details, and notifies the player upon successful setup.
     *
     * @param player The player confirming the shop setup.
     * @param event  The inventory click event.
     */
    private void handleConfirmSelectionClick(Player player, InventoryClickEvent event) {
        Inventory shopSetupInventory = event.getInventory();
        ItemStack itemToSell = shopSetupInventory.getItem(SELL_SLOT);
        ItemStack itemToCharge = shopSetupInventory.getItem(CHARGE_SLOT);
        ItemStack shopNameItem = shopSetupInventory.getItem(SHOP_NAME_TAG_SLOT);
        ItemMeta shopNameMeta = Objects.requireNonNull(shopNameItem).getItemMeta();
        Component displayNameComponent = Objects.requireNonNull(shopNameMeta.displayName());
        String shopName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);
        if (itemToSell != null && itemToCharge != null) {
            playerShopManager.savePlayerShop(player, shopName, itemToSell, itemToCharge);
            player.sendMessage(Component.text("Shop '" + shopName + "' setup confirmed!"));
        } else {
            player.sendMessage(Component.text("Please place items in both the 'item to sell' and 'item to charge' slots."));
        }
        player.closeInventory();
    }
}