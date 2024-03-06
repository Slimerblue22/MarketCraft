/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

/**
 * Provides utility functions for sign-related operations in the MarketCraft plugin.
 * This class contains methods for determining whether a block is a sign and verifying if a sign meets specific criteria for MarketCraft.
 * <p>
 * Key functionalities include:
 * - Checking if a given Material is a type of sign.
 * - Validating whether a block is a correctly formatted MarketCraft sign, including the text content and the waxed status.
 */
public class SignUtils {

    /**
     * Checks if the given material is a sign.
     *
     * @param material The material to check.
     * @return True if the material is a sign, false otherwise.
     */
    public static boolean isSign(Material material) {
        return material.name().endsWith("_SIGN");
    }

    /**
     * Validates whether a block is a correctly formatted MarketCraft sign.
     * Checks the sign's first line and waxed status to determine its validity.
     *
     * @param block The block to check.
     * @return True if it is a valid MarketCraft sign, false otherwise.
     */
    public static boolean isValidMarketcraftSign(Block block) {
        if (block.getState() instanceof Sign sign) {
            Component firstLine = (sign.getSide(Side.FRONT).line(0));
            String firstLineText = PlainTextComponentSerializer.plainText().serialize(firstLine);
            return "Marketcraft".equals(firstLineText.trim()) && sign.isWaxed();
        }
        return false;
    }
}