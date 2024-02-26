/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.Signs;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SignsManager {
    private final File signsFolder;

    public SignsManager(File pluginFolder) {
        this.signsFolder = new File(pluginFolder, "Signs");
        if (!signsFolder.exists() && !signsFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the Signs directory, the plugin may fail to function correctly!");
        }
    }

    public void createSignLink(Player player, String shopName, Block signBlock) {
        UUID playerUUID = player.getUniqueId();
        Location signLocation = signBlock.getLocation();
        File signsFile = new File(signsFolder, "signs.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(signsFile);
        String locationKey = getLocationKey(signLocation);
        config.set(locationKey + ".owner", playerUUID.toString());
        config.set(locationKey + ".shopName", shopName);
        try {
            config.save(signsFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "An error occurred while saving sign data: ", e);
            player.sendMessage(Component.text("An error occurred while saving your sign data. Please try again later."));
        }
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public Optional<Map<String, String>> getSignData(Block signBlock) {
        Location signLocation = signBlock.getLocation();
        File signsFile = new File(signsFolder, "signs.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(signsFile);
        String locationKey = getLocationKey(signLocation);
        if (config.contains(locationKey)) {
            Map<String, String> signData = new HashMap<>();
            signData.put("owner", config.getString(locationKey + ".owner"));
            signData.put("shopName", config.getString(locationKey + ".shopName"));
            return Optional.of(signData);
        }
        return Optional.empty();
    }
}
