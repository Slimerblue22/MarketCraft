package com.marketcraft.Shops.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OpenShopListener implements Listener {

    public OpenShopListener() {
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Shop"))) {
            event.setCancelled(true); // Cancel all interactions by default

            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getRawSlot();

            processShopInteraction(player, clickedSlot);
        }
    }

    private void processShopInteraction(Player player, int slot) {
        switch (slot) {
            case 45: // Close Shop Button
                handleCloseShop(player);
                break;
            case 53: // Buy Button
                handleBuy(player);
                break;
        }
    }

    private void handleCloseShop(Player player) {
        player.sendMessage("You clicked the Close Shop button!");
        player.closeInventory();
    }

    private void handleBuy(Player player) {
        player.sendMessage("You clicked the Buy button!");
        // Add logic for handling purchase
    }
}
