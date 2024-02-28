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

public class SignUtils {

    public static boolean isSign(Material material) {
        return material.name().endsWith("_SIGN");
    }

    public static boolean isValidMarketcraftSign(Block block) {
        if (block.getState() instanceof Sign sign) {
            Component firstLine = (sign.getSide(Side.FRONT).line(0));
            String firstLineText = PlainTextComponentSerializer.plainText().serialize(firstLine);
            return "Marketcraft".equals(firstLineText.trim()) && sign.isWaxed();
        }
        return false;
    }
}
