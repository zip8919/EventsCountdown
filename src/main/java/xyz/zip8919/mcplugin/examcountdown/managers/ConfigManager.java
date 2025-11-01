package xyz.zip8919.mcplugin.examcountdown.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.zip8919.mcplugin.examcountdown.ExamCountdown;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ConfigManager {
    private static ConfigManager instance;
    private FileConfiguration config;
    private JavaPlugin plugin;
    
    private ConfigManager() {
        // 延迟初始化plugin变量，避免在ExamCountdown实例完全初始化之前调用getPlugin
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    public void initialize() {
        if (this.plugin == null) {
            this.plugin = ExamCountdown.getInstance();
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
    
    public LocalDateTime getExamDate() {
        // 检查是否有指定年份的考试日期
        String specificDate = config.getString("exam-date");
        if (specificDate != null && !specificDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(specificDate, formatter);
        }
        
        // 使用每年固定的考试日期模式
        String datePattern = config.getString("exam-date-pattern", "06-07 09:00:00");
        DateTimeFormatter patternFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
        
        // 解析日期模式（只包含月、日、时、分、秒）
        java.time.LocalTime timePart = java.time.LocalTime.parse(datePattern.substring(6), 
                DateTimeFormatter.ofPattern("HH:mm:ss"));
        java.time.MonthDay monthDayPart = java.time.MonthDay.parse(datePattern.substring(0, 5), 
                DateTimeFormatter.ofPattern("MM-dd"));
        
        // 获取当前年份
        int currentYear = LocalDateTime.now().getYear();
        
        // 创建今年的考试日期
        LocalDateTime examDate = LocalDateTime.of(currentYear, monthDayPart.getMonthValue(), 
                monthDayPart.getDayOfMonth(), timePart.getHour(), timePart.getMinute(), 
                timePart.getSecond());
        
        // 如果今年的考试日期已经过去，则使用明年的日期
        LocalDateTime now = LocalDateTime.now();
        if (examDate.isBefore(now)) {
            examDate = examDate.plusYears(1);
        }
        
        return examDate;
    }
    
    public String getDisplayFormat() {
        return config.getString("display-format", "&6高考倒计时: &e{days}天 {hours}小时 {minutes}分钟 {seconds}秒\n&m&l------------------------\n&b{litemotto}\n&m&l------------------------");
    }
    
    public boolean isLitemottoEnabled() {
        return config.getBoolean("litemotto-enabled", true);
    }
    
    public String getLitemottoPrompt() {
        return config.getString("litemotto-prompt", "请返回一句鼓励高考学生的格言，要简洁有力，不要包含任何前后缀、额外的文字或解释。");
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
            return "努力拼搏，高考必胜！"; // 默认格言
        }
        
        Random random = new Random();
        int index = random.nextInt(mottos.size());
        return mottos.get(index);
    }
    
    /**
     * 获取考试年份
     * @return 考试年份
     */
    public int getExamYear() {
        LocalDateTime examDate = getExamDate();
        return examDate.getYear();
    }
}