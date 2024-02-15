/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.Commands;

import com.marketcraft.Shops.GUI.PlayerShopSetupGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the 'createshop' subcommand of the /marketcraft command set.
 * This class is responsible for initiating the shop creation process for players.
 * When executed by a player, it opens the shop configuration GUI, allowing the player
 * to set up their shop with custom items for sale and items required for purchase.
 * The command is designed to be player-specific, ensuring that each player can create
 * and manage their own unique shop.
 */
public class CreateShopCommand {
    private final PlayerShopSetupGUI playerShopSetupGUI;

    public CreateShopCommand() {
        this.playerShopSetupGUI = new PlayerShopSetupGUI();
    }

    public boolean handleCreateShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft createshop <name>"));
            return false;
        }
        sender.sendMessage(Component.text("Creating shop."));
        playerShopSetupGUI.openShopSetupGUI(player, args[1]);
        return true;
    }
}