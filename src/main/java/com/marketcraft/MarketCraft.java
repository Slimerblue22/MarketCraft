/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft;

import com.marketcraft.gui.PlayerOpenShopGUI;
import com.marketcraft.listeners.OpenShopListener;
import com.marketcraft.listeners.ShopSetupListener;
import com.marketcraft.listeners.SignListener;
import com.marketcraft.listeners.VaultInventoryListener;
import com.marketcraft.shops.PlayerShopManager;
import com.marketcraft.signs.SignsManager;
import com.marketcraft.util.DebugManager;
import com.marketcraft.vaults.PlayerVaultManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MarketCraft extends JavaPlugin {
    private static String pluginVersion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        //noinspection deprecation
        pluginVersion = this.getDescription().getVersion();
        PlayerVaultManager playerVaultManager = new PlayerVaultManager(getDataFolder());
        PlayerShopManager playerShopManager = new PlayerShopManager(getDataFolder());
        SignsManager signsManager = new SignsManager(getDataFolder());
        PlayerOpenShopGUI playerOpenShopGUI = new PlayerOpenShopGUI(playerShopManager, playerVaultManager, this);
        getServer().getPluginManager().registerEvents(new VaultInventoryListener(playerVaultManager, playerShopManager, this), this);
        getServer().getPluginManager().registerEvents(new ShopSetupListener(playerShopManager), this);
        getServer().getPluginManager().registerEvents(new OpenShopListener(playerVaultManager, this), this);
        getServer().getPluginManager().registerEvents(new SignListener(signsManager, playerShopManager, playerOpenShopGUI), this);
        Objects.requireNonNull(getCommand("marketcraftdebug")).setExecutor(new DebugManager.ToggleDebugCommand());
        Objects.requireNonNull(getCommand("marketcraft")).setExecutor(new CommandHandler(playerVaultManager, playerShopManager, this, playerOpenShopGUI, signsManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }
}