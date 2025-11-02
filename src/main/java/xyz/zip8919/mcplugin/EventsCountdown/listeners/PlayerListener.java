package xyz.zip8919.mcplugin.EventsCountdown.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;
import xyz.zip8919.mcplugin.EventsCountdown.managers.LiteMottoIntegration;
import xyz.zip8919.mcplugin.EventsCountdown.managers.PlayerDataManager;
import xyz.zip8919.mcplugin.EventsCountdown.utils.CountdownUtils;
import xyz.zip8919.mcplugin.EventsCountdown.utils.DebugUtils;
import xyz.zip8919.mcplugin.EventsCountdown.utils.Utils;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 检查是否启用玩家切换功能
        if (ConfigManager.getInstance().usePlayerToggle()) {
            // 检查玩家设置
            if (!PlayerDataManager.getInstance().shouldShowCountdown(player)) {
                return; // 玩家设置为不显示，直接返回
            }
        }
        
        // 显示倒计时消息
        sendCountdownMessage(player);
    }
    
    @SuppressWarnings("unused")
    private void sendCountdownMessage(Player player) {
        DebugUtils.debugSection("PlayerListener.sendCountdownMessage() 开始执行");
        DebugUtils.debug("- 玩家: %s", player.getName());
        DebugUtils.debug("- 玩家UUID: %s", player.getUniqueId());
        
        ConfigManager config = ConfigManager.getInstance();
        CountdownUtils.CountdownData data = CountdownUtils.calculateCountdown();
        
        String message = config.getDisplayFormat();
        
        // 替换倒计时占位符
        message = message.replace("{days}", String.valueOf(data.getDays()))
                         .replace("{hours}", String.valueOf(data.getHours()))
                         .replace("{minutes}", String.valueOf(data.getMinutes()))
                         .replace("{seconds}", String.valueOf(data.getSeconds()))
                         .replace("{year}", String.valueOf(config.getExamYear()));
        
        // 检查LiteMotto是否启用
        boolean liteMottoEnabled = config.isLitemottoEnabled();
        DebugUtils.debug("LiteMotto启用状态: %s", liteMottoEnabled);
        
        if (liteMottoEnabled) {
            DebugUtils.debug("LiteMotto已启用，开始获取自定义提示词");
            
            // 从配置文件获取提示词
            String customPrompt = config.getLitemottoPrompt();
            DebugUtils.debug("获取自定义提示词: %s", customPrompt);
            
            DebugUtils.debug("检查LiteMotto插件是否可用");
            boolean liteMottoAvailable = LiteMottoIntegration.getInstance().isLiteMottoAvailable();
            DebugUtils.debug("LiteMotto插件可用性: %s", liteMottoAvailable);
            
            if (liteMottoAvailable) {
                DebugUtils.debug("开始异步获取LiteMotto格言");
                
                // 创建final变量供lambda表达式使用
                final String finalMessage = new String(message); // 创建副本以避免并发问题
                
                // 使用异步方法获取格言，传递自定义提示词
                LiteMottoIntegration.getInstance().getMottoAsync(customPrompt, motto -> {
                    DebugUtils.debugSection("收到LiteMotto格言回调");
                    DebugUtils.debug("- 玩家: %s", player.getName());
                    DebugUtils.debug("- 格言内容: %s", motto);
                    DebugUtils.debug("- 格言长度: %d", motto.length());
                    
                    String processedMessage = finalMessage;
                    
                    if (motto != null) {
                        DebugUtils.debug("使用LiteMotto格言替换占位符");
                        processedMessage = processedMessage.replace("{litemotto}", motto);
                    } else {
                        // 当LiteMotto返回null时，从备选格言中随机选择
                        String fallbackMotto = config.getRandomFallbackMotto();
                        DebugUtils.debug("LiteMotto返回null，使用备选格言: %s", fallbackMotto);
                        processedMessage = processedMessage.replace("{litemotto}", fallbackMotto);
                    }
                    
                    DebugUtils.debug("处理后的消息: %s", processedMessage);
                    DebugUtils.debug("消息长度: %d", processedMessage.length());
                    // 解析PlaceholderAPI变量并发送消息
                    processAndSendMessage(player, processedMessage, config);
                    DebugUtils.debugSection("LiteMotto格言回调处理完成");
                });
                
                DebugUtils.debug("已请求LiteMotto格言，等待回调");
            } else {
                DebugUtils.debug("LiteMotto插件不可用，使用备选格言");
                String fallbackMotto = config.getRandomFallbackMotto();
                DebugUtils.debug("- 备选格言: %s", fallbackMotto);
                message = message.replace("{litemotto}", fallbackMotto);
                DebugUtils.debug("使用备选格言: %s", fallbackMotto);
                // 解析PlaceholderAPI变量并发送消息
                processAndSendMessage(player, message, config);
            }
        } else {
            DebugUtils.debug("LiteMotto未启用，使用备选格言");
            String fallbackMotto = config.getRandomFallbackMotto();
            DebugUtils.debug("- 备选格言: %s", fallbackMotto);
            message = message.replace("{litemotto}", fallbackMotto);
            DebugUtils.debug("使用备选格言: %s", fallbackMotto);
            // 解析PlaceholderAPI变量并发送消息
                processAndSendMessage(player, message, config);
        }
        DebugUtils.debugSection("PlayerListener.sendCountdownMessage() 执行完成");
    }
    
    private void processAndSendMessage(Player player, String message, ConfigManager config) {
        DebugUtils.debug("PlayerListener.processAndSendMessage() 开始执行");
        DebugUtils.debug("- 玩家: %s", player.getName());
        DebugUtils.debug("- 原始消息: %s", message);
        DebugUtils.debug("- 原始消息长度: %d", message.length());
        
        // 检查是否启用了PlaceholderAPI
        boolean papiEnabled = player.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        DebugUtils.debug("PlaceholderAPI启用状态: %s", papiEnabled);
        
        if (papiEnabled) {
            DebugUtils.debug("PlaceholderAPI已启用，开始解析占位符");
            String beforePapi = message;
            try {
                Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                message = (String) placeholderAPI.getMethod("setPlaceholders", Player.class, String.class)
                        .invoke(null, player, message);
                DebugUtils.debug("- 解析前消息: %s", beforePapi);
                DebugUtils.debug("- 解析后消息: %s", message);
                DebugUtils.debug("- 解析后消息长度: %d", message.length());
            } catch (Exception e) {
                DebugUtils.debug("PlaceholderAPI解析异常: %s", e.getMessage());
                // PlaceholderAPI不可用，继续使用原始消息
            }
        } else {
            DebugUtils.debug("PlaceholderAPI未启用，跳过占位符解析");
        }
        
        // 解析颜色代码
        DebugUtils.debug("开始解析颜色代码");
        String beforeColor = message;
        message = Utils.colorize(message);
        DebugUtils.debug("- 颜色解析前消息: %s", beforeColor);
        DebugUtils.debug("- 颜色解析后消息: %s", message);
        DebugUtils.debug("- 颜色解析后消息长度: %d", message.length());
        
        // 检查消息是否为空或无效
        if (message == null || message.trim().isEmpty()) {
            DebugUtils.debug("警告: 消息为空或无效，使用默认消息");
            message = "&6高考倒计时信息暂时不可用，请稍后再试。";
            message = Utils.colorize(message);
        }
        
        // 发送消息给玩家
        DebugUtils.debug("准备发送消息给玩家");
        DebugUtils.debug("- 最终消息: %s", message);
        DebugUtils.debug("- 最终消息长度: %d", message.length());
        
        try {
            player.sendMessage(message);
            DebugUtils.debug("消息已成功发送给玩家");
        } catch (Exception e) {
            DebugUtils.debug("发送消息时出现异常: %s", e.getMessage());
            if (config.isDebugMode()) {
                e.printStackTrace();
            }
        }
        
        // 如果启用了调试模式，发送额外的调试信息
        if (config.isDebugMode()) {
            String motto = message.contains("{litemotto}") ? "未替换" : "已替换";
            player.sendMessage(Utils.colorize("&7[DEBUG] 格言状态: " + motto));
            
            if (config.isLitemottoEnabled()) {
                // 不再重复调用API，直接显示格言状态信息
                player.sendMessage(Utils.colorize("&7[DEBUG] LiteMotto已启用，使用AI生成格言"));
            } else {
                player.sendMessage(Utils.colorize("&7[DEBUG] 使用备选格言: " + config.getRandomFallbackMotto()));
            }
        }
    }
}