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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class PlayerVaultGUI {
    private final PlayerVaultManager playerVaultManager;
    private final MarketCraft marketCraft;
    private static final int VAULT_SIZE = 54;
    private static final int[] DIVIDER_LINE_SLOTS = {13, 22, 31, 40, 49};
    private static final int INFO_BOOK_SLOT = 4;

    public PlayerVaultGUI(PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.playerVaultManager = playerVaultManager;
        this.marketCraft = marketCraft;
    }

    public void openVault(Player player, String shopName) {
        UUID playerUUID = player.getUniqueId();
        File playerVaultFile = playerVaultManager.getPlayerVaultFile(playerUUID);
        if (playerVaultFile == null) {
            // The player should never be able to get to this point unless something goes wrong
            player.sendMessage(Component.text("An unexpected error has occurred, please wait a moment then try again."));
            return;
        }
        Inventory vaultInventory = Bukkit.createInventory(player, VAULT_SIZE, Component.text("Your Vault"));
        ItemStack infoBook = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = infoBook.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(marketCraft, "shopName"), PersistentDataType.STRING, shopName);
            // Set the lore text for the book
            List<Component> lore = List.of(
                    Component.text("Currently open shop vault " + shopName + "."),
                    // TODO: Enforce this
                    Component.text("Items you are buying are on the right."),
                    Component.text("Items you are selling are on the left.")
            );
            meta.lore(lore);
        }
        infoBook.setItemMeta(meta);
        // Divider line
        for (int slot : DIVIDER_LINE_SLOTS) {
            vaultInventory.setItem(slot, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        vaultInventory.setItem(INFO_BOOK_SLOT, infoBook);
        // Continue with loading the rest of the vault
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);
        String shopVaultPath = "vault." + shopName;
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
        player.openInventory(vaultInventory);
    }
}