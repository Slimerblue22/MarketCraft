/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.gui.PlayerShopSetupGUI;
import com.marketcraft.shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for the 'createshop' subcommand within the MarketCraft plugin.
 * Utilizes PlayerShopSetupGUI for shop setup interactions.
 */
public class CreateShopCommand {
    private final PlayerShopSetupGUI playerShopSetupGUI;
    private final PlayerShopManager playerShopManager;

    public CreateShopCommand(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
        this.playerShopSetupGUI = new PlayerShopSetupGUI();
    }

    /**
     * Handles the 'createshop' subcommand of the /marketcraft command set.
     * This method is responsible for initiating the shop creation process for players.
     * When executed by a player, it first checks whether the player has reached their shop limit as defined in
     * the plugin's configuration. If the player is below the limit, it opens the shop configuration GUI, allowing
     * the player to set up their shop with custom items for sale and items required for purchase. If the player
     * has reached the shop limit, a message is sent informing them that no more shops can be created.
     * The command is designed to be player-specific, ensuring that each player can create
     * and manage their own unique shop within their allowed limits.
     *
     * @param sender The sender of the command, should be a player.
     * @param args   The arguments provided with the command, expected to contain the shop name.
     * @return true if the command is successfully handled, false otherwise.
     */
    public boolean handleCreateShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft createshop <name>"));
            return false;
        }
        if (!playerShopManager.isAtShopLimit(player)) {
            sender.sendMessage(Component.text("You have reached your limit of shops."));
            return false;
        }
        sender.sendMessage(Component.text("Creating shop."));
        playerShopSetupGUI.openShopSetupGUI(player, args[1]);
        return true;
    }
}