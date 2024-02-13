package com.marketcraft.Vaults.GUI;

import com.marketcraft.MarketCraft;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class PlayerVaultGUI {
    private final PlayerVaultManager playerVaultManager;
    private final MarketCraft marketCraft;
    private static final int VAULT_SIZE = 27; // TODO: Make this configurable later

    public PlayerVaultGUI(PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.marketCraft = marketCraft;
    }

    public void openVault(Player player, String shopName) {
        File playerVaultFile = playerVaultManager.getPlayerVaultFile(player.getUniqueId());
        if (playerVaultFile == null) {
            // The player should never be able to get to this point unless something goes wrong
            player.sendMessage(Component.text("An unexpected error has occurred, please wait a moment then try again."));
            return;
        }
        // Some stuff to store the shop name info
        // TODO: Replace this with something better later. Maybe a confirm and cancel button like how the shops work?
        ItemStack nameInfoHolder = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) nameInfoHolder.getItemMeta();
        bookMeta.setTitle(shopName + " Vault");
        bookMeta.setAuthor("Server");
        bookMeta.getPersistentDataContainer().set(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING, shopName);
        nameInfoHolder.setItemMeta(bookMeta);
        // Continue with loading the rest of the vault
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        Inventory vaultInventory = Bukkit.createInventory(player, VAULT_SIZE, Component.text("Your Vault"));
        String shopVaultPath = "vault." + shopName;
        // Check if the shopVaultPath exists in the config
        if (config.contains(shopVaultPath)) {
            ConfigurationSection shopVaultSection = config.getConfigurationSection(shopVaultPath);
            if (shopVaultSection != null) {
                Set<String> keys = shopVaultSection.getKeys(false);
                for (String key : keys) {
                    Map<String, Object> itemData = Objects.requireNonNull(shopVaultSection.getConfigurationSection(key)).getValues(false);
                    ItemStack item = ItemStack.deserialize(itemData);
                    int slot = Integer.parseInt(key.replace("slot_", ""));
                    vaultInventory.setItem(slot, item);
                }
            }
        }
        vaultInventory.setItem(26, nameInfoHolder);
        player.openInventory(vaultInventory);
    }
}