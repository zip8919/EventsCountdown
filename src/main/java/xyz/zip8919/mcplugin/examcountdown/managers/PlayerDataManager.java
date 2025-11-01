package xyz.zip8919.mcplugin.examcountdown.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.zip8919.mcplugin.examcountdown.ExamCountdown;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private static PlayerDataManager instance;
    private Map<UUID, Boolean> playerShowSettings;
    private File playerDataFile;
    private FileConfiguration playerDataConfig;
    
    private PlayerDataManager() {
        this.playerShowSettings = new HashMap<>();
        this.playerDataFile = new File(ExamCountdown.getInstance().getDataFolder(), "playerdata.yml");
        reloadPlayerData();
    }
    
    public static PlayerDataManager getInstance() {
        if (instance == null) {
            instance = new PlayerDataManager();
        }
        return instance;
    }
    
    public void reloadPlayerData() {
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        
        // 从配置文件加载玩家设置
        playerShowSettings.clear();
        for (String key : playerDataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            boolean show = playerDataConfig.getBoolean(key + ".show", true);
            playerShowSettings.put(uuid, show);
        }
    }
    
    public void savePlayerData() {
        for (Map.Entry<UUID, Boolean> entry : playerShowSettings.entrySet()) {
            playerDataConfig.set(entry.getKey().toString() + ".show", entry.getValue());
        }
        
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean shouldShowCountdown(Player player) {
        UUID uuid = player.getUniqueId();
        // 如果没有设置，则使用默认值
        return playerShowSettings.getOrDefault(uuid, ConfigManager.getInstance().getDefaultShow());
    }
    
    public void setPlayerShowSetting(Player player, boolean show) {
        UUID uuid = player.getUniqueId();
        playerShowSettings.put(uuid, show);
        // 立即保存到文件
        playerDataConfig.set(uuid.toString() + ".show", show);
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}