package com.warungkupos.view.admin;

import com.warungkupos.model.User;
import com.warungkupos.util.AppConstants;
import com.warungkupos.util.InputValidator; 

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private JTextField userIdField;
    private JTextField usernameField;
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton resetPasswordButton;
    private JButton clearButton;

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Form Pengguna", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), AppConstants.COLOR_PRIMARY_BLUE
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("ID User:"), gbc);
        userIdField = new JTextField(5);
        userIdField.setEditable(false);
        userIdField.setBackground(new Color(230,230,230));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(userIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        fullNameField = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Password (Baru/Reset):"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        String[] roles = {AppConstants.ROLE_CUSTOMER, AppConstants.ROLE_ADMIN};
        roleComboBox = new JComboBox<>(roles);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        formPanel.add(roleComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addButton = new JButton("Tambah User");
        updateButton = new JButton("Update User");
        deleteButton = new JButton("Hapus User");
        resetPasswordButton = new JButton("Reset Password");
        clearButton = new JButton("Clear Form");

        styleButton(addButton, AppConstants.COLOR_PRIMARY_BLUE);
        styleButton(updateButton, new Color(255, 165, 0));
        styleButton(deleteButton, AppConstants.COLOR_ERROR_RED);
        styleButton(resetPasswordButton, new Color(0, 150, 136));
        styleButton(clearButton, Color.GRAY);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);

        String[] columnNames = {"ID", "Username", "Nama Lengkap", "Role"}; 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; 
                return String.class;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(AppConstants.COLOR_PRIMARY_BLUE);
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoCreateRowSorter(true);

        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);  
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); 
        userTable.getColumnModel().getColumn(2).setPreferredWidth(200); 
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100); 

        JScrollPane scrollPane = new JScrollPane(userTable);

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
        userIdField.setText("");
        usernameField.setText("");
        fullNameField.setText("");
        passwordField.setText(""); 
        roleComboBox.setSelectedItem(AppConstants.ROLE_CUSTOMER); 
        usernameField.setEditable(true); 
        usernameField.requestFocus();
        userTable.clearSelection();
    }

    public void displayUsers(List<User> users) {
        tableModel.setRowCount(0); 
        if (users != null) {
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole()
                });
            }
        }
    }

    public User getUserFromForm() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // Validasi di Sisi View
        if (userIdField.getText().isEmpty()) { // Mode Tambah
            if (!InputValidator.isNonEmpty(username, "Username")) {
                showMessage("Username tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                usernameField.requestFocusInWindow();
                return null;
            }
            if (!InputValidator.hasMinLength(username, 3)) {
                showMessage("Username minimal 3 karakter.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                usernameField.requestFocusInWindow();
                return null;
            }
            if (!InputValidator.isNonEmpty(password, "Password")) {
                showMessage("Password tidak boleh kosong untuk user baru.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocusInWindow();
                return null;
            }
            if (!InputValidator.hasMinLength(password, 6)) {
                showMessage("Password minimal 6 karakter.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocusInWindow();
                return null;
            }
        } else { // Mode Update, password opsional
             if (!InputValidator.isNonEmpty(username, "Username")) {
                showMessage("Username tidak boleh kosong.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                usernameField.requestFocusInWindow();
                return null;
            }
            if (!password.isEmpty() && !InputValidator.hasMinLength(password, 6)) {
                showMessage("Password baru minimal 6 karakter.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocusInWindow();
                return null;
            }
        }
        if (role == null || (!role.equals(AppConstants.ROLE_ADMIN) && !role.equals(AppConstants.ROLE_CUSTOMER))) {
            showMessage("Role tidak valid.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        User user = new User();
        if (!userIdField.getText().isEmpty()) {
            try {
                user.setId(Integer.parseInt(userIdField.getText()));
            } catch (NumberFormatException e) {
                // Abaikan jika ID tidak valid, mungkin sedang operasi tambah
            }
        }
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPassword(password); 
        user.setRole(role);
        
        return user;
    }

    public void fillFormFromSelectedTableRow() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            userIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            fullNameField.setText(tableModel.getValueAt(selectedRow, 2) != null ? tableModel.getValueAt(selectedRow, 2).toString() : "");
            roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
            passwordField.setText(""); 
            usernameField.setEditable(false); 
        }
    }

    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getResetPasswordButton() { return resetPasswordButton; }
    public JButton getClearButton() { return clearButton; }
    public JTable getUserTable() { return userTable; }
    public JTextField getUserIdField() { return userIdField; }
    public JTextField getUsernameField() { return usernameField; } 
    public JPasswordField getPasswordField() { return passwordField; } 
    
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}