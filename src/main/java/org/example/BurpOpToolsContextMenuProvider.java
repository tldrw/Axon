package org.example;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Range;
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

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BurpOpToolsContextMenuProvider implements ContextMenuItemsProvider {
    
    private final MontoyaApi api;
    
    public BurpOpToolsContextMenuProvider(MontoyaApi api) {
        this.api = api;
    }
    
    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> menuItems = new ArrayList<>();
        
        // 创建主菜单 BurpOpTools
        JMenu opToolsMenu = new JMenu("BurpOpTools");
        
        // 获取选中的文本，如果没有选中则使用空字符串
        Optional<String> selectedText = getSelectedText(event);
        String text = selectedText.orElse("");
        
        // 即使没有选中文本，也显示菜单（但部分功能会被禁用）
        boolean hasSelection = !text.isEmpty();
        
        // 编码菜单
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
        
        // 解码菜单
        JMenu decodeMenu = new JMenu(I18n.decode());
        decodeMenu.add(createMenuItem(I18n.base64Decode(), text, t -> DecoderUtils.base64Decode(t), event));
        decodeMenu.add(createMenuItem(I18n.hexDecode(), text, t -> DecoderUtils.hexDecode(t), event));
        decodeMenu.add(createMenuItem(I18n.htmlEntityDecode(), text, t -> DecoderUtils.htmlEntityDecode(t), event));
        decodeMenu.add(createMenuItem(I18n.unicodeDecode(), text, t -> DecoderUtils.unicodeDecode(t), event));
        decodeMenu.add(createMenuItem(I18n.urlDecode(), text, t -> DecoderUtils.urlDecode(t), event));

        // 格式化子菜单
        JMenu formatMenu = new JMenu(I18n.format());
        formatMenu.add(createMenuItem(I18n.jsonFormat(), text, t -> JsonProcessor.format(t), event));
        formatMenu.add(createMenuItem(I18n.jsonCompress(), text, t -> JsonProcessor.compress(t), event));
        formatMenu.addSeparator();
        formatMenu.add(createMenuItem(I18n.xmlFormat(), text, t -> XmlProcessor.format(t), event));
        formatMenu.add(createMenuItem(I18n.xmlCompress(), text, t -> XmlProcessor.compress(t), event));
        
        // HTTP请求修改菜单（直接修改，不经过预览窗口）
        JMenu httpMenu = new JMenu(I18n.httpRequestModify());
        httpMenu.add(createHttpRequestMenuItem(I18n.convertToMultipart(), t -> HttpRequestConverter.convertToMultipart(t), event));
        httpMenu.add(createHttpRequestMenuItem(I18n.convertToFormUrlEncoded(), t -> HttpRequestConverter.convertToFormUrlEncoded(t), event));
        httpMenu.add(createHttpRequestMenuItem(I18n.convertToJsonPost(), t -> HttpRequestConverter.convertToJsonPost(t), event));
        httpMenu.add(createHttpRequestMenuItem(I18n.convertToXmlPost(), t -> HttpRequestConverter.convertToXmlPost(t), event));
        
        // 添加子菜单到主菜单
        opToolsMenu.add(encodeMenu);
        opToolsMenu.add(decodeMenu);
        opToolsMenu.add(formatMenu);
        opToolsMenu.add(httpMenu);
        
        // 如果没有选中文本，禁用部分菜单项
        if (!hasSelection) {
            encodeMenu.setEnabled(false);
            decodeMenu.setEnabled(false);
            formatMenu.setEnabled(false);
        }
        
        menuItems.add(opToolsMenu);
        
        return menuItems;
    }
    
    private JMenuItem createMenuItem(String name, String originalText, TextProcessor processor, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            String processedText = processor.process(originalText);
            showPreviewDialog(name, originalText, processedText, event);
        });
        return menuItem;
    }
    
    /**
     * 为UTF-8编码创建特殊的菜单项，支持hex格式化
     */
    private JMenuItem createUtf8MenuItem(String name, String originalText, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            // 默认使用\x前缀格式
            String processedText = EncoderUtils.utf8Encode(originalText);
            showUtf8PreviewDialog(name, originalText, processedText, event);
        });
        return menuItem;
    }
    
    /**
     * 为UTF-16LE编码创建特殊的菜单项，支持hex格式化
     */
    private JMenuItem createUtf16MenuItem(String name, String originalText, ContextMenuEvent event) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            String processedText = EncoderUtils.utf16leEncode(originalText);
            showUtf16PreviewDialog(name, originalText, processedText, event);
        });
        return menuItem;
    }
    
    /**
     * 创建HTTP请求修改菜单项（直接修改，不经过预览窗口）
     */
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
    
    /**
     * 显示UTF-8编码的预览对话框，支持hex格式化
     */
    private void showUtf8PreviewDialog(String title, String beforeText, String afterText, ContextMenuEvent event) {
        SwingUtilities.invokeLater(() -> {
            // UTF-8的hex格式选项（默认\x前缀）
            String[] utf8HexOptions = new String[]{
                I18n.hexFormatSpace(),
                I18n.hexFormatComma(),
                I18n.hexFormatSemicolon(),
                I18n.hexFormat0x(),
                I18n.hexFormat0xComma(),
                I18n.hexFormatBackslashX(),
                I18n.hexFormatNone()
            };
            
            // 索引到格式的映射（UTF-8版本）
            java.util.function.Function<Integer, EncoderUtils.HexFormat> utf8IndexMapper = index -> {
                switch (index) {
                    case 0: return EncoderUtils.HexFormat.SPACE;
                    case 1: return EncoderUtils.HexFormat.COMMA;
                    case 2: return EncoderUtils.HexFormat.SEMICOLON;
                    case 3: return EncoderUtils.HexFormat.PREFIX_0X;
                    case 4: return EncoderUtils.HexFormat.PREFIX_0X_COMMA;
                    case 5: return EncoderUtils.HexFormat.PREFIX_BACKSLASH_X;
                    case 6: return EncoderUtils.HexFormat.NONE;
                    default: return EncoderUtils.HexFormat.PREFIX_BACKSLASH_X;
                }
            };
            
            PreviewDialog dialog = new PreviewDialog(
                null,
                title,
                beforeText,
                afterText,
                utf8HexOptions,
                utf8IndexMapper,
                5, // 默认选中\x（索引5）
                (originalText, format) -> {
                    // hex格式化器
                    String hexString = EncoderUtils.hexEncode(beforeText);
                    return EncoderUtils.formatHexString(hexString, format);
                }
            );
            dialog.setVisible(true);
            
            if (dialog.isReplaced()) {
                replaceSelectedText(event, dialog.getResultText());
            }
        });
    }
    
    /**
     * 显示UTF-16LE编码的预览对话框，支持hex格式化
     */
    private void showUtf16PreviewDialog(String title, String beforeText, String afterText, ContextMenuEvent event) {
        SwingUtilities.invokeLater(() -> {
            // UTF-16LE的hex格式选项（默认：False，不启用hex输出）
            String[] utf16HexOptions = new String[]{
                I18n.hexFormatFalse(),
                I18n.hexFormatSpace(),
                I18n.hexFormatComma(),
                I18n.hexFormatSemicolon(),
                I18n.hexFormat0x(),
                I18n.hexFormat0xComma(),
                I18n.hexFormatBackslashX(),
                I18n.hexFormatNone()
            };
            
            // 索引到格式的映射（UTF-16LE版本）
            java.util.function.Function<Integer, EncoderUtils.HexFormat> utf16IndexMapper = index -> {
                switch (index) {
                    case 0: return EncoderUtils.HexFormat.FALSE;
                    case 1: return EncoderUtils.HexFormat.SPACE;
                    case 2: return EncoderUtils.HexFormat.COMMA;
                    case 3: return EncoderUtils.HexFormat.SEMICOLON;
                    case 4: return EncoderUtils.HexFormat.PREFIX_0X;
                    case 5: return EncoderUtils.HexFormat.PREFIX_0X_COMMA;
                    case 6: return EncoderUtils.HexFormat.PREFIX_BACKSLASH_X;
                    case 7: return EncoderUtils.HexFormat.NONE;
                    default: return EncoderUtils.HexFormat.FALSE;
                }
            };
            
            // 创建带hex格式化功能的预览对话框
            PreviewDialog dialog = new PreviewDialog(
                null, 
                title, 
                beforeText, 
                afterText,
                utf16HexOptions,
                utf16IndexMapper,
                0, // 默认选中False（索引0）
                (originalText, format) -> {
                    // hex格式化器
                    if (format == EncoderUtils.HexFormat.FALSE) {
                        // False：显示原始的UTF-16LE编码后的字符串（非hex）
                        return originalText;
                    } else {
                        // 其他格式：先转hex，再按格式输出
                        String hexString = EncoderUtils.utf16leEncodeToHex(beforeText);
                        return EncoderUtils.formatHexString(hexString, format);
                    }
                }
            );
            dialog.setVisible(true);
            
            if (dialog.isReplaced()) {
                replaceSelectedText(event, dialog.getResultText());
            }
        });
    }
    
    /**
     * 直接替换HTTP请求（不经过预览窗口）
     */
    private void replaceHttpRequest(TextProcessor processor, ContextMenuEvent event) {
        if (event.messageEditorRequestResponse().isPresent()) {
            MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
            MessageEditorHttpRequestResponse.SelectionContext context = editor.selectionContext();
            
            // 只处理请求
            if (context == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                HttpRequest request = editor.requestResponse().request();
                
                // 获取完整的HTTP请求
                String fullRequest = new String(request.toByteArray().getBytes(), StandardCharsets.UTF_8);
                
                // 处理请求
                String processedRequest = processor.process(fullRequest);
                
                // 替换整个请求
                byte[] newBytes = processedRequest.getBytes(StandardCharsets.UTF_8);
                HttpRequest newRequest = HttpRequest.httpRequest(
                    request.httpService(),
                    burp.api.montoya.core.ByteArray.byteArray(newBytes)
                );
                editor.setRequest(newRequest);
            }
        }
    }
    
    private void replaceSelectedText(ContextMenuEvent event, String newText) {
        // 尝试获取消息编辑器
        if (event.messageEditorRequestResponse().isPresent()) {
            MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
            Optional<Range> selection = editor.selectionOffsets();
            
            if (selection.isPresent()) {
                Range range = selection.get();
                MessageEditorHttpRequestResponse.SelectionContext context = editor.selectionContext();
                
                // 根据上下文判断是请求还是响应
                if (context == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                    HttpRequest request = editor.requestResponse().request();
                    HttpRequest newRequest = replaceTextAtOffset(request, range, newText);
                    editor.setRequest(newRequest);
                } else if (context == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE) {
                    HttpResponse response = editor.requestResponse().response();
                    if (response != null) {
                        HttpResponse newResponse = replaceTextAtOffset(response, range, newText);
                        editor.setResponse(newResponse);
                    }
                }
                return;
            }
        }
    }
    
    private HttpRequest replaceTextAtOffset(
            HttpRequest request,
            Range range,
            String newText) {
        byte[] originalBytes = request.toByteArray().getBytes();
        
        // Range 是字符偏移量，需要先转换为字符串进行操作
        String originalText = new String(originalBytes, StandardCharsets.UTF_8);
        int charStart = range.startIndexInclusive();
        int charEnd = range.endIndexExclusive();
        
        // 使用字符串替换
        String resultText = originalText.substring(0, charStart) + newText + originalText.substring(charEnd);
        
        // 转回字节数组
        byte[] resultBytes = resultText.getBytes(StandardCharsets.UTF_8);
        
        return HttpRequest.httpRequest(
            request.httpService(),
            burp.api.montoya.core.ByteArray.byteArray(resultBytes)
        );
    }
    
    private HttpResponse replaceTextAtOffset(
            HttpResponse response,
            Range range,
            String newText) {
        byte[] originalBytes = response.toByteArray().getBytes();
        
        // Range 是字符偏移量，需要先转换为字符串进行操作
        String originalText = new String(originalBytes, StandardCharsets.UTF_8);
        int charStart = range.startIndexInclusive();
        int charEnd = range.endIndexExclusive();
        
        // 使用字符串替换
        String resultText = originalText.substring(0, charStart) + newText + originalText.substring(charEnd);
        
        // 转回字节数组
        byte[] resultBytes = resultText.getBytes(StandardCharsets.UTF_8);
        
        return HttpResponse.httpResponse(
            burp.api.montoya.core.ByteArray.byteArray(resultBytes)
        );
    }
    
    private Optional<String> getSelectedText(ContextMenuEvent event) {
        // 从消息编辑器获取选中的文本
        if (event.messageEditorRequestResponse().isPresent()) {
            MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
            Optional<Range> selection = editor.selectionOffsets();
            
            if (selection.isPresent()) {
                Range range = selection.get();
                MessageEditorHttpRequestResponse.SelectionContext context = editor.selectionContext();
                
                int start = range.startIndexInclusive();
                int end = range.endIndexExclusive();
                
                // 根据上下文获取文本
                // 注意：必须使用当前编辑器显示的内容，而不是 requestResponse 中的历史数据
                byte[] currentBytes;
                if (context == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                    // 使用 requestResponse().request() 获取当前编辑器的内容
                    currentBytes = editor.requestResponse().request().toByteArray().getBytes();
                } else if (context == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE) {
                    HttpResponse response = editor.requestResponse().response();
                    if (response != null) {
                        currentBytes = response.toByteArray().getBytes();
                    } else {
                        return Optional.empty();
                    }
                } else {
                    return Optional.empty();
                }
                
                return extractTextByCharOffset(currentBytes, start, end);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * 使用字符偏移量从字节数组中提取文本
     */
    private Optional<String> extractTextByCharOffset(byte[] bytes, int charStart, int charEnd) {
        try {
            // 将字节数组转为字符串，再用字符偏移提取
            String fullText = new String(bytes, StandardCharsets.UTF_8);
            
            if (charStart < 0 || charEnd > fullText.length() || charStart >= charEnd) {
                return Optional.empty();
            }
            
            return Optional.of(fullText.substring(charStart, charEnd));
        } catch (Exception e) {
            api.logging().logToError("Failed to extract text: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    @FunctionalInterface
    interface TextProcessor {
        String process(String input);
    }
}
