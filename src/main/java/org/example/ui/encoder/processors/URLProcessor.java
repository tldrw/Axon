package org.example.ui.encoder.processors;

import org.example.i18n.I18n;
import org.example.ui.encoder.EncoderProcessor;
import org.example.utils.DecoderUtils;
import org.example.utils.EncoderUtils;

import javax.swing.*;
import java.awt.*;

public class URLProcessor implements EncoderProcessor {
    
    private final JPanel optionsPanel;
    private final JCheckBox specialCharsOnlyCheck;

    public URLProcessor() {
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        specialCharsOnlyCheck = new JCheckBox(I18n.optSpecialCharsOnly());
        specialCharsOnlyCheck.setSelected(false);
        optionsPanel.add(specialCharsOnlyCheck);
    }

    @Override
    public String getName() {
        return "URL";
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
                if (specialCharsOnlyCheck.isSelected()) {
                    return EncoderUtils.urlEncodeSpecial(input);
                } else {
                    return EncoderUtils.urlEncode(input);
                }
            } else {
                return DecoderUtils.urlDecode(input);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}