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
 * Command handler for the 'removesign' subcommand within the MarketCraft plugin.
 */
public class RemoveSignCommand {
    private final SignsManager signsManager;

    public RemoveSignCommand(SignsManager signsManager) {
        this.signsManager = signsManager;
    }

    /**
     * Handles the 'removesign' subcommand of the /marketcraft command set.
     * This method allows players to unlink a sign from any associated shop. It determines the sign
     * the player is currently looking at and checks if it's a valid sign. If so, the method proceeds
     * to remove any links between the sign and a shop. This action ensures that signs can be repurposed
     * or removed from the game without retaining any previous shop associations.
     *
     * @param sender The sender of the command, expected to be a player.
     * @param args   The arguments provided with the command. No additional arguments are expected for this command.
     * @return true if the sign is successfully unlinked from a shop, false if there is an error such as
     * the command not being used by a player, incorrect usage, or if the player is not looking at a sign.
     */
    public boolean handleRemoveSignCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /marketcraft removesign"));
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
        signsManager.removeSignLink(signBlock, player);
        return true;
    }
}