package com.marketcraft.Commands;

import com.marketcraft.MarketCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

public class VersionCommand {

    public boolean handleVersionCommand(CommandSender sender) {
        String version = MarketCraft.getPluginVersion();

        // Create a component for the version
        Component versionMessage = Component.text("MarketCraft version: " + version, NamedTextColor.GOLD)
                .append(Component.newline());

        // Add author
        versionMessage = versionMessage.append(Component.text("Author: Slimerblue22", NamedTextColor.GOLD))
                .append(Component.newline());

        // Add a clickable link for GitHub Repo
        Component gitHubLink = Component.text("GitHub Repo", NamedTextColor.GOLD, TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://github.com/Slimerblue22/MarketCraft"))
                .hoverEvent(Component.text("Go to the GitHub repository", NamedTextColor.GRAY));

        versionMessage = versionMessage.append(Component.text("Visit the ", NamedTextColor.GOLD))
                .append(gitHubLink)
                .append(Component.text(" for more info.", NamedTextColor.GOLD));

        sender.sendMessage(versionMessage);
        return true;
    }
}