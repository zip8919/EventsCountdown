package xyz.zip8919.mcplugin.examcountdown;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.zip8919.mcplugin.examcountdown.commands.ExamCountdownCommand;
import xyz.zip8919.mcplugin.examcountdown.listeners.PlayerListener;
import xyz.zip8919.mcplugin.examcountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.examcountdown.managers.CountdownTask;
import xyz.zip8919.mcplugin.examcountdown.managers.PlayerDataManager;
import xyz.zip8919.mcplugin.examcountdown.managers.PluginManager;

public class ExamCountdown extends JavaPlugin {
    
    private static ExamCountdown instance;
    
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
        getCommand("examcountdown").setExecutor(new ExamCountdownCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info("ExamCountdown has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop countdown task
        CountdownTask.getInstance().stop();
        
        // Save player data
        PlayerDataManager.getInstance().savePlayerData();
        
        getLogger().info("ExamCountdown has been disabled!");
    }
    
    public static ExamCountdown getInstance() {
        return instance;
    }
}