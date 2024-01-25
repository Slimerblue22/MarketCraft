package com.marketcraft.Commands;

import com.marketcraft.Shops.PlayerShopManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Handles the 'listshops' subcommand of the /marketcraft command set.
 * This class allows administrators with the appropriate permissions to list all player shops.
 * It supports pagination to display the shops in a manageable format, showing a set number
 * of entries per page. The command ensures that only authorized users can view the list of shops,
 * and it provides functionality to navigate through the shops in a paginated manner.
 */
public class ListShopsCommand {

    private static final int ENTRIES_PER_PAGE = 10;
    private final PlayerShopManager playerShopManager;

    public ListShopsCommand(PlayerShopManager playerShopManager) {
        this.playerShopManager = playerShopManager;
    }

    public boolean handleListShopsCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("marketcraft.admin")) {
            sender.sendMessage(Component.text("You don't have permission to run this command."));
            return false;
        }

        // Default to page 1 if no page number is provided
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    sender.sendMessage(Component.text("Page number must be a positive integer."));
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid page number."));
                return false;
            }
        }

        File[] shopFiles = playerShopManager.listAllShops();
        if (shopFiles == null || shopFiles.length == 0) {
            sender.sendMessage(Component.text("No shops found."));
            return true;
        }

        int totalEntries = shopFiles.length;
        int totalPages = (int) Math.ceil((double) totalEntries / ENTRIES_PER_PAGE);

        if (page > totalPages) {
            sender.sendMessage(Component.text("Page " + page + " does not exist."));
            return false;
        }

        int start = (page - 1) * ENTRIES_PER_PAGE;
        int end = Math.min(start + ENTRIES_PER_PAGE, totalEntries);

        sender.sendMessage(Component.text("Shops (Page " + page + " of " + totalPages + "):"));
        for (int i = start; i < end; i++) {
            sender.sendMessage(Component.text(shopFiles[i].getName()));
        }

        return true;
    }
}
