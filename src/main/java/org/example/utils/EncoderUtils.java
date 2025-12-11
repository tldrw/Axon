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
    
    // UTF-16LE编码（16进制）
    public static String utf16leEncode(String input) {
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
