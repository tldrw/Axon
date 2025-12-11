package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * JSON 处理工具类
 * 负责 JSON 格式化、压缩等操作
 */
public class JsonProcessor {
    
    /**
     * JSON 格式化
     * 将压缩的 JSON 转换为带缩进的格式化输出
     */
    public static String format(String input) {
        try {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()  // 禁用 HTML 转义，避免中文变成 Unicode
                .create();
            Object jsonObject = JsonParser.parseString(input);
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            return "格式化失败：" + e.getMessage();
        }
    }
    
    /**
     * JSON 压缩
     * 移除所有空白字符，生成紧凑的 JSON
     */
    public static String compress(String input) {
        try {
            Gson gson = new GsonBuilder()
                .disableHtmlEscaping()  // 禁用 HTML 转义，避免中文变成 Unicode
                .create();
            Object jsonObject = JsonParser.parseString(input);
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            return "压缩失败：" + e.getMessage();
        }
    }
    
    /**
     * 验证 JSON 格式是否有效
     */
    public static boolean isValidJson(String input) {
        try {
            JsonParser.parseString(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
