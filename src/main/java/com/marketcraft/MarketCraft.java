package com.marketcraft;

import com.marketcraft.Util.DebugManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MarketCraft extends JavaPlugin {
    private static String pluginVersion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginVersion = this.getDescription().getVersion();
        Objects.requireNonNull(getCommand("marketcraftdebug")).setExecutor(new DebugManager.ToggleDebugCommand());
        Objects.requireNonNull(getCommand("marketcraft")).setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }
}
