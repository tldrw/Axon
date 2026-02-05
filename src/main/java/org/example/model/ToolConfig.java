package org.example.model;

public class ToolConfig {
    private boolean enabled;
    private String name;
    private String command;
    private String path;
    private boolean showPreview;

    public ToolConfig() {
    }

    public ToolConfig(boolean enabled, String name, String command, String path, boolean showPreview) {
        this.enabled = enabled;
        this.name = name;
        this.command = command;
        this.path = path;
        this.showPreview = showPreview;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }
}
