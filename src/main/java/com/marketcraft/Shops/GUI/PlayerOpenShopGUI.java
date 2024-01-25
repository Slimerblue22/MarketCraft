package com.marketcraft.Shops.GUI;

import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PlayerOpenShopGUI {

    private static final int INVENTORY_SIZE = 54; // 6 rows x 9 columns for a double chest
    private final PlayerShopManager playerShopManager;

    public PlayerOpenShopGUI(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
    }

    public void openPlayerShopGUI(Player player, UUID shopOwnerUUID) {
        ItemStack[] shopItems = playerShopManager.getPlayerShopItems(shopOwnerUUID);

        if (shopItems == null) {
            player.sendMessage("Shop does not exist or is invalid");
            return;
        }

        Inventory shopInventory = Bukkit.createInventory(player, INVENTORY_SIZE, Component.text("Shop"));

        // Create the items for the inventory
        ItemStack greenPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack itemBeingSoldTag = new ItemStack(Material.NAME_TAG);
        ItemStack itemCostTag = new ItemStack(Material.NAME_TAG);
        ItemStack itemBeingSold = shopItems[0] != null ? shopItems[0] : new ItemStack(Material.AIR);
        ItemStack itemCost = shopItems[1] != null ? shopItems[1] : new ItemStack(Material.AIR);
        ItemStack confirmSelection = new ItemStack(Material.GREEN_WOOL);
        ItemStack cancelSelection = new ItemStack(Material.RED_WOOL);

        // Set names for special tag items
        ItemMeta itemBeingSoldTagMeta = itemBeingSoldTag.getItemMeta();
        itemBeingSoldTagMeta.displayName(Component.text("Selling"));
        itemBeingSoldTag.setItemMeta(itemBeingSoldTagMeta);

        ItemMeta itemCostTagMeta = itemCostTag.getItemMeta();
        itemCostTagMeta.displayName(Component.text("Cost"));
        itemCostTag.setItemMeta(itemCostTagMeta);

        // Set names for action items
        ItemMeta closeShopMeta = cancelSelection.getItemMeta();
        closeShopMeta.displayName(Component.text("Close shop"));
        cancelSelection.setItemMeta(closeShopMeta);

        ItemMeta buyMeta = confirmSelection.getItemMeta();
        buyMeta.displayName(Component.text("Buy"));
        confirmSelection.setItemMeta(buyMeta);

        // Fill the first 3 rows with green panes
        for (int i = 0; i < 27; i++) {
            shopInventory.setItem(i, greenPane);
        }

        // Fill the last 3 rows with red panes
        for (int i = 27; i < INVENTORY_SIZE; i++) {
            shopInventory.setItem(i, redPane);
        }

        // Menu items
        shopInventory.setItem(4, itemBeingSoldTag);
        shopInventory.setItem(31, itemCostTag);
        shopInventory.setItem(45, cancelSelection);
        shopInventory.setItem(53, confirmSelection);

        // Items being sold and bought
        shopInventory.setItem(13, itemBeingSold);
        shopInventory.setItem(40, itemCost);

        player.openInventory(shopInventory);
    }
}
