/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.gui;

import com.marketcraft.MarketCraft;
import com.marketcraft.locks.ShopLockManager;
import com.marketcraft.vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

import static com.marketcraft.util.GUIUtils.createNamedItem;

/**
 * Manages the graphical user interface (GUI) for player vaults in the MarketCraft plugin.
 * This class is responsible for the creation, display, and interaction of the vault inventory
 * for players, which is a key component of the plugin's shop system.
 */
public class PlayerVaultGUI {
    private final PlayerVaultManager playerVaultManager;
    private final ShopLockManager shopLockManager;
    private final MarketCraft marketCraft;
    private static final int VAULT_SIZE = 54;
    private static final int[] DIVIDER_LINE_SLOTS = {13, 22, 31, 40, 49};
    private static final int INFO_BOOK_SLOT = 4;

    public PlayerVaultGUI(PlayerVaultManager playerVaultManager, ShopLockManager shopLockManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.shopLockManager = shopLockManager;
        this.marketCraft = marketCraft;
    }

    /**
     * Opens a vault GUI for a specific player and shop.
     * This method creates and displays the inventory interface representing the player's vault
     * for a particular shop. The vault includes items the player is buying or selling. The method
     * ensures that the vault is personalized for the player and the specified shop, and it loads
     * the current state of the vault from the stored data. If the vault file does not exist or
     * an error occurs, the player is notified with an appropriate message.
     *
     * @param player   The player for whom the vault is being opened.
     * @param shopName The name of the shop associated with the vault to be opened.
     */
    public void openVault(Player player, String shopName) {
        UUID playerUUID = player.getUniqueId();
        File playerVaultFile = playerVaultManager.getPlayerVaultFile(playerUUID);
        if (playerVaultFile == null) {
            // The player should never be able to get to this point unless something goes wrong
            player.sendMessage(Component.text("An unexpected error has occurred, please wait a moment then try again."));
            return;
        }
        // Lock the shop linked to this vault
        shopLockManager.lockShop(playerUUID, shopName, playerUUID);
        Inventory vaultInventory = Bukkit.createInventory(player, VAULT_SIZE, Component.text("Your Vault"));
        ItemStack infoBook = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = infoBook.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING, shopName);
        // Set the lore text for the book
        List<Component> lore = List.of(
                Component.text("Currently open shop vault " + shopName + "."),
                Component.text("Items you are buying are on the right."),
                Component.text("Items you are selling are on the left.")
        );
        meta.lore(lore);
        infoBook.setItemMeta(meta);
        // Divider line
        for (int slot : DIVIDER_LINE_SLOTS) {
            vaultInventory.setItem(slot, createNamedItem(Material.GRAY_STAINED_GLASS_PANE, "")); // Empty name
        }
        vaultInventory.setItem(INFO_BOOK_SLOT, infoBook);
        // Continue with loading the rest of the vault
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String shopVaultPath = "vault." + shopName;
        if (config.contains(shopVaultPath)) {
            ConfigurationSection shopVaultSection = config.getConfigurationSection(shopVaultPath);
            Set<String> keys = Objects.requireNonNull(shopVaultSection).getKeys(false);
            for (String key : keys) {
                Map<String, Object> itemData = Objects.requireNonNull(shopVaultSection.getConfigurationSection(key)).getValues(false);
                ItemStack item = ItemStack.deserialize(itemData);
                int slot = Integer.parseInt(key.replace("slot_", ""));
                vaultInventory.setItem(slot, item);
            }
        }
        player.openInventory(vaultInventory);
    }
}