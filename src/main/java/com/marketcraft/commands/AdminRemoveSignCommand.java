/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.signs.SignsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import static com.marketcraft.util.SignUtils.isSign;

/**
 * Command handler for the 'removesign' subcommand within the MarketCraft plugin for the admin command set.
 */
public class AdminRemoveSignCommand {
    private final SignsManager signsManager;

    public AdminRemoveSignCommand(SignsManager signsManager) {
        this.signsManager = signsManager;
    }

    /**
     * Handles the 'removesign' subcommand of the /marketcraftadmin command set.
     * This method allows administrators to forcibly unlink a sign from any associated shop, without
     * checking the ownership of the sign. It is particularly useful for administrative purposes such as
     * clearing signs that are no longer valid or necessary. The command identifies the sign the
     * administrator is currently looking at and removes its link to any shop.
     * <p>
     * This action is critical for ensuring that administrative tasks can be performed efficiently,
     * such as repurposing or removing signs in the game world, especially in cases where the original
     * owner is not available to perform the unlinking.
     *
     * @param sender The sender of the command, expected to be a player with administrative privileges.
     * @param args   The arguments provided with the command. No additional arguments are expected for this command.
     * @return true if the sign is successfully unlinked from any shop, false if there is an error such as
     * the command not being used by a player, incorrect usage, or if the player is not looking at a sign.
     */
    public boolean handleAdminRemoveSignCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /marketcraftadmin removesign"));
            return false;
        }
        Block signBlock = null;
        BlockIterator iterator = new BlockIterator(player, 5);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getType() != Material.AIR) {
                signBlock = block;
                break;
            }
        }
        if (signBlock == null || !isSign(signBlock.getType())) {
            sender.sendMessage(Component.text("You are not looking at a sign."));
            return false;
        }
        signsManager.adminRemoveSignLink(signBlock, player);
        return true;
    }
}