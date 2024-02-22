/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.Listeners;

import com.marketcraft.Shops.GUI.PlayerOpenShopGUI;
import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

/**
 * This class provides preliminary functionalities for a sign-based shop access system.
 * It is currently in a testing phase and subject to potential removal or significant
 * revisions in the future.
 * <p>
 * Note: The functions within this class should be used with caution as it is part of an
 * ongoing development process and may undergo substantial changes.
 */
public class SignListener implements Listener {
    private final PlayerOpenShopGUI playerOpenShopGUI;
    private final PlayerShopManager playerShopManager;

    public SignListener(PlayerOpenShopGUI playerOpenShopGUI, PlayerShopManager playerShopManager) {
        this.playerOpenShopGUI = playerOpenShopGUI;
        this.playerShopManager = playerShopManager;
    }

    /**
     * (IMPORTANT) This has a fatal flaw!
     * If a shop name or player name is too long to fit on a sign, a sign for them can't
     * be created. Currently working on ways to bypass this while still using signs.
     * If the shop name and player name fit correctly, the rest works fine.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Ignore left click actions
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block != null && isSign(block.getType())) {
            Sign sign = (Sign) block.getState();
            // Check if the sign is waxed
            if (sign.isWaxed()) {
                PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
                String line1 = serializer.serialize(sign.line(0)).trim();
                String line2 = serializer.serialize(sign.line(1)).trim();
                String line3 = serializer.serialize(sign.line(2)).trim();
                // Check if the sign follows the shop format
                if (line1.equalsIgnoreCase("[Marketcraft]")
                        && line2.startsWith("[") && line2.endsWith("]")
                        && line3.startsWith("[") && line3.endsWith("]")) {
                    String shopName = line2.substring(1, line2.length() - 1);
                    String ownerName = line3.substring(1, line3.length() - 1);
                    // Need to convert the name into a UUID
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ownerName);
                    UUID shopOwnerUUID = offlinePlayer.getUniqueId();
                    // Check if the shop exists first
                    if (!playerShopManager.doesPlayerShopExist(shopOwnerUUID, shopName)) {
                        player.sendMessage("This shop does not exist");
                        return;
                    }
                    // If it exists, open it
                    playerOpenShopGUI.openPlayerShopGUI(player, shopOwnerUUID, shopName);
                    player.sendMessage("Opening shop");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isSign(block.getType())) {
            Sign sign = (Sign) block.getState();
            if (sign.isWaxed()) {
                PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
                String line1 = serializer.serialize(sign.line(0)).trim();
                String line2 = serializer.serialize(sign.line(1)).trim();
                String line3 = serializer.serialize(sign.line(2)).trim();
                // Check if the sign follows the shop format
                if (line1.equalsIgnoreCase("[Marketcraft]")
                        && line2.startsWith("[") && line2.endsWith("]")
                        && line3.startsWith("[") && line3.endsWith("]")) {
                    String ownerName = line3.substring(1, line3.length() - 1);
                    // Need to convert the name into a UUID
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ownerName);
                    UUID shopOwnerUUID = offlinePlayer.getUniqueId();
                    // Prevent the user from breaking the sign if it isn't their shop
                    if (!player.getUniqueId().equals(shopOwnerUUID)) {
                        player.sendMessage("You are not allowed to break this sign.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * This method might not be required. A bukkit API class may provide this information.
     * However, I am unable to find the location of such a class. This will be used in the meantime.
     */
    private boolean isSign(Material material) {
        return material == Material.OAK_SIGN || material == Material.OAK_WALL_SIGN || material == Material.OAK_HANGING_SIGN
                || material == Material.BIRCH_SIGN || material == Material.BIRCH_WALL_SIGN || material == Material.BIRCH_HANGING_SIGN
                || material == Material.SPRUCE_SIGN || material == Material.SPRUCE_WALL_SIGN || material == Material.SPRUCE_HANGING_SIGN
                || material == Material.JUNGLE_SIGN || material == Material.JUNGLE_WALL_SIGN || material == Material.JUNGLE_HANGING_SIGN
                || material == Material.ACACIA_SIGN || material == Material.ACACIA_WALL_SIGN || material == Material.ACACIA_HANGING_SIGN
                || material == Material.CHERRY_SIGN || material == Material.CHERRY_WALL_SIGN || material == Material.CHERRY_HANGING_SIGN
                || material == Material.BAMBOO_SIGN || material == Material.BAMBOO_WALL_SIGN || material == Material.BAMBOO_HANGING_SIGN
                || material == Material.CRIMSON_SIGN || material == Material.CRIMSON_WALL_SIGN || material == Material.CRIMSON_HANGING_SIGN
                || material == Material.WARPED_SIGN || material == Material.WARPED_WALL_SIGN || material == Material.WARPED_HANGING_SIGN
                || material == Material.MANGROVE_SIGN || material == Material.MANGROVE_WALL_SIGN || material == Material.MANGROVE_HANGING_SIGN
                || material == Material.DARK_OAK_SIGN || material == Material.DARK_OAK_WALL_SIGN || material == Material.DARK_OAK_HANGING_SIGN;
    }
}