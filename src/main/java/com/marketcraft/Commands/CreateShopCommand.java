package com.marketcraft.Commands;

import com.marketcraft.Shops.GUI.PlayerShopGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateShopCommand {
    private final PlayerShopGUI playerShopGUI;

    public CreateShopCommand() {
        this.playerShopGUI = new PlayerShopGUI();
    }

    public boolean handleCreateShopCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return false;
        }

        sender.sendMessage(Component.text("Opening shop config.", NamedTextColor.GREEN));
        playerShopGUI.openShopSetupGUI(player);
        return true;
    }
}