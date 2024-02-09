package com.marketcraft.Util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Utility class for Graphical User Interface (GUI) related functionalities in Minecraft.
 */
public class GUIUtils {
    /**
     * Creates an ItemStack with a specified material and display name.
     * This utility method simplifies the process of creating items for use in
     * Minecraft GUIs by setting the display name of the item in a single method call.
     *
     * @param material    The material of the item to be created.
     * @param displayName The display name to be set for the item.
     * @return An ItemStack with the specified material and display name.
     */
    public static ItemStack createNamedItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(displayName));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an ItemStack representing a player's head with a custom display name.
     * The display name is set to "{player name}'s Shop".
     *
     * @param playerUuid The UUID of the player whose head is to be created.
     * @return An ItemStack of the player's head with the custom display name.
     */
    public static ItemStack createPlayerHead(UUID playerUuid) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
        skullMeta.setOwningPlayer(player);
        String displayName = player.getName() + "'s Shop";
        skullMeta.displayName(Component.text(displayName));
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }
}