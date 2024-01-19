package com.marketcraft.Shops.GUI;

import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ShopSetupListener implements Listener {

    private final PlayerShopManager playerShopManager;

    public ShopSetupListener(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
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

    // It would appear there is an issue with adding items that already exist within the GUI
    // Since the listener is canceling clicks on these items, you can usually add them but can't remove them afterward
    // TODO: Look into fix later (Maybe manually define the slots to cancel?)
    private void handleItemClick(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        int slot = event.getRawSlot();

        if (isNonInteractiveItem(clickedItem)) {
            event.setCancelled(true);
            return;
        }

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

    private boolean isNonInteractiveItem(ItemStack item) {
        if (item == null) return false;
        return item.getType() == Material.GREEN_STAINED_GLASS_PANE ||
                item.getType() == Material.RED_STAINED_GLASS_PANE ||
                item.getType() == Material.NAME_TAG;
    }

    // TODO: Items are lost when GUI is closed regardless of option picked
    // Need to find a way to ensure they keep the item(s) they have put in the GUI once the process is done
    // whether that be by canceling, confirming, or simply closing the inventory via the escape key
    private void handleRedWoolClick(Player player, InventoryClickEvent event) {
        player.sendMessage(Component.text("Shop creation canceled!", NamedTextColor.GREEN));
        event.setCancelled(true);
        player.closeInventory();
    }

    private void handleGreenWoolClick(Player player, InventoryClickEvent event) {
        Inventory shopSetupInventory = event.getInventory();
        ItemStack itemToSell = shopSetupInventory.getItem(13);
        ItemStack itemToCharge = shopSetupInventory.getItem(40);

        if (itemToSell != null && itemToCharge != null) {
            playerShopManager.savePlayerShop(player, itemToSell, itemToCharge);
            player.sendMessage(Component.text("Shop setup confirmed!", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Please place items in both the 'item to sell' and 'item to charge' slots.", NamedTextColor.RED));
        }
        player.closeInventory();
    }
}