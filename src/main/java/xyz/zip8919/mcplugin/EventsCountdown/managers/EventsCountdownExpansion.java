package xyz.zip8919.mcplugin.EventsCountdown.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import xyz.zip8919.mcplugin.EventsCountdown.EventsCountdown;
import xyz.zip8919.mcplugin.EventsCountdown.utils.CountdownUtils;

public class EventsCountdownExpansion extends PlaceholderExpansion {
    
    private EventsCountdown plugin;
    
    public EventsCountdownExpansion(EventsCountdown plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "EventsCountdown";
    }
    
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        
        CountdownUtils.CountdownData data = CountdownUtils.calculateCountdown();
        ConfigManager config = ConfigManager.getInstance();
        LiteMottoIntegration liteMotto = LiteMottoIntegration.getInstance();
        
        switch (identifier.toLowerCase()) {
            case "days":
                return String.valueOf(data.getDays());
            case "hours":
                return String.valueOf(data.getHours());
            case "minutes":
                return String.valueOf(data.getMinutes());
            case "seconds":
                return String.valueOf(data.getSeconds());
            case "year":
                return String.valueOf(config.getEventYear());
            case "event_date":
                return formatEventDate(config.getEventDate(), "yyyy-MM-dd HH:mm:ss");
            case "event_date_short":
                return formatEventDate(config.getEventDate(), "yyyy-MM-dd");
            case "event_date_chinese":
                return formatEventDate(config.getEventDate(), "yyyy年MM月dd日 HH:mm:ss");
            case "event_date_chinese_short":
                return formatEventDate(config.getEventDate(), "yyyy年MM月dd日");
            case "litemotto":
                return liteMotto.getMottoSync();
            case "litemotto_enabled":
                return String.valueOf(liteMotto.isLiteMottoAvailable());
            case "formatted":
                return CountdownUtils.formatCountdown(data);
            default:
                return null;
        }
    }
    
    /**
     * 格式化事件日期
     * @param date 事件日期
     * @param pattern 日期格式
     * @return 格式化后的日期字符串
     */
    private String formatEventDate(java.time.LocalDateTime date, String pattern) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
}