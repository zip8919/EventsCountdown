package xyz.zip8919.mcplugin.EventsCountdown.utils;

import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CountdownUtils {
    
    public static class CountdownData {
        private final long days;
        private final long hours;
        private final long minutes;
        private final long seconds;
        
        public CountdownData(long days, long hours, long minutes, long seconds) {
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }
        
        public long getDays() { return days; }
        public long getHours() { return hours; }
        public long getMinutes() { return minutes; }
        public long getSeconds() { return seconds; }
    }
    
    public static CountdownData calculateCountdown() {
        LocalDateTime examDate = ConfigManager.getInstance().getExamDate();
        return calculateCountdown(examDate);
    }
    
    public static CountdownData calculateCountdown(LocalDateTime examDate) {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(examDate)) {
            return new CountdownData(0, 0, 0, 0);
        }
        
        long totalSeconds = ChronoUnit.SECONDS.between(now, examDate);
        long days = totalSeconds / (24 * 3600);
        totalSeconds %= (24 * 3600);
        long hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        return new CountdownData(days, hours, minutes, seconds);
    }
    
    public static String formatCountdown(CountdownData data) {
        return String.format("%d天 %d小时 %d分钟 %d秒", data.getDays(), data.getHours(), data.getMinutes(), data.getSeconds());
    }
    
    public static String formatWithLeadingZeros(int value, int width) {
        return String.format("%0" + width + "d", value);
    }
}