package org.example.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP请求转换工具类
 * 负责不同POST格式之间的转换
 * 处理完整的HTTP请求（包括请求头和请求体）
 */
public class HttpRequestConverter {
    
    /**
     * 转换为上传数据包（multipart/form-data）
     * 处理完整的HTTP请求
     */
    public static String convertToMultipart(String fullRequest) {
        try {
            HttpRequestParts parts = parseHttpRequest(fullRequest);
            Map<String, String> params = parseParameters(parts.body);
            String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            
            StringBuilder result = new StringBuilder();
            
            // 重建请求行
            result.append(parts.requestLine).append("\n");
            
            // 重建请求头（更新Content-Type，移除旧的Content-Length）
            for (String header : parts.headers) {
                String lowerHeader = header.toLowerCase();
                if (!lowerHeader.startsWith("content-type:") && !lowerHeader.startsWith("content-length:")) {
                    result.append(header).append("\n");
                }
            }
            result.append("Content-Type: multipart/form-data; boundary=").append(boundary).append("\n");
            
            // 构建请求体
            StringBuilder body = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                body.append("--").append(boundary).append("\r\n");
                body.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
                body.append(entry.getValue()).append("\r\n");
            }
            body.append("--").append(boundary).append("--");
            
            result.append("Content-Length: ").append(body.length()).append("\n");
            result.append("\n");
            result.append(body);
            
            return result.toString();
        } catch (Exception e) {
            return "转换失败：" + e.getMessage();
        }
    }
    
    /**
     * 转换为普通POST参数（application/x-www-form-urlencoded）
     * 处理完整的HTTP请求
     */
    public static String convertToFormUrlEncoded(String fullRequest) {
        try {
            HttpRequestParts parts = parseHttpRequest(fullRequest);
            Map<String, String> params = parseParameters(parts.body);
            
            StringBuilder result = new StringBuilder();
            
            // 重建请求行
            result.append(parts.requestLine).append("\n");
            
            // 重建请求头（更新Content-Type，移除旧的Content-Length）
            for (String header : parts.headers) {
                String lowerHeader = header.toLowerCase();
                if (!lowerHeader.startsWith("content-type:") && !lowerHeader.startsWith("content-length:")) {
                    result.append(header).append("\n");
                }
            }
            result.append("Content-Type: application/x-www-form-urlencoded\n");
            
            // 构建请求体
            StringBuilder body = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    body.append("&");
                }
                body.append(urlEncode(entry.getKey())).append("=").append(urlEncode(entry.getValue()));
                first = false;
            }
            
            result.append("Content-Length: ").append(body.length()).append("\n");
            result.append("\n");
            result.append(body);
            
            return result.toString();
        } catch (Exception e) {
            return "转换失败：" + e.getMessage();
        }
    }
    
    /**
     * 转换为JSON POST参数（application/json）
     * 处理完整的HTTP请求
     */
    public static String convertToJsonPost(String fullRequest) {
        try {
            HttpRequestParts parts = parseHttpRequest(fullRequest);
            Map<String, String> params = parseParameters(parts.body);
            
            StringBuilder result = new StringBuilder();
            
            // 重建请求行
            result.append(parts.requestLine).append("\n");
            
            // 重建请求头（更新Content-Type，移除旧的Content-Length）
            for (String header : parts.headers) {
                String lowerHeader = header.toLowerCase();
                if (!lowerHeader.startsWith("content-type:") && !lowerHeader.startsWith("content-length:")) {
                    result.append(header).append("\n");
                }
            }
            result.append("Content-Type: application/json\n");
            
            // 构建请求体
            StringBuilder body = new StringBuilder();
            body.append("{\n");
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    body.append(",\n");
                }
                body.append("  \"").append(escapeJson(entry.getKey())).append("\": \"")
                      .append(escapeJson(entry.getValue())).append("\"");
                first = false;
            }
            body.append("\n}");
            
            result.append("Content-Length: ").append(body.toString().getBytes("UTF-8").length).append("\n");
            result.append("\n");
            result.append(body);
            
            return result.toString();
        } catch (Exception e) {
            return "转换失败：" + e.getMessage();
        }
    }
    
    /**
     * 转换为XML POST参数（application/xml）
     * 处理完整的HTTP请求
     */
    public static String convertToXmlPost(String fullRequest) {
        try {
            HttpRequestParts parts = parseHttpRequest(fullRequest);
            Map<String, String> params = parseParameters(parts.body);
            
            StringBuilder result = new StringBuilder();
            
            // 重建请求行
            result.append(parts.requestLine).append("\n");
            
            // 重建请求头（更新Content-Type，移除旧的Content-Length）
            for (String header : parts.headers) {
                String lowerHeader = header.toLowerCase();
                if (!lowerHeader.startsWith("content-type:") && !lowerHeader.startsWith("content-length:")) {
                    result.append(header).append("\n");
                }
            }
            result.append("Content-Type: application/xml\n");
            
            // 构建请求体（紧凑格式）
            StringBuilder body = new StringBuilder();
            body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            body.append("<root>");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                body.append("<").append(sanitizeXmlTag(entry.getKey())).append(">")
                      .append(escapeXml(entry.getValue()))
                      .append("</").append(sanitizeXmlTag(entry.getKey())).append(">");
            }
            body.append("</root>");
            
            result.append("Content-Length: ").append(body.toString().getBytes("UTF-8").length).append("\n");
            result.append("\n");
            result.append(body);
            
            return result.toString();
        } catch (Exception e) {
            return "转换失败：" + e.getMessage();
        }
    }
    
    /**
     * 解析HTTP请求，分离请求行、请求头和请求体
     */
    private static HttpRequestParts parseHttpRequest(String request) {
        HttpRequestParts parts = new HttpRequestParts();
        String[] lines = request.split("\n", -1);
        
        int lineIndex = 0;
        
        // 解析请求行
        if (lineIndex < lines.length) {
            parts.requestLine = lines[lineIndex++].trim();
        }
        
        // 解析请求头
        while (lineIndex < lines.length) {
            String line = lines[lineIndex++];
            if (line.trim().isEmpty()) {
                // 空行，请求头结束
                break;
            }
            parts.headers.add(line);
        }
        
        // 解析请求体
        StringBuilder bodyBuilder = new StringBuilder();
        while (lineIndex < lines.length) {
            if (bodyBuilder.length() > 0) {
                bodyBuilder.append("\n");
            }
            bodyBuilder.append(lines[lineIndex++]);
        }
        parts.body = bodyBuilder.toString();
        
        return parts;
    }
    
    /**
     * 解析参数（支持多种格式）
     */
    private static Map<String, String> parseParameters(String input) {
        Map<String, String> params = new LinkedHashMap<>();
        
        // 尝试解析 URL 编码格式 (key1=value1&key2=value2)
        if (input.contains("=") && (input.contains("&") || !input.contains("{"))) {
            String[] pairs = input.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = urlDecode(pair.substring(0, idx).trim());
                    String value = idx < pair.length() - 1 ? urlDecode(pair.substring(idx + 1).trim()) : "";
                    params.put(key, value);
                }
            }
        }
        // 尝试解析 JSON 格式
        else if (input.trim().startsWith("{")) {
            Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*?)\"");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                params.put(matcher.group(1), matcher.group(2));
            }
        }
        // 尝试解析 multipart 格式
        else if (input.contains("Content-Disposition")) {
            Pattern pattern = Pattern.compile("name=\"([^\"]+)\"\\s*\\n\\s*\\n([^-]+?)(?=\\n--|$)");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                params.put(matcher.group(1), matcher.group(2).trim());
            }
        }
        // 默认：将整个内容作为单个参数
        else {
            params.put("data", input);
        }
        
        return params;
    }
    
    /**
     * URL 编码
     */
    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
    
    /**
     * URL 解码
     */
    private static String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
    
    /**
     * JSON 字符串转义
     */
    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
    
    /**
     * XML 字符串转义
     */
    private static String escapeXml(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
    
    /**
     * 清理 XML 标签名称（移除非法字符）
     */
    private static String sanitizeXmlTag(String tag) {
        // XML 标签名只能包含字母、数字、下划线、连字符和点
        return tag.replaceAll("[^a-zA-Z0-9_\\-.]", "_");
    }
    
    /**
     * HTTP请求各部分
     */
    private static class HttpRequestParts {
        String requestLine = "";
        java.util.List<String> headers = new java.util.ArrayList<>();
        String body = "";
    }
}
