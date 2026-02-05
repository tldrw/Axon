package org.example.ui.encoder.processors;

import org.example.i18n.I18n;
import org.example.ui.encoder.EncoderProcessor;
import org.example.utils.DecoderUtils;

import javax.swing.*;
import java.awt.*;

public class UnicodeProcessor implements EncoderProcessor {
    
    private final JPanel optionsPanel;
    private final JComboBox<String> formatCombo;
    private final JCheckBox ignoreAsciiCheck;

    public UnicodeProcessor() {
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        optionsPanel.add(new JLabel(I18n.optFormat() + ": "));
        formatCombo = new JComboBox<>(new String[]{
            I18n.fmtUnicodeDefault(), // \\uXXXX
            I18n.fmtUnicodeUPlus(),   // U+XXXX
            I18n.fmtUnicodeHtmlDec(), // &#XX;
            I18n.fmtUnicodeHtmlHex(), // &#xXXXX;
            I18n.fmtUnicodeCssHex()   // \XXXX
        });
        optionsPanel.add(formatCombo);
        
        ignoreAsciiCheck = new JCheckBox(I18n.optIgnoreAscii());
        optionsPanel.add(ignoreAsciiCheck);
    }

    @Override
    public String getName() {
        return "Unicode";
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
                return encode(input);
            } else {
                return decode(input);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private String encode(String input) {
        StringBuilder result = new StringBuilder();
        int format = formatCombo.getSelectedIndex();
        boolean ignoreAscii = ignoreAsciiCheck.isSelected();
        
        for (char c : input.toCharArray()) {
            if (ignoreAscii && c <= 127) {
                result.append(c);
                continue;
            }
            
            switch (format) {
                case 0: // \\uXXXX
                    result.append("\\u").append(String.format("%04x", (int) c));
                    break;
                case 1: // U+XXXX
                    result.append("U+").append(String.format("%04X", (int) c));
                    break;
                case 2: // &#XX;
                    result.append("&#").append((int) c).append(";");
                    break;
                case 3: // &#xXXXX;
                    result.append("&#x").append(Integer.toHexString(c)).append(";");
                    break;
                case 4: // \XXXX (CSS)
                    result.append("\\").append(String.format("%04x", (int) c));
                    break;
            }
        }
        return result.toString();
    }
    
    private String decode(String input) {
        int format = formatCombo.getSelectedIndex();
        
        if (format == 2 || format == 3) {
            return DecoderUtils.htmlEntityDecode(input);
        } else if (format == 1) {
            // U+XXXX -> \\uXXXX
            String normalized = input.replaceAll("(?i)U\\+([0-9a-f]{4})", "\\\\u$1");
            return DecoderUtils.unicodeDecode(normalized);
        } else if (format == 4) {
            // \XXXX -> \\uXXXX
            // 使用正则替换时，需要小心处理反斜杠
            String normalized = input.replaceAll("\\\\([0-9a-fA-F]{4})", "\\\\u$1");
            return DecoderUtils.unicodeDecode(normalized);
        } else {
            return DecoderUtils.unicodeDecode(input);
        }
    }
}