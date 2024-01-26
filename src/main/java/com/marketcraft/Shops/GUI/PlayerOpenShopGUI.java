package com.marketcraft.Shops.GUI;

import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.marketcraft.Util.GUIUtils.createNamedItem;

/**
 * Handles the graphical user interface for opening and interacting with a player's shop.
 * This class manages the creation and display of the shop inventory, allowing players to view and interact with
 * items for sale, including their costs and available stock.
 * @see com.marketcraft.Util.GUIUtils
 */
public class PlayerOpenShopGUI {
    private static final int INVENTORY_SIZE = 54; // 6 rows x 9 columns for a double chest
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;

    /**
     * Constructs an instance of PlayerOpenShopGUI with the given shop and vault managers.
     *
     * @param playerShopManager The manager responsible for handling player shop data.
     * @param playerVaultManager The manager responsible for handling player vault data.
     */
    public PlayerOpenShopGUI(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
    }

    /**
     * Opens the shop GUI for the specified player, showing items for sale, their costs, and stock availability.
     * The method sets up the shop inventory based on the shop owner's UUID, displaying items for sale and purchase options.
     *
     * @param player        The player for whom the shop GUI is to be opened.
     * @param shopOwnerUUID The UUID of the shop owner whose items are being displayed in the shop.
     */
    public void openPlayerShopGUI(Player player, UUID shopOwnerUUID) {
        // If the method returns null, we can assume the shop does not exist or is invalid
        ItemStack[] shopItems = playerShopManager.getPlayerShopItems(shopOwnerUUID);
        if (shopItems == null) {
            player.sendMessage("Shop does not exist or is invalid");
            return;
        }

        Inventory shopInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop"));

        ItemStack itemBeingSoldTag = createNamedItem(Material.NAME_TAG, "Selling");
        ItemStack itemCostTag = createNamedItem(Material.NAME_TAG, "Cost");
        ItemStack confirmSelection = createNamedItem(Material.GREEN_WOOL, "Buy");
        ItemStack cancelSelection = createNamedItem(Material.RED_WOOL, "Close shop");

        // Create the customized items
        ItemStack itemBeingSold = shopItems[0] != null ? shopItems[0] : new ItemStack(Material.AIR);
        ItemStack itemCost = shopItems[1] != null ? shopItems[1] : new ItemStack(Material.AIR);
        int stockCount = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold);
        ItemStack stockIndicator = createNamedItem(Material.NAME_TAG, "Shop has " + stockCount + " in stock");

        // Fill the entire inventory with the background
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            shopInventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Replacing certain slots with the menu items
        shopInventory.setItem(4, itemBeingSoldTag);
        shopInventory.setItem(31, itemCostTag);
        shopInventory.setItem(45, cancelSelection);
        shopInventory.setItem(53, confirmSelection);
        shopInventory.setItem(14, stockIndicator);
        shopInventory.setItem(13, itemBeingSold);
        shopInventory.setItem(40, itemCost);

        // Setup is done, create the inventory
        player.openInventory(shopInventory);
    }
}
