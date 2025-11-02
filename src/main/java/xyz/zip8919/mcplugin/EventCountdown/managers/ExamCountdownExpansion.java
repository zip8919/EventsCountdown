package xyz.zip8919.mcplugin.EventCountdown.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import xyz.zip8919.mcplugin.EventCountdown.EventCountdown;
import xyz.zip8919.mcplugin.EventCountdown.utils.CountdownUtils;

public class ExamCountdownExpansion extends PlaceholderExpansion {
    
    private EventCountdown plugin;
    
    public ExamCountdownExpansion(EventCountdown plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "EventCountdown";
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
                return String.valueOf(config.getExamYear());
            case "exam_date":
                return formatExamDate(config.getExamDate(), "yyyy-MM-dd HH:mm:ss");
            case "exam_date_short":
                return formatExamDate(config.getExamDate(), "yyyy-MM-dd");
            case "exam_date_chinese":
                return formatExamDate(config.getExamDate(), "yyyy年MM月dd日 HH:mm:ss");
            case "exam_date_chinese_short":
                return formatExamDate(config.getExamDate(), "yyyy年MM月dd日");
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
     * 格式化考试日期
     * @param date 考试日期
     * @param pattern 日期格式
     * @return 格式化后的日期字符串
     */
    private String formatExamDate(java.time.LocalDateTime date, String pattern) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
}