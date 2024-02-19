/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft;

import com.marketcraft.Shops.GUI.OpenShopListener;
import com.marketcraft.Shops.GUI.ShopSetupListener;
import com.marketcraft.Shops.PlayerShopManager;
import com.marketcraft.Util.DebugManager;
import com.marketcraft.Vaults.GUI.VaultInventoryListener;
import com.marketcraft.Vaults.PlayerVaultManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MarketCraft extends JavaPlugin {
    private static String pluginVersion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginVersion = this.getDescription().getVersion();
        PlayerVaultManager playerVaultManager = new PlayerVaultManager(getDataFolder());
        PlayerShopManager playerShopManager = new PlayerShopManager(getDataFolder());
        getServer().getPluginManager().registerEvents(new VaultInventoryListener(playerVaultManager, playerShopManager, this), this);
        getServer().getPluginManager().registerEvents(new ShopSetupListener(playerShopManager), this);
        getServer().getPluginManager().registerEvents(new OpenShopListener(playerVaultManager, this), this);
        Objects.requireNonNull(getCommand("marketcraftdebug")).setExecutor(new DebugManager.ToggleDebugCommand());
        Objects.requireNonNull(getCommand("marketcraft")).setExecutor(new CommandHandler(playerVaultManager, playerShopManager, this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }
}