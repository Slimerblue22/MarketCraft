package com.marketcraft.Commands;

import com.marketcraft.MarketCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;

/**
 * Handles the 'version' subcommand of the /marketcraft command set.
 * This class is responsible for displaying the current version of the MarketCraft plugin,
 * along with additional information such as the author and a link to the GitHub repository.
 */
public class VersionCommand {

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