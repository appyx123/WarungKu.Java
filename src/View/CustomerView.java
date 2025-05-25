package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CustomerView extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(30, 144, 255);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color HOVER_COLOR = new Color(65, 105, 225);

    private DefaultTableModel productTableModel;
    private DefaultTableModel transactionTableModel;
    private JTextField productIdField;
    private JTextField quantityField;
    private JButton buyButton;
    private JButton logoutButton;

    public CustomerView() {
        setTitle("Toko Online - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
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
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(backgroundPanel);

        JLabel headerLabel = new JLabel("Customer Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        backgroundPanel.add(headerLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        tabbedPane.setBackground(SECONDARY_COLOR);

        JPanel productPanel = createProductPanel();
        tabbedPane.addTab("Products", productPanel);

        JPanel transactionPanel = createTransactionPanel();
        tabbedPane.addTab("My Transactions", transactionPanel);

        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);

        logoutButton = createStyledButton("Logout", ACCENT_COLOR);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutButton);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

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
        exitButton.setBounds(760, 10, 30, 30);
        backgroundPanel.add(exitButton);

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

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        productTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Stock", "Category ID"}, 0);
        JTable productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productIdField = new JTextField(10);
        quantityField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        buyButton = createStyledButton("Buy", PRIMARY_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(buyButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        transactionTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Products", "Total"}, 0);
        JTable transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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

    public DefaultTableModel getProductTableModel() { return productTableModel; }
    public DefaultTableModel getTransactionTableModel() { return transactionTableModel; }
    public JTextField getProductIdField() { return productIdField; }
    public JTextField getQuantityField() { return quantityField; }
    public JButton getBuyButton() { return buyButton; }
    public JButton getLogoutButton() { return logoutButton; }
}