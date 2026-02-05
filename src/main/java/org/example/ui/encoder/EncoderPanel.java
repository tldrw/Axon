package org.example.ui.encoder;

import org.example.i18n.I18n;
import org.example.ui.encoder.processors.Base64Processor;
import org.example.ui.encoder.processors.URLProcessor;
import org.example.ui.encoder.processors.HtmlEntityProcessor;
import org.example.ui.encoder.processors.UnicodeProcessor;
import org.example.ui.encoder.processors.Utf8Processor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EncoderPanel extends JPanel {

    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JList<String> processorList;
    private final JPanel optionsContainer;
    private final CardLayout cardLayout;
    private final List<EncoderProcessor> processors;

    public EncoderPanel() {
        setLayout(new BorderLayout());

        // 1. Initialize Processors
        processors = new ArrayList<>();
        processors.add(new Base64Processor());
        processors.add(new URLProcessor());
        processors.add(new HtmlEntityProcessor());
        processors.add(new UnicodeProcessor());
        processors.add(new Utf8Processor());
        // Add more later

        // 2. Left Side: List
        String[] names = processors.stream().map(EncoderProcessor::getName).toArray(String[]::new);
        processorList = new JList<>(names);
        processorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        processorList.setSelectedIndex(0);
        processorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = processorList.getSelectedIndex();
                if (index >= 0 && index < processors.size()) {
                    showOptions(processors.get(index).getName());
                }
            }
        });
        JScrollPane listScroll = new JScrollPane(processorList);
        listScroll.setPreferredSize(new Dimension(150, 0));

        // 3. Right Side: Input/Options/Output
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Input
        inputArea = new JTextArea(5, 40);
        inputArea.setLineWrap(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder(I18n.input()));

        // Output
        outputArea = new JTextArea(5, 40);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder(I18n.output()));

        // Middle: Options + Buttons
        JPanel middlePanel = new JPanel(new BorderLayout());
        
        // Options Container (CardLayout)
        cardLayout = new CardLayout();
        optionsContainer = new JPanel(cardLayout);
        optionsContainer.setBorder(BorderFactory.createTitledBorder(I18n.options()));
        
        for (EncoderProcessor p : processors) {
            optionsContainer.add(p.getOptionsPanel(), p.getName());
        }

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton encodeBtn = new JButton(I18n.encodeBtn());
        JButton decodeBtn = new JButton(I18n.decodeBtn());
        JButton copyBtn = new JButton(I18n.copy());
        JButton clearBtn = new JButton(I18n.clear());
        
        // Style buttons
        Dimension btnSize = new Dimension(85, 26);
        encodeBtn.setPreferredSize(btnSize);
        decodeBtn.setPreferredSize(btnSize);
        copyBtn.setPreferredSize(btnSize);
        clearBtn.setPreferredSize(btnSize);

        encodeBtn.addActionListener(e -> doProcess(true));
        decodeBtn.addActionListener(e -> doProcess(false));
        
        copyBtn.addActionListener(e -> {
            String content = outputArea.getText();
            if (content != null && !content.isEmpty()) {
                java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(content);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                JOptionPane.showMessageDialog(this, I18n.copySuccess(), I18n.success(), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        buttonPanel.add(encodeBtn);
        buttonPanel.add(decodeBtn);
        buttonPanel.add(copyBtn);
        buttonPanel.add(clearBtn);

        middlePanel.add(optionsContainer, BorderLayout.CENTER);
        middlePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Assembly Right Panel
        // Use GridBag or simpler split to allocate space. 
        // Let's split Input (Top) vs Middle+Output (Bottom) or simply 3 rows.
        // Actually, Input/Output should grow. Options is fixed height usually.
        // A JSplitPane vertical for Input / (Middle+Output) is good.
        
        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(middlePanel, BorderLayout.NORTH);
        bottomHalf.add(outputScroll, BorderLayout.CENTER);
        
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, bottomHalf);
        verticalSplit.setResizeWeight(0.5); // Even split
        
        rightPanel.add(verticalSplit, BorderLayout.CENTER);

        // 4. Main Split
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, rightPanel);
        mainSplit.setDividerLocation(150);

        add(mainSplit, BorderLayout.CENTER);
        
        // Init state
        showOptions(names[0]);
    }

    private void showOptions(String name) {
        cardLayout.show(optionsContainer, name);
    }

    private void doProcess(boolean isEncode) {
        int index = processorList.getSelectedIndex();
        if (index < 0) return;
        
        EncoderProcessor processor = processors.get(index);
        String input = inputArea.getText();
        String result = processor.process(input, isEncode);
        outputArea.setText(result);
    }

    public void setInputText(String text) {
        inputArea.setText(text);
    }
    
    // Optional: Method to select specific processor by name or type
    public void selectProcessor(String name) {
        // Implement if needed for smart routing
    }
}
