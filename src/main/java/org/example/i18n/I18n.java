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

    public static String format() {
        return IS_CHINESE ? "格式化" : "Format";
    }
    
    public static String httpRequestModify() {
        return IS_CHINESE ? "HTTP请求修改" : "HTTP Modify";
    }
    
    // 编码菜单项
    public static String base64Encode() {
        return IS_CHINESE ? "Base64 编码" : "Base64 Encode";
    }

    public static String hexEncode() {
        return IS_CHINESE ? "HEX 编码（16进制）" : "HEX Encode";
    }

    public static String htmlEntityEncode() { return IS_CHINESE ? "HTML 实体编码" : "HTML Entity Encode"; }

    public static String unicodeEncode() {
        return IS_CHINESE ? "Unicode 编码" : "Unicode Encode";
    }

    public static String unicodeEncodeIgnoreAscii() {
        return IS_CHINESE ? "Unicode 编码（忽略ASCII字符）" : "Unicode Encode (Ignore ASCII)";
    }

    public static String unicodeEncodeJsonValues() {
        return IS_CHINESE ? "Unicode 编码（JSON键值编码）" : "Unicode Encode (JSON Values)";
    }

    public static String urlEncode() {
        return IS_CHINESE ? "URL 编码" : "URL Encode";
    }
    
    public static String urlEncodeSpecial() {
        return IS_CHINESE ? "URL 编码（仅特殊符号）" : "URL Encode (Special Chars)";
    }
    
    public static String utf8Encode() {
        return IS_CHINESE ? "UTF-8 编码" : "UTF-8 Encode";
    }
    
    public static String utf16leEncode() {
        return IS_CHINESE ? "UTF-16LE 编码" : "UTF-16LE Encode";
    }
    
    // 解码菜单项
    public static String base64Decode() {
        return IS_CHINESE ? "Base64 解码" : "Base64 Decode";
    }

    public static String hexDecode() {
        return IS_CHINESE ? "HEX 解码（16进制）" : "HEX Decode";
    }
    
    public static String htmlEntityDecode() {
        return IS_CHINESE ? "HTML 实体解码" : "HTML Entity Decode";
    }
    
    public static String urlDecode() {
        return IS_CHINESE ? "URL 解码" : "URL Decode";
    }
    
    public static String unicodeDecode() {
        return IS_CHINESE ? "Unicode 解码" : "Unicode Decode";
    }
    
    // HTTP请求修改菜单项
    public static String jsonFormat() {
        return IS_CHINESE ? "JSON 格式化" : "JSON Format";
    }
    
    public static String jsonCompress() {
        return IS_CHINESE ? "JSON 压缩" : "JSON Compress";
    }
    
    public static String xmlFormat() {
        return IS_CHINESE ? "XML 格式化" : "XML Format";
    }
    
    public static String xmlCompress() {
        return IS_CHINESE ? "XML 压缩" : "XML Compress";
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

    public static String length() { return IS_CHINESE ? "长度" : "Length"; }
    
    // Hex格式化选项
    public static String hexFormatFalse() { return IS_CHINESE ? "不启用" : "False"; }
    
    public static String hexFormatSpace() {
        return IS_CHINESE ? "空格分隔" : "Space";
    }
    
    public static String hexFormatComma() {
        return IS_CHINESE ? "逗号分隔" : "Comma";
    }
    
    public static String hexFormatSemicolon() {
        return IS_CHINESE ? "分号分隔" : "Semi-colon";
    }
    
    public static String hexFormat0x() {
        return IS_CHINESE ? "0x前缀格式" : "0x";
    }
    
    public static String hexFormat0xComma() {
        return IS_CHINESE ? "0x前缀逗号分隔" : "0x with comma";
    }
    
    public static String hexFormatBackslashX() {
        return IS_CHINESE ? "\\x前缀格式" : "\\x";
    }
    
    public static String hexFormatNone() {
        return IS_CHINESE ? "无格式" : "None";
    }

    public static String hexOutputLabel() { return IS_CHINESE ? "Hex输出：" : "Hex Output:"; }

    // 工具联动菜单项
    public static String toolIntegration() {
        return IS_CHINESE ? "工具联动" : "Tool Integration";
    }

    public static String add() {
        return IS_CHINESE ? "添加" : "Add";
    }

    public static String edit() {
        return IS_CHINESE ? "编辑" : "Edit";
    }

    public static String remove() {
        return IS_CHINESE ? "删除" : "Remove";
    }

    public static String enabled() {
        return IS_CHINESE ? "启用" : "Enabled";
    }

    public static String name() {
        return IS_CHINESE ? "名称" : "Name";
    }

    public static String command() {
        return IS_CHINESE ? "命令" : "Command";
    }

    public static String path() {
        return IS_CHINESE ? "路径" : "Path";
    }

    public static String directory() {
        return IS_CHINESE ? "目录" : "Directory";
    }

    public static String previewBeforeExecution() {
        return IS_CHINESE ? "执行前预览" : "Preview before execution";
    }

    public static String confirm() {
        return IS_CHINESE ? "确定" : "Confirm";
    }

    public static String warning() {
        return IS_CHINESE ? "警告" : "Warning";
    }

    public static String selectItem() {
        return IS_CHINESE ? "请选择一项" : "Please select an item";
    }

    public static String confirmDelete() {
        return IS_CHINESE ? "确定要删除吗？" : "Are you sure you want to delete?";
    }

    public static String templateVariables() {
        return IS_CHINESE ? "模板变量" : "Template Variables";
    }

    public static String example() {
        return IS_CHINESE ? "示例" : "Example";
    }

    public static String variable() {
        return IS_CHINESE ? "变量" : "Variable";
    }

    public static String description() {
        return IS_CHINESE ? "描述" : "Description";
    }

    public static String previewResult() {
        return IS_CHINESE ? "提取预览 (基于示例URL)" : "Preview Result (Based on example URL)";
    }

    public static String toolCommandPreview() {
        return IS_CHINESE ? "工具命令预览" : "Tool Command Preview";
    }
    
    public static String pasteCurlToRepeater() {
        return IS_CHINESE ? "粘贴 cURL 到 Repeater" : "Paste cURL to Repeater";
    }
    
    public static String errorReadClipboard() {
        return IS_CHINESE ? "无法读取剪贴板" : "Failed to read clipboard";
    }
    
    public static String errorParseCurl() {
        return IS_CHINESE ? "cURL 解析失败" : "Failed to parse cURL command";
    }

    public static String run() {
        return IS_CHINESE ? "运行" : "Run";
    }

    public static String copy() {
        return IS_CHINESE ? "复制" : "Copy";
    }
    public static String clear() {
        return IS_CHINESE ? "清空" : "Clear";
    }

    // Encoder Panel
    public static String encoderTab() { return IS_CHINESE ? "编码解码" : "Encoder/Decoder"; }
    public static String sendToEncoder() { return IS_CHINESE ? "发送到编码器" : "Send to Encoder"; }
    public static String input() { return IS_CHINESE ? "输入" : "Input"; }
    public static String output() { return IS_CHINESE ? "输出" : "Output"; }
    public static String options() { return IS_CHINESE ? "选项" : "Options"; }
    public static String encodeBtn() { return IS_CHINESE ? "编码" : "Encode"; }
    public static String decodeBtn() { return IS_CHINESE ? "解码" : "Decode"; }
    
    // Processor Options
    public static String optUrlSafe() { return IS_CHINESE ? "URL 安全 (URL Safe)" : "URL Safe"; }
    public static String optEncodeAllChars() { return IS_CHINESE ? "编码所有字符" : "Encode all characters"; }
    public static String optSpecialCharsOnly() { return IS_CHINESE ? "仅特殊符号" : "Special characters only"; }
    
    public static String optFormat() { return IS_CHINESE ? "编码格式" : "Format"; }
    public static String fmtHtmlDecimal() { return IS_CHINESE ? "10进制" : "Decimal"; }
    public static String fmtHtmlHex() { return IS_CHINESE ? "16进制" : "Hex"; }
    
    public static String optIgnoreAscii() { return IS_CHINESE ? "忽略 ASCII" : "Ignore ASCII"; }

    public static String fmtBackslashX() { return IS_CHINESE ? "\\xXX" : "\\xXX"; }
    public static String fmtPercent() { return IS_CHINESE ? "%XX" : "%XX"; }
    public static String fmtByteStream() { return IS_CHINESE ? "字节流" : "Byte Stream"; }
    public static String fmtSequence() { return IS_CHINESE ? "序列" : "Sequence"; }
    
    public static String fmtUnicodeDefault() { return IS_CHINESE ? "Unicode 默认模式 \\uXXXX" : "\\uXXXX"; }
    public static String fmtUnicodeUPlus() { return IS_CHINESE ? "Unicode 编码模式 U+XXXX" : "U+XXXX"; }
    public static String fmtUnicodeHtmlDec() { return IS_CHINESE ? "HTML实体 10进制 (&#XX;)" : "HTML Decimal (&#XX;)"; }
    public static String fmtUnicodeHtmlHex() { return IS_CHINESE ? "HTML实体 16进制 (&#xXXXX;)" : "HTML Hex (&#xXXXX;)"; }
    public static String fmtUnicodeCssHex() { return IS_CHINESE ? "CSS实体 16进制 (\\XXXX)" : "CSS Hex (\\XXXX)"; }
    
    public static String optEscapeMode() { return IS_CHINESE ? "转义模式" : "Escape Mode"; }
    public static String optEscapeAll() { return IS_CHINESE ? "转义所有字符" : "All Characters"; }
    public static String optEscapeReserved() { return IS_CHINESE ? "仅转义保留字符" : "Reserved Characters Only"; }
    public static String tipEscapeReserved() { 
        return IS_CHINESE ? "仅对字符串中保留的HTML符号进行转义（双引号 \", 单引号 ', 和符号 &, 小于号 <, 大于号 >）" : 
                            "Only escape reserved HTML characters (\", ', &, <, >)"; 
    }
    public static String optUseHtmlAbbr() { return IS_CHINESE ? "特殊字符使用HTML缩写 (&lt;)" : "Use HTML Abbreviations for special chars (&lt;)"; }
    
    // Template Variable Descriptions
    public static String varHost() { return IS_CHINESE ? "主机名（不带端口）" : "Host (without port)"; }
    public static String varPort() { return IS_CHINESE ? "端口" : "Port"; }
    public static String varProtocol() { return IS_CHINESE ? "协议 (http/https)" : "Protocol (http/https)"; }
    public static String varUrlFull() { return IS_CHINESE ? "完整URL" : "Full URL"; }
    public static String varUrlBase() { return IS_CHINESE ? "基础URL (不含路径)" : "Base URL (no path)"; }
    public static String varUrlNoQuery() { return IS_CHINESE ? "URL (不含查询参数)" : "URL (no query)"; }
    public static String varUrlPath() { return IS_CHINESE ? "URL路径" : "URL Path"; }
    public static String varUrlQuery() { return IS_CHINESE ? "URL查询参数" : "URL Query"; }
    public static String varMethod() { return IS_CHINESE ? "HTTP方法" : "HTTP Method"; }
    public static String varFile() { return IS_CHINESE ? "请求数据包文件路径" : "Request packet file path"; }
    public static String varCookie() { return IS_CHINESE ? "Cookie字符串" : "Cookie string"; }
    public static String varUserAgent() { return IS_CHINESE ? "User-Agent字符串" : "User-Agent string"; }
}
