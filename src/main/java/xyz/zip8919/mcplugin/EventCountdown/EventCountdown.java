package xyz.zip8919.mcplugin.EventCountdown;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.zip8919.mcplugin.EventCountdown.commands.EventsCountdownCommand;
import xyz.zip8919.mcplugin.EventCountdown.listeners.PlayerListener;
import xyz.zip8919.mcplugin.EventCountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.EventCountdown.managers.CountdownTask;
import xyz.zip8919.mcplugin.EventCountdown.managers.PlayerDataManager;
import xyz.zip8919.mcplugin.EventCountdown.managers.PluginManager;

public class EventCountdown extends JavaPlugin {
    
    private static EventCountdown instance;
    
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
        getCommand("EventCountdown").setExecutor(new EventsCountdownCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info("EventCountdown has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop countdown task
        CountdownTask.getInstance().stop();
        
        // Save player data
        PlayerDataManager.getInstance().savePlayerData();
        
        getLogger().info("EventCountdown has been disabled!");
    }
    
    public static EventCountdown getInstance() {
        return instance;
    }
}