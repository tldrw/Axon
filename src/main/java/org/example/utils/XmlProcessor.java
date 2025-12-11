package org.example.utils;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * XML 处理工具类
 * 负责 XML 格式化、压缩等操作
 */
public class XmlProcessor {
    
    /**
     * XML 格式化
     * 将压缩的 XML 转换为带缩进的格式化输出
     */
    public static String format(String input) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            StreamSource source = new StreamSource(new StringReader(input));
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            
            transformer.transform(source, result);
            return writer.toString();
        } catch (Exception e) {
            return "格式化失败：" + e.getMessage();
        }
    }
    
    /**
     * XML 压缩
     * 移除所有空白字符，生成紧凑的 XML
     */
    public static String compress(String input) {
        try {
            // 移除标签之间的空白字符
            String compressed = input.replaceAll(">\\s+<", "><");
            // 移除首尾空白
            compressed = compressed.trim();
            return compressed;
        } catch (Exception e) {
            return "压缩失败：" + e.getMessage();
        }
    }
    
    /**
     * 验证 XML 格式是否有效
     */
    public static boolean isValidXml(String input) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamSource source = new StreamSource(new StringReader(input));
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
