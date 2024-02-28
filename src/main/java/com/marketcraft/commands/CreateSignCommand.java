/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.commands;

import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.signs.SignsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.marketcraft.util.SignUtils.isValidMarketcraftSign;
import static com.marketcraft.util.SignUtils.isSign;

public class CreateSignCommand {
    private final PlayerShopManager playerShopManager;
    private final SignsManager signsManager;

    public CreateSignCommand(PlayerShopManager playerShopManager, SignsManager signsManager) {
        this.playerShopManager = playerShopManager;
        this.signsManager = signsManager;
    }

    public boolean handleCreateSignCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return false;
        }
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /marketcraft createsign <shop name>"));
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
        Optional<Map<String, String>> existingSignData = signsManager.getSignData(signBlock);
        if (existingSignData.isPresent()) {
            sender.sendMessage(Component.text("This sign is already registered to a shop."));
            return false;
        }
        String shopName = args[1];
        UUID playerUUID = player.getUniqueId();
        if (!playerShopManager.doesPlayerShopExist(playerUUID, shopName)) {
            sender.sendMessage(Component.text("You do not have a shop by this name."));
            return false;
        }
        if (!isValidMarketcraftSign(signBlock)) {
            Component message = Component.text()
                    .append(Component.text("This sign is not formatted correctly. "))
                    .append(Component.text("The first line on the FRONT side should read 'Marketcraft'. "))
                    .append(Component.text("Other lines can include different text or be left blank. "))
                    .append(Component.text("Additionally, the sign must be waxed."))
                    .build();
            sender.sendMessage(message);
            return false;
        }
        sender.sendMessage(Component.text("Sign registered for shop " + shopName + "."));
        signsManager.createSignLink(player, shopName, signBlock);
        return true;
    }
}
