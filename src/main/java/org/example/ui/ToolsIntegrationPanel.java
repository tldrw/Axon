package org.example.ui;

import org.example.config.ConfigManager;
import org.example.i18n.I18n;
import org.example.model.ToolConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ToolsIntegrationPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final ConfigManager configManager;

    public ToolsIntegrationPanel() {
        this.configManager = ConfigManager.getInstance();
        
        setLayout(new BorderLayout());

        // --- Upper Panel: Tools Configuration ---
        JPanel configPanel = new JPanel(new BorderLayout(10, 10));
        configPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create Table Model
        // Columns: Enabled, Name, Command, Path (Directory), Preview
        String[] columnNames = {
                I18n.enabled(),
                I18n.name(),
                I18n.command(),
                I18n.path(),
                I18n.previewBeforeExecution()
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        // Create Table
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Create Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton(I18n.add());
        JButton editButton = new JButton(I18n.edit());
        JButton removeButton = new JButton(I18n.remove());

        // Align buttons
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, addButton.getPreferredSize().height));
        editButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, editButton.getPreferredSize().height));
        removeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, removeButton.getPreferredSize().height));

        // Add Action Listeners
        addButton.addActionListener(this::onAdd);
        editButton.addActionListener(this::onEdit);
        removeButton.addActionListener(this::onRemove);

        buttonsPanel.add(addButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(editButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(removeButton);
        buttonsPanel.add(Box.createVerticalGlue());

        configPanel.add(buttonsPanel, BorderLayout.WEST);
        configPanel.add(scrollPane, BorderLayout.CENTER);

        // Init Data
        initData();

        // --- Lower Panel: Template Variables Help ---
        JPanel helpPanel = new JPanel(new BorderLayout(10, 10));
        helpPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                I18n.templateVariables(), 
                TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, 
                new Font(Font.SANS_SERIF, Font.BOLD, 12)
        ));

        // Example URL
        String exampleUrl = "https://www.example.com:8080/api/v1/user?id=123&type=admin";
        JLabel exampleLabel = new JLabel(I18n.example() + ": " + exampleUrl);
        exampleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        exampleLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Variables Table
        String[] helpColumns = {I18n.variable(), I18n.description(), I18n.previewResult()};
        Object[][] helpData = {
                {"%H", I18n.varHost(), "www.example.com"},
                {"%P", I18n.varPort(), "8080"},
                {"%S", I18n.varProtocol(), "https"},
                {"%M", I18n.varMethod(), "GET"},
                {"%U", I18n.varUrlFull(), exampleUrl},
                {"%E", I18n.varUrlNoQuery(), "https://www.example.com:8080/api/v1/user"}, 
                {"%B", I18n.varUrlBase(), "https://www.example.com:8080"},
                {"%L", I18n.varUrlPath(), "/api/v1/user"}, 
                {"%Q", I18n.varUrlQuery(), "id=123&type=admin"},
                {"%R", I18n.varFile(), "/tmp/req.txt"},
                {"%C", I18n.varCookie(), "JSESSIONID=..."},
                {"%A", I18n.varUserAgent(), "Mozilla/5.0..."}
        };
        
        JTable helpTable = new JTable(new DefaultTableModel(helpData, helpColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        helpTable.setFillsViewportHeight(true);
        helpTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        helpTable.getColumnModel().getColumn(0).setMaxWidth(80);
        helpTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane helpScroll = new JScrollPane(helpTable);
        helpPanel.add(exampleLabel, BorderLayout.NORTH);
        helpPanel.add(helpScroll, BorderLayout.CENTER);
        
        // --- Main Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, configPanel, helpPanel);
        splitPane.setResizeWeight(0.7); // Top panel gets 70% of space
        splitPane.setDividerLocation(400); // Updated initial height guess for 7:3 ratio

        add(splitPane, BorderLayout.CENTER);
    }
    
    private void initData() {
        configManager.loadConfig();
        List<ToolConfig> configs = configManager.getToolConfigs();
        
        if (configs == null || configs.isEmpty()) {
            if (!configManager.hasConfig()) {
                // Add default tools if no config file exists
                addDefaultRow(true, "SQLMap", "python3 sqlmap.py -r %R --batch", "D:/Tools/sqlmap/", true);
                addDefaultRow(true, "Nuclei", "nuclei -u %U", "D:/Tools/nuclei/", true);
                saveTableData();
            }
        } else {
            for (ToolConfig config : configs) {
                tableModel.addRow(new Object[]{
                    config.isEnabled(),
                    config.getName(),
                    config.getCommand(),
                    config.getPath(),
                    config.isShowPreview()
                });
            }
        }
    }
    
    private void addDefaultRow(boolean enabled, String name, String command, String path, boolean preview) {
        tableModel.addRow(new Object[]{enabled, name, command, path, preview});
    }

    private void onAdd(ActionEvent e) {
        showToolDialog(null, -1);
    }

    private void onEdit(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, I18n.selectItem(), I18n.warning(), JOptionPane.WARNING_MESSAGE);
            return;
        }
        showToolDialog(selectedRow);
    }

    private void onRemove(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, I18n.selectItem(), I18n.warning(), JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, I18n.confirmDelete(), I18n.warning(), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            saveTableData();
        }
    }

    private void showToolDialog(int rowIndex) {
        Object[] rowData = new Object[5];
        for (int i = 0; i < 5; i++) {
            rowData[i] = tableModel.getValueAt(rowIndex, i);
        }
        showToolDialog(rowData, rowIndex);
    }

    private void showToolDialog(Object[] existingData, int rowIndex) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), rowIndex == -1 ? I18n.add() : I18n.edit(), true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Fields
        JCheckBox enabledCheck = new JCheckBox();
        JTextField nameField = new JTextField(40);
        JTextField commandField = new JTextField(40);
        JTextField pathField = new JTextField(40);
        JCheckBox previewCheck = new JCheckBox();

        // Populate if editing
        if (existingData != null) {
            enabledCheck.setSelected((Boolean) existingData[0]);
            nameField.setText((String) existingData[1]);
            commandField.setText((String) existingData[2]);
            pathField.setText((String) existingData[3]);
            previewCheck.setSelected((Boolean) existingData[4]);
        } else {
            // Defaults for new item
            enabledCheck.setSelected(true);
            previewCheck.setSelected(true);
        }

        // Add to form
        int row = 0;
        
        // Enabled
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel(I18n.enabled() + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(enabledCheck, gbc);
        row++;

        // Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel(I18n.name() + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nameField, gbc);
        row++;

        // Command
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel(I18n.command() + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(commandField, gbc);
        row++;

        // Path / Directory
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel(I18n.directory() + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(pathField, gbc);
        row++;

        // Preview
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel(I18n.previewBeforeExecution() + ":"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(previewCheck, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton(I18n.confirm());
        JButton cancelButton = new JButton(I18n.cancel());

        confirmButton.addActionListener(e -> {
            Object[] newData = {
                    enabledCheck.isSelected(),
                    nameField.getText(),
                    commandField.getText(),
                    pathField.getText(),
                    previewCheck.isSelected()
            };

            if (rowIndex == -1) {
                tableModel.addRow(newData);
            } else {
                for (int i = 0; i < newData.length; i++) {
                    tableModel.setValueAt(newData[i], rowIndex, i);
                }
            }
            saveTableData();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void saveTableData() {
        List<ToolConfig> configs = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            configs.add(new ToolConfig(
                (Boolean) tableModel.getValueAt(i, 0),
                (String) tableModel.getValueAt(i, 1),
                (String) tableModel.getValueAt(i, 2),
                (String) tableModel.getValueAt(i, 3),
                (Boolean) tableModel.getValueAt(i, 4)
            ));
        }
        configManager.setToolConfigs(configs);
        configManager.saveConfig();
    }
}
