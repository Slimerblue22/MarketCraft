/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.MarketCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;

/**
 * Command handler for the 'version' subcommand within the MarketCraft plugin.
 */
public class VersionCommand {

    /**
     * Handles the 'version' subcommand of the /marketcraft command set.
     * This method is responsible for displaying the current version of the MarketCraft plugin,
     * along with additional information such as the author and a link to the GitHub repository.
     * It provides an interactive text component where users can click to visit the GitHub page directly
     * from the chat interface. The command is designed to inform users about the plugin's version and
     * other relevant details for reference and support purposes.
     *
     * @param sender The sender of the command, can be a player or the console.
     * @return true after successfully displaying the version information.
     */
    public boolean handleVersionCommand(CommandSender sender) {
        String version = MarketCraft.getPluginVersion();
        // Create a component for the version
        Component versionMessage = Component.text("MarketCraft version: " + version)
                .append(Component.newline());
        // Add author
        versionMessage = versionMessage.append(Component.text("Author: Slimerblue22"))
                .append(Component.newline());
        // Add a clickable link for GitHub Repo
        Component gitHubLink = Component.text("GitHub Repo")
                .clickEvent(ClickEvent.openUrl("https://github.com/Slimerblue22/MarketCraft"))
                .hoverEvent(Component.text("Go to the GitHub repository"));
        versionMessage = versionMessage.append(Component.text("Visit the "))
                .append(gitHubLink)
                .append(Component.text(" for more info."));
        sender.sendMessage(versionMessage);
        return true;
    }
}