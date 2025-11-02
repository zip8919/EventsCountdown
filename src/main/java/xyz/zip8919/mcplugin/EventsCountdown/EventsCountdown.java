package xyz.zip8919.mcplugin.EventsCountdown;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import xyz.zip8919.mcplugin.EventsCountdown.commands.EventsCountdownCommand;
import xyz.zip8919.mcplugin.EventsCountdown.listeners.PlayerListener;
import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.CountdownTask;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PlayerDataManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PluginManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.UpdateChecker;

public class EventsCountdown extends JavaPlugin {
    
    private static EventsCountdown instance;
    private Metrics metrics;
    private UpdateChecker updateChecker;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize bStats metrics with plugin ID 27823
        metrics = new Metrics(this, 27823);
        
        // Initialize config
        ConfigManager.getInstance().initialize();
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        
        // Initialize update checker
        updateChecker = new UpdateChecker(this, true);
        
        // Start countdown task
        CountdownTask.getInstance().start();
        
        // Register commands
        getCommand("EventsCountdown").setExecutor(new EventsCountdownCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        // Check for updates on startup
        updateChecker.checkForUpdates();
        
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
    
    /**
     * 获取更新检测器实例
     * @return 更新检测器实例
     */
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}