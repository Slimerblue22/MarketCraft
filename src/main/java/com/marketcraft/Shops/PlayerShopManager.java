package com.marketcraft.Shops;

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

    // NOTICE: This overwrites anything that previously existed in the file if one already exists
    // If a file does not yet exist, it simply creates one
    public void savePlayerShop(Player player, ItemStack itemToSell, ItemStack itemToCharge) {
        UUID playerUUID = player.getUniqueId();
        File playerShopFile = new File(shopsFolder, playerUUID + ".yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerShopFile);

        // Serialize the ItemStacks (Allows storing on NBT data)
        config.set("shop.itemToSell", itemToSell.serialize());
        config.set("shop.itemToCharge", itemToCharge.serialize());

        // I don't have a backup plan when it fails aside from printing the stack trace, need to figure something out
        try {
            config.save(playerShopFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
