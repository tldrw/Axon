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
}
