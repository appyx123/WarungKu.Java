package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class AdminView extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(30, 144, 255);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color HOVER_COLOR = new Color(65, 105, 225);

    private DefaultTableModel productTableModel;
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel transactionTableModel;
    private DefaultTableModel supplierTableModel;
    private DefaultTableModel recycleBinTableModel;

    private JTextField productIdField, productNameField, productPriceField, productStockField, productCategoryIdField;
    private JTextField categoryIdField, categoryNameField;
    private JTextField transactionIdField, transactionProductIdField, transactionQuantityField;
    private JTextField supplierIdField, supplierNameField, supplierContactField, supplierAddressField;
    private JTextField recycleBinIdField;
    private JTextArea reportArea;
    private JButton productAddButton, productUpdateButton, productDeleteButton;
    private JButton categoryAddButton, categoryUpdateButton, categoryDeleteButton;
    private JButton transactionAddButton, transactionDeleteButton;
    private JButton supplierAddButton, supplierUpdateButton, supplierDeleteButton;
    private JButton generateReportButton, restoreButton, logoutButton;

    public AdminView() {
        setTitle("Toko Online - Admin Dashboard");
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

        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        backgroundPanel.add(headerLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        tabbedPane.setBackground(SECONDARY_COLOR);

        JPanel productPanel = createProductPanel();
        tabbedPane.addTab("Products", productPanel);

        JPanel categoryPanel = createCategoryPanel();
        tabbedPane.addTab("Categories", categoryPanel);

        JPanel transactionPanel = createTransactionPanel();
        tabbedPane.addTab("Transactions", transactionPanel);

        JPanel supplierPanel = createSupplierPanel();
        tabbedPane.addTab("Suppliers", supplierPanel);

        JPanel recycleBinPanel = createRecycleBinPanel();
        tabbedPane.addTab("Recycle Bin", recycleBinPanel);

        JPanel reportPanel = createReportPanel();
        tabbedPane.addTab("Reports", reportPanel);

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
        productNameField = new JTextField(10);
        productPriceField = new JTextField(10);
        productStockField = new JTextField(10);
        productCategoryIdField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productStockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Category ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productCategoryIdField, gbc);

        productAddButton = createStyledButton("Add", PRIMARY_COLOR);
        productUpdateButton = createStyledButton("Update", PRIMARY_COLOR);
        productDeleteButton = createStyledButton("Delete", ACCENT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(productAddButton);
        buttonPanel.add(productUpdateButton);
        buttonPanel.add(productDeleteButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        categoryTableModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0);
        JTable categoryTable = new JTable(categoryTableModel);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        categoryIdField = new JTextField(10);
        categoryNameField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryNameField, gbc);

        categoryAddButton = createStyledButton("Add", PRIMARY_COLOR);
        categoryUpdateButton = createStyledButton("Update", PRIMARY_COLOR);
        categoryDeleteButton = createStyledButton("Delete", ACCENT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(categoryAddButton);
        buttonPanel.add(categoryUpdateButton);
        buttonPanel.add(categoryDeleteButton);

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

        transactionTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Products", "Total", "Username"}, 0);
        JTable transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        transactionIdField = new JTextField(10);
        transactionProductIdField = new JTextField(10);
        transactionQuantityField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Transaction ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(transactionIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(transactionProductIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(transactionQuantityField, gbc);

        transactionAddButton = createStyledButton("Add", PRIMARY_COLOR);
        transactionDeleteButton = createStyledButton("Delete", ACCENT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(transactionAddButton);
        buttonPanel.add(transactionDeleteButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        supplierTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact", "Address"}, 0);
        JTable supplierTable = new JTable(supplierTableModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        supplierIdField = new JTextField(10);
        supplierNameField = new JTextField(10);
        supplierContactField = new JTextField(10);
        supplierAddressField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(supplierIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(supplierNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(supplierContactField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(supplierAddressField, gbc);

        supplierAddButton = createStyledButton("Add", PRIMARY_COLOR);
        supplierUpdateButton = createStyledButton("Update", PRIMARY_COLOR);
        supplierDeleteButton = createStyledButton("Delete", ACCENT_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(supplierAddButton);
        buttonPanel.add(supplierUpdateButton);
        buttonPanel.add(supplierDeleteButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRecycleBinPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        recycleBinTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Products", "Total", "Username"}, 0);
        JTable recycleBinTable = new JTable(recycleBinTableModel);
        JScrollPane scrollPane = new JScrollPane(recycleBinTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        recycleBinIdField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Transaction ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(recycleBinIdField, gbc);

        restoreButton = createStyledButton("Restore", PRIMARY_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(restoreButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        reportArea = new JTextArea();
        reportArea.setFont(new Font("Arial", Font.PLAIN, 12));
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        generateReportButton = createStyledButton("Generate Report", PRIMARY_COLOR);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateReportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

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
    public DefaultTableModel getCategoryTableModel() { return categoryTableModel; }
    public DefaultTableModel getTransactionTableModel() { return transactionTableModel; }
    public DefaultTableModel getSupplierTableModel() { return supplierTableModel; }
    public DefaultTableModel getRecycleBinTableModel() { return recycleBinTableModel; }
    public JTextField getProductIdField() { return productIdField; }
    public JTextField getProductNameField() { return productNameField; }
    public JTextField getProductPriceField() { return productPriceField; }
    public JTextField getProductStockField() { return productStockField; }
    public JTextField getProductCategoryIdField() { return productCategoryIdField; }
    public JTextField getCategoryIdField() { return categoryIdField; }
    public JTextField getCategoryNameField() { return categoryNameField; }
    public JTextField getTransactionIdField() { return transactionIdField; }
    public JTextField getTransactionProductIdField() { return transactionProductIdField; }
    public JTextField getTransactionQuantityField() { return transactionQuantityField; }
    public JTextField getSupplierIdField() { return supplierIdField; }
    public JTextField getSupplierNameField() { return supplierNameField; }
    public JTextField getSupplierContactField() { return supplierContactField; }
    public JTextField getSupplierAddressField() { return supplierAddressField; }
    public JTextField getRecycleBinIdField() { return recycleBinIdField; }
    public JTextArea getReportArea() { return reportArea; }
    public JButton getProductAddButton() { return productAddButton; }
    public JButton getProductUpdateButton() { return productUpdateButton; }
    public JButton getProductDeleteButton() { return productDeleteButton; }
    public JButton getCategoryAddButton() { return categoryAddButton; }
    public JButton getCategoryUpdateButton() { return categoryUpdateButton; }
    public JButton getCategoryDeleteButton() { return categoryDeleteButton; }
    public JButton getTransactionAddButton() { return transactionAddButton; }
    public JButton getTransactionDeleteButton() { return transactionDeleteButton; }
    public JButton getSupplierAddButton() { return supplierAddButton; }
    public JButton getSupplierUpdateButton() { return supplierUpdateButton; }
    public JButton getSupplierDeleteButton() { return supplierDeleteButton; }
    public JButton getGenerateReportButton() { return generateReportButton; }
    public JButton getRestoreButton() { return restoreButton; }
    public JButton getLogoutButton() { return logoutButton; }
}