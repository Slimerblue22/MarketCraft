package com.marketcraft.Vaults;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerVaultManager {
    private final File vaultsFolder;

    public PlayerVaultManager(File pluginFolder) {
        this.vaultsFolder = new File(pluginFolder, "Vaults");
        if (!vaultsFolder.exists() && !vaultsFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the Vaults directory, the plugin may fail to function correctly!");
        }
    }

    public boolean doesPlayerVaultExist(UUID playerUUID) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        return playerVaultFile.exists();
    }

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
            if (shopVaultSection != null) {
                for (String key : shopVaultSection.getKeys(false)) {
                    ItemStack item = ItemStack.deserialize(Objects.requireNonNull(shopVaultSection.getConfigurationSection(key)).getValues(false));
                    // Check if the item is similar to the one we are looking for
                    if (item.isSimilar(itemToCheck)) {
                        itemCount += item.getAmount();
                    }
                }
            }
        }
        return itemCount;
    }

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
            for (int i = 0; i < 27; i++) { // This is currently a hardcoded vault size limit
                String slotKey = shopVaultPath + ".slot_" + i;
                if (config.contains(slotKey)) {
                    ItemStack existingItem = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection(slotKey)).getValues(false));
                    if (existingItem.isSimilar(itemToAdd)) {
                        // Increase amount if similar item found
                        int newAmount = existingItem.getAmount() + amount;
                        existingItem.setAmount(newAmount);
                        config.set(slotKey, existingItem.serialize());
                        itemAdded = true;
                        break;
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

    public void removeItemsFromPlayerVault(UUID playerUUID, ItemStack itemToRemove, int amount, String shopName) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return;
        }
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
            boolean itemRemoved = false;
            String shopVaultPath = "vault." + shopName;
            // Iterate through the specific shop's vault
            ConfigurationSection shopVaultSection = config.getConfigurationSection(shopVaultPath);
            if (shopVaultSection != null) {
                for (String key : shopVaultSection.getKeys(false)) {
                    String fullKeyPath = shopVaultPath + "." + key;
                    ItemStack item = ItemStack.deserialize(Objects.requireNonNull(shopVaultSection.getConfigurationSection(key)).getValues(false));
                    if (item.isSimilar(itemToRemove)) {
                        int currentAmount = item.getAmount();
                        if (currentAmount > amount) {
                            item.setAmount(currentAmount - amount);
                            config.set(fullKeyPath, item.serialize());
                            itemRemoved = true;
                            break;
                        } else if (currentAmount == amount) {
                            config.set(fullKeyPath, null);
                            itemRemoved = true;
                            break;
                        }
                    }
                }
            }
            if (itemRemoved) {
                config.save(playerVaultFile);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while removing items from a player's vault: " + shopName, e);
        }
    }

    public boolean canAddItemToPlayerVault(UUID playerUUID, ItemStack itemToAdd, int amount, String shopName) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String shopVaultPath = "vault." + shopName;
        // Check existing slots for a match or an empty slot
        for (int i = 0; i < 27; i++) { // This is currently a hardcoded vault size limit
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

    public File getPlayerVaultFile(UUID playerUUID) {
        if (doesPlayerVaultExist(playerUUID)) {
            return new File(vaultsFolder, playerUUID + ".yml");
        }
        return null;
    }

    public File[] listAllVaults() {
        return vaultsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public void savePlayerVault(Player player, Inventory vaultInventory, String shopName) {
        File playerVaultFile = getPlayerVaultFile(player.getUniqueId());
        if (playerVaultFile == null) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String shopVaultPath = "vault." + shopName;
        for (int i = 0; i < 27; i++) { // This is currently a hardcoded vault size limit
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

    public boolean removePlayerVaultFile(String uuidString) {
        UUID playerUUID = UUID.fromString(uuidString);
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (doesPlayerVaultExist(playerUUID)) {
            return playerVaultFile.delete();
        } else {
            return false;
        }
    }
}