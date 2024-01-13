package com.marketcraft.Commands;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class ListVaultsCommand {

    private final PlayerVaultManager playerVaultManager;
    private static final int ENTRIES_PER_PAGE = 10;

    public ListVaultsCommand(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public boolean handleListVaultsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command.", NamedTextColor.RED));
            return false;
        }

        // Default to page 1 if no page number is provided
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    sender.sendMessage(Component.text("Page number must be a positive integer.", NamedTextColor.RED));
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid page number.", NamedTextColor.RED));
                return false;
            }
        }

        File[] vaultFiles = playerVaultManager.listAllVaults();
        if (vaultFiles == null || vaultFiles.length == 0) {
            sender.sendMessage(Component.text("No vaults found.", NamedTextColor.RED));
            return true;
        }

        int totalEntries = vaultFiles.length;
        int totalPages = (int) Math.ceil((double) totalEntries / ENTRIES_PER_PAGE);

        if (page > totalPages) {
            sender.sendMessage(Component.text("Page " + page + " does not exist.", NamedTextColor.RED));
            return false;
        }

        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, totalEntries);

        sender.sendMessage(Component.text("Vaults (Page " + page + " of " + totalPages + "):", NamedTextColor.YELLOW));
        for (int i = start; i < end; i++) {
            sender.sendMessage(Component.text(vaultFiles[i].getName(), NamedTextColor.GREEN));
        }

        return true;
    }
}
