package org.example.ui;

import org.example.i18n.I18n;
import org.example.ui.encoder.EncoderPanel;

import javax.swing.*;
import java.awt.*;

public class AxonTab extends JPanel {

    // Static reference for access from ContextMenu
    private static EncoderPanel encoderPanel;

    public AxonTab() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Encoder Tab
        encoderPanel = new EncoderPanel();
        tabbedPane.addTab(I18n.encoderTab(), encoderPanel);
        
        // 2. Tools Integration Tab
        ToolsIntegrationPanel toolsIntegrationPanel = new ToolsIntegrationPanel();
        tabbedPane.addTab(I18n.toolIntegration(), toolsIntegrationPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static EncoderPanel getEncoderPanel() {
        return encoderPanel;
    }
}
