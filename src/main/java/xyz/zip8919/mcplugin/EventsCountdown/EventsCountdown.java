package xyz.zip8919.mcplugin.EventsCountdown;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import xyz.zip8919.mcplugin.EventsCountdown.commands.EventsCountdownCommand;
import xyz.zip8919.mcplugin.EventsCountdown.listeners.PlayerListener;
import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.UpdateChecker;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PlayerDataManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PluginManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.CountdownTask;
import org.bstats.bukkit.Metrics;

public class EventsCountdown extends JavaPlugin {
    
    private static EventsCountdown instance;
    private Metrics metrics;
    private UpdateChecker updateChecker;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化 bStats
        int pluginId = 27823; // bStats 插件 ID
        metrics = new Metrics(this, pluginId);
        
        // 初始化配置
        ConfigManager.getInstance().initialize();
        
        // 初始化管理器
        PluginManager.getInstance().initialize();
        
        // 初始化更新检查器
        updateChecker = new UpdateChecker(this, true, false);
        
        // 启动倒计时任务
        CountdownTask.getInstance().start();
        
        // 注册命令
        getCommand("EventsCountdown").setExecutor(new EventsCountdownCommand());
        
        // 注册监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        // 根据配置决定是否在启动时检查更新
        if (getConfig().getBoolean("update-check.on-startup", true)) {
            updateChecker.checkForUpdates();
        }
        
        getLogger().info("EventsCountdown 插件已启用！");
    }

    @Override
    public void onDisable() {
        // 停止倒计时任务
        CountdownTask.getInstance().stop();
        
        // 保存玩家数据
        PlayerDataManager.getInstance().savePlayerData();
        
        getLogger().info("EventsCountdown 插件已禁用！");
    }
    
    // onCommand方法已移至EventsCountdownCommand.java中统一处理
    
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