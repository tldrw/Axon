package org.example;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import org.example.i18n.I18n;
import org.example.ui.AxonTab;

public class AxonExtension implements BurpExtension {
    
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Axon");
        
        // 注册右键菜单
        api.userInterface().registerContextMenuItemsProvider(new AxonContextMenuProvider(api));
        
        // 注册主Tab
        api.userInterface().registerSuiteTab("Axon", new AxonTab());

        // 获取版本号
        String version = getVersion();
        
        // 输出加载成功日志
        String language = I18n.isChinese() ? "Chinese" : "English";
        String banner = "Axon Plugin Loaded Successfully.\n" +
                "\n" +
                "Language: " + language + "\n" +
                "Version: " + version + "\n" +
                "Author: tldrw\n" +
                "GitHub: https://github.com/tldrw/Axon\n" +
                "\n" +
                "Happy Hunting!\n";
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