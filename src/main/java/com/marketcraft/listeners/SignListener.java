/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.listeners;

import com.marketcraft.gui.PlayerOpenShopGUI;
import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.signs.SignsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.marketcraft.util.SignUtils.isSign;

public class SignListener implements Listener {
    private final SignsManager signsManager;
    private final PlayerShopManager playerShopManager;
    private final PlayerOpenShopGUI playerOpenShopGUI;

    public SignListener(SignsManager signsManager, PlayerShopManager playerShopManager, PlayerOpenShopGUI playerOpenShopGUI) {
        this.signsManager = signsManager;
        this.playerShopManager = playerShopManager;
        this.playerOpenShopGUI = playerOpenShopGUI;
    }

    /**
     * Handles the block break event to prevent players from breaking linked signs.
     * <p>
     * If the player attempting to break the sign is the owner of the sign or has the 'marketcraft.admin' permission,
     * they are informed to unlink the sign first. If the player is neither the owner nor an admin, they are informed
     * that they do not have permission to break the sign.
     *
     * @param event The block break event that is triggered when a block is broken by a player.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isSign(block.getType())) {
            Optional<Map<String, String>> existingSignData = signsManager.getSignData(block);
            if (existingSignData.isPresent()) {
                Map<String, String> signData = existingSignData.get();
                UUID ownerUUID = UUID.fromString(signData.get("owner"));
                event.setCancelled(true);
                if (player.getUniqueId().equals(ownerUUID) || player.hasPermission("marketcraft.admin")) {
                    player.sendMessage(Component.text("This is a registered sign. Please unlink the sign before breaking it."));
                } else {
                    player.sendMessage(Component.text("You do not have permission to break this sign."));
                }
            }
        }
    }

    /**
     * Handles player interactions with signs to allow access to the linked shops.
     * <p>
     * This method ignores left-click actions to avoid conflicts with the sign break protection.
     * If a player right-clicks a valid sign, it checks if the corresponding shop exists and, if so,
     * opens the shop GUI for the player.
     *
     * @param event The player interaction event that is triggered when a player interacts with a block.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null && isSign(block.getType())) {
            Optional<Map<String, String>> existingSignData = signsManager.getSignData(block);
            if (existingSignData.isPresent()) {
                // Signs should be waxed and clicking them should do nothing but this exists as a fail-safe
                event.setCancelled(true);
                // Getting some data needed later
                Map<String, String> signData = existingSignData.get();
                UUID shopOwnerUUID = UUID.fromString(signData.get("owner"));
                String shopName = signData.get("shopName");
                // Check if the shop exists first
                if (!playerShopManager.doesPlayerShopExist(shopOwnerUUID, shopName)) {
                    player.sendMessage(Component.text("This shop does not exist"));
                    return;
                }
                // If it exists, open it
                playerOpenShopGUI.openPlayerShopGUI(player, shopOwnerUUID, shopName);
                player.sendMessage(Component.text("Opening shop " + shopName));
            }
        }
    }
}