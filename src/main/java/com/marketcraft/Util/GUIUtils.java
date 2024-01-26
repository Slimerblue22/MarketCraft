package com.marketcraft.Util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
}
