package com.marketcraft.Shops.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerShopSetupGUI {

    private static final int INVENTORY_SIZE = 54; // 6 rows x 9 columns for a double chest

    public void openShopSetupGUI(Player player) {
        Inventory shopSetupInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop Setup"));

        // Create the items for the inventory
        ItemStack greenPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack itemToSell = new ItemStack(Material.NAME_TAG);
        ItemStack itemToCharge = new ItemStack(Material.NAME_TAG);
        ItemStack confirmSelection = new ItemStack(Material.GREEN_WOOL);
        ItemStack cancelSelection = new ItemStack(Material.RED_WOOL);

        // Set names for special items
        ItemMeta itemToSellMeta = itemToSell.getItemMeta();
        itemToSellMeta.displayName(Component.text("Place item to sell here"));
        itemToSell.setItemMeta(itemToSellMeta);

        ItemMeta itemToChargeMeta = itemToCharge.getItemMeta();
        itemToChargeMeta.displayName(Component.text("Place item to charge here"));
        itemToCharge.setItemMeta(itemToChargeMeta);

        ItemMeta cancelSelectionMeta = cancelSelection.getItemMeta();
        cancelSelectionMeta.displayName(Component.text("Click to cancel selection"));
        cancelSelection.setItemMeta(cancelSelectionMeta);

        ItemMeta confirmSelectionMeta = confirmSelection.getItemMeta();
        confirmSelectionMeta.displayName(Component.text("Click to confirm selection"));
        confirmSelection.setItemMeta(confirmSelectionMeta);

        // Fill the first 3 rows with green panes
        for (int i = 0; i < 27; i++) {
            shopSetupInventory.setItem(i, greenPane);
        }

        // Fill the last 3 rows with red panes
        for (int i = 27; i < INVENTORY_SIZE; i++) {
            shopSetupInventory.setItem(i, redPane);
        }

        // Set special items
        shopSetupInventory.setItem(4, itemToSell);
        shopSetupInventory.setItem(31, itemToCharge);
        shopSetupInventory.setItem(45, cancelSelection);
        shopSetupInventory.setItem(53, confirmSelection);

        // Empty slots 13 and 40
        shopSetupInventory.clear(13);
        shopSetupInventory.clear(40);

        player.openInventory(shopSetupInventory);
    }
}