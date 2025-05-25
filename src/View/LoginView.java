package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginView extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(30, 144, 255);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color HOVER_COLOR = new Color(65, 105, 225);

    private JComboBox<String> roleCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginView() {
        setTitle("Toko Online - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setLocationRelativeTo(null);
        setUndecorated(true);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(SECONDARY_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        cardPanel.setBounds(50, 50, 350, 450);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("🏬 Toko Online", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        cardPanel.add(titleLabel, gbc);

        roleCombo = new JComboBox<>(new String[]{"Admin", "Customer"});
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 1;
        cardPanel.add(roleCombo, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 2;
        cardPanel.add(createLabeledField("Username:", usernameField), gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 3;
        cardPanel.add(createLabeledField("Password:", passwordField), gbc);

        loginButton = createStyledButton("Login", PRIMARY_COLOR);
        registerButton = createStyledButton("Register", ACCENT_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        gbc.gridy = 4;
        cardPanel.add(buttonPanel, gbc);

        JButton exitButton = new JButton("✕");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(255, 69, 0));
        exitButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { exitButton.setBackground(new Color(220, 20, 60)); }
            @Override
            public void mouseExited(MouseEvent e) { exitButton.setBackground(new Color(255, 69, 0)); }
        });
        exitButton.setBounds(410, 10, 30, 30);
        backgroundPanel.add(exitButton);

        backgroundPanel.add(cardPanel);

        final Point[] dragPoint = {null};
        backgroundPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dragPoint[0] = e.getPoint(); }
        });
        backgroundPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - dragPoint[0].x, p.y + e.getY() - dragPoint[0].y);
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(HOVER_COLOR); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register", true);
        dialog.setSize(300, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Daftar Akun", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JComboBox<String> regRoleCombo = new JComboBox<>(new String[]{"Admin", "Customer"});
        regRoleCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(regRoleCombo, gbc);

        JTextField regUsernameField = new JTextField(15);
        regUsernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 2;
        panel.add(createLabeledField("Username:", regUsernameField), gbc);

        JPasswordField regPasswordField = new JPasswordField(15);
        regPasswordField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 3;
        panel.add(createLabeledField("Password:", regPasswordField), gbc);

        JButton regButton = createStyledButton("Daftar", ACCENT_COLOR);
        regButton.addActionListener(e -> {
            String role = (String) regRoleCombo.getSelectedItem();
            String username = regUsernameField.getText();
            String password = new String(regPasswordField.getPassword());
            roleCombo.setSelectedItem(role);
            usernameField.setText(username);
            passwordField.setText(password);
            dialog.dispose();
            registerButton.doClick();
        });

        JButton cancelButton = createStyledButton("Batal", PRIMARY_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(regButton);
        buttonPanel.add(cancelButton);
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public JComboBox<String> getRoleCombo() { return roleCombo; }
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
}