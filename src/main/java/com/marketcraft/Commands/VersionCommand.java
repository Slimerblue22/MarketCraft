package com.marketcraft.Commands;

import com.marketcraft.MarketCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class VersionCommand {

    public boolean handleVersionCommand(CommandSender sender) {
        String version = MarketCraft.getPluginVersion();
        sender.sendMessage(Component.text("MarketCraft version: " + version, NamedTextColor.GOLD));
        return true;
    }
}
