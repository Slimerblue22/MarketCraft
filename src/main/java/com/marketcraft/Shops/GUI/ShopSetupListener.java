package com.marketcraft.Shops.GUI;

import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopSetupListener implements Listener {

    private final PlayerShopManager playerShopManager;

    public ShopSetupListener(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player player && event.getView().title().equals(Component.text("Shop Setup"))) {
            Inventory shopSetupInventory = event.getInventory();

            List<ItemStack> itemsToReturn = new ArrayList<>();
            ItemStack item13 = shopSetupInventory.getItem(13);
            ItemStack item40 = shopSetupInventory.getItem(40);

            if (item13 != null && item13.getType() != Material.AIR) {
                itemsToReturn.add(item13);
            }
            if (item40 != null && item40.getType() != Material.AIR) {
                itemsToReturn.add(item40);
            }

            returnItemsToPlayer(player, itemsToReturn);
        }
    }

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

    private void handleShiftClick(InventoryClickEvent event, Player player, boolean isTopInventory) {
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            if (isTopInventory || event.getClickedInventory() == player.getInventory()) {
                event.setCancelled(true);
            }
        }
    }

    private void handleItemClick(InventoryClickEvent event, Player player) {
        int slot = event.getRawSlot();

        switch (slot) {
            case 13:
            case 40:
                // Allow interactions with slots 13 and 40
                return;
            case 45:
                handleRedWoolClick(player, event);
                break;
            case 53:
                handleGreenWoolClick(player, event);
                break;
            default:
                // Cancel all other interactions within the top inventory
                event.setCancelled(true);
                break;
        }
    }

    private void handleRedWoolClick(Player player, InventoryClickEvent event) {
        player.sendMessage(Component.text("Shop creation canceled!"));
        event.setCancelled(true);
        player.closeInventory();
    }

    private void handleGreenWoolClick(Player player, InventoryClickEvent event) {
        Inventory shopSetupInventory = event.getInventory();
        ItemStack itemToSell = shopSetupInventory.getItem(13);
        ItemStack itemToCharge = shopSetupInventory.getItem(40);

        if (itemToSell != null && itemToCharge != null) {
            playerShopManager.savePlayerShop(player, itemToSell, itemToCharge);
            player.sendMessage(Component.text("Shop setup confirmed!"));
        } else {
            player.sendMessage(Component.text("Please place items in both the 'item to sell' and 'item to charge' slots."));
        }
        player.closeInventory();
    }
}
