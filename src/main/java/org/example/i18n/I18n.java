package org.example.i18n;

import java.util.TimeZone;

/**
 * 国际化工具类
 * 根据系统时区自动判断语言
 */
public class I18n {
    
    private static final boolean IS_CHINESE;
    
    static {
        // 获取系统默认时区
        TimeZone timeZone = TimeZone.getDefault();
        String zoneId = timeZone.getID();
        
        // 中国大陆时区: Asia/Shanghai, Asia/Chongqing, Asia/Harbin 等
        // 香港时区: Asia/Hong_Kong
        // 台湾时区: Asia/Taipei
        IS_CHINESE = zoneId.startsWith("Asia/Shanghai") || 
                     zoneId.startsWith("Asia/Chongqing") ||
                     zoneId.startsWith("Asia/Harbin") ||
                     zoneId.startsWith("Asia/Hong_Kong") ||
                     zoneId.startsWith("Asia/Taipei") ||
                     zoneId.equals("Asia/Urumqi") ||
                     zoneId.equals("PRC") ||
                     zoneId.equals("CTT");
    }
    
    /**
     * 判断是否使用中文
     */
    public static boolean isChinese() {
        return IS_CHINESE;
    }
    
    // 菜单文本
    public static String encode() {
        return IS_CHINESE ? "编码" : "Encode";
    }
    
    public static String decode() {
        return IS_CHINESE ? "解码" : "Decode";
    }
    
    public static String httpRequestModify() {
        return IS_CHINESE ? "HTTP请求修改" : "HTTP Modify";
    }
    
    public static String format() {
        return IS_CHINESE ? "格式化" : "Format";
    }
    
    // 编码菜单项
    public static String urlEncode() {
        return IS_CHINESE ? "URL编码" : "URL Encode";
    }
    
    public static String urlEncodeSpecial() {
        return IS_CHINESE ? "URL编码（特殊符号）" : "URL Encode (Special Chars)";
    }
    
    public static String base64Encode() {
        return IS_CHINESE ? "Base64编码" : "Base64 Encode";
    }
    
    public static String unicodeEncode() {
        return IS_CHINESE ? "Unicode编码" : "Unicode Encode";
    }
    
    public static String unicodeEncodeIgnoreAscii() {
        return IS_CHINESE ? "Unicode编码（忽略ASCII字符）" : "Unicode Encode (Ignore ASCII)";
    }
    
    public static String unicodeEncodeJsonValues() {
        return IS_CHINESE ? "Unicode编码（JSON键值编码）" : "Unicode Encode (JSON Values)";
    }
    
    public static String utf8Encode() {
        return IS_CHINESE ? "UTF-8编码（16进制）" : "UTF-8 Encode (Hex)";
    }
    
    public static String utf16leEncode() {
        return IS_CHINESE ? "UTF-16LE编码（16进制）" : "UTF-16LE Encode (Hex)";
    }
    
    public static String hexEncode() {
        return IS_CHINESE ? "HEX编码（16进制）" : "HEX Encode";
    }
    
    // 解码菜单项
    public static String base64Decode() {
        return IS_CHINESE ? "Base64解码" : "Base64 Decode";
    }
    
    public static String urlDecode() {
        return IS_CHINESE ? "URL解码" : "URL Decode";
    }
    
    public static String unicodeDecode() {
        return IS_CHINESE ? "Unicode解码" : "Unicode Decode";
    }
    
    public static String hexDecode() {
        return IS_CHINESE ? "HEX解码（16进制）" : "HEX Decode";
    }
    
    // HTTP请求修改菜单项
    public static String jsonFormat() {
        return IS_CHINESE ? "JSON格式化" : "JSON Format";
    }
    
    public static String jsonCompress() {
        return IS_CHINESE ? "JSON压缩" : "JSON Compress";
    }
    
    public static String xmlFormat() {
        return IS_CHINESE ? "XML格式化" : "XML Format";
    }
    
    public static String xmlCompress() {
        return IS_CHINESE ? "XML压缩" : "XML Compress";
    }
    
    // HTTP请求转换菜单项
    public static String convertToMultipart() {
        return IS_CHINESE ? "修改为上传数据包" : "Convert to Multipart";
    }
    
    public static String convertToFormUrlEncoded() {
        return IS_CHINESE ? "转换为普通POST参数" : "Convert to Form URL Encoded";
    }
    
    public static String convertToJsonPost() {
        return IS_CHINESE ? "转换为JSON-POST参数" : "Convert to JSON POST";
    }
    
    public static String convertToXmlPost() {
        return IS_CHINESE ? "转换为XML-POST参数" : "Convert to XML POST";
    }
    
    // 预览对话框文本
    public static String before() {
        return IS_CHINESE ? "编码前：" : "Before:";
    }
    
    public static String after() {
        return IS_CHINESE ? "编码后：" : "After:";
    }
    
    public static String replace() {
        return IS_CHINESE ? "替换" : "Replace";
    }
    
    public static String cancel() {
        return IS_CHINESE ? "取消" : "Cancel";
    }
    
    public static String copyResult() {
        return IS_CHINESE ? "复制结果" : "Copy Result";
    }
    
    public static String copySuccess() {
        return IS_CHINESE ? "已复制到剪贴板" : "Copied to clipboard";
    }
    
    public static String success() {
        return IS_CHINESE ? "成功" : "Success";
    }
}
