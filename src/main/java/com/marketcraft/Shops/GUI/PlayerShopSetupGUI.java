package com.marketcraft.Shops.GUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.marketcraft.Util.GUIUtils.createNamedItem;

/**
 * Handles the graphical user interface for setting up a player's shop in the game.
 * This class is responsible for creating and managing the inventory interface
 * where players can configure the items they wish to sell and the price for each item.
 * @see com.marketcraft.Util.GUIUtils
 */
public class PlayerShopSetupGUI {
    private static final int INVENTORY_SIZE = 54; // 6 rows x 9 columns for a double chest

    /**
     * Opens the shop setup GUI for the specified player.
     * This method creates an inventory with custom items allowing the player to
     * specify which items they want to sell and at what cost. It also provides options
     * to confirm or cancel the shop setup.
     *
     * @param player The player for whom the shop setup GUI is to be opened.
     */
    public void openShopSetupGUI(Player player) {
        Inventory shopSetupInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop Setup"));

        // Create the generic items for the inventory
        ItemStack itemToSellTag = createNamedItem(Material.NAME_TAG, "Place item to sell here");
        ItemStack itemToChargeTag = createNamedItem(Material.NAME_TAG, "Place item to charge here");
        ItemStack cancelSelection = createNamedItem(Material.RED_WOOL, "Click to cancel selection");
        ItemStack confirmSelection = createNamedItem(Material.GREEN_WOOL, "Click to confirm selection");

        // Fill the entire inventory with the background
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            shopSetupInventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Replacing certain slots with the menu items
        shopSetupInventory.setItem(4, itemToSellTag);
        shopSetupInventory.setItem(31, itemToChargeTag);
        shopSetupInventory.setItem(45, cancelSelection);
        shopSetupInventory.setItem(53, confirmSelection);

        // Clearing these slots to accept buy and sell items
        shopSetupInventory.clear(13);
        shopSetupInventory.clear(40);

        // Setup is done, create the inventory
        player.openInventory(shopSetupInventory);
    }
}