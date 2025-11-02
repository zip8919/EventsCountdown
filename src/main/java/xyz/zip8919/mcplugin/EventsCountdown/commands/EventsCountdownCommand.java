package xyz.zip8919.mcplugin.EventsCountdown.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.zip8919.mcplugin.EventsCountdown.EventsCountdown;
import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PlayerDataManager;

public class EventsCountdownCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // 检查是否启用玩家切换功能
                if (!ConfigManager.getInstance().usePlayerToggle()) {
                    player.sendMessage("§c此功能已被管理员禁用！");
                    return true;
                }
                // 显示帮助信息
                sendHelpMessage(sender);
            } else {
                // 控制台命令
                sendHelpMessage(sender);
            }
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "on":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c只有玩家可以使用这个命令！");
                    return true;
                }
                
                Player player = (Player) sender;
                // 检查是否启用玩家切换功能
                if (!ConfigManager.getInstance().usePlayerToggle()) {
                    player.sendMessage("§c此功能已被管理员禁用！");
                    return true;
                }
                
                PlayerDataManager.getInstance().setPlayerShowSetting(player, true);
                player.sendMessage("§a已开启高考倒计时显示！");
                return true;
                
            case "off":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c只有玩家可以使用这个命令！");
                    return true;
                }
                
                player = (Player) sender;
                // 检查是否启用玩家切换功能
                if (!ConfigManager.getInstance().usePlayerToggle()) {
                    player.sendMessage("§c此功能已被管理员禁用！");
                    return true;
                }
                
                PlayerDataManager.getInstance().setPlayerShowSetting(player, false);
                player.sendMessage("§a已关闭高考倒计时显示！");
                return true;
                
            case "toggle":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c只有玩家可以使用这个命令！");
                    return true;
                }
                
                player = (Player) sender;
                // 检查是否启用玩家切换功能
                if (!ConfigManager.getInstance().usePlayerToggle()) {
                    player.sendMessage("§c此功能已被管理员禁用！");
                    return true;
                }
                
                boolean currentSetting = PlayerDataManager.getInstance().shouldShowCountdown(player);
                PlayerDataManager.getInstance().setPlayerShowSetting(player, !currentSetting);
                if (currentSetting) {
                    player.sendMessage("§a已关闭高考倒计时显示！");
                } else {
                    player.sendMessage("§a已开启高考倒计时显示！");
                }
                return true;
                
            case "update":
                // 检查权限
                if (!sender.hasPermission("EventsCountdown.admin")) {
                    sender.sendMessage("§c你没有权限使用这个命令！");
                    return true;
                }
                
                // 手动检查更新
                if (sender instanceof Player) {
                    EventsCountdown.getInstance().getUpdateChecker().checkForUpdates((Player) sender);
                } else {
                    EventsCountdown.getInstance().getUpdateChecker().checkForUpdates();
                }
                return true;
                
            case "reload":
                // 检查权限
                if (!sender.hasPermission("EventsCountdown.admin")) {
                    sender.sendMessage("§c你没有权限使用这个命令！");
                    return true;
                }
                
                // 重新加载配置
                ConfigManager.getInstance().reloadConfig();
                sender.sendMessage("§a高考倒计时插件配置已重新加载！");
                return true;
                
            case "help":
            default:
                sendHelpMessage(sender);
                return true;
        }
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6===== §e高考倒计时 §6=====");
        sender.sendMessage("§a/ec on §7- 开启倒计时显示");
        sender.sendMessage("§a/ec off §7- 关闭倒计时显示");
        sender.sendMessage("§a/ec toggle §7- 切换倒计时显示状态");
        sender.sendMessage("§a/ec help §7- 显示此帮助信息");
        
        // 只有拥有管理员权限的用户才能看到reload和update命令
        if (sender.hasPermission("EventsCountdown.admin")) {
            sender.sendMessage("§a/ec reload §7- 重新加载配置文件");
            sender.sendMessage("§a/ec update §7- 手动检查插件更新");
        }
        
        sender.sendMessage("§6========================");
    }
}