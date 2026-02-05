package org.example.ui.encoder;

import javax.swing.JPanel;

public interface EncoderProcessor {
    /**
     * 获取编码器名称（显示在左侧列表）
     */
    String getName();

    /**
     * 获取选项面板（显示在中间区域）
     */
    JPanel getOptionsPanel();

    /**
     * 执行处理
     * @param input 输入字符串
     * @param isEncode true 为编码，false 为解码
     * @return 处理结果
     */
    String process(String input, boolean isEncode);
}
