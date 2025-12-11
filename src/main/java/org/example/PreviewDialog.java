package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionListener;
import org.example.i18n.I18n;

public class PreviewDialog extends JDialog {
    private JTextArea beforeTextArea;
    private JTextArea afterTextArea;
    private JButton replaceButton;
    private JButton cancelButton;
    private boolean replaced = false;
    private String resultText;
    
    public PreviewDialog(Frame parent, String title, String beforeText, String afterText) {
        super(parent, title, true);
        
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
        beforePanel.add(beforeLabel, BorderLayout.NORTH);
        beforePanel.add(beforeScrollPane, BorderLayout.CENTER);
        
        // 右侧：编码后
        JPanel afterPanel = new JPanel(new BorderLayout(5, 5));
        JLabel afterLabel = new JLabel(I18n.after());
        afterLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        afterTextArea = new JTextArea(afterText);
        afterTextArea.setLineWrap(true);
        afterTextArea.setWrapStyleWord(true);
        afterTextArea.setEditable(false);
        afterTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane afterScrollPane = new JScrollPane(afterTextArea);
        afterPanel.add(afterLabel, BorderLayout.NORTH);
        afterPanel.add(afterScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(beforePanel);
        mainPanel.add(afterPanel);
        
        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton copyButton = new JButton(I18n.copyResult());
        copyButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        copyButton.setPreferredSize(new Dimension(100, 35));
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(afterText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(this, I18n.copySuccess(), I18n.success(), JOptionPane.INFORMATION_MESSAGE);
        });
        
        replaceButton = new JButton(I18n.replace());
        replaceButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        replaceButton.setPreferredSize(new Dimension(100, 35));
        replaceButton.addActionListener(e -> {
            resultText = afterText;
            replaced = true;
            dispose();
        });
        
        cancelButton = new JButton(I18n.cancel());
        cancelButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 35));
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
}
