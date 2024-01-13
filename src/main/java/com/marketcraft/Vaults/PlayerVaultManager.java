package com.marketcraft.Vaults;

import com.marketcraft.Util.DebugManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerVaultManager {

    private final File vaultsFolder;

    public PlayerVaultManager(File pluginFolder) {
        this.vaultsFolder = new File(pluginFolder, "Vaults");
        if (!vaultsFolder.exists()) {
            vaultsFolder.mkdirs();
        }
    }

    public boolean doesPlayerVaultExist(UUID playerUUID) {
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
        return playerVaultFile.exists();
    }

    public void createPlayerVaultFile(Player player) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        if (!doesPlayerVaultExist(playerUUID)) {
            try {
                File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");
                boolean isNewFileCreated = playerVaultFile.createNewFile();

                if (isNewFileCreated) {
                    DebugManager.log(DebugManager.Category.DEBUG, "Creating new vault file for player: " + playerName);
                    YamlConfiguration config = new YamlConfiguration();

                    // Initialize with a basic structure otherwise we get NPE errors when trying to open it
                    config.createSection("vault"); // Create an empty 'vault' section

                    // Save the file with the initial structure
                    config.save(playerVaultFile);
                    DebugManager.log(DebugManager.Category.DEBUG, "Vault file successfully created for player: " + playerName);
                }

            } catch (IOException e) {
                DebugManager.log(DebugManager.Category.DEBUG, "Failed to create vault file for player: " + playerName + " due to IOException.");
                e.printStackTrace();
            }
        } else {
            DebugManager.log(DebugManager.Category.DEBUG, "Player: " + playerName + " already has a vault file.");
        }
    }

    public File getPlayerVaultFile(UUID playerUUID) {
        if (doesPlayerVaultExist(playerUUID)) {
            return new File(vaultsFolder, playerUUID + ".yml");
        }
        return null;
    }

    public File[] listAllVaults() {
        return vaultsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    public void savePlayerVault(Player player, Inventory vaultInventory) {
        String playerName = player.getName();

        File playerVaultFile = getPlayerVaultFile(player.getUniqueId());
        if (playerVaultFile == null) return;

        DebugManager.log(DebugManager.Category.DEBUG, "Saving vault for player: " + playerName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerVaultFile);

        for (int i = 0; i < vaultInventory.getSize(); i++) {
            ItemStack item = vaultInventory.getItem(i);
            if (item != null) {
                config.set("vault.slot_" + i, item.serialize());
            } else {
                config.set("vault.slot_" + i, null);
            }
        }

        // TODO: Need better error handling here, otherwise people may lose items or even duplicate them!
        try {
            config.save(playerVaultFile);
        } catch (IOException e) {
            DebugManager.log(DebugManager.Category.DEBUG, "Failed to save vault for player: " + playerName + " A stacktrace has been printed to console.");
            e.printStackTrace();
        }
    }

    public boolean removePlayerVaultFile(String uuidString) {
        UUID playerUUID = UUID.fromString(uuidString);
        File playerVaultFile = new File(vaultsFolder, playerUUID + ".yml");

        if (doesPlayerVaultExist(playerUUID)) {
            if (playerVaultFile.delete()) {
                DebugManager.log(DebugManager.Category.DEBUG, "Vault file successfully deleted for UUID: " + uuidString);
                return true;
            } else {
                DebugManager.log(DebugManager.Category.DEBUG, "Failed to delete vault file for UUID: " + uuidString);
                return false;
            }
        } else {
            DebugManager.log(DebugManager.Category.DEBUG, "No vault file exists for UUID: " + uuidString + " to delete.");
            return false;
        }
    }
}