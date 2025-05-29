package com.warungkupos.view.admin;

import com.warungkupos.model.Category;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.InputValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryManagementPanel extends JPanel {

    private JTextField categoryIdField;
    private JTextField categoryNameField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private JTable categoryTable;
    private DefaultTableModel tableModel;

    public CategoryManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Form Kategori", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_PRIMARY_BLUE
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("ID Kategori:"), gbc);
        categoryIdField = new JTextField(5);
        categoryIdField.setEditable(false);
        categoryIdField.setBackground(new Color(230,230,230));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(categoryIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Nama Kategori:"), gbc);
        categoryNameField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        formPanel.add(categoryNameField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addButton = new JButton("Tambah");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Hapus");
        clearButton = new JButton("Clear Form");

        styleButton(addButton, AppConstants.COLOR_PRIMARY_BLUE);
        styleButton(updateButton, new Color(255, 165, 0));
        styleButton(deleteButton, AppConstants.COLOR_ERROR_RED);
        styleButton(clearButton, Color.GRAY);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);

        String[] columnNames = {"ID", "Nama Kategori"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; 
                return String.class;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryTable.setRowHeight(25);
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        categoryTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        categoryTable.getTableHeader().setForeground(Color.WHITE);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setAutoCreateRowSorter(true);

        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);  
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(350); 

        JScrollPane scrollPane = new JScrollPane(categoryTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void clearForm() {
        categoryIdField.setText("");
        categoryNameField.setText("");
        categoryNameField.requestFocus();
        categoryTable.clearSelection();
    }

    public void displayCategories(List<Category> categories) {
        tableModel.setRowCount(0); 
        if (categories != null) {
            for (Category category : categories) {
                tableModel.addRow(new Object[]{category.getId(), category.getName()});
            }
        }
    }

    // @Override // <--- ANOTASI INI DIHAPUS
    public Category getCategoryFromForm() {
        String name = categoryNameField.getText().trim();

        if (!InputValidator.isNonEmpty(name, "Nama Kategori")) {
            showMessage("Nama Kategori tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            categoryNameField.requestFocusInWindow();
            return null;
        }

        Category category = new Category();
        category.setName(name);
        if (!categoryIdField.getText().isEmpty()) {
            try {
                category.setId(Integer.parseInt(categoryIdField.getText()));
            } catch (NumberFormatException e) {
            }
        }
        return category;
    }

    public void fillFormFromSelectedTableRow() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow != -1) {
            categoryIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            categoryNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        }
    }

    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getClearButton() { return clearButton; }
    public JTable getCategoryTable() { return categoryTable; }
    public JTextField getCategoryIdField() { return categoryIdField; }
    public JTextField getCategoryNameField() { return categoryNameField; } 

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}