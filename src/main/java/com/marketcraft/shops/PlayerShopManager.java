/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.shops;

import com.marketcraft.MarketCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages player shops in the MarketCraft plugin.
 * This class is responsible for handling the creation, deletion, and management of player shops.
 * It interacts with the plugin's file system to store and retrieve shop data, ensuring persistence across server sessions.
 * <p>
 * Key functionalities include:
 * - Saving shop configurations set by players.
 * - Retrieving items associated with a specific shop.
 * - Listing all existing shop files for administrative purposes.
 * - Deleting shops based on player UUID and shop name.
 * - Checking the existence of a player's shop.
 * <p>
 * The shop data is stored as YAML configurations, with each player having a unique file based on their UUID.
 * The class provides a streamlined interface for other components of the plugin to interact with shop data.
 */
public class PlayerShopManager {
    private final File shopsFolder;

    public PlayerShopManager(File pluginFolder) {
        this.shopsFolder = new File(pluginFolder, "Shops");
        if (!shopsFolder.exists() && !shopsFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the Shops directory, the plugin may fail to function correctly!");
        }
    }

    /**
     * Saves the configuration of a player's shop to a YAML file.
     * This includes serializing the items to sell and charge in the shop.
     *
     * @param player       The player who owns the shop.
     * @param shopName     The name of the shop.
     * @param itemToSell   The item to be sold in the shop.
     * @param itemToCharge The item to be charged in the shop.
     */
    public void savePlayerShop(Player player, String shopName, ItemStack itemToSell, ItemStack itemToCharge) {
        UUID playerUUID = player.getUniqueId();
        String basePath = "shops." + shopName;
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerShopFile);
        // Serialize the ItemStacks (Allows storing of NBT data)
        config.set(basePath + ".itemToSell", itemToSell.serialize());
        config.set(basePath + ".itemToCharge", itemToCharge.serialize());
        try {
            config.save(playerShopFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while saving" + player.getName() + "'s shop: ", e);
            player.sendMessage(Component.text("An error occurred while saving your shop. Please try again later."));
        }
    }

    /**
     * Retrieves items associated with a player's shop from the shop's YAML configuration file.
     * Returns an array of ItemStacks containing the item to sell and the item to charge.
     *
     * @param playerUUID The UUID of the player who owns the shop.
     * @param shopName   The name of the shop.
     * @return An array of ItemStacks containing the selling and charging items, or null if the shop or items do not exist.
     */
    public ItemStack[] getPlayerShopItems(UUID playerUUID, String shopName) {
        String basePath = "shops." + shopName;
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");
        // This only checks if the file exists or not
        if (!playerShopFile.exists()) {
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerShopFile);
        // This then checks if the specific shop exists within the file
        if (!config.contains(basePath)) {
            return null;
        }
        ItemStack itemToSell = null;
        ItemStack itemToCharge = null;
        if (config.contains(basePath + ".itemToSell")) {
            itemToSell = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection(basePath + ".itemToSell")).getValues(false));
        }
        if (config.contains(basePath + ".itemToCharge")) {
            itemToCharge = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection(basePath + ".itemToCharge")).getValues(false));
        }
        return new ItemStack[]{itemToSell, itemToCharge};
    }

    /**
     * Deletes a player's shop configuration from the YAML file.
     * Returns a boolean indicating the success of the deletion process.
     *
     * @param uuidString The string representation of the player's UUID.
     * @param shopName   The name of the shop to be deleted.
     * @return True if the shop was successfully deleted, false otherwise.
     */
    public boolean deletePlayerShop(String uuidString, String shopName) {
        String basePath = "shops." + shopName;
        UUID playerUUID = UUID.fromString(uuidString);
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");
        // This only checks if the file exists or not
        if (!playerShopFile.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerShopFile);
        // This then checks if the specific shop exists within the file
        if (!config.contains(basePath)) {
            return false;
        }
        config.set(basePath, null);
        try {
            config.save(playerShopFile);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while deleting a player's shop: ", e);
            return false;
        }
    }

    /**
     * Checks whether a player has reached their shop creation limit.
     * This method examines the player's shop file to determine the number of shops they currently own,
     * comparing this count to the maximum number of shops allowed per player, as specified in the plugin's configuration.
     * It returns true if the player is below their shop limit, thereby allowing the creation of additional shops.
     *
     * @param player The player whose shop count is being checked.
     * @return true if the player is below their shop limit, false otherwise.
     */
    public boolean isAtShopLimit(Player player) {
        UUID playerUUID = player.getUniqueId();
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");
        if (!playerShopFile.exists()) {
            return true; // No shops file means the player is below the limit.
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerShopFile);
        ConfigurationSection shopsSection = config.getConfigurationSection("shops");
        if (shopsSection == null) {
            return true; // No "shops" section means the player is below the limit.
        }
        int shopCount = shopsSection.getKeys(false).size();
        int shopLimit = MarketCraft.getShopLimit();
        return shopCount < shopLimit;
    }

    /**
     * Checks if a player's shop exists in the YAML configuration file.
     *
     * @param playerUUID The UUID of the player.
     * @param shopName   The name of the shop.
     * @return True if the shop exists, false otherwise.
     */
    public boolean doesPlayerShopExist(UUID playerUUID, String shopName) {
        String basePath = "shops." + shopName;
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");
        if (!playerShopFile.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerShopFile);
        return config.contains(basePath);
    }
}