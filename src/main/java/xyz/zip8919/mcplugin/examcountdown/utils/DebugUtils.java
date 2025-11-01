package xyz.zip8919.mcplugin.examcountdown.utils;

import xyz.zip8919.mcplugin.examcountdown.managers.ConfigManager;

public class DebugUtils {
    
    public static void debug(String message) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[ExamCountdown DEBUG] " + message);
        }
    }
    
    public static void debug(String format, Object... args) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[ExamCountdown DEBUG] " + String.format(format, args));
        }
    }
    
    public static void debugSection(String sectionName) {
        if (ConfigManager.getInstance().isDebugMode()) {
            System.out.println("[ExamCountdown DEBUG] =========================================");
            System.out.println("[ExamCountdown DEBUG] " + sectionName);
            System.out.println("[ExamCountdown DEBUG] =========================================");
        }
    }
}