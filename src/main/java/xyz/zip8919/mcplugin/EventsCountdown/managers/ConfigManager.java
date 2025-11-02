package xyz.zip8919.mcplugin.EventsCountdown.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zip8919.mcplugin.EventsCountdown.EventsCountdown;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class ConfigManager {
    private static ConfigManager instance;
    private FileConfiguration config;
    private JavaPlugin plugin;
    
    private ConfigManager() {
        // 延迟初始化plugin变量，避免在EventsCountdown实例完全初始化之前调用getPlugin
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    public void initialize() {
        if (this.plugin == null) {
            this.plugin = EventsCountdown.getInstance();
        }
        saveDefaultConfig();
        reloadConfig();
    }
    
    public void saveDefaultConfig() {
        plugin.saveResource("config.yml", false);
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public LocalDateTime getEventDate() {
        // 检查是否有指定年份的事件日期
        String specificDate = config.getString("event-date");
        if (specificDate != null && !specificDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(specificDate, formatter);
        }
        
        // 使用每年固定的事件日期模式
        String datePattern = config.getString("event-date-pattern", "06-07 09:00:00");
        DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");

        // 解析日期模式（只包含月、日、时、分、秒）
        // 正确解析日期模式：格式为 "MM-dd HH:mm:ss"
        String[] parts = datePattern.split(" ");
        if (parts.length < 2) {
            // 如果格式不正确，使用默认值
            parts = new String[]{"06-07", "09:00:00"};
        }
        
        java.time.MonthDay monthDayPart = java.time.MonthDay.parse(parts[0], 
                DateTimeFormatter.ofPattern("MM-dd"));
        java.time.LocalTime timePart = java.time.LocalTime.parse(parts[1], 
                DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        // 获取当前年份
        int currentYear = LocalDateTime.now().getYear();
        
        // 创建今年的事件日期
        LocalDateTime eventDate = LocalDateTime.of(currentYear, monthDayPart.getMonthValue(), 
                monthDayPart.getDayOfMonth(), timePart.getHour(), timePart.getMinute(), 
                timePart.getSecond());
        
        // 如果今年的事件日期已经过去，则使用明年的日期
        LocalDateTime now = LocalDateTime.now();
        if (eventDate.isBefore(now)) {
            eventDate = eventDate.plusYears(1);
        }
        
        return eventDate;
    }
    
    public String getDisplayFormat() {
        return config.getString("display-format", "&6事件倒计时: &e{days}天 {hours}小时 {minutes}分钟 {seconds}秒\n&m&l------------------------\n&b{litemotto}\n&m&l------------------------");
    }
    
    public boolean isLitemottoEnabled() {
        return config.getBoolean("litemotto-enabled", true);
    }
    
    public String getLitemottoPrompt() {
        return config.getString("litemotto-prompt", "请返回一句鼓励事件参与者的格言，要简洁有力，不要包含任何前后缀、额外的文字或解释。");
    }
    
    public boolean usePlayerToggle() {
        return config.getBoolean("use-player-toggle", true);
    }
    
    public boolean getDefaultShow() {
        return config.getBoolean("default-show", true);
    }
    
    public int getUpdateInterval() {
        return config.getInt("update-interval", 20);
    }
    
    public boolean isDebugMode() {
        return config.getBoolean("debug", false);
    }
    
    public List<String> getFallbackMottos() {
        return config.getStringList("fallback-mottos");
    }
    
    public String getRandomFallbackMotto() {
        List<String> mottos = getFallbackMottos();
        if (mottos == null || mottos.isEmpty()) {
            return "努力拼搏，事件必胜！"; // 默认格言
        }
        
        Random random = new Random();
        int index = random.nextInt(mottos.size());
        return mottos.get(index);
    }
    
    /**
     * 获取事件年份
     * @return 事件年份
     */
    public int getEventYear() {
        LocalDateTime eventDate = getEventDate();
        return eventDate.getYear();
    }
}