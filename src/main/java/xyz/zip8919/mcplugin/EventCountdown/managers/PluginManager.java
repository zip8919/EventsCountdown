package xyz.zip8919.mcplugin.EventCountdown.managers;

import org.bukkit.Bukkit;
import xyz.zip8919.mcplugin.EventCountdown.EventCountdown;

public class PluginManager {
    private static PluginManager instance;
    private EventCountdown plugin;
    
    private PluginManager() {
        this.plugin = EventCountdown.getInstance();
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
            new ExamCountdownExpansion(plugin).register();
            plugin.getLogger().info("PlaceholderAPI expansion registered!");
        }
        
        // TODO: Initialize other managers
    }
}