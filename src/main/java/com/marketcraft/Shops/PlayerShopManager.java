package com.marketcraft.Shops;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerShopManager {
    private final File shopsFolder;

    public PlayerShopManager(File pluginFolder) {
        this.shopsFolder = new File(pluginFolder, "Shops");
        if (!shopsFolder.exists() && !shopsFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the Shops directory, the plugin may fail to function correctly!");
        }
    }

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

    public File[] listAllShops() {
        return shopsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

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
}