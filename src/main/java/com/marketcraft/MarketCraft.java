package com.marketcraft;

import com.marketcraft.Util.DebugManager;
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
        Objects.requireNonNull(getCommand("marketcraftdebug")).setExecutor(new DebugManager.ToggleDebugCommand());
        Objects.requireNonNull(getCommand("marketcraft")).setExecutor(new CommandHandler(playerVaultManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }
}
