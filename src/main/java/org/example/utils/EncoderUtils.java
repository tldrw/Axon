package org.example.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

public class EncoderUtils {
    
    // URL编码（全部）
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "编码失败：" + e.getMessage();
        }
    }
    
    // URL编码（只编码特殊字符）
    public static String urlEncodeSpecial(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            // 只对特殊字符进行编码
            if (isSpecialChar(c)) {
                try {
                    result.append(URLEncoder.encode(String.valueOf(c), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private static boolean isSpecialChar(char c) {
        // 特殊字符列表
        return c == ' ' || c == '!' || c == '#' || c == '$' || c == '%' || 
               c == '&' || c == '\'' || c == '(' || c == ')' || c == '*' || 
               c == '+' || c == ',' || c == '/' || c == ':' || c == ';' || 
               c == '=' || c == '?' || c == '@' || c == '[' || c == ']';
    }
    
    // Base64编码
    public static String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
    
    // Unicode编码（全编码）
    public static String unicodeEncode(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append("\\u").append(String.format("%04x", (int) c));
        }
        return result.toString();
    }
    
    // Unicode编码（忽略ASCII字符）
    public static String unicodeEncodeIgnoreAscii(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c > 127) {
                result.append("\\u").append(String.format("%04x", (int) c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    // UTF-8编码（转换为\x十六进制格式）
    public static String utf8Encode(String input) {
        try {
            byte[] bytes = input.getBytes("UTF-8");
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("\\x%02x", b & 0xFF));
            }
            return result.toString();
        } catch (Exception e) {
            return "编码失败：" + e.getMessage();
        }
    }
    
    // UTF-16LE编码（返回原始字符串，不转hex）
    public static String utf16leEncode(String input) {
        try {
            byte[] bytes = input.getBytes("UTF-16LE");
            // 直接返回原始字节序列的字符串表示
            return new String(bytes, "ISO-8859-1"); // 使用ISO-8859-1保持字节不变
        } catch (Exception e) {
            return "编码失败：" + e.getMessage();
        }
    }
    
    // UTF-16LE编码转hex（16进制）
    public static String utf16leEncodeToHex(String input) {
        try {
            byte[] bytes = input.getBytes("UTF-16LE");
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b & 0xFF));
            }
            return result.toString();
        } catch (Exception e) {
            return "编码失败：" + e.getMessage();
        }
    }
    
    /**
     * 将hex字符串格式化为不同格式
     * @param hexString hex字符串（如: "48656c6c6f"）
     * @param format 格式类型
     * @return 格式化后的字符串
     */
    public static String formatHexString(String hexString, HexFormat format) {
        if (hexString == null || hexString.isEmpty()) {
            return hexString;
        }
        
        // 移除可能存在的空格和分隔符，确保是纯hex字符串
        String cleanHex = hexString.replaceAll("[^0-9a-fA-F]", "");
        
        if (cleanHex.isEmpty()) {
            return hexString;
        }
        
        StringBuilder result = new StringBuilder();
        
        switch (format) {
            case FALSE:
                // 默认：不启用hex格式输出
                return cleanHex;
                
            case SPACE:
                // 空格分隔：48 65 6c 6c 6f
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    if (i > 0) result.append(" ");
                    result.append(cleanHex.substring(i, Math.min(i + 2, cleanHex.length())));
                }
                return result.toString();
                
            case COMMA:
                // 逗号分隔：48,65,6c,6c,6f
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    if (i > 0) result.append(",");
                    result.append(cleanHex.substring(i, Math.min(i + 2, cleanHex.length())));
                }
                return result.toString();
                
            case SEMICOLON:
                // 分号分隔：48;65;6c;6c;6f
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    if (i > 0) result.append(";");
                    result.append(cleanHex.substring(i, Math.min(i + 2, cleanHex.length())));
                }
                return result.toString();
                
            case PREFIX_0X:
                // 0x前缀：0x480x650x6c0x6c0x6f
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    result.append("0x");
                    result.append(cleanHex.substring(i, Math.min(i + 2, cleanHex.length())));
                }
                return result.toString();
                
            case PREFIX_0X_COMMA:
                // 0x前缀逗号分隔：0x48,0x65,0x6c,0x6c,0x6f
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    if (i > 0) result.append(",");
                    result.append("0x");
                    result.append(cleanHex.substring(i, Math.min(i + 2, cleanHex.length())));
                }
                return result.toString();
                
            case PREFIX_BACKSLASH_X:
                // \x前缀：\x48\x65\x6c\x6c\x6f
                for (int i = 0; i < cleanHex.length(); i += 2) {
                    result.append("\\x");
                    result.append(cleanHex.substring(i, Math.min(i + 2, cleanHex.length())));
                }
                return result.toString();
                
            case NONE:
                // 无格式：48656c6c6f
                return cleanHex;
                
            default:
                return cleanHex;
        }
    }
    
    /**
     * Hex格式化类型枚举
     */
    public enum HexFormat {
        FALSE,                   // 默认：不启用
        SPACE,                   // 空格分隔
        COMMA,                   // 逗号分隔
        SEMICOLON,               // 分号分隔
        PREFIX_0X,               // 0x前缀（无分隔符）
        PREFIX_0X_COMMA,         // 0x前缀（逗号分隔）
        PREFIX_BACKSLASH_X,      // \x前缀
        NONE                     // 无格式
    }
    
    // HEX编码（16进制编码）
    public static String hexEncode(String input) {
        try {
            byte[] bytes = input.getBytes("UTF-8");
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b & 0xFF));
            }
            return result.toString();
        } catch (Exception e) {
            return "编码失败：" + e.getMessage();
        }
    }
    
    // HTML实体编码
    public static String htmlEntityEncode(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c > 127 || isHtmlSpecialChar(c)) {
                // 对非ASCII字符和HTML特殊字符进行实体编码
                result.append("&#").append((int) c).append(";");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private static boolean isHtmlSpecialChar(char c) {
        return c == '<' || c == '>' || c == '&' || c == '"' || c == '\'';
    }
    
    // Unicode编码（JSON键值编码）- 只对JSON中的键和值进行Unicode编码，保持JSON结构
    public static String unicodeEncodeJsonValues(String input) {
        try {
            StringBuilder result = new StringBuilder();
            boolean inString = false;
            boolean escapeNext = false;
            
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                
                // 处理转义字符
                if (escapeNext) {
                    result.append("\\").append(c);
                    escapeNext = false;
                    continue;
                }
                
                // 检查是否是转义符
                if (c == '\\' && inString) {
                    escapeNext = true;
                    continue;
                }
                
                // 检查是否进入或退出字符串
                if (c == '"') {
                    result.append(c);
                    inString = !inString;
                    continue;
                }
                
                // 在字符串内，对所有字符进行Unicode编码
                if (inString) {
                    result.append("\\u").append(String.format("%04x", (int) c));
                } else {
                    result.append(c);
                }
            }
            
            return result.toString();
        } catch (Exception e) {
            return "编码失败：" + e.getMessage();
        }
    }

}
