package com.marketcraft.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean handleHelpCommand(CommandSender sender) {
        Component welcomeMessage = Component.text()
                .append(Component.text("Welcome to ", NamedTextColor.GREEN))
                .append(Component.text("MarketCraft!", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("This is a Spigot plugin that allows users to make custom shops using an item for item transaction system.", NamedTextColor.YELLOW))
                .append(Component.newline())
                // TODO Don't forget to add instructions!
                .append(Component.text("To begin [PLACEHOLDER TO FILL LATER WITH INSTRUCTIONS]", NamedTextColor.YELLOW))
                .build();

        sender.sendMessage(welcomeMessage);
        return true;
    }
}
