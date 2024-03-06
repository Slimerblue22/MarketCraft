/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

/**
 * Command handler for the 'help' subcommand within the MarketCraft plugin.
 * Provides help and guidance to players on plugin usage.
 */
public class HelpCommand {

    /**
     * Handles the 'help' subcommand of the /marketcraft command set.
     * This method provides a help message to users, offering an overview of the MarketCraft plugin,
     * its purpose, and basic instructions for use. The message includes a welcoming greeting and
     * essential information to guide users in their initial interaction with the plugin.
     *
     * @param sender The sender of the command. Can be a player or the console.
     * @return true after displaying the help message.
     */
    public boolean handleHelpCommand(CommandSender sender) {
        Component welcomeMessage = Component.text()
                .append(Component.text("Welcome to MarketCraft!"))
                .append(Component.newline())
                .append(Component.text("This is a Spigot plugin that allows users to make custom shops using an item for item transaction system."))
                .append(Component.newline())
                // TODO Don't forget to add instructions!
                .append(Component.text("To begin [PLACEHOLDER TO FILL LATER WITH INSTRUCTIONS]"))
                .build();
        sender.sendMessage(welcomeMessage);
        return true;
    }
}