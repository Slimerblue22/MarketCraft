/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.gui.PlayerOpenShopGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command handler for the 'openshop' subcommand within the MarketCraft plugin.
 */
public class OpenShopCommand {
    private final PlayerOpenShopGUI playerOpenShopGUI;

    public OpenShopCommand(PlayerOpenShopGUI playerOpenShopGUI) {
        this.playerOpenShopGUI = playerOpenShopGUI;
    }

    /**
     * Handles the 'openshop' subcommand of the /marketcraft command set.
     * This method is responsible for opening a specified player's shop interface for the command executor.
     * It leverages the PlayerOpenShopGUI for the graphical representation of the shop. The command is designed
     * for player use and requires the player's name and the shop's name as arguments. It validates the existence
     * of the specified shop and the player's identity before opening the shop GUI. This ensures a seamless and
     * intuitive shopping experience within the game, promoting player interaction and commerce within the
     * server's economy.
     *
     * @param sender The sender of the command, expected to be a player.
     * @param args   The arguments provided with the command, including the target player's name and the shop's name.
     * @return true if the shop GUI is successfully opened for the player, false if there is an error such as the command
     * not being used by a player, incorrect usage, or invalid player or shop name.
     */
    public boolean handleOpenShopCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /marketcraft openshop <playerName> <shopName>"));
            return false;
        }
        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        UUID shopOwnerUUID = offlinePlayer.getUniqueId();
        String shopName = args[2];
        playerOpenShopGUI.openPlayerShopGUI(player, shopOwnerUUID, shopName);
        return true;
    }
}