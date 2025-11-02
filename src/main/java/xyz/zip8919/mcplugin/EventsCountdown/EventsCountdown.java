package xyz.zip8919.mcplugin.EventsCountdown;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.zip8919.mcplugin.EventsCountdown.commands.EventsCountdownCommand;
import xyz.zip8919.mcplugin.EventsCountdown.listeners.PlayerListener;
import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.CountdownTask;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PlayerDataManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PluginManager;

public class EventsCountdown extends JavaPlugin {
    
    private static EventsCountdown instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize config
        ConfigManager.getInstance().initialize();
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        
        // Start countdown task
        CountdownTask.getInstance().start();
        
        // Register commands
        getCommand("EventsCountdown").setExecutor(new EventsCountdownCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info("EventsCountdown has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop countdown task
        CountdownTask.getInstance().stop();
        
        // Save player data
        PlayerDataManager.getInstance().savePlayerData();
        
        getLogger().info("EventsCountdown has been disabled!");
    }
    
    public static EventsCountdown getInstance() {
        return instance;
    }
}