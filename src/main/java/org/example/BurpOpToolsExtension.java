package org.example;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import org.example.i18n.I18n;

public class BurpOpToolsExtension implements BurpExtension {
    
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("BurpOpTools");
        
        // 注册右键菜单
        api.userInterface().registerContextMenuItemsProvider(new BurpOpToolsContextMenuProvider(api));
        
        // 获取版本号
        String version = getVersion();
        
        // 输出加载成功日志
        String language = I18n.isChinese() ? "Chinese" : "English";
        String banner = "BurpOpTools Plugin Loaded Successfully.\n" +
                "\n" +
                "Language: " + language + "\n" +
                "Version: " + version + "\n" +
                "Author: TLDRO\n" +
                "GitHub: https://github.com/TLDRO/BurpOpTools\n" +
                "\n" +
                "Good Hunting!\n";
        api.logging().logToOutput(banner);
    }
    
    /**
     * 从 MANIFEST.MF 获取版本号
     */
    private String getVersion() {
        try {
            Package pkg = this.getClass().getPackage();
            String implVersion = pkg.getImplementationVersion();
            if (implVersion != null && !implVersion.isEmpty()) {
                return implVersion;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "unknown";
    }
}
