/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.vaults;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages the vaults of players in the MarketCraft plugin.
 * This class handles the storage, retrieval, and modification of player vaults, which are used to manage shop inventory.
 * It ensures the persistence of vault contents, provides functionalities for item transactions, and manages vault file operations.
 * <p>
 * Key functionalities include:
 * - Creating and removing player vault files.
 * - Saving and loading vault contents.
 * - Managing items within vaults including adding, removing, and checking item counts.
 * - Listing all vault files for administrative purposes.
 */
public class PlayerVaultManager {
    private final File vaultsFolder;
    private static final Set<Integer> GUI_SLOTS = Set.of(4, 13, 22, 31, 40, 49);
    private static final Set<Integer> SELLING_SLOTS = Set.of(0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48);

    public PlayerVaultManager(File pluginFolder) {
        this.vaultsFolder = new File(pluginFolder, "Vaults");
        if (!vaultsFolder.exists() && !vaultsFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the Vaults directory, the plugin may fail to function correctly!");
        }
    }

    /**
     * Checks if a player's vault file exists.
     *
     * @param playerUUID The UUID of the player.
     * @return True if the vault file exists, false otherwise.
     */
    public boolean doesPlayerVaultExist(UUID playerUUID) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        return playerVaultFile.exists();
    }

    /**
     * Creates a file for a player's vault associated with a specific shop.
     *
     * @param player   The player for whom the vault is being created.
     * @param shopName The name of the shop associated with the vault.
     */
    public void createPlayerVaultFile(Player player, String shopName) {
        UUID playerUUID = player.getUniqueId();
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
            boolean isNewFileCreated = playerVaultFile.createNewFile();
            if (isNewFileCreated) {
                // Create a section for the shop in the vault
                config.createSection("vault." + shopName); // Create an empty section for the specific shop
                // Save the file with the new structure
                config.save(playerVaultFile);
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while creating" + player.getName() + "'s vault for " + shopName + ": ", e);
            player.sendMessage(Component.text("An error occurred while creating your vault for " + shopName + ". Please try again later."));
        }
    }

    /**
     * Counts the number of a specific item in a player's vault for a given shop.
     *
     * @param playerUUID  The UUID of the player owning the vault.
     * @param itemToCheck The item to count in the vault.
     * @param shopName    The name of the shop associated with the vault.
     * @return The count of the specified item in the vault.
     */
    public int getItemCountInPlayerVault(UUID playerUUID, ItemStack itemToCheck, String shopName) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return 0;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        int itemCount = 0;
        String shopVaultPath = "vault." + shopName;
        // Check if the shopVaultPath exists in the config
        if (config.contains(shopVaultPath)) {
            ConfigurationSection shopVaultSection = config.getConfigurationSection(shopVaultPath);
            for (String key : Objects.requireNonNull(shopVaultSection).getKeys(false)) {
                // Determine the slot index from the key and skip GUI slots
                int slotIndex = Integer.parseInt(key.replace("slot_", ""));
                if (GUI_SLOTS.contains(slotIndex)) {
                    continue;
                }
                ItemStack item = ItemStack.deserialize(Objects.requireNonNull(shopVaultSection.getConfigurationSection(key)).getValues(false));
                // Check if the item is similar to the one we are looking for
                if (item.isSimilar(itemToCheck)) {
                    itemCount += item.getAmount();
                }
            }
        }
        return itemCount;
    }

    /**
     * Adds a specified amount of an item to a player's vault for a given shop.
     *
     * @param playerUUID The UUID of the player owning the vault.
     * @param itemToAdd  The item to be added to the vault.
     * @param amount     The amount of the item to add.
     * @param shopName   The name of the shop associated with the vault.
     */
    public void addItemsToPlayerVault(UUID playerUUID, ItemStack itemToAdd, int amount, String shopName) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return;
        }
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
            boolean itemAdded = false;
            String shopVaultPath = "vault." + shopName;
            // Check existing slots within the specific shop's vault
            for (int i = 0; i < 54; i++) { // This is currently a hardcoded vault size limit
                // We ignore the menu slots since they are menu items
                // We ignore the selling slots since this method is only ever used to add payment items
                // from a successful sell, those items should be put into the payment slots instead
                if (GUI_SLOTS.contains(i) || (SELLING_SLOTS.contains(i))) {
                    // Skip GUI slots
                    continue;
                }
                String slotKey = shopVaultPath + ".slot_" + i;
                if (config.contains(slotKey)) {
                    ItemStack existingItem = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection(slotKey)).getValues(false));
                    if (existingItem.isSimilar(itemToAdd)) {
                        // Increase amount if similar item found
                        int newAmount = existingItem.getAmount() + amount;
                        if (newAmount <= existingItem.getMaxStackSize()) {
                            existingItem.setAmount(newAmount);
                            config.set(slotKey, existingItem.serialize());
                            itemAdded = true;
                            break;
                        }
                    }
                } else {
                    // Add item to a new empty slot
                    itemToAdd.setAmount(amount);
                    config.set(slotKey, itemToAdd.serialize());
                    itemAdded = true;
                    break;
                }
            }
            // Save changes if an item was added
            if (itemAdded) {
                config.save(playerVaultFile);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while adding items to a player's vault: " + shopName, e);
        }
    }

    /**
     * Removes a specified amount of an item from a player's vault for a given shop.
     *
     * @param playerUUID     The UUID of the player owning the vault.
     * @param itemToRemove   The item to be removed from the vault.
     * @param amountToRemove The amount of the item to remove.
     * @param shopName       The name of the shop associated with the vault.
     */
    public void removeItemsFromPlayerVault(UUID playerUUID, ItemStack itemToRemove, int amountToRemove, String shopName) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return;
        }
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
            String shopVaultPath = "vault." + shopName;
            // Track the remaining amount to remove
            int remainingAmount = amountToRemove;
            ConfigurationSection shopVaultSection = config.getConfigurationSection(shopVaultPath);
            for (String key : Objects.requireNonNull(shopVaultSection).getKeys(false)) {
                // Determine the slot index from the key
                int slotIndex = Integer.parseInt(key.replace("slot_", ""));
                // Skip GUI slots
                if (GUI_SLOTS.contains(slotIndex)) {
                    continue;
                }
                if (remainingAmount <= 0) break; // Stop if the required amount has been removed
                String fullKeyPath = shopVaultPath + "." + key;
                ItemStack item = ItemStack.deserialize(Objects.requireNonNull(shopVaultSection.getConfigurationSection(key)).getValues(false));
                if (item.isSimilar(itemToRemove)) {
                    int currentAmount = item.getAmount();
                    if (currentAmount > remainingAmount) {
                        item.setAmount(currentAmount - remainingAmount);
                        config.set(fullKeyPath, item.serialize());
                        remainingAmount = 0;
                    } else {
                        // Remove the entire stack and decrement the remaining amount
                        remainingAmount -= currentAmount;
                        config.set(fullKeyPath, null); // Remove the item stack from the slot
                    }
                }
            }
            // Save changes if any items were removed
            if (remainingAmount < amountToRemove) {
                config.save(playerVaultFile);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while removing items from a player's vault: " + shopName, e);
        }
    }

    /**
     * Checks if a specific item can be added to a player's vault, considering available space.
     *
     * @param playerUUID The UUID of the player owning the vault.
     * @param itemToAdd  The item to be added to the vault.
     * @param amount     The amount of the item to add.
     * @param shopName   The name of the shop associated with the vault.
     * @return True if the item can be added, false otherwise.
     */
    public boolean canAddItemToPlayerVault(UUID playerUUID, ItemStack itemToAdd, int amount, String shopName) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String shopVaultPath = "vault." + shopName;
        // Check existing slots for a match or an empty slot
        for (int i = 0; i < 54; i++) { // This is currently a hardcoded vault size limit
            // We ignore the menu slots since they are menu items
            // We ignore the selling slots since this method is only ever used to add payment items
            // from a successful sell, those items should be put into the payment slots instead
            if (GUI_SLOTS.contains(i) || (SELLING_SLOTS.contains(i))) {
                // Skip GUI slots
                continue;
            }
            String slotKey = shopVaultPath + ".slot_" + i;
            if (config.contains(slotKey)) {
                ItemStack existingItem = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection(slotKey)).getValues(false));
                if (existingItem.isSimilar(itemToAdd)) {
                    // Check if the existing similar item can hold more
                    int totalAmount = existingItem.getAmount() + amount;
                    if (totalAmount <= existingItem.getMaxStackSize()) {
                        return true;
                    }
                }
            } else {
                // Found an empty slot
                return true;
            }
        }
        // No available slot found
        return false;
    }

    /**
     * Retrieves the file corresponding to a player's vault.
     *
     * @param playerUUID The UUID of the player.
     * @return The file of the player's vault, or null if it doesn't exist.
     */
    public File getPlayerVaultFile(UUID playerUUID) {
        if (doesPlayerVaultExist(playerUUID)) {
            return new File(vaultsFolder, playerUUID + ".yml");
        }
        return null;
    }

    /**
     * Saves the contents of a player's vault to a file.
     *
     * @param player         The player whose vault is being saved.
     * @param vaultInventory The inventory of the vault.
     * @param shopName       The name of the shop associated with the vault.
     */
    public void savePlayerVault(Player player, Inventory vaultInventory, String shopName) {
        File playerVaultFile = getPlayerVaultFile(player.getUniqueId());
        if (playerVaultFile == null) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String shopVaultPath = "vault." + shopName;
        for (int i = 0; i < 54; i++) { // This is currently a hardcoded vault size limit
            if (GUI_SLOTS.contains(i)) {
                // Skip the loop iteration if the slot is in the set
                continue;
            }
            ItemStack item = vaultInventory.getItem(i);
            if (item != null) {
                config.set(shopVaultPath + ".slot_" + i, item.serialize());
            } else {
                config.set(shopVaultPath + ".slot_" + i, null);
            }
        }
        // TODO: Need better error handling here, otherwise people may lose items or even duplicate them!
        try {
            config.save(playerVaultFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while saving " + player.getName() + "'s vault: " + shopName, e);
        }
    }

    /**
     * Checks if a player's vault is empty for a specific shop.
     *
     * @param uuidString The UUID of the player as a string.
     * @param vaultName  The name of the vault.
     * @return True if the vault is empty, false if it contains any items.
     */
    public boolean isPlayerVaultEmpty(String uuidString, String vaultName) {
        UUID playerUUID = UUID.fromString(uuidString);
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return true; // Vault file does not exist, hence empty
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String basePath = "vault." + vaultName;
        if (!config.contains(basePath)) {
            return true; // Vault does not exist, hence empty
        }
        ConfigurationSection vaultSection = config.getConfigurationSection(basePath);
        if (vaultSection == null || vaultSection.getKeys(false).isEmpty()) {
            return true; // Vault exists but has no items, hence empty
        }
        for (String key : vaultSection.getKeys(false)) {
            if (!GUI_SLOTS.contains(Integer.parseInt(key.replace("slot_", "")))) {
                ItemStack item = ItemStack.deserialize(Objects.requireNonNull(vaultSection.getConfigurationSection(key)).getValues(false));
                if (item.getType() != Material.AIR) {
                    return false; // Found a non-empty slot, hence not empty
                }
            }
        }
        return true; // No non-empty slots found, hence empty
    }

    /**
     * Removes a specific vault for a player from the YAML configuration file.
     * This method deletes only the specified vault associated with the player's UUID,
     * rather than removing the entire file.
     *
     * @param uuidString The UUID of the player as a string.
     * @param vaultName  The name of the vault to be removed.
     * @return True if the vault was successfully removed, false otherwise. Reasons for failure
     * might include the non-existence of the vault file, the absence of the specified vault,
     * or an error occurring during the file update.
     */
    public boolean removePlayerVault(String uuidString, String vaultName) {
        UUID playerUUID = UUID.fromString(uuidString);
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String basePath = "vault." + vaultName;
        if (!config.contains(basePath)) {
            return false;
        }
        config.set(basePath, null);
        try {
            config.save(playerVaultFile);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while deleting a player's vault: ", e);
            return false;
        }
    }
}