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

public class RemoveSignCommand {

    private final SignsManager signsManager;

    public RemoveSignCommand(SignsManager signsManager) {
        this.signsManager = signsManager;
    }

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