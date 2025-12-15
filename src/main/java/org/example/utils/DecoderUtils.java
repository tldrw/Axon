package org.example.utils;

import java.net.URLDecoder;
import java.util.Base64;

public class DecoderUtils {
    
    // URL解码
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (Exception e) {
            return "解码失败：" + e.getMessage();
        }
    }
    
    // Base64解码
    public static String base64Decode(String input) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            return new String(decodedBytes);
        } catch (Exception e) {
            return "解码失败：" + e.getMessage();
        }
    }
    
    // Unicode解码
    public static String unicodeDecode(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (i + 5 < input.length() && input.charAt(i) == '\\' && input.charAt(i + 1) == 'u') {
                try {
                    String hex = input.substring(i + 2, i + 6);
                    int code = Integer.parseInt(hex, 16);
                    result.append((char) code);
                    i += 6;
                } catch (Exception e) {
                    result.append(input.charAt(i));
                    i++;
                }
            } else {
                result.append(input.charAt(i));
                i++;
            }
        }
        return result.toString();
    }
    
    // HEX解码（16进制解码）
    public static String hexDecode(String input) {
        try {
            // 移除可能存在的空格和分隔符
            String hex = input.replaceAll("[\\s:-]", "");
            
            if (hex.length() % 2 != 0) {
                return "解码失败：十六进制字符串长度必须为偶数";
            }
            
            byte[] bytes = new byte[hex.length() / 2];
            for (int i = 0; i < hex.length(); i += 2) {
                String byteStr = hex.substring(i, i + 2);
                bytes[i / 2] = (byte) Integer.parseInt(byteStr, 16);
            }
            
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            return "解码失败：" + e.getMessage();
        }
    }
    
    // HTML实体解码
    public static String htmlEntityDecode(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '&') {
                // 查找分号位置
                int semicolonIndex = input.indexOf(';', i);
                if (semicolonIndex != -1 && semicolonIndex - i <= 10) {
                    String entity = input.substring(i + 1, semicolonIndex);
                    
                    // 处理数字实体 &#123; 或 &#x7B;
                    if (entity.startsWith("#")) {
                        try {
                            int code;
                            if (entity.length() > 1 && (entity.charAt(1) == 'x' || entity.charAt(1) == 'X')) {
                                // 十六进制格式 &#xAB;
                                code = Integer.parseInt(entity.substring(2), 16);
                            } else {
                                // 十进制格式 &#123;
                                code = Integer.parseInt(entity.substring(1));
                            }
                            result.append((char) code);
                            i = semicolonIndex + 1;
                            continue;
                        } catch (NumberFormatException e) {
                            // 解析失败，按原样输出
                        }
                    } else {
                        // 处理命名实体
                        String decoded = decodeNamedEntity(entity);
                        if (decoded != null) {
                            result.append(decoded);
                            i = semicolonIndex + 1;
                            continue;
                        }
                    }
                }
            }
            result.append(input.charAt(i));
            i++;
        }
        return result.toString();
    }
    
    // 解码HTML命名实体
    private static String decodeNamedEntity(String entity) {
        switch (entity) {
            case "lt": return "<";
            case "gt": return ">";
            case "amp": return "&";
            case "quot": return "\"";
            case "apos": return "'";
            case "nbsp": return "\u00A0";
            case "copy": return "©";
            case "reg": return "®";
            case "trade": return "™";
            default: return null;
        }
    }
}
