package xyz.zip8919.mcplugin.EventsCountdown.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xyz.zip8919.mcplugin.EventsCountdown.utils.DebugUtils;

import java.lang.reflect.Method;

public class LiteMottoIntegration {
    private static LiteMottoIntegration instance;
    private Plugin liteMottoPlugin;
    private boolean isEnabled;
    
    private LiteMottoIntegration() {
        checkLiteMottoAvailability();
    }
    
    public static LiteMottoIntegration getInstance() {
        if (instance == null) {
            instance = new LiteMottoIntegration();
        }
        return instance;
    }
    
    private void checkLiteMottoAvailability() {
        liteMottoPlugin = Bukkit.getPluginManager().getPlugin("LiteMotto");
        isEnabled = (liteMottoPlugin != null && liteMottoPlugin.isEnabled());
        
        DebugUtils.debug("LiteMotto插件检测结果:");
        DebugUtils.debug("- 插件实例: %s", liteMottoPlugin);
        DebugUtils.debug("- 插件是否启用: %s", isEnabled);
        if (liteMottoPlugin != null) {
            DebugUtils.debug("- 插件名称: %s", liteMottoPlugin.getName());
            DebugUtils.debug("- 插件版本: %s", liteMottoPlugin.getDescription().getVersion());
            DebugUtils.debug("- 插件状态: %s", (liteMottoPlugin.isEnabled() ? "已启用" : "未启用"));
        }
    }
    
    public boolean isLiteMottoAvailable() {
        DebugUtils.debug("LiteMottoIntegration.isLiteMottoAvailable() 返回: %s", isEnabled);
        return isEnabled;
    }
    
    /**
     * 同步获取格言（用于PAPI变量）
     * @return 格言字符串
     */
    public String getMottoSync() {
        if (!isEnabled) {
            return ConfigManager.getInstance().getRandomFallbackMotto();
        }
        
        try {
            Class<?> apiClass = Class.forName("org.baicaizhale.litemotto.api.LiteMottoAPI");
            Method fetchMottoMethod = apiClass.getMethod("fetchMottoWithPrompt", String.class);
            Object apiInstance = apiClass.newInstance();
            String prompt = ConfigManager.getInstance().getLitemottoPrompt();
            return (String) fetchMottoMethod.invoke(apiInstance, prompt);
        } catch (Exception e) {
            if (ConfigManager.getInstance().isDebugMode()) {
                e.printStackTrace();
            }
            return ConfigManager.getInstance().getRandomFallbackMotto();
        }
    }
    
    public void getMottoAsync(String customPrompt, MottoCallback callback) {
        DebugUtils.debug("LiteMottoIntegration.getMottoAsync() 开始调用");
        DebugUtils.debug("- 自定义提示词: %s", customPrompt);
        DebugUtils.debug("- 回调函数: %s", callback);
        
        if (!isEnabled) {
            DebugUtils.debug("LiteMotto插件不可用，使用备选格言");
            String fallbackMotto = ConfigManager.getInstance().getRandomFallbackMotto();
            DebugUtils.debug("备选格言: %s", fallbackMotto);
            callback.onMottoReceived(fallbackMotto);
            return;
        }
        
        DebugUtils.debug("尝试通过反射调用LiteMottoAPI");
        
        // 创建final变量用于lambda表达式
        final String finalPrompt = customPrompt;
        final MottoCallback finalCallback = callback;
        
        Bukkit.getScheduler().runTaskAsynchronously(liteMottoPlugin, () -> {
            DebugUtils.debug("异步任务开始执行");
            
            try {
                DebugUtils.debug("尝试加载LiteMottoAPI类");
                Class<?> apiClass = Class.forName("org.baicaizhale.litemotto.api.LiteMottoAPI");
                DebugUtils.debug("LiteMottoAPI类加载成功: %s", apiClass);
                
                DebugUtils.debug("尝试获取fetchMottoWithPrompt方法");
                Method fetchMottoMethod = apiClass.getMethod("fetchMottoWithPrompt", String.class);
                DebugUtils.debug("方法获取成功: %s", fetchMottoMethod);
                
                DebugUtils.debug("创建LiteMottoAPI实例");
                Object apiInstance = apiClass.newInstance();
                DebugUtils.debug("API实例创建成功: %s", apiInstance);
                
                DebugUtils.debug("调用fetchMottoWithPrompt方法，提示词: %s", finalPrompt);
                String motto = (String) fetchMottoMethod.invoke(apiInstance, finalPrompt);
                DebugUtils.debug("API调用成功，返回格言: %s", motto);
                
                final String finalMotto = motto;
                
                // 在主线程中执行回调
                Bukkit.getScheduler().runTask(liteMottoPlugin, () -> {
                    DebugUtils.debug("在主线程中执行回调，格言: %s", finalMotto);
                    finalCallback.onMottoReceived(finalMotto);
                });
                
            } catch (Exception e) {
                DebugUtils.debug("API调用失败，错误信息: %s", e.getMessage());
                if (ConfigManager.getInstance().isDebugMode()) {
                    e.printStackTrace();
                }
                
                DebugUtils.debug("使用备选方案: ConfigManager.getRandomFallbackMotto()");
                String fallbackMotto = ConfigManager.getInstance().getRandomFallbackMotto();
                DebugUtils.debug("备选格言: %s", fallbackMotto);
                
                final String finalMotto = fallbackMotto;
                
                // 在主线程中执行回调
                Bukkit.getScheduler().runTask(liteMottoPlugin, () -> {
                    DebugUtils.debug("在主线程中执行备选回调，格言: %s", finalMotto);
                    finalCallback.onMottoReceived(finalMotto);
                });
            }
        });
    }
    
    public interface MottoCallback {
        void onMottoReceived(String motto);
    }
}