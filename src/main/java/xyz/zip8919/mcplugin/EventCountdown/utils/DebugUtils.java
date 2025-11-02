package xyz.zip8919.mcplugin.EventCountdown.utils;

import xyz.zip8919.mcplugin.EventCountdown.managers.ConfigManager;

public class DebugUtils {
    
    public static void debug(String message) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[EventCountdown DEBUG] " + message);
        }
    }
    
    public static void debug(String format, Object... args) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[EventCountdown DEBUG] " + String.format(format, args));
        }
    }
    
    public static void debugSection(String sectionName) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[EventCountdown DEBUG] =========================================");
            System.out.println("[EventCountdown DEBUG] " + sectionName);
            System.out.println("[EventCountdown DEBUG] =========================================");
        }
    }
}