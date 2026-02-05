package org.example.ui.encoder.processors;

import org.example.i18n.I18n;
import org.example.ui.encoder.EncoderProcessor;
import org.example.utils.DecoderUtils;

import javax.swing.*;
import java.awt.*;

public class HtmlEntityProcessor implements EncoderProcessor {
    
    private final JPanel optionsPanel;
    private final JComboBox<String> formatCombo;
    private final JComboBox<String> escapeModeCombo;
    private final JCheckBox useAbbreviationCheck;

    public HtmlEntityProcessor() {
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Escape Mode
        optionsPanel.add(new JLabel(I18n.optEscapeMode() + ": "));
        escapeModeCombo = new JComboBox<>(new String[]{
            I18n.optEscapeReserved(), // Default
            I18n.optEscapeAll()
        });
        escapeModeCombo.setToolTipText(I18n.tipEscapeReserved());
        optionsPanel.add(escapeModeCombo);
        
        // Format
        optionsPanel.add(new JLabel(I18n.optFormat() + ": "));
        formatCombo = new JComboBox<>(new String[]{
            I18n.fmtHtmlDecimal(),
            I18n.fmtHtmlHex()
        });
        formatCombo.setSelectedIndex(1); // Set Hex as default
        optionsPanel.add(formatCombo);
        
        // Abbreviation
        useAbbreviationCheck = new JCheckBox(I18n.optUseHtmlAbbr());
        optionsPanel.add(useAbbreviationCheck);
    }

    @Override
    public String getName() {
        return "HTML";
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
                return DecoderUtils.htmlEntityDecode(input);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private String encode(String input) {
        StringBuilder result = new StringBuilder();
        boolean escapeAll = escapeModeCombo.getSelectedIndex() == 1;
        boolean useHex = formatCombo.getSelectedIndex() == 1;
        boolean useAbbr = useAbbreviationCheck.isSelected();
        
        for (char c : input.toCharArray()) {
            boolean isReserved = isReservedChar(c);
            
            // Logic change: If escapeAll is false (Reserved Only), 
            // we ONLY encode reserved chars. We DO NOT encode c > 127 automatically.
            // If user wants to encode Chinese, they should use "Escape All" (or we add a third option "Non-ASCII")
            // But "Reserved Only" implies STRICTLY reserved HTML syntax chars.
            
            boolean shouldEncode;
            if (escapeAll) {
                shouldEncode = true; // Encode everything
            } else {
                shouldEncode = isReserved; // Only encode < > & " '
            }
            
            if (shouldEncode) {
                // Try Abbreviation first if enabled and applicable
                if (useAbbr && isReserved) {
                    String abbr = getAbbreviation(c);
                    if (abbr != null) {
                        result.append(abbr);
                        continue;
                    }
                }
                
                // Use Numeric Format
                if (useHex) {
                    result.append("&#x").append(Integer.toHexString(c)).append(";");
                } else {
                    result.append("&#").append((int) c).append(";");
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private boolean isReservedChar(char c) {
        return c == '<' || c == '>' || c == '&' || c == '"' || c == '\'';
    }
    
    private String getAbbreviation(char c) {
        switch (c) {
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '&': return "&amp;";
            case '"': return "&quot;";
            case '\'': return "&apos;";
            default: return null;
        }
    }
}