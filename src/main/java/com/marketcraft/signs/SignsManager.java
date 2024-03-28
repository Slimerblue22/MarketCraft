/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.signs;

import com.marketcraft.MarketCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages the linking of signs to player shops in the MarketCraft plugin.
 * This class provides functionalities to create and remove links between signs and shops,
 * allowing players to interact with shops through physical signs in the game world.
 * <p>
 * It handles the storage and retrieval of sign-related data, ensuring persistence across server sessions,
 * and provides methods for creating, removing, and getting data for sign-shop links.
 */
public class SignsManager {
    private final File signsFolder;

    public SignsManager(File pluginFolder) {
        this.signsFolder = new File(pluginFolder, "Signs");
        if (!signsFolder.exists() && !signsFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the Signs directory, the plugin may fail to function correctly!");
        }
    }

    /**
     * Creates a link between a sign and a player's shop.
     * Stores the sign's location and the associated shop's details in a configuration file.
     *
     * @param player    The player creating the sign link.
     * @param shopName  The name of the shop to link.
     * @param signBlock The block representing the sign.
     */
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

    /**
     * Removes a link between a sign and a shop.
     * Validates the player's ownership before unlinking.
     * Updates the configuration file to reflect the removal of the link.
     *
     * @param signBlock The block representing the sign.
     * @param player    The player attempting to remove the sign link.
     */
    public void removeSignLink(Block signBlock, Player player) {
        UUID playerUUID = player.getUniqueId();
        Optional<Map<String, String>> signDataOptional = getSignData(signBlock);
        if (signDataOptional.isPresent()) {
            Map<String, String> signData = signDataOptional.get();
            UUID ownerUUID = UUID.fromString(signData.get("owner"));
            // Check if the player is the owner
            if (playerUUID.equals(ownerUUID)) {
                Location signLocation = signBlock.getLocation();
                File signsFile = new File(signsFolder, "signs.yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(signsFile);
                String locationKey = getLocationKey(signLocation);
                config.set(locationKey, null); // Remove the sign link
                try {
                    config.save(signsFile);
                    player.sendMessage(Component.text("Sign link successfully removed."));
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "An error occurred while removing sign data: ", e);
                    player.sendMessage(Component.text("An error occurred while removing sign data. Please try again later."));
                }
            } else {
                player.sendMessage(Component.text("You do not have permission to remove this sign link."));
            }
        } else {
            player.sendMessage(Component.text("No sign link found at this location."));
        }
    }

    /**
     * Removes a link between a sign and a shop without validating the player's ownership.
     * This version is intended for administrative use or situations where ownership validation is not required.
     * Updates the configuration file to reflect the removal of the link.
     *
     * @param signBlock The block representing the sign.
     * @param player    The player (or administrator) attempting to remove the sign link.
     */
    public void adminRemoveSignLink(Block signBlock, Player player) {
        Optional<Map<String, String>> signDataOptional = getSignData(signBlock);
        if (signDataOptional.isPresent()) {
            Location signLocation = signBlock.getLocation();
            File signsFile = new File(signsFolder, "signs.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(signsFile);
            String locationKey = getLocationKey(signLocation);
            config.set(locationKey, null); // Remove the sign link
            try {
                config.save(signsFile);
                player.sendMessage(Component.text("Sign link successfully removed."));
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "An error occurred while removing sign data: ", e);
                player.sendMessage(Component.text("An error occurred while removing sign data. Please try again later."));
            }
        } else {
            player.sendMessage(Component.text("No sign link found at this location."));
        }
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    /**
     * Checks whether a player has reached their limit for creating signs.
     * This method counts the number of signs currently owned by the player and compares it
     * against the maximum number of signs a player is allowed, as defined in the plugin's configuration.
     * It returns true if the player has not yet reached their sign limit, allowing them to create more signs.
     *
     * @param player The player whose sign count is being checked.
     * @return true if the player has not reached their sign limit, false otherwise.
     */
    public boolean isAtSignLimit(Player player) {
        UUID playerUUID = player.getUniqueId();
        File signsFile = new File(signsFolder, "signs.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(signsFile);
        int count = 0;
        // Count the number of signs that belong to the player
        for (String key : config.getKeys(false)) {
            if (Objects.equals(config.getString(key + ".owner"), playerUUID.toString())) {
                count++;
            }
        }
        // Get the sign limit from the config
        int signLimit = MarketCraft.getSignLimit();
        return count < signLimit;
    }

    /**
     * Retrieves the data associated with a sign, including owner and linked shop information.
     *
     * @param signBlock The block representing the sign.
     * @return An Optional containing the sign's data if present, otherwise an empty Optional.
     */
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