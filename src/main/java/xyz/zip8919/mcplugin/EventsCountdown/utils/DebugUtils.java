package xyz.zip8919.mcplugin.EventsCountdown.utils;

import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;

public class DebugUtils {
    
    public static void debug(String message) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[EventsCountdown DEBUG] " + message);
        }
    }
    
    public static void debug(String format, Object... args) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[EventsCountdown DEBUG] " + String.format(format, args));
        }
    }
    
    public static void debugSection(String sectionName) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[EventsCountdown DEBUG] =========================================");
            System.out.println("[EventsCountdown DEBUG] " + sectionName);
            System.out.println("[EventsCountdown DEBUG] =========================================");
        }
    }
}