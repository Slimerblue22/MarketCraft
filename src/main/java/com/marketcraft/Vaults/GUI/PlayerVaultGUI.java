package com.marketcraft.Vaults.GUI;

import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PlayerVaultGUI {

    private final PlayerVaultManager playerVaultManager;
    private static final int VAULT_SIZE = 27; // TODO: Make this configurable later

    public PlayerVaultGUI(PlayerVaultManager playerVaultManager) {
        this.playerVaultManager = playerVaultManager;
    }

    public void openVault(Player player) {
        File playerVaultFile = playerVaultManager.getPlayerVaultFile(player.getUniqueId());
        if (playerVaultFile == null) {
            // The player should never be able to get to this point unless something goes wrong
            player.sendMessage(Component.text("An unexpected error has occurred, please wait a moment then try again.", NamedTextColor.RED));
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        Inventory vaultInventory = Bukkit.createInventory(player, VAULT_SIZE, Component.text("Your Vault"));

        // Load items from YML file
        Set<String> keys = Objects.requireNonNull(config.getConfigurationSection("vault")).getKeys(false);
        for (String key : keys) {
            Map<String, Object> itemData = Objects.requireNonNull(config.getConfigurationSection("vault." + key)).getValues(false);
            ItemStack item = ItemStack.deserialize(itemData);
            int slot = Integer.parseInt(key.replace("slot_", ""));
            vaultInventory.setItem(slot, item);
        }

        player.openInventory(vaultInventory);
    }
}
