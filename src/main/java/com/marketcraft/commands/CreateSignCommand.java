/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.signs.SignsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.marketcraft.util.SignUtils.isSign;
import static com.marketcraft.util.SignUtils.isValidMarketcraftSign;

/**
 * Command handler for the 'createsign' subcommand within the MarketCraft plugin.
 * Utilizes PlayerShopManager and SignsManager for sign creation and management.
 */
public class CreateSignCommand {
    private final PlayerShopManager playerShopManager;
    private final SignsManager signsManager;

    public CreateSignCommand(PlayerShopManager playerShopManager, SignsManager signsManager) {
        this.playerShopManager = playerShopManager;
        this.signsManager = signsManager;
    }

    /**
     * Handles the 'createsign' subcommand of the /marketcraft command set.
     * This method allows players to link a sign to their existing shop, facilitating shop interactions
     * through the sign. The method first checks if the player has reached their sign limit as defined in
     * the plugin's configuration. It then checks if the player is looking at a sign and validates whether
     * the sign can be linked to a shop (e.g., not already linked, formatted correctly, and waxed). It ensures
     * that the player owns the shop they are attempting to link and provides feedback accordingly. If the
     * player has reached the sign limit, a message is sent to inform them that no more signs can be created.
     *
     * @param sender The sender of the command, should be a player.
     * @param args   The arguments provided with the command, expected to contain the shop name.
     * @return true if the command is successfully handled and the sign is linked to the shop, false otherwise.
     */
    public boolean handleCreateSignCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft createsign <shop name>"));
            return false;
        }
        if (!signsManager.isAtSignLimit(player)) {
            sender.sendMessage(Component.text("You have reached your limit of shop signs."));
            return false;
        }
        Block signBlock = null;
        BlockIterator iterator = new BlockIterator(player, 5);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getType() != Material.AIR) {
                signBlock = block;
                break;
            }
        }
        if (signBlock == null || !isSign(signBlock.getType())) {
            sender.sendMessage(Component.text("You are not looking at a sign."));
            return false;
        }
        Optional<Map<String, String>> existingSignData = signsManager.getSignData(signBlock);
        if (existingSignData.isPresent()) {
            sender.sendMessage(Component.text("This sign is already registered to a shop."));
            return false;
        }
        String shopName = args[1];
        UUID playerUUID = player.getUniqueId();
        if (!playerShopManager.doesPlayerShopExist(playerUUID, shopName)) {
            sender.sendMessage(Component.text("You do not have a shop by this name."));
            return false;
        }
        if (!isValidMarketcraftSign(signBlock)) {
            Component message = Component.text()
                    .append(Component.text("This sign is not formatted correctly. "))
                    .append(Component.text("The first line on the FRONT side should read 'Marketcraft'. "))
                    .append(Component.text("Other lines can include different text or be left blank. "))
                    .append(Component.text("Additionally, the sign must be waxed."))
                    .build();
            sender.sendMessage(message);
            return false;
        }
        sender.sendMessage(Component.text("Sign registered for shop " + shopName + "."));
        signsManager.createSignLink(player, shopName, signBlock);
        return true;
    }
}