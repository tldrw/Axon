package org.example;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Range;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.example.i18n.I18n;
import org.example.utils.DecoderUtils;
import org.example.utils.EncoderUtils;
import org.example.utils.HttpRequestConverter;
import org.example.utils.JsonProcessor;
import org.example.utils.XmlProcessor;
import org.example.config.ConfigManager;
import org.example.model.ToolConfig;
import org.example.utils.TemplateUtils;
import org.example.utils.CommandExecutor;
import org.example.ui.AxonTab;
import org.example.ui.encoder.EncoderPanel;
import org.example.utils.CurlParser;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AxonContextMenuProvider implements ContextMenuItemsProvider {
    
    private final MontoyaApi api;
    
    public AxonContextMenuProvider(MontoyaApi api) {
        this.api = api;
    }
    
    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> menuItems = new ArrayList<>();
        
        // 创建主菜单 Axon
        JMenu axonMenu = new JMenu("Axon");

        // --- 工具联动菜单 ---
        JMenu toolIntegrationMenu = new JMenu(I18n.toolIntegration());
        boolean hasRequest = event.messageEditorRequestResponse().isPresent() || !event.selectedRequestResponses().isEmpty();

        if (hasRequest) {
            ConfigManager.getInstance().loadConfig();
            List<ToolConfig> configs = ConfigManager.getInstance().getToolConfigs();
            if (configs != null && !configs.isEmpty()) {
                boolean hasEnabledTools = false;
                for (ToolConfig config : configs) {
                    if (config.isEnabled()) {
                        hasEnabledTools = true;
                        JMenuItem toolItem = new JMenuItem(config.getName());
                        toolItem.addActionListener(e -> {
                            handleToolAction(config, event);
                        });
                        toolIntegrationMenu.add(toolItem);
                    }
                }
                if (!hasEnabledTools) {
                    toolIntegrationMenu.setEnabled(false);
                }
            } else {
                toolIntegrationMenu.setEnabled(false);
            }
        } else {
            toolIntegrationMenu.setEnabled(false);
        }
        axonMenu.add(toolIntegrationMenu);
        
        // 获取选中的文本，如果没有选中则使用空字符串
        Optional<String> selectedText = getSelectedText(event);
        String text = selectedText.orElse("");
        
        // 即使没有选中文本，也显示菜单（但部分功能会被禁用）
        boolean hasSelection = !text.isEmpty();

        // --- 0. Send to Encoder ---
        JMenuItem sendToEncoderItem = new JMenuItem(I18n.sendToEncoder());
        sendToEncoderItem.addActionListener(e -> {
            EncoderPanel panel = AxonTab.getEncoderPanel();
            if (panel != null) {
                panel.setInputText(text);
            }
        });
        if (!hasSelection) sendToEncoderItem.setEnabled(false);
        axonMenu.add(sendToEncoderItem);
        
        // Only show Encode/Decode/Format/HTTP Modify in Repeater or Intruder
        if (event.isFromTool(ToolType.REPEATER) || event.isFromTool(ToolType.INTRUDER)) {
            axonMenu.addSeparator();
            // --- 1. 编码菜单 ---
            JMenu encodeMenu = new JMenu(I18n.encode());
            encodeMenu.add(createMenuItem(I18n.base64Encode(), text, t -> EncoderUtils.base64Encode(t), event));
            encodeMenu.add(createMenuItem(I18n.hexEncode(), text, t -> EncoderUtils.hexEncode(t), event));
            encodeMenu.add(createMenuItem(I18n.htmlEntityEncode(), text, t -> EncoderUtils.htmlEntityEncode(t), event));
            encodeMenu.add(createMenuItem(I18n.unicodeEncode(), text, t -> EncoderUtils.unicodeEncode(t), event));
            encodeMenu.add(createMenuItem(I18n.unicodeEncodeIgnoreAscii(), text, t -> EncoderUtils.unicodeEncodeIgnoreAscii(t), event));
            encodeMenu.add(createMenuItem(I18n.unicodeEncodeJsonValues(), text, t -> EncoderUtils.unicodeEncodeJsonValues(t), event));
            encodeMenu.add(createMenuItem(I18n.urlEncode(), text, t -> EncoderUtils.urlEncode(t), event));
            encodeMenu.add(createMenuItem(I18n.urlEncodeSpecial(), text, t -> EncoderUtils.urlEncodeSpecial(t), event));
            // UTF-8编码使用特殊的预览对话框，支持hex格式化
            encodeMenu.add(createUtf8MenuItem(I18n.utf8Encode(), text, event));
            // UTF-16LE编码使用特殊的预览对话框，支持hex格式化
            encodeMenu.add(createUtf16MenuItem(I18n.utf16leEncode(), text, event));
            
            // --- 2. 解码菜单 ---
            JMenu decodeMenu = new JMenu(I18n.decode());
            decodeMenu.add(createMenuItem(I18n.base64Decode(), text, t -> DecoderUtils.base64Decode(t), event));
            decodeMenu.add(createMenuItem(I18n.hexDecode(), text, t -> DecoderUtils.hexDecode(t), event));
            decodeMenu.add(createMenuItem(I18n.htmlEntityDecode(), text, t -> DecoderUtils.htmlEntityDecode(t), event));
            decodeMenu.add(createMenuItem(I18n.unicodeDecode(), text, t -> DecoderUtils.unicodeDecode(t), event));
            decodeMenu.add(createMenuItem(I18n.urlDecode(), text, t -> DecoderUtils.urlDecode(t), event));

            // --- 3. 格式化子菜单 ---
            JMenu formatMenu = new JMenu(I18n.format());
            formatMenu.add(createMenuItem(I18n.jsonFormat(), text, t -> JsonProcessor.format(t), event));
            formatMenu.add(createMenuItem(I18n.jsonCompress(), text, t -> JsonProcessor.compress(t), event));
            formatMenu.addSeparator();
            formatMenu.add(createMenuItem(I18n.xmlFormat(), text, t -> XmlProcessor.format(t), event));
            formatMenu.add(createMenuItem(I18n.xmlCompress(), text, t -> XmlProcessor.compress(t), event));
            
            // --- 4. HTTP请求修改菜单（直接修改，不经过预览窗口）---
            JMenu httpMenu = new JMenu(I18n.httpRequestModify());
            httpMenu.add(createHttpRequestMenuItem(I18n.convertToMultipart(), t -> HttpRequestConverter.convertToMultipart(t), event));
            httpMenu.add(createHttpRequestMenuItem(I18n.convertToFormUrlEncoded(), t -> HttpRequestConverter.convertToFormUrlEncoded(t), event));
            httpMenu.add(createHttpRequestMenuItem(I18n.convertToJsonPost(), t -> HttpRequestConverter.convertToJsonPost(t), event));
            httpMenu.add(createHttpRequestMenuItem(I18n.convertToXmlPost(), t -> HttpRequestConverter.convertToXmlPost(t), event));
            
            // 如果没有选中文本，禁用部分菜单项
            if (!hasSelection) {
                encodeMenu.setEnabled(false);
                decodeMenu.setEnabled(false);
                formatMenu.setEnabled(false);
            }

            axonMenu.add(encodeMenu);
            axonMenu.add(decodeMenu);
            axonMenu.add(formatMenu);
            axonMenu.add(httpMenu);
            axonMenu.addSeparator();
        }

        // --- Paste cURL to Repeater ---
        if (event.isFromTool(ToolType.REPEATER) || event.isFromTool(ToolType.INTRUDER)) {
            JMenuItem pasteCurlItem = new JMenuItem(I18n.pasteCurlToRepeater());
            pasteCurlItem.addActionListener(e -> {
                try {
                    String clipboardData = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if (clipboardData != null && !clipboardData.isEmpty()) {
                        try {
                            HttpRequest request = CurlParser.parse(clipboardData);
                            api.repeater().sendToRepeater(request, "cURL Request");
                        } catch (Exception ex) {
                            api.logging().logToError("Parsing error: " + ex.getMessage());
                            JOptionPane.showMessageDialog(null, I18n.errorParseCurl() + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, I18n.errorReadClipboard(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            axonMenu.add(pasteCurlItem);
        }

        menuItems.add(axonMenu);
        return menuItems;
    }
    
    private void handleToolAction(ToolConfig config, ContextMenuEvent event) {
        HttpRequest request = null;
        if (event.messageEditorRequestResponse().isPresent()) {
            request = event.messageEditorRequestResponse().get().requestResponse().request();
        } else if (!event.selectedRequestResponses().isEmpty()) {
            request = event.selectedRequestResponses().get(0).request();
        }

        if (request != null) {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            String finalCommand = TemplateUtils.processCommand(config.getCommand(), request, tempDir);
            
            showToolPreviewDialog(config.getName(), finalCommand, config.getPath());
        }
    }

    private void showToolPreviewDialog(String title, String command, String directory) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((Frame) null, I18n.toolCommandPreview() + ": " + title, true);
            dialog.setLayout(new BorderLayout(10, 10));
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Command Field
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            formPanel.add(new JLabel(I18n.command() + ":"), gbc);
            
            gbc.gridx = 1; gbc.weightx = 1;
            JTextArea commandArea = new JTextArea(command, 3, 40);
            commandArea.setLineWrap(true);
            commandArea.setWrapStyleWord(true);
            JScrollPane commandScroll = new JScrollPane(commandArea);
            formPanel.add(commandScroll, gbc);
            
            // Directory Field
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            formPanel.add(new JLabel(I18n.directory() + ":"), gbc);
            
            gbc.gridx = 1; gbc.weightx = 1;
            JTextField dirField = new JTextField(directory, 40);
            formPanel.add(dirField, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton runButton = new JButton(I18n.run());
            JButton copyButton = new JButton(I18n.copy());
            JButton cancelButton = new JButton(I18n.cancel());
            
            runButton.addActionListener(e -> {
                String cmd = commandArea.getText();
                String dir = dirField.getText();
                try {
                    CommandExecutor.executeCommand(cmd, dir);
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error running command: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            copyButton.addActionListener(e -> {
                String cmd = commandArea.getText();
                java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(cmd);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                JOptionPane.showMessageDialog(dialog, I18n.copySuccess(), I18n.success(), JOptionPane.INFORMATION_MESSAGE);
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(runButton);
            buttonPanel.add(copyButton);
            buttonPanel.add(cancelButton);
            
            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }

    private JMenuItem createMenuItem(String name, String originalText, TextProcessor processor, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            String processedText = processor.process(originalText);
            showPreviewDialog(name, originalText, processedText, event);
        });
        return menuItem;
    }
    
    private JMenuItem createUtf8MenuItem(String name, String originalText, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            String processedText = EncoderUtils.utf8Encode(originalText);
            showUtf8PreviewDialog(name, originalText, processedText, event);
        });
        return menuItem;
    }
    
    private JMenuItem createUtf16MenuItem(String name, String originalText, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            String processedText = EncoderUtils.utf16leEncode(originalText);
            showUtf16PreviewDialog(name, originalText, processedText, event);
        });
        return menuItem;
    }
    
    private JMenuItem createHttpRequestMenuItem(String name, TextProcessor processor, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            replaceHttpRequest(processor, event);
        });
        return menuItem;
    }
    
    private void showPreviewDialog(String title, String beforeText, String afterText, ContextMenuEvent event) {
        SwingUtilities.invokeLater(() -> {
            PreviewDialog dialog = new PreviewDialog(null, title, beforeText, afterText);
            dialog.setVisible(true);
            
            if (dialog.isReplaced()) {
                replaceSelectedText(event, dialog.getResultText());
            }
        });
    }
    
    private void showUtf8PreviewDialog(String title, String beforeText, String afterText, ContextMenuEvent event) {
        SwingUtilities.invokeLater(() -> {
            String[] utf8HexOptions = new String[]{
                I18n.hexFormatSpace(), I18n.hexFormatComma(), I18n.hexFormatSemicolon(),
                I18n.hexFormat0x(), I18n.hexFormat0xComma(), I18n.hexFormatBackslashX(), I18n.hexFormatNone()
            };
            
            java.util.function.Function<Integer, EncoderUtils.HexFormat> utf8IndexMapper = index -> {
                switch (index) {
                    case 0: return EncoderUtils.HexFormat.SPACE;
                    case 1: return EncoderUtils.HexFormat.COMMA;
                    case 2: return EncoderUtils.HexFormat.SEMICOLON;
                    case 3: return EncoderUtils.HexFormat.PREFIX_0X;
                    case 4: return EncoderUtils.HexFormat.PREFIX_0X_COMMA;
                    case 5: return EncoderUtils.HexFormat.PREFIX_BACKSLASH_X;
                    default: return EncoderUtils.HexFormat.NONE;
                }
            };
            
            PreviewDialog dialog = new PreviewDialog(null, title, beforeText, afterText, utf8HexOptions, utf8IndexMapper, 5, (ot, f) -> {
                String hexString = EncoderUtils.hexEncode(beforeText);
                return EncoderUtils.formatHexString(hexString, f);
            });
            dialog.setVisible(true);
            if (dialog.isReplaced()) {
                replaceSelectedText(event, dialog.getResultText());
            }
        });
    }
    
    private void showUtf16PreviewDialog(String title, String beforeText, String afterText, ContextMenuEvent event) {
        SwingUtilities.invokeLater(() -> {
            String[] utf16HexOptions = new String[]{
                I18n.hexFormatFalse(), I18n.hexFormatSpace(), I18n.hexFormatComma(), I18n.hexFormatSemicolon(),
                I18n.hexFormat0x(), I18n.hexFormat0xComma(), I18n.hexFormatBackslashX(), I18n.hexFormatNone()
            };
            
            java.util.function.Function<Integer, EncoderUtils.HexFormat> utf16IndexMapper = index -> {
                switch (index) {
                    case 0: return EncoderUtils.HexFormat.FALSE;
                    case 1: return EncoderUtils.HexFormat.SPACE;
                    case 2: return EncoderUtils.HexFormat.COMMA;
                    case 3: return EncoderUtils.HexFormat.SEMICOLON;
                    case 4: return EncoderUtils.HexFormat.PREFIX_0X;
                    case 5: return EncoderUtils.HexFormat.PREFIX_0X_COMMA;
                    case 6: return EncoderUtils.HexFormat.PREFIX_BACKSLASH_X;
                    default: return EncoderUtils.HexFormat.NONE;
                }
            };
            
            PreviewDialog dialog = new PreviewDialog(null, title, beforeText, afterText, utf16HexOptions, utf16IndexMapper, 0, (ot, f) -> {
                if (f == EncoderUtils.HexFormat.FALSE) return ot;
                String hexString = EncoderUtils.utf16leEncodeToHex(beforeText);
                return EncoderUtils.formatHexString(hexString, f);
            });
            dialog.setVisible(true);
            if (dialog.isReplaced()) {
                replaceSelectedText(event, dialog.getResultText());
            }
        });
    }
    
    private void replaceHttpRequest(TextProcessor processor, ContextMenuEvent event) {
        if (event.messageEditorRequestResponse().isPresent()) {
            MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
            if (editor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                HttpRequest request = editor.requestResponse().request();
                String fullRequest = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
                String processedRequest = processor.process(fullRequest);
                editor.setRequest(HttpRequest.httpRequest(request.httpService(), burp.api.montoya.core.ByteArray.byteArray(processedRequest.getBytes(StandardCharsets.UTF_8))));
            }
        }
    }
    
    private void replaceSelectedText(ContextMenuEvent event, String newText) {
        if (event.messageEditorRequestResponse().isPresent()) {
            MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
            editor.selectionOffsets().ifPresent(range -> {
                if (editor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                    editor.setRequest(replaceTextAtOffset(editor.requestResponse().request(), range, newText));
                } else if (editor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE) {
                    Optional.ofNullable(editor.requestResponse().response()).ifPresent(res -> 
                        editor.setResponse(replaceTextAtOffset(res, range, newText)));
                }
            });
        }
    }
    
    private HttpRequest replaceTextAtOffset(HttpRequest request, Range range, String newText) {
        String originalText = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
        String resultText = originalText.substring(0, range.startIndexInclusive()) + newText + originalText.substring(range.endIndexExclusive());
        return HttpRequest.httpRequest(request.httpService(), burp.api.montoya.core.ByteArray.byteArray(resultText.getBytes(StandardCharsets.UTF_8)));
    }
    
    private HttpResponse replaceTextAtOffset(HttpResponse response, Range range, String newText) {
        String originalText = new String(response.toByteArray().getBytes(), StandardCharsets.UTF_8);
        String resultText = originalText.substring(0, range.startIndexInclusive()) + newText + originalText.substring(range.endIndexExclusive());
        return HttpResponse.httpResponse(burp.api.montoya.core.ByteArray.byteArray(resultText.getBytes(StandardCharsets.UTF_8)));
    }
    
    private Optional<String> getSelectedText(ContextMenuEvent event) {
        if (event.messageEditorRequestResponse().isPresent()) {
            MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
            return editor.selectionOffsets().map(range -> {
                byte[] bytes = (editor.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) ?
                        editor.requestResponse().request().toByteArray().getBytes() :
                        (editor.requestResponse().response() != null ? editor.requestResponse().response().toByteArray().getBytes() : null);
                if (bytes == null) return null;
                String fullText = new String(bytes, StandardCharsets.UTF_8);
                return fullText.substring(range.startIndexInclusive(), range.endIndexExclusive());
            });
        }
        return Optional.empty();
    }
    
    @FunctionalInterface
    interface TextProcessor {
        String process(String input);
    }
}
