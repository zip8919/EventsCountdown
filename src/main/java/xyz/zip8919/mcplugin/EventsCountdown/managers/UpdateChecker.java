package xyz.zip8919.mcplugin.EventsCountdown.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import xyz.zip8919.mcplugin.EventsCountdown.managers.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/zip8919/EventsCountdown/releases/latest";
    private static final String[] MIRROR_URLS = {
            "https://hub.gitmirror.com/",
            "https://ghproxy.com/"
    };
    
    private final JavaPlugin plugin;
    private final boolean checkOnJoin;
    private final boolean isManualCheck;
    
    public UpdateChecker(JavaPlugin plugin, boolean checkOnJoin, boolean isManualCheck) {
        this.plugin = plugin;
        this.checkOnJoin = checkOnJoin;
        this.isManualCheck = isManualCheck;
    }
    
    // 兼容旧构造函数
    public UpdateChecker(JavaPlugin plugin, boolean checkOnJoin) {
        this(plugin, checkOnJoin, false);
    }
    
    /**
     * 检查更新
     */
    public void checkForUpdates() {
        checkForUpdates(null);
    }
    
    /**
     * 检查更新并向指定玩家发送信息
     * @param player 要接收更新信息的玩家，如果为null则使用默认行为
     */
    public void checkForUpdates(Player player) {
        // 添加调试信息
        plugin.getLogger().info("[DEBUG] 开始检查更新 - 手动检查: " + isManualCheck + ", 玩家: " + (player != null ? player.getName() : "null"));
        
        // 手动检查不受配置限制
        if (!isManualCheck) {
            if (!ConfigManager.getInstance().getConfig().getBoolean("update-check.enabled", true)) {
                plugin.getLogger().info("[DEBUG] 自动更新检查被配置禁用，跳过检查");
                return;
            }
        }
        
        // 检查是否为快照版本，如果是则跳过更新检查
        String localVersion = plugin.getDescription().getVersion();
        plugin.getLogger().info("[DEBUG] 本地版本: " + localVersion);
        
        if (localVersion.startsWith("#")) {
            // 快照版本不检查更新
            plugin.getLogger().info("[DEBUG] 检测到快照版本，跳过更新检查");
            if (isManualCheck) {
                if (player != null) {
                    player.sendMessage("§6[EventsCountdown] §e当前为快照版本，跳过更新检查");
                } else {
                    plugin.getLogger().info("当前为快照版本，跳过更新检查");
                }
            }
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            plugin.getLogger().info("[DEBUG] 异步线程开始执行更新检查");
            try {
                plugin.getLogger().info("[DEBUG] 开始获取GitHub最新版本信息");
                JSONObject releaseInfo = getLatestReleaseInfo();
                
                if (releaseInfo == null) {
                    plugin.getLogger().warning("[DEBUG] getLatestReleaseInfo()返回null");
                    // 只有在手动检查或自动检查出错时才记录日志
                    if (isManualCheck) {
                        if (player != null) {
                            // 在主线程中发送消息给玩家
                            Bukkit.getScheduler().runTask(plugin, () -> 
                                player.sendMessage("§6[EventsCountdown] §c检查更新失败，无法获取最新版本信息"));
                        } else {
                            plugin.getLogger().info("检查更新失败，无法获取最新版本信息");
                        }
                    } else {
                        plugin.getLogger().warning("自动检查更新失败，无法获取最新版本信息");
                    }
                    return;
                }
                
                plugin.getLogger().info("[DEBUG] 成功获取GitHub版本信息");
                String remoteVersionRaw = releaseInfo.getString("tag_name");
                String remoteVersion = remoteVersionRaw.startsWith("v") ? 
                        remoteVersionRaw.substring(1) : remoteVersionRaw;
                
                plugin.getLogger().info("[DEBUG] 远程版本: " + remoteVersion + ", 本地版本: " + localVersion);
                
                if (localVersion.equals(remoteVersion)) {
                    plugin.getLogger().info("[DEBUG] 版本相同，已是最新版本");
                    // 只有在手动检查时才提示已是最新版本
                    if (isManualCheck) {
                        if (player != null) {
                            plugin.getLogger().info("[DEBUG] 准备发送已是最新版本消息给玩家");
                            // 在主线程中发送消息给玩家
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                plugin.getLogger().info("[DEBUG] 在主线程中发送已是最新版本消息");
                                player.sendMessage("§6[EventsCountdown] §eEventsCountdown 插件已是最新版本 (§f" + localVersion + "§e)");
                            });
                        } else {
                            plugin.getLogger().info("EventsCountdown 插件已是最新版本 (" + localVersion + ")");
                        }
                    }
                    return;
                }
                
                // 获取下载链接
                String downloadUrl = null;
                String sha256 = null;
                
                if (releaseInfo.has("assets") && releaseInfo.getJSONArray("assets").length() > 0) {
                    JSONObject asset = releaseInfo.getJSONArray("assets").getJSONObject(0);
                    downloadUrl = asset.getString("browser_download_url");
                    
                    if (asset.has("digest")) {
                        sha256 = asset.getString("digest");
                    }
                }
                
                // 构造镜像链接
                String mirrorUrl = null;
                if (downloadUrl != null && MIRROR_URLS.length > 0) {
                    mirrorUrl = MIRROR_URLS[0] + downloadUrl;
                }
                
                plugin.getLogger().info("[DEBUG] 发现新版本，准备发送提醒");
                // 控制台提醒（发现新版本时总是提醒）
                if (player != null) {
                    plugin.getLogger().info("[DEBUG] 准备发送新版本消息给玩家");
                    // 在主线程中发送消息给玩家
                    final String finalRemoteVersion = remoteVersion;
                    final String finalLocalVersion = localVersion;
                    final String finalDownloadUrl = downloadUrl;
                    final String finalMirrorUrl = mirrorUrl;
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getLogger().info("[DEBUG] 在主线程中发送新版本消息");
                        player.sendMessage("§6[EventsCountdown] §e发现新版本: §f" + finalRemoteVersion + " §e(当前版本: §f" + finalLocalVersion + "§e)");
                        player.sendMessage("§6[EventsCountdown] §e原始下载链接: §f" + finalDownloadUrl);
                        if (finalMirrorUrl != null) {
                            player.sendMessage("§6[EventsCountdown] §e镜像下载链接: §f" + finalMirrorUrl);
                        }
                    });
                } else {
                    plugin.getLogger().info("发现新版本: " + remoteVersion + " (当前版本: " + localVersion + ")");
                    plugin.getLogger().info("原始下载链接: " + downloadUrl);
                    if (mirrorUrl != null) {
                        plugin.getLogger().info("镜像下载链接: " + mirrorUrl);
                    }
                    
                    // 向有权限的在线玩家发送提醒
                    if (checkOnJoin) {
                        plugin.getLogger().info("[DEBUG] 准备向在线玩家发送新版本提醒");
                        String finalDownloadUrl = downloadUrl;
                        String finalMirrorUrl = mirrorUrl;
                        Bukkit.getScheduler().runTask(plugin, () -> 
                                notifyPlayers(remoteVersion, localVersion, finalDownloadUrl, finalMirrorUrl));
                    }
                }
                
            } catch (Exception e) {
                plugin.getLogger().warning("[DEBUG] 更新检查过程中发生异常: " + e.getMessage());
                e.printStackTrace();
                // 只有在手动检查或自动检查出错时才记录日志
                if (isManualCheck) {
                    if (player != null) {
                        plugin.getLogger().info("[DEBUG] 准备发送错误消息给玩家");
                        // 在主线程中发送消息给玩家
                        final String errorMessage = e.getMessage();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.getLogger().info("[DEBUG] 在主线程中发送错误消息");
                            player.sendMessage("§6[EventsCountdown] §c检查更新时发生错误: " + errorMessage);
                        });
                    } else {
                        plugin.getLogger().warning("检查更新时发生错误: " + e.getMessage());
                    }
                } else {
                    plugin.getLogger().warning("自动检查更新时发生错误: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * 获取最新版本信息
     */
    private JSONObject getLatestReleaseInfo() throws IOException {
        plugin.getLogger().info("[DEBUG] 开始请求GitHub API: " + GITHUB_API_URL);
        URL url = new URL(GITHUB_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "EventsCountdown-Update-Checker");
        
        int responseCode = connection.getResponseCode();
        plugin.getLogger().info("[DEBUG] GitHub API响应码: " + responseCode);
        
        if (responseCode != 200) {
            plugin.getLogger().warning("[DEBUG] 第一次请求失败，响应码: " + responseCode + ", 开始重试");
            // 重试一次
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "EventsCountdown-Update-Checker");
            responseCode = connection.getResponseCode();
            plugin.getLogger().info("[DEBUG] 重试后响应码: " + responseCode);
            
            if (responseCode != 200) {
                plugin.getLogger().warning("[DEBUG] 重试后仍然失败，抛出异常");
                throw new IOException("HTTP响应码: " + responseCode);
            }
        }
        
        plugin.getLogger().info("[DEBUG] 开始读取响应内容");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        plugin.getLogger().info("[DEBUG] 响应内容长度: " + response.length());
        if (response.length() > 100) {
            plugin.getLogger().info("[DEBUG] 响应内容前100字符: " + response.substring(0, 100));
        }
        
        return new JSONObject(response.toString());
    }
    
    /**
     * 向有权限的玩家发送更新提醒
     */
    private void notifyPlayers(String remoteVersion, String localVersion, String downloadUrl, String mirrorUrl) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("eventscountdown.update")) {
                player.sendMessage("§6[EventsCountdown] §e发现新版本 §f" + remoteVersion + " §e(当前版本: §f" + localVersion + "§e)");
                if (downloadUrl != null) {
                    player.sendMessage("§6[EventsCountdown] §e原始下载链接: §f" + downloadUrl);
                }
                if (mirrorUrl != null) {
                    player.sendMessage("§6[EventsCountdown] §e镜像下载链接: §f" + mirrorUrl);
                }
            }
        }
    }
}