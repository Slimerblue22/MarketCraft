package com.marketcraft.Shops.GUI;

import com.marketcraft.MarketCraft;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static com.marketcraft.Util.GUIUtils.createNamedItem;
import static com.marketcraft.Util.GUIUtils.createPlayerHead;

/**
 * Handles the graphical user interface for opening and interacting with a player's shop.
 * This class manages the creation and display of the shop inventory, allowing players to view and interact with
 * items for sale, including their costs and available stock.
 * @see com.marketcraft.Util.GUIUtils
 */
public class PlayerOpenShopGUI {
    private static final int INVENTORY_SIZE = 27;
    private static final int SELL_TAG_SLOT = 10;
    private static final int CHARGE_TAG_SLOT = 14;
    private static final int CANCEL_SLOT = 22;
    private static final int CONFIRM_SLOT = 16;
    private static final int SELL_SLOT = 11;
    private static final int CHARGE_SLOT = 15;
    public static final int STOCK_INDICATOR_SLOT = 20;
    public static final int OWNER_HEAD_SLOT = 4;
    private final PlayerShopManager playerShopManager;
    private final PlayerVaultManager playerVaultManager;
    private final MarketCraft marketCraft;

    /**
     * Constructs an instance of PlayerOpenShopGUI with the given shop and vault managers.
     *
     * @param playerShopManager The manager responsible for handling player shop data.
     * @param playerVaultManager The manager responsible for handling player vault data.
     * @param marketCraft The main plugin class.
     */
    public PlayerOpenShopGUI(PlayerShopManager playerShopManager, PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerShopManager = playerShopManager;
        this.playerVaultManager = playerVaultManager;
        this.marketCraft = marketCraft;
    }

    /**
     * Opens the shop GUI for the specified player, showing items for sale, their costs, and stock availability.
     * The method sets up the shop inventory based on the shop owner's UUID, displaying items for sale and purchase options.
     *
     * @param player        The player for whom the shop GUI is to be opened.
     * @param shopOwnerUUID The UUID of the shop owner whose items are being displayed in the shop.
     * @param shopName The name of the shop that the player is accessing.
     */
    public void openPlayerShopGUI(Player player, UUID shopOwnerUUID, String shopName) {
        // If the method returns null, we can assume the shop does not exist or is invalid
        ItemStack[] shopItems = playerShopManager.getPlayerShopItems(shopOwnerUUID, shopName);
        if (shopItems == null) {
            player.sendMessage("Shop does not exist or is invalid");
            return;
        }

        Inventory shopInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop"));

        ItemStack itemBeingSoldTag = createNamedItem(Material.NAME_TAG, "Selling");
        ItemStack itemCostTag = createNamedItem(Material.NAME_TAG, "Cost");
        ItemStack confirmSelection = createNamedItem(Material.LIME_STAINED_GLASS_PANE, "Buy");
        ItemStack cancelSelection = createNamedItem(Material.RED_STAINED_GLASS_PANE, "Close shop");

        // Create the customized items
        ItemStack itemBeingSold = shopItems[0] != null ? shopItems[0] : new ItemStack(Material.AIR);
        ItemStack itemCost = shopItems[1] != null ? shopItems[1] : new ItemStack(Material.AIR);
        int stockCount = playerVaultManager.getItemCountInPlayerVault(shopOwnerUUID, itemBeingSold);
        ItemStack stockIndicator = createNamedItem(Material.NAME_TAG, "Shop has " + stockCount + " in stock");
        ItemStack ownerIdentifier = createPlayerHead(shopOwnerUUID);
        ItemMeta meta = ownerIdentifier.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(marketCraft, "shopOwnerUUID"), PersistentDataType.STRING, shopOwnerUUID.toString());
        ownerIdentifier.setItemMeta(meta);

        // Fill the entire inventory with the background
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            shopInventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Replacing certain slots with the menu items
        shopInventory.setItem(SELL_TAG_SLOT, itemBeingSoldTag);
        shopInventory.setItem(CHARGE_TAG_SLOT, itemCostTag);
        shopInventory.setItem(CANCEL_SLOT, cancelSelection);
        shopInventory.setItem(CONFIRM_SLOT, confirmSelection);
        shopInventory.setItem(STOCK_INDICATOR_SLOT, stockIndicator);
        shopInventory.setItem(SELL_SLOT, itemBeingSold);
        shopInventory.setItem(CHARGE_SLOT, itemCost);
        shopInventory.setItem(OWNER_HEAD_SLOT, ownerIdentifier);

        // Setup is done, create the inventory
        player.openInventory(shopInventory);
    }
}
