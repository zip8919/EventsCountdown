package xyz.zip8919.mcplugin.EventsCountdown.managers;

import org.bukkit.Bukkit;
import xyz.zip8919.mcplugin.EventsCountdown.EventsCountdown;

public class PluginManager {
    private static PluginManager instance;
    private EventsCountdown plugin;
    
    private PluginManager() {
        this.plugin = EventsCountdown.getInstance();
    }
    
    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }
    
    public void initialize() {
        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new EventsCountdownExpansion(plugin).register();
            plugin.getLogger().info("PlaceholderAPI expansion registered!");
        }
        
        // TODO: Initialize other managers
    }
}