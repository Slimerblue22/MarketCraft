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
 *
 * @see com.marketcraft.Util.GUIUtils
 */
public class PlayerShopSetupGUI {
    private static final int INVENTORY_SIZE = 27;
    private static final int SELL_TAG_SLOT = 10;
    private static final int CHARGE_TAG_SLOT = 15;
    private static final int CANCEL_SLOT = 21;
    private static final int CONFIRM_SLOT = 23;
    private static final int SELL_SLOT = 11;
    private static final int CHARGE_SLOT = 16;
    private static final int SHOP_NAME_TAG_SLOT = 4;

    /**
     * Opens the shop setup GUI for the specified player.
     * This method creates an inventory with custom items allowing the player to
     * specify which items they want to sell and at what cost. It also provides options
     * to confirm or cancel the shop setup.
     *
     * @param player   The player for whom the shop setup GUI is to be opened.
     * @param shopName The name of the shop being created.
     */
    public void openShopSetupGUI(Player player, String shopName) {
        Inventory shopSetupInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop Setup"));
        // Create the generic items for the inventory
        ItemStack shopNameTag = createNamedItem(Material.NAME_TAG, shopName);
        ItemStack itemToSellTag = createNamedItem(Material.NAME_TAG, "Place item to sell to the right");
        ItemStack itemToChargeTag = createNamedItem(Material.NAME_TAG, "Place item to charge to the right");
        ItemStack cancelSelection = createNamedItem(Material.RED_STAINED_GLASS_PANE, "Click to cancel selection");
        ItemStack confirmSelection = createNamedItem(Material.LIME_STAINED_GLASS_PANE, "Click to confirm selection");
        // Fill the entire inventory with the background
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            shopSetupInventory.setItem(i, createNamedItem(Material.GRAY_STAINED_GLASS_PANE, "")); // Empty name
        }
        // Replacing certain slots with the menu items
        shopSetupInventory.setItem(SHOP_NAME_TAG_SLOT, shopNameTag);
        shopSetupInventory.setItem(SELL_TAG_SLOT, itemToSellTag);
        shopSetupInventory.setItem(CHARGE_TAG_SLOT, itemToChargeTag);
        shopSetupInventory.setItem(CANCEL_SLOT, cancelSelection);
        shopSetupInventory.setItem(CONFIRM_SLOT, confirmSelection);
        // Clearing these slots to accept buy and sell items
        shopSetupInventory.clear(SELL_SLOT);
        shopSetupInventory.clear(CHARGE_SLOT);
        // Setup is done, create the inventory
        player.openInventory(shopSetupInventory);
    }
}