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

/**
 * The main class for the MarketCraft plugin.
 * This class serves as the entry point for the plugin, handling initialization and shutdown processes.
 * It sets up necessary managers for vaults, shops, and signs, along with event listeners and command executors.
 * The class also provides a static method to get the current version of the plugin.
 */
public final class MarketCraft extends JavaPlugin {
    private static String pluginVersion;
    private static int shopLimit;
    private static int signLimit;

    /**
     * Initializes the plugin when it is enabled.
     * This method is called when the server loads the plugin.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        shopLimit = getConfig().getInt("shopLimit", 5);
        signLimit = getConfig().getInt("signLimit", 5);
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

    /**
     * Handles cleanup logic when the plugin is disabled.
     * This method is called when the server shuts down or the plugin is disabled manually.
     * Does not currently contain any shutdown logic as none is needed.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }

    public static int getShopLimit() {
        return shopLimit;
    }

    public static int getSignLimit() {
        return signLimit;
    }
}