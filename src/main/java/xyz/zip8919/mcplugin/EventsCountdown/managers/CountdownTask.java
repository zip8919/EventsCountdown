package xyz.zip8919.mcplugin.EventsCountdown.managers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.zip8919.mcplugin.EventsCountdown.EventsCountdown;

public class CountdownTask implements Runnable {
    private static CountdownTask instance;
    private BukkitTask task;
    private EventsCountdown plugin;
    
    private CountdownTask() {
        this.plugin = EventsCountdown.getInstance();
    }
    
    public static CountdownTask getInstance() {
        if (instance == null) {
            instance = new CountdownTask();
        }
        return instance;
    }
    
    public void start() {
        if (task != null) {
            task.cancel();
        }
        
        int interval = ConfigManager.getInstance().getUpdateInterval();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, interval);
    }
    
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
    
    @Override
    public void run() {
        // 定期更新逻辑（例如更新记分板）
        // 目前我们只是简单地向所有在线玩家发送倒计时消息
        // 在实际应用中，你可能想要使用记分板或其他更高效的方式
        
        // 这里只是一个示例，实际使用中你可能想要更高效的方式
        /*
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ConfigManager.getInstance().usePlayerToggle() && 
                !PlayerDataManager.getInstance().shouldShowCountdown(player)) {
                continue;
            }
            
            // 发送倒计时消息
            // 注意：频繁发送聊天消息可能会干扰玩家，建议使用记分板
            // new PlayerListener().sendCountdownMessage(player);
        }
        */
    }
}