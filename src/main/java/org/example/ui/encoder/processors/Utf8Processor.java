package org.example.ui.encoder.processors;

import org.example.i18n.I18n;
import org.example.ui.encoder.EncoderProcessor;

import javax.swing.*;
import java.awt.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Utf8Processor implements EncoderProcessor {
    
    private final JPanel optionsPanel;
    private final JComboBox<String> formatCombo;

    public Utf8Processor() {
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.add(new JLabel(I18n.optFormat() + ": "));
        
        formatCombo = new JComboBox<>(new String[]{
            I18n.fmtBackslashX(), // \xXX
            I18n.fmtPercent(),     // %XX
            I18n.fmtByteStream(),  // 41 42
            I18n.fmtSequence()     // 4142
        });
        
        optionsPanel.add(formatCombo);
    }

    @Override
    public String getName() {
        return "UTF-8";
    }

    @Override
    public JPanel getOptionsPanel() {
        return optionsPanel;
    }

    @Override
    public String process(String input, boolean isEncode) {
        if (input == null) return "";
        try {
            if (isEncode) {
                byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
                StringBuilder result = new StringBuilder();
                int index = formatCombo.getSelectedIndex();
                for (byte b : bytes) {
                    String hex = String.format("%02x", b & 0xFF);
                    switch (index) {
                        case 0: result.append("\\x").append(hex); break;
                        case 1: result.append("%").append(hex); break;
                        case 2: result.append(hex).append(" "); break;
                        case 3: result.append(hex); break;
                    }
                }
                return result.toString().trim();
            } else {
                String normalized = input;
                int index = formatCombo.getSelectedIndex();
                if (index == 0) { // \xXX
                    normalized = input.replaceAll("\\\\x", "%25");
                } else if (index == 2) { // 41 42
                    normalized = "%" + input.trim().replaceAll("\\s+", "%25");
                } else if (index == 3) { // 4142
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < input.length(); i += 2) {
                        sb.append("%").append(input.substring(i, Math.min(i + 2, input.length())));
                    }
                    normalized = sb.toString();
                }
                return URLDecoder.decode(normalized, "UTF-8");
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}