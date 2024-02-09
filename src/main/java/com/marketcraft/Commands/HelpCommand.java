package com.marketcraft.Commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

/**
 * Handles the 'help' subcommand of the /marketcraft command set.
 * This class provides a help message to users, offering an overview of the MarketCraft plugin,
 * its purpose, and basic instructions for use. The message includes a welcoming greeting and
 * essential information to guide users in their initial interaction with the plugin.
 */
public class HelpCommand {
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
