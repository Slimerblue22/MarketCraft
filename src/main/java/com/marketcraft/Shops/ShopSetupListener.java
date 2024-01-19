package com.marketcraft.Shops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ShopSetupListener implements Listener {

    // It would appear there is an issue with adding items that already exist within the GUI
    // Since the listener is canceling clicks on these items, you can usually add them but can't remove them afterward
    // TODO: Look into fix later (Maybe manually define the slots to cancel?)
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player player && event.getView().title().equals(Component.text("Shop Setup"))) {
            ItemStack clickedItem = event.getCurrentItem();
            boolean isTopInventory = event.getView().getTopInventory().getType() == InventoryType.CHEST &&
                    event.getRawSlot() < event.getView().getTopInventory().getSize();

            // Prevent shift-clicking items into the custom inventory as this can cause issues
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                if (isTopInventory || event.getClickedInventory() == player.getInventory()) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (isTopInventory) {
                // Cancel clicks on non-interactive items
                if (clickedItem != null && (clickedItem.getType() == Material.GREEN_STAINED_GLASS_PANE ||
                        clickedItem.getType() == Material.RED_STAINED_GLASS_PANE ||
                        clickedItem.getType() == Material.NAME_TAG)) {
                    event.setCancelled(true);
                } else if (event.getRawSlot() == 13 || event.getRawSlot() == 40) {
                    // Allow interactions with empty slots (13 and 40)
                    return; // Allow placing and taking items
                } else if (clickedItem != null && clickedItem.getType() == Material.RED_WOOL) {
                    // Handling red wool click
                    player.sendMessage(Component.text("Red wool clicked for canceling", NamedTextColor.GREEN));
                    event.setCancelled(true);
                    player.closeInventory();
                    return;
                } else if (clickedItem != null && clickedItem.getType() == Material.GREEN_WOOL) {
                    // Handling green wool click
                    player.sendMessage(Component.text("Green wool clicked for confirming", NamedTextColor.GREEN));
                    event.setCancelled(true);
                    player.closeInventory();
                    return;
                }

                // Cancel all other interactions within the top inventory
                event.setCancelled(true);
            }
        }
    }
}
