package forms;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;

public class GeneralDashboard extends javax.swing.JFrame {

    // Stats Cards Labels
    private JLabel lblTotalCustomers, lblTotalTailors, lblTotalOrders;
    private JLabel lblTotalRevenue, lblTotalCommission, lblTotalLoan;
    private JLabel lblTotalSpecialProjects, lblSpecialProjectsProfit;
    private JLabel lblTotalExtras, lblTotalExtrasCost;
    private JLabel lblTotalMaterials, lblTotalMaterialStock;
    
    // Order Status - Stats Cards (separate labels for cards)
    private JLabel lblPendingOrdersCard;
    private JLabel lblInProgressOrdersCard;
    private JLabel lblCompletedOrdersCard;
    
    // Order Status - Distribution Panel (separate labels for status boxes)
    private JLabel lblPendingOrdersStatus;
    private JLabel lblInProgressOrdersStatus;
    private JLabel lblCompletedOrdersStatus;
    private JLabel lblDeliveredOrdersStatus;
    
    private JTable recentOrdersTable, recentProjectsTable;
    private DefaultTableModel recentOrdersModel, recentProjectsModel;
    private JPanel statsPanel;
    private JLabel jLabelLogo;

    private static final Color COLOR_PRIMARY = new Color(27, 33, 56);
    private static final Color COLOR_SUCCESS = new Color(0, 153, 0);
    private static final Color COLOR_WARNING = new Color(204, 102, 0);
    private static final Color COLOR_DANGER = new Color(204, 0, 0);
    private static final Color COLOR_INFO = new Color(0, 102, 204);

    public GeneralDashboard() {
        initComponents();
        loadLogo();
        loadDashboardData();
        setLocationRelativeTo(null);
    }

    private void loadLogo() {
        try {
            java.net.URL logoUrl = getClass().getResource("/images/leefmancare-logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaledLogo = logoIcon.getImage().getScaledInstance(180, 150, Image.SCALE_SMOOTH);
                jLabelLogo.setIcon(new ImageIcon(scaledLogo));
                jLabelLogo.setText("");
            } else {
                File logoFile = new File("src/images/leefmancare-logo.png");
                if (logoFile.exists()) {
                    ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
                    Image scaledLogo = logoIcon.getImage().getScaledInstance(180, 150, Image.SCALE_SMOOTH);
                    jLabelLogo.setIcon(new ImageIcon(scaledLogo));
                    jLabelLogo.setText("");
                } else {
                    jLabelLogo.setText("Leefman");
                    jLabelLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    jLabelLogo.setForeground(Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jLabelLogo.setText("Leefman");
            jLabelLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
            jLabelLogo.setForeground(Color.WHITE);
        }
    }

    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                loadStatistics();
                loadRecentOrders();
                loadRecentProjects();
                return null;
            }

            @Override
            protected void done() {
                repaint();
                revalidate();
            }
        };
        worker.execute();
    }

    private void loadStatistics() {
        try {
            // Customer count
            String sqlCustomers = "SELECT COUNT(*) FROM customers";
            lblTotalCustomers.setText(String.valueOf((int) getSingleValue(sqlCustomers)));

            // Tailor count
            String sqlTailors = "SELECT COUNT(*) FROM tailors";
            lblTotalTailors.setText(String.valueOf((int) getSingleValue(sqlTailors)));

            // Orders count
            String sqlOrders = "SELECT COUNT(*) FROM orders";
            lblTotalOrders.setText(String.valueOf((int) getSingleValue(sqlOrders)));

            // Total Revenue - Sum of all order costs
            String sqlRevenue = "SELECT COALESCE(SUM(cost), 0) FROM orders";
            lblTotalRevenue.setText(String.format("₦%,.0f", getSingleValue(sqlRevenue)));

            // Total Commission - Sum of tailor_commission from orders
            String sqlCommission = "SELECT COALESCE(SUM(tailor_commission), 0) FROM orders";
            lblTotalCommission.setText(String.format("₦%,.0f", getSingleValue(sqlCommission)));

            // Total Loan - Sum of ALL loans (including paid and unpaid)
            String sqlLoan = "SELECT COALESCE(SUM(amount), 0) FROM tailor_loans";
            lblTotalLoan.setText(String.format("₦%,.0f", getSingleValue(sqlLoan)));

            // Order status counts
            String sqlPending = "SELECT COUNT(*) FROM orders WHERE status = 'Pending'";
            int pendingCount = (int) getSingleValue(sqlPending);
            
            String sqlInProgress = "SELECT COUNT(*) FROM orders WHERE status = 'In Progress'";
            int inProgressCount = (int) getSingleValue(sqlInProgress);
            
            String sqlCompleted = "SELECT COUNT(*) FROM orders WHERE status = 'Completed' OR status = 'Delivered'";
            int completedCount = (int) getSingleValue(sqlCompleted);
            
            String sqlDelivered = "SELECT COUNT(*) FROM orders WHERE status = 'Delivered'";
            int deliveredCount = (int) getSingleValue(sqlDelivered);

            // Update Stats Cards labels
            lblPendingOrdersCard.setText(String.valueOf(pendingCount));
            lblInProgressOrdersCard.setText(String.valueOf(inProgressCount));
            lblCompletedOrdersCard.setText(String.valueOf(completedCount));
            
            // Update Status Distribution labels
            lblPendingOrdersStatus.setText(String.valueOf(pendingCount));
            lblInProgressOrdersStatus.setText(String.valueOf(inProgressCount));
            lblCompletedOrdersStatus.setText(String.valueOf(completedCount));
            lblDeliveredOrdersStatus.setText(String.valueOf(deliveredCount));

            // Special Projects
            String sqlProjects = "SELECT COUNT(*) FROM special_projects";
            lblTotalSpecialProjects.setText(String.valueOf((int) getSingleValue(sqlProjects)));

            String sqlProjectProfit = "SELECT COALESCE(SUM(net_profit), 0) FROM special_projects";
            lblSpecialProjectsProfit.setText(String.format("₦%,.0f", getSingleValue(sqlProjectProfit)));

            // Loss
            String sqlExtras = "SELECT COUNT(*) FROM extras";
            lblTotalExtras.setText(String.valueOf((int) getSingleValue(sqlExtras)));

            String sqlExtrasCost = "SELECT COALESCE(SUM(cost), 0) FROM extras";
            lblTotalExtrasCost.setText(String.format("₦%,.0f", getSingleValue(sqlExtrasCost)));

            // Materials
            String sqlMaterials = "SELECT COUNT(*) FROM materials";
            lblTotalMaterials.setText(String.valueOf((int) getSingleValue(sqlMaterials)));

            // Stock Quantity - Sum of all material quantities
            String sqlStock = "SELECT COALESCE(SUM(quantity), 0) FROM materials";
            lblTotalMaterialStock.setText(String.valueOf((int) getSingleValue(sqlStock)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getSingleValue(String sql) {
        try (Connection con = dbConnection.ConnectionProvider.getCon(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void loadRecentOrders() {
        recentOrdersModel.setRowCount(0);
        String sql = "SELECT o.order_id, c.name, o.cost, o.amount_paid, o.balance, o.status, o.date_received "
                + "FROM orders o "
                + "LEFT JOIN customers c ON o.customer_id = c.customer_id "
                + "ORDER BY o.created_at DESC LIMIT 10";

        try (Connection con = dbConnection.ConnectionProvider.getCon(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                recentOrdersModel.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getString("name") != null ? rs.getString("name") : "N/A",
                    String.format("₦%,.2f", rs.getDouble("cost")),
                    String.format("₦%,.2f", rs.getDouble("amount_paid")),
                    String.format("₦%,.2f", rs.getDouble("balance")),
                    rs.getString("status"),
                    sdf.format(rs.getDate("date_received"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecentProjects() {
        recentProjectsModel.setRowCount(0);
        String sql = "SELECT project_code, project_name, client_name, total_price, net_profit, project_status "
                + "FROM special_projects "
                + "ORDER BY created_at DESC LIMIT 10";

        try (Connection con = dbConnection.ConnectionProvider.getCon(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                recentProjectsModel.addRow(new Object[]{
                    rs.getString("project_code"),
                    rs.getString("project_name"),
                    rs.getString("client_name"),
                    String.format("₦%,.2f", rs.getDouble("total_price")),
                    String.format("₦%,.2f", rs.getDouble("net_profit")),
                    rs.getString("project_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(COLOR_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Leefman Record System - Dashboard");
        setSize(1350, 900);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(1350, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Leefman Record System - General Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm").format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(200, 200, 200));
        headerPanel.add(dateLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Panel with Scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Stats Cards - Row 1
        JPanel statsRow1 = new JPanel(new GridLayout(1, 5, 12, 12));
        statsRow1.setOpaque(false);

        lblTotalCustomers = new JLabel("0");
        lblTotalTailors = new JLabel("0");
        lblTotalOrders = new JLabel("0");
        lblTotalSpecialProjects = new JLabel("0");
        lblTotalExtras = new JLabel("0");

        statsRow1.add(createStatCard("Total Customers", lblTotalCustomers, COLOR_INFO));
        statsRow1.add(createStatCard("Total Tailors", lblTotalTailors, COLOR_INFO));
        statsRow1.add(createStatCard("Total Orders", lblTotalOrders, COLOR_INFO));
        statsRow1.add(createStatCard("Special Projects", lblTotalSpecialProjects, new Color(0, 102, 204)));
        statsRow1.add(createStatCard("Loss", lblTotalExtras, new Color(204, 51, 0)));

        mainPanel.add(statsRow1);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Stats Cards - Row 2 (Order Status Cards) - Using separate labels
        JPanel statsRow2 = new JPanel(new GridLayout(1, 5, 12, 12));
        statsRow2.setOpaque(false);

        // Initialize separate labels for stats cards
        lblInProgressOrdersCard = new JLabel("0");
        lblCompletedOrdersCard = new JLabel("0");
        lblPendingOrdersCard = new JLabel("0");
        lblTotalMaterials = new JLabel("0");
        lblTotalMaterialStock = new JLabel("0");

        statsRow2.add(createStatCard("In Progress", lblInProgressOrdersCard, COLOR_INFO));
        statsRow2.add(createStatCard("Completed Orders", lblCompletedOrdersCard, COLOR_SUCCESS));
        statsRow2.add(createStatCard("Pending Orders", lblPendingOrdersCard, COLOR_WARNING));
        statsRow2.add(createStatCard("Materials", lblTotalMaterials, new Color(0, 102, 153)));
        statsRow2.add(createStatCard("Total Stock Quantity", lblTotalMaterialStock, new Color(0, 153, 102)));

        mainPanel.add(statsRow2);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Order Status Chart Panel - Using separate labels for status boxes
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartPanel.setOpaque(false);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Order Status
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel statusTitle = new JLabel("Order Status Distribution");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusTitle.setForeground(COLOR_PRIMARY);
        statusPanel.add(statusTitle, BorderLayout.NORTH);

        JPanel statusGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        statusGrid.setOpaque(false);
        statusGrid.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Initialize separate labels for status boxes
        lblPendingOrdersStatus = new JLabel("0");
        lblInProgressOrdersStatus = new JLabel("0");
        lblCompletedOrdersStatus = new JLabel("0");
        lblDeliveredOrdersStatus = new JLabel("0");

        // Use separate labels for status distribution
        statusGrid.add(createStatusBox("Pending", lblPendingOrdersStatus, COLOR_WARNING));
        statusGrid.add(createStatusBox("In Progress", lblInProgressOrdersStatus, COLOR_INFO));
        statusGrid.add(createStatusBox("Completed", lblCompletedOrdersStatus, COLOR_SUCCESS));
        statusGrid.add(createStatusBox("Delivered", lblDeliveredOrdersStatus, new Color(0, 150, 0)));

        statusPanel.add(statusGrid, BorderLayout.CENTER);
        chartPanel.add(statusPanel);

        // Financial Summary
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel summaryTitle = new JLabel("Financial Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTitle.setForeground(COLOR_PRIMARY);
        summaryPanel.add(summaryTitle, BorderLayout.NORTH);

        JPanel summaryGrid = new JPanel(new GridLayout(3, 2, 15, 15));
        summaryGrid.setOpaque(false);
        summaryGrid.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Initialize the labels before using them
        lblTotalRevenue = new JLabel("₦0");
        lblTotalCommission = new JLabel("₦0");
        lblTotalLoan = new JLabel("₦0");
        lblSpecialProjectsProfit = new JLabel("₦0");
        lblTotalExtrasCost = new JLabel("₦0");

        summaryGrid.add(createMetricBox("Total Revenue", lblTotalRevenue, COLOR_SUCCESS));
        summaryGrid.add(createMetricBox("Total Commission", lblTotalCommission, new Color(153, 51, 153)));
        summaryGrid.add(createMetricBox("Total Loan", lblTotalLoan, COLOR_DANGER));
        summaryGrid.add(createMetricBox("Projects Profit", lblSpecialProjectsProfit, COLOR_SUCCESS));
        summaryGrid.add(createMetricBox("Extras Cost", lblTotalExtrasCost, COLOR_DANGER));

        summaryPanel.add(summaryGrid, BorderLayout.CENTER);
        chartPanel.add(summaryPanel);

        mainPanel.add(chartPanel);

        // Recent Tables Section
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setOpaque(false);

        // Recent Orders
        JPanel ordersPanel = createSectionPanel("Recent Orders");
        String[] orderColumns = {"Order ID", "Customer", "Cost", "Paid", "Balance", "Status", "Date"};
        recentOrdersModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recentOrdersTable = new JTable(recentOrdersModel);
        styleTable(recentOrdersTable);
        JScrollPane ordersScroll = new JScrollPane(recentOrdersTable);
        ordersScroll.setPreferredSize(new Dimension(600, 200));
        ordersPanel.add(ordersScroll, BorderLayout.CENTER);
        tablesPanel.add(ordersPanel);

        // Recent Projects
        JPanel projectsPanel = createSectionPanel("Recent Special Projects");
        String[] projectColumns = {"Code", "Name", "Client", "Total Price", "Profit", "Status"};
        recentProjectsModel = new DefaultTableModel(projectColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recentProjectsTable = new JTable(recentProjectsModel);
        styleTable(recentProjectsTable);
        JScrollPane projectsScroll = new JScrollPane(recentProjectsTable);
        projectsScroll.setPreferredSize(new Dimension(600, 200));
        projectsPanel.add(projectsScroll, BorderLayout.CENTER);
        tablesPanel.add(projectsPanel);

        mainPanel.add(tablesPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh Dashboard");
        btnRefresh.setBackground(COLOR_PRIMARY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> loadDashboardData());
        buttonPanel.add(btnRefresh);

        JButton btnGoToOrders = new JButton("Go to Orders");
        btnGoToOrders.setBackground(new Color(0, 102, 204));
        btnGoToOrders.setForeground(Color.WHITE);
        btnGoToOrders.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGoToOrders.addActionListener(e -> {
            dispose();
            new Order().setVisible(true);
        });
        buttonPanel.add(btnGoToOrders);

        JButton btnGoToTailors = new JButton("Go to Tailors");
        btnGoToTailors.setBackground(new Color(0, 153, 0));
        btnGoToTailors.setForeground(Color.WHITE);
        btnGoToTailors.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGoToTailors.addActionListener(e -> {
            dispose();
            new Tailors().setVisible(true);
        });
        buttonPanel.add(btnGoToTailors);

        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createStatusBox(String label, JLabel valueLabel, Color color) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(Color.GRAY);
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        box.add(valueLabel, BorderLayout.CENTER);
        box.add(labelLabel, BorderLayout.SOUTH);

        return box;
    }

    private JPanel createMetricBox(String label, JLabel valueLabel, Color color) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelLabel.setForeground(Color.GRAY);
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        box.add(valueLabel, BorderLayout.CENTER);
        box.add(labelLabel, BorderLayout.SOUTH);

        return box;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(5, 5));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                if (value instanceof String) {
                    String val = (String) value;
                    if (val.contains("Pending")) {
                        c.setForeground(COLOR_WARNING);
                    } else if (val.contains("Completed") || val.contains("Delivered")) {
                        c.setForeground(COLOR_SUCCESS);
                    } else if (val.contains("In Progress")) {
                        c.setForeground(COLOR_INFO);
                    }
                }
                return c;
            }
        });
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COLOR_PRIMARY);
        sidebar.setPreferredSize(new Dimension(200, 900));
        sidebar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Logo
        jLabelLogo = new JLabel();
        jLabelLogo.setHorizontalAlignment(SwingConstants.CENTER);
        jLabelLogo.setPreferredSize(new Dimension(180, 150));
        gbc.gridy = 0;
        sidebar.add(jLabelLogo, gbc);

        // Navigation buttons
        String[][] buttons = {
            {"Dashboard", "dashboard"},
            {"Customers", "customers"},
            {"Tailors", "tailors"},
            {"Orders", "orders"},
            {"Special Projects", "projects"},
            {"Loss", "loss"},
            {"Materials", "materials"},
            {"Transaction History", "history"},
            {"Logout", "logout"}
        };

        for (String[] btn : buttons) {
            JButton button = new JButton(btn[0]);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(COLOR_PRIMARY);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setPreferredSize(new Dimension(180, 40));

            if (btn[1].equals("logout")) {
                button.setBackground(new Color(204, 0, 0));
                button.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                            "Logout", JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        dispose();
                        new Login().setVisible(true);
                    }
                });
            } else {
                final String target = btn[1];
                button.addActionListener(e -> {
                    dispose();
                    switch (target) {
                        case "customers":
                            new Customers().setVisible(true);
                            break;
                        case "tailors":
                            new Tailors().setVisible(true);
                            break;
                        case "orders":
                            new Order().setVisible(true);
                            break;
                        case "projects":
                            new SpecialProjects().setVisible(true);
                            break;
                        case "extras":
                            new Loss().setVisible(true);
                            break;
                        case "materials":
                            new Materials().setVisible(true);
                            break;
                        case "history":
                            new TransactionHistory().setVisible(true);
                            break;
                        case "dashboard":
                            new GeneralDashboard().setVisible(true);
                            break;
                    }
                });
            }
            gbc.gridy++;
            sidebar.add(button, gbc);
        }

        return sidebar;
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            new GeneralDashboard().setVisible(true);
        });
    }
}