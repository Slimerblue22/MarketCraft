package com.marketcraft.Vaults;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

    public void createPlayerVaultFile(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (!doesPlayerVaultExist(playerUUID)) {
            try {
                File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
                boolean isNewFileCreated = playerVaultFile.createNewFile();
                if (isNewFileCreated) {
                    YamlConfiguration config = new YamlConfiguration();
                    // Initialize with a basic structure otherwise we get NPE errors when trying to open it
                    config.createSection("vault"); // Create an empty 'vault' section
                    // Save the file with the initial structure
                    config.save(playerVaultFile);
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "An error has occurred while creating" + player.getName() + "'s vault: ", e);
                player.sendMessage(Component.text("An error occurred creating your vault. Please try again later."));
            }
        }
    }

    public int getItemCountInPlayerVault(UUID playerUUID, ItemStack itemToCheck) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return 0;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        int itemCount = 0;
        for (String key : Objects.requireNonNull(config.getConfigurationSection("vault")).getKeys(false)) {
            ItemStack item = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection("vault." + key)).getValues(false));
            // Check if the item is similar to the one we are looking for
            if (item.isSimilar(itemToCheck)) {
                itemCount += item.getAmount();
            }
        }
        return itemCount;
    }

    public void addItemsToPlayerVault(UUID playerUUID, ItemStack itemToAdd, int amount) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return;
        }
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
            boolean itemAdded = false;
            // Check existing slots for a match or an empty slot
            for (int i = 0; i < 27; i++) { // This is currently a hardcoded vault size limit
                String slotKey = "vault.slot_" + i;
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
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while adding items to a players vault", e);
        }
    }

    public void removeItemsFromPlayerVault(UUID playerUUID, ItemStack itemToRemove, int amount) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return;
        }
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
            boolean itemRemoved = false;
            for (String key : Objects.requireNonNull(config.getConfigurationSection("vault")).getKeys(false)) {
                ItemStack item = ItemStack.deserialize(Objects.requireNonNull(config.getConfigurationSection("vault." + key)).getValues(false));
                if (item.isSimilar(itemToRemove)) {
                    int currentAmount = item.getAmount();
                    if (currentAmount > amount) {
                        item.setAmount(currentAmount - amount);
                        config.set("vault." + key, item.serialize());
                        itemRemoved = true;
                        break;
                    } else if (currentAmount == amount) {
                        config.set("vault." + key, null);
                        itemRemoved = true;
                        break;
                    }
                }
            }
            if (itemRemoved) {
                config.save(playerVaultFile);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while removing items from a players vault", e);
        }
    }

    public boolean canAddItemToPlayerVault(UUID playerUUID, ItemStack itemToAdd, int amount) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        if (!playerVaultFile.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        // Check existing slots for a match or an empty slot
        for (int i = 0; i < 27; i++) { // This is currently a hardcoded vault size limit
            String slotKey = "vault.slot_" + i;
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

    public void savePlayerVault(Player player, Inventory vaultInventory) {
        File playerVaultFile = getPlayerVaultFile(player.getUniqueId());
        if (playerVaultFile == null) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        for (int i = 0; i < vaultInventory.getSize(); i++) {
            ItemStack item = vaultInventory.getItem(i);
            if (item != null) {
                config.set("vault.slot_" + i, item.serialize());
            } else {
                config.set("vault.slot_" + i, null);
            }
        }
        // TODO: Need better error handling here, otherwise people may lose items or even duplicate them!
        try {
            config.save(playerVaultFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error has occurred while saving" + player.getName() + "'s vault", e);
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