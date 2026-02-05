package org.example.config;

import org.example.model.ToolConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static final String CONFIG_DIR = ".config" + File.separator + "Axon";
    private static final String CONFIG_FILE = "config.yaml";
    private static final String KEY_TOOLS = "tools";
    
    private static ConfigManager instance;
    private List<ToolConfig> toolConfigs;

    private ConfigManager() {
        this.toolConfigs = new ArrayList<>();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public List<ToolConfig> getToolConfigs() {
        return toolConfigs;
    }

    public void setToolConfigs(List<ToolConfig> toolConfigs) {
        this.toolConfigs = toolConfigs;
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        File configFile = getConfigPath().toFile();
        if (!configFile.exists()) {
            return;
        }

        try (InputStream inputStream = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Object loaded = yaml.load(inputStream);
            
            List<?> toolsList = null;

            if (loaded instanceof Map) {
                // New structure: root is a map
                Map<String, Object> root = (Map<String, Object>) loaded;
                Object toolsObj = root.get(KEY_TOOLS);
                if (toolsObj instanceof List) {
                    toolsList = (List<?>) toolsObj;
                }
            } else if (loaded instanceof List) {
                // Legacy structure: root is the list of tools
                toolsList = (List<?>) loaded;
            }

            if (toolsList != null) {
                List<ToolConfig> newList = new ArrayList<>();
                for (Object item : toolsList) {
                    if (item instanceof ToolConfig) {
                        newList.add((ToolConfig) item);
                    } else if (item instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) item;
                        ToolConfig config = new ToolConfig();
                        config.setEnabled(getBoolean(map, "enabled", true));
                        config.setName(getString(map, "name", ""));
                        config.setCommand(getString(map, "command", ""));
                        config.setPath(getString(map, "path", ""));
                        config.setShowPreview(getBoolean(map, "showPreview", true));
                        newList.add(config);
                    }
                }
                this.toolConfigs = newList;
            }
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    private boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public void saveConfig() {
        try {
            Path configPath = getConfigPath();
            if (!Files.exists(configPath.getParent())) {
                Files.createDirectories(configPath.getParent());
            }

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            options.setIndent(4);
            options.setIndicatorIndent(2);
            
            Yaml yaml = new Yaml(options);
            
            // Prepare root map
            Map<String, Object> root = new LinkedHashMap<>();
            
            // Convert tool objects to maps
            List<Map<String, Object>> toolsData = new ArrayList<>();
            for (ToolConfig config : this.toolConfigs) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("enabled", config.isEnabled());
                map.put("name", config.getName());
                map.put("command", config.getCommand());
                map.put("path", config.getPath());
                map.put("showPreview", config.isShowPreview());
                toolsData.add(map);
            }
            
            root.put(KEY_TOOLS, toolsData);

            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                yaml.dump(root, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getConfigPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, CONFIG_DIR, CONFIG_FILE);
    }
    
    public boolean hasConfig() {
        return getConfigPath().toFile().exists();
    }
}
