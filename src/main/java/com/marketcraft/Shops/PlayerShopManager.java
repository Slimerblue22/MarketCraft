package com.marketcraft.Shops;

import com.marketcraft.Util.DebugManager;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerShopManager {
    private final File shopsFolder;

    public PlayerShopManager(File pluginFolder) {
        this.shopsFolder = new File(pluginFolder, "Shops");
        if (!shopsFolder.exists()) {
            shopsFolder.mkdirs();
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
            e.printStackTrace();
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
            itemToSell = ItemStack.deserialize(config.getConfigurationSection(basePath + ".itemToSell").getValues(false));
        }
        if (config.contains(basePath + ".itemToCharge")) {
            itemToCharge = ItemStack.deserialize(config.getConfigurationSection(basePath + ".itemToCharge").getValues(false));
        }

        return new ItemStack[] { itemToSell, itemToCharge };
    }

    public File[] listAllShops() {
        return shopsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public boolean removePlayerShopFile(String uuidString) {
        UUID playerUUID = UUID.fromString(uuidString);
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");

        if (playerShopFile.exists()) {
            if (playerShopFile.delete()) {
                DebugManager.log(DebugManager.Category.DEBUG, "Shop file successfully deleted for UUID: " + uuidString);
                return true;
            } else {
                DebugManager.log(DebugManager.Category.DEBUG, "Failed to delete shop file for UUID: " + uuidString);
                return false;
            }
        } else {
            DebugManager.log(DebugManager.Category.DEBUG, "No shop file exists for UUID: " + uuidString + " to delete.");
            return false;
        }
    }
}
