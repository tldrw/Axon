package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionListener;
import org.example.i18n.I18n;
import org.example.utils.EncoderUtils;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PreviewDialog extends JDialog {
    private JTextArea beforeTextArea;
    private JTextArea afterTextArea;
    private JButton replaceButton;
    private JButton cancelButton;
    private boolean replaced = false;
    private String resultText;
    private JComboBox<String> hexFormatComboBox;
    private String originalAfterText; // 保存原始的编码后文本
    private BiFunction<String, EncoderUtils.HexFormat, String> hexFormatter; // hex格式化器
    private Function<Integer, EncoderUtils.HexFormat> indexToFormatMapper; // 索引到格式的映射
    private JLabel beforeLengthLabel; // 编码前长度标签
    private JLabel afterLengthLabel; // 编码后长度标签
    
    public PreviewDialog(Frame parent, String title, String beforeText, String afterText) {
        this(parent, title, beforeText, afterText, null, null, 0, null);
    }
    
    /**
     * 带hex格式化功能的构造函数（通用版本）
     * @param parent 父窗口
     * @param title 标题
     * @param beforeText 编码前文本
     * @param afterText 编码后文本
     * @param hexOptions 下拉菜单选项数组，为null则不显示下拉菜单
     * @param indexToFormatMapper 索引到HexFormat的映射函数
     * @param defaultIndex 默认选中的索引
     * @param hexFormatter hex格式化函数，接收原始文本和格式类型，返回格式化后的文本
     */
    public PreviewDialog(Frame parent, String title, String beforeText, String afterText,
                        String[] hexOptions, Function<Integer, EncoderUtils.HexFormat> indexToFormatMapper,
                        int defaultIndex, BiFunction<String, EncoderUtils.HexFormat, String> hexFormatter) {
        super(parent, title, true);
        
        this.originalAfterText = afterText;
        this.hexFormatter = hexFormatter;
        this.indexToFormatMapper = indexToFormatMapper;
        
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 左侧：编码前
        JPanel beforePanel = new JPanel(new BorderLayout(5, 5));
        JLabel beforeLabel = new JLabel(I18n.before());
        beforeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        beforeTextArea = new JTextArea(beforeText);
        beforeTextArea.setLineWrap(true);
        beforeTextArea.setWrapStyleWord(true);
        beforeTextArea.setEditable(false);
        beforeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane beforeScrollPane = new JScrollPane(beforeTextArea);
        
        // 编码前长度显示（左对齐）
        JPanel beforeBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        beforeLengthLabel = new JLabel(I18n.length() + ": " + beforeText.length());
        beforeLengthLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        beforeBottomPanel.add(beforeLengthLabel);
        
        beforePanel.add(beforeLabel, BorderLayout.NORTH);
        beforePanel.add(beforeScrollPane, BorderLayout.CENTER);
        beforePanel.add(beforeBottomPanel, BorderLayout.SOUTH);
        
        // 右侧：编码后
        JPanel afterPanel = new JPanel(new BorderLayout(5, 5));
        
        // 顶部面板：包含标签和hex格式下拉框
        JPanel afterTopPanel = new JPanel(new BorderLayout(10, 0));
        JLabel afterLabel = new JLabel(I18n.after());
        afterLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        afterTopPanel.add(afterLabel, BorderLayout.WEST);
        
        // 如果启用hex格式化，添加下拉框
        if (hexOptions != null && hexOptions.length > 0) {
            JPanel hexFormatPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            
            // 添加"Hex输出："标签
            JLabel hexLabel = new JLabel(I18n.hexOutputLabel());
            hexLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hexFormatPanel.add(hexLabel);
            
            // 添加下拉框
            hexFormatComboBox = new JComboBox<>(hexOptions);
            hexFormatComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            hexFormatComboBox.setSelectedIndex(defaultIndex);
            
            // 监听下拉框变化
            hexFormatComboBox.addActionListener(e -> updateHexFormat());
            
            hexFormatPanel.add(hexFormatComboBox);
            afterTopPanel.add(hexFormatPanel, BorderLayout.EAST);
        }
        
        afterTextArea = new JTextArea(afterText);
        afterTextArea.setLineWrap(true);
        afterTextArea.setWrapStyleWord(true);
        afterTextArea.setEditable(false);
        afterTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane afterScrollPane = new JScrollPane(afterTextArea);
        
        // 编码后长度显示（右对齐）
        JPanel afterBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        afterLengthLabel = new JLabel(I18n.length() + ": " + afterText.length());
        afterLengthLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        afterBottomPanel.add(afterLengthLabel);
        
        afterPanel.add(afterTopPanel, BorderLayout.NORTH);
        afterPanel.add(afterScrollPane, BorderLayout.CENTER);
        afterPanel.add(afterBottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(beforePanel);
        mainPanel.add(afterPanel);
        
        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton copyButton = new JButton(I18n.copyResult());
        copyButton.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        copyButton.setPreferredSize(new Dimension(90, 25));
        copyButton.addActionListener(e -> {
            String textToCopy = afterTextArea.getText(); // 复制当前显示的文本
            StringSelection stringSelection = new StringSelection(textToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, I18n.copySuccess(), I18n.success(), JOptionPane.INFORMATION_MESSAGE);
        });
        
        replaceButton = new JButton(I18n.replace());
        replaceButton.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        replaceButton.setPreferredSize(new Dimension(90, 25));
        replaceButton.addActionListener(e -> {
            resultText = afterTextArea.getText(); // 使用当前显示的文本（可能是格式化后的）
            replaced = true;
            dispose();
        });
        
        cancelButton = new JButton(I18n.cancel());
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        cancelButton.setPreferredSize(new Dimension(90, 25));
        cancelButton.addActionListener(e -> {
            replaced = false;
            dispose();
        });
        
        buttonPanel.add(replaceButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(copyButton);
        
        // 添加到主窗口
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public boolean isReplaced() {
        return replaced;
    }
    
    public String getResultText() {
        return resultText;
    }
    
    /**
     * 更新hex格式化
     */
    private void updateHexFormat() {
        if (hexFormatComboBox == null || hexFormatter == null || indexToFormatMapper == null) {
            return;
        }
        
        int selectedIndex = hexFormatComboBox.getSelectedIndex();
        EncoderUtils.HexFormat format = indexToFormatMapper.apply(selectedIndex);
        
        // 应用格式化
        String formattedText = hexFormatter.apply(originalAfterText, format);
        afterTextArea.setText(formattedText);
        
        // 更新编码后长度
        afterLengthLabel.setText(I18n.length() + ": " + formattedText.length());
    }
}
