package org.example.ui.encoder.processors;

import org.example.i18n.I18n;
import org.example.ui.encoder.EncoderProcessor;
import org.example.utils.DecoderUtils;
import org.example.utils.EncoderUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Processor implements EncoderProcessor {
    
    private final JPanel optionsPanel;
    private final JCheckBox urlSafeCheck;

    public Base64Processor() {
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlSafeCheck = new JCheckBox(I18n.optUrlSafe());
        // Burp's helper utils in EncoderUtils might not support URL Safe via flag directly, 
        // but we can implement simple logic here or extend Utils.
        // For demonstration, I'll implement logic here or assume standard behavior.
        
        optionsPanel.add(urlSafeCheck);
    }

    @Override
    public String getName() {
        return "Base64";
    }

    @Override
    public JPanel getOptionsPanel() {
        return optionsPanel;
    }

    @Override
    public String process(String input, boolean isEncode) {
        if (input == null) return "";
        try {
            boolean urlSafe = urlSafeCheck.isSelected();
            if (isEncode) {
                if (urlSafe) {
                    return Base64.getUrlEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
                } else {
                    return EncoderUtils.base64Encode(input);
                }
            } else {
                if (urlSafe) {
                    return new String(Base64.getUrlDecoder().decode(input), StandardCharsets.UTF_8);
                } else {
                    return DecoderUtils.base64Decode(input);
                }
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
