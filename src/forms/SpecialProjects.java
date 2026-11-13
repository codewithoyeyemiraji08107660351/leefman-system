/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.SpecialProjectDao;
import models.SpecialProject;
import models.ProjectSummary;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author oyeyemi
 */
public class SpecialProjects extends javax.swing.JFrame {

    private File designImageFile;
    private File clientImageFile;
    private File invoiceImageFile;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SpecialProject currentProject;
    private boolean isEditMode = false;
    private JPanel statsPanel;

    /**
     * Creates new form SpecialProjects
     */
    public SpecialProjects() {
        initComponents();
        loadLogo();
        generateProjectCode();
        loadCategories();
        loadDashboardSummary();
        loadAllProjects();
        btnSubmit.setEnabled(false);
        setupListeners();
        setLocationRelativeTo(null);
    }

    private void loadLogo() {
        try {
            java.net.URL logoUrl = getClass().getResource("/images/leefmancare-logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image scaledLogo = logoIcon.getImage().getScaledInstance(191, 187, Image.SCALE_SMOOTH);
                jLabelLogo.setIcon(new ImageIcon(scaledLogo));
                jLabelLogo.setText("");
            } else {
                File logoFile = new File("src/images/leefmancare-logo.png");
                if (logoFile.exists()) {
                    ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
                    Image scaledLogo = logoIcon.getImage().getScaledInstance(191, 187, Image.SCALE_SMOOTH);
                    jLabelLogo.setIcon(new ImageIcon(scaledLogo));
                    jLabelLogo.setText("");
                } else {
                    jLabelLogo.setText("Leefman");
                    jLabelLogo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
                    jLabelLogo.setForeground(java.awt.Color.WHITE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jLabelLogo.setText("Leefman");
            jLabelLogo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
            jLabelLogo.setForeground(java.awt.Color.WHITE);
        }
    }

    private void setupListeners() {
        java.awt.event.KeyAdapter calculator = new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateAndUpdateFields();
                validateFields();
            }
        };

        txtFabricCost.addKeyListener(calculator);
        txtThreadCost.addKeyListener(calculator);
        txtAccessoriesCost.addKeyListener(calculator);
        txtPackagingCost.addKeyListener(calculator);
        txtOtherMaterials.addKeyListener(calculator);
        txtTailoringLabour.addKeyListener(calculator);
        txtEmbroideryMachine.addKeyListener(calculator);
        txtWorkersPayment.addKeyListener(calculator);
        txtTransportation.addKeyListener(calculator);
        txtOtherExpenses.addKeyListener(calculator);
        txtTotalPrice.addKeyListener(calculator);
        txtAmountPaid.addKeyListener(calculator);
        txtQuantity.addKeyListener(calculator);
        txtCompletionPercentage.addKeyListener(calculator);

        cmbProjectStatus.addActionListener(e -> updateCompletionPercentage());

        jTableProjects.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableProjectsMouseClicked(evt);
            }
        });
    }

    private File chooseImageFile(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files (JPG, PNG)", "jpg", "jpeg", "png");
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (file.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this,
                        "Image size must be less than 10MB",
                        "File too large",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String name = file.getName().toLowerCase();
            if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))) {
                JOptionPane.showMessageDialog(this,
                        "Only JPG or PNG images are allowed",
                        "Invalid file type",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return file;
        }
        return null;
    }

    private void showImagePreview(File imageFile, javax.swing.JLabel label) {
        try {
            if (imageFile == null) {
                return;
            }
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(
                    label.getWidth() > 0 ? label.getWidth() : 150,
                    label.getHeight() > 0 ? label.getHeight() : 150,
                    Image.SCALE_SMOOTH
            );
            label.setIcon(new ImageIcon(scaled));
            label.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveImageToFolder(File sourceFile, String subFolder) throws IOException {
        String baseFolder = "uploads/special-event/" + subFolder + "/";
        File folder = new File(baseFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String newFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        File destination = new File(baseFolder + newFileName);
        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return baseFolder + newFileName;
    }

    private void browseDesignImage() {
        designImageFile = chooseImageFile("Select Design Image");
        if (designImageFile != null) {
            showImagePreview(designImageFile, lblDesignImage);
        }
    }

    private void browseClientImage() {
        clientImageFile = chooseImageFile("Select Client Reference Image");
        if (clientImageFile != null) {
            showImagePreview(clientImageFile, lblClientImage);
        }
    }

    private void browseInvoiceImage() {
        invoiceImageFile = chooseImageFile("Select Invoice Image");
        if (invoiceImageFile != null) {
            showImagePreview(invoiceImageFile, lblInvoiceImage);
        }
    }

    private void generateProjectCode() {
        if (txtProjectCode != null) {
            txtProjectCode.setText(SpecialProjectDao.generateProjectCode());
        }
    }

    private void loadCategories() {
        List<String> categories = SpecialProjectDao.getCategories();
        cmbCategory.removeAllItems();
        for (String category : categories) {
            cmbCategory.addItem(category);
        }
    }

    private void validateFields() {
        if (txtProjectName == null || txtClientName == null) {
            return;
        }

        String projectName = txtProjectName.getText().trim();
        String clientName = txtClientName.getText().trim();
        String description = txtDescription.getText().trim();
        String quantity = txtQuantity.getText().trim();
        String totalPrice = txtTotalPrice.getText().trim();

        btnSubmit.setEnabled(!projectName.isEmpty() && !clientName.isEmpty()
                && !description.isEmpty() && !quantity.isEmpty() && !totalPrice.isEmpty());
    }

    private void calculateAndUpdateFields() {
        try {
            double fabric = parseDouble(txtFabricCost.getText());
            double thread = parseDouble(txtThreadCost.getText());
            double accessories = parseDouble(txtAccessoriesCost.getText());
            double packaging = parseDouble(txtPackagingCost.getText());
            double otherMaterials = parseDouble(txtOtherMaterials.getText());
            double totalMaterial = fabric + thread + accessories + packaging + otherMaterials;
            txtTotalMaterialCost.setText(String.format("₦%.2f", totalMaterial));

            double labour = parseDouble(txtTailoringLabour.getText());
            double embroidery = parseDouble(txtEmbroideryMachine.getText());
            double workers = parseDouble(txtWorkersPayment.getText());
            double transport = parseDouble(txtTransportation.getText());
            double otherExpenses = parseDouble(txtOtherExpenses.getText());
            double totalProduction = labour + embroidery + workers + transport + otherExpenses;
            txtTotalProductionCost.setText(String.format("₦%.2f", totalProduction));

            double totalPrice = parseDouble(txtTotalPrice.getText());
            double amountPaid = parseDouble(txtAmountPaid.getText());
            double totalExpenses = totalMaterial + totalProduction;
            double netProfit = totalPrice - totalExpenses;
            double balance = totalPrice - amountPaid;

            txtTotalIncome.setText(String.format("₦%.2f", totalPrice));
            txtTotalExpenses.setText(String.format("₦%.2f", totalExpenses));
            txtNetProfit.setText(String.format("₦%.2f", netProfit));
            txtBalanceRemaining.setText(String.format("₦%.2f", balance));

            if (balance <= 0) {
                cmbPaymentStatus.setSelectedItem("Paid");
            } else if (amountPaid > 0) {
                cmbPaymentStatus.setSelectedItem("Partially Paid");
            } else {
                cmbPaymentStatus.setSelectedItem("Pending");
            }

        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateCompletionPercentage() {
        String status = (String) cmbProjectStatus.getSelectedItem();
        if (status != null) {
            switch (status) {
                case "Planning":
                    txtCompletionPercentage.setText("10");
                    break;
                case "Cutting":
                    txtCompletionPercentage.setText("25");
                    break;
                case "Sewing":
                    txtCompletionPercentage.setText("50");
                    break;
                case "Embroidery":
                    txtCompletionPercentage.setText("75");
                    break;
                case "Completed":
                case "Delivered":
                    txtCompletionPercentage.setText("100");
                    break;
                default:
                    txtCompletionPercentage.setText("0");
            }
        }
    }

    void loadDashboardSummary() {
        List<ProjectSummary> summaries = SpecialProjectDao.getProjectSummary();
        DefaultTableModel model = (DefaultTableModel) jTableSummary.getModel();
        model.setRowCount(0);

        // Calculate totals for stats cards
        int totalProjects = 0;
        int totalQuantity = 0;
        double totalRevenue = 0;
        double totalCost = 0;
        double totalProfit = 0;
        double pendingPayments = 0;
        int completedProjects = 0;

        for (ProjectSummary summary : summaries) {
            totalProjects += summary.getTotalProjects();
            totalQuantity += summary.getTotalQuantity();
            totalRevenue += summary.getTotalRevenue();
            totalCost += summary.getTotalCost();
            totalProfit += summary.getTotalProfit();
            pendingPayments += summary.getPendingPayments();
            completedProjects += summary.getCompletedProjects();

            model.addRow(new Object[]{
                summary.getCategoryName(),
                summary.getTotalProjects(),
                summary.getTotalQuantity(),
                summary.getTotalRevenue(),
                summary.getTotalCost(),
                summary.getTotalProfit(),
                summary.getPendingPayments(),
                summary.getCompletedProjects(),
                summary.getLargestProjectName() != null ? summary.getLargestProjectName() : "-",
                summary.getMostProfitableProject() != null ? summary.getMostProfitableProject() : "-"
            });
        }

        // Update stats cards
        updateStatsCards(totalProjects, totalQuantity, totalRevenue, totalCost, totalProfit, pendingPayments, completedProjects);
    }

    private void updateStatsCards(int totalProjects, int totalQuantity, double totalRevenue, 
                                   double totalCost, double totalProfit, double pendingPayments, int completedProjects) {
        // This will be called from the dashboard panel creation
        if (statsPanel != null) {
            statsPanel.removeAll();
            statsPanel.add(createStatCard("Total Projects", String.valueOf(totalProjects), new Color(27, 33, 56)));
            statsPanel.add(createStatCard("Total Quantity", String.valueOf(totalQuantity), new Color(0, 102, 204)));
            statsPanel.add(createStatCard("Total Revenue", String.format("₦%,.0f", totalRevenue), new Color(0, 153, 0)));
            statsPanel.add(createStatCard("Total Cost", String.format("₦%,.0f", totalCost), new Color(204, 102, 0)));
            statsPanel.add(createStatCard("Total Profit", String.format("₦%,.0f", totalProfit), new Color(0, 102, 51)));
            statsPanel.add(createStatCard("Pending Payments", String.format("₦%,.0f", pendingPayments), new Color(204, 0, 0)));
            statsPanel.add(createStatCard("Completed", String.valueOf(completedProjects), new Color(0, 150, 0)));
            statsPanel.revalidate();
            statsPanel.repaint();
        }
    }

    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new java.awt.BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelLabel.setForeground(Color.GRAY);
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(valueLabel, java.awt.BorderLayout.CENTER);
        card.add(labelLabel, java.awt.BorderLayout.SOUTH);

        return card;
    }

    void loadAllProjects() {
        List<SpecialProject> projects = SpecialProjectDao.getAllProjects();
        DefaultTableModel model = (DefaultTableModel) jTableProjects.getModel();
        model.setRowCount(0);

        for (SpecialProject project : projects) {
            model.addRow(new Object[]{
                project.getProjectCode(),
                project.getProjectName(),
                project.getCategoryName(),
                project.getClientName(),
                project.getQuantity(),
                String.format("₦%.2f", project.getTotalPrice()),
                String.format("₦%.2f", project.getAmountPaid()),
                String.format("₦%.2f", project.getBalanceRemaining()),
                project.getPaymentStatus(),
                project.getProjectStatus(),
                project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "",
                String.format("₦%.2f", project.getNetProfit())
            });
        }
    }

    private void clearForm() {
        txtProjectName.setText("");
        txtClientName.setText("");
        txtClientPhone.setText("");
        txtClientAddress.setText("");
        txtPersonInCharge.setText("");
        txtDescription.setText("");
        txtQuantity.setText("");
        txtDesignName.setText("");
        txtEmbroideryDetails.setText("");
        txtSpecialRequirements.setText("");
        txtTotalPrice.setText("");
        txtAmountPaid.setText("");
        txtFabricCost.setText("");
        txtThreadCost.setText("");
        txtAccessoriesCost.setText("");
        txtPackagingCost.setText("");
        txtOtherMaterials.setText("");
        txtTailoringLabour.setText("");
        txtEmbroideryMachine.setText("");
        txtWorkersPayment.setText("");
        txtTransportation.setText("");
        txtOtherExpenses.setText("");
        txtNotes.setText("");
        txtCompletionPercentage.setText("0");
        txtTotalMaterialCost.setText("₦0.00");
        txtTotalProductionCost.setText("₦0.00");
        txtTotalIncome.setText("₦0.00");
        txtTotalExpenses.setText("₦0.00");
        txtNetProfit.setText("₦0.00");
        txtBalanceRemaining.setText("₦0.00");

        cmbCategory.setSelectedIndex(0);
        cmbProductType.setSelectedIndex(0);
        cmbPaymentMethod.setSelectedIndex(0);
        cmbPaymentStatus.setSelectedIndex(0);
        cmbProjectStatus.setSelectedIndex(0);

        jDateEvent.setDate(null);
        jDateStart.setDate(new Date());
        jDateCompletion.setDate(null);

        lblDesignImage.setIcon(null);
        lblDesignImage.setText("Design");
        lblClientImage.setIcon(null);
        lblClientImage.setText("Client Ref");
        lblInvoiceImage.setIcon(null);
        lblInvoiceImage.setText("Invoice");

        designImageFile = null;
        clientImageFile = null;
        invoiceImageFile = null;

        isEditMode = false;
        currentProject = null;
        btnSubmit.setEnabled(false);
        generateProjectCode();
    }

    private void jTableProjectsMouseClicked(java.awt.event.MouseEvent evt) {
        int row = jTableProjects.getSelectedRow();
        if (row < 0) {
            return;
        }

        String projectCode = (String) jTableProjects.getValueAt(row, 0);
        SpecialProject project = SpecialProjectDao.getProjectByCode(projectCode);

        if (project != null) {
            ProjectDetailsDialog dialog = new ProjectDetailsDialog(this, project);
            dialog.setVisible(true);
            loadAllProjects();
            loadDashboardSummary();
        } else {
            JOptionPane.showMessageDialog(this, "Project not found!");
        }
    }

    private void saveProject() {
        if (txtProjectCode == null || txtProjectName == null || txtClientName == null) {
            JOptionPane.showMessageDialog(this, "Form not properly initialized. Please restart the application.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SpecialProject project = new SpecialProject();

        project.setProjectCode(txtProjectCode.getText());
        project.setProjectName(txtProjectName.getText().trim());
        String category = (String) cmbCategory.getSelectedItem();
        project.setCategoryId(SpecialProjectDao.getCategoryIdByName(category));
        project.setClientName(txtClientName.getText().trim());
        project.setClientPhone(txtClientPhone != null ? txtClientPhone.getText().trim() : "");
        project.setClientAddress(txtClientAddress != null ? txtClientAddress.getText().trim() : "");
        project.setPersonInCharge(txtPersonInCharge != null ? txtPersonInCharge.getText().trim() : "");

        project.setEventDate(jDateEvent != null ? jDateEvent.getDate() : null);
        project.setStartDate(jDateStart != null && jDateStart.getDate() != null ? jDateStart.getDate() : new Date());
        project.setCompletionDate(jDateCompletion != null ? jDateCompletion.getDate() : null);

        project.setDescription(txtDescription != null ? txtDescription.getText().trim() : "");
        try {
            project.setQuantity(txtQuantity != null ? Integer.parseInt(txtQuantity.getText().trim()) : 0);
        } catch (NumberFormatException e) {
            project.setQuantity(0);
        }
        project.setProductType(cmbProductType != null ? (String) cmbProductType.getSelectedItem() : "");
        project.setDesignName(txtDesignName != null ? txtDesignName.getText().trim() : "");
        project.setEmbroideryDetails(txtEmbroideryDetails != null ? txtEmbroideryDetails.getText().trim() : "");
        project.setSpecialRequirements(txtSpecialRequirements != null ? txtSpecialRequirements.getText().trim() : "");

        project.setTotalPrice(parseDouble(txtTotalPrice != null ? txtTotalPrice.getText() : ""));
        project.setAmountPaid(parseDouble(txtAmountPaid != null ? txtAmountPaid.getText() : ""));
        project.setPaymentMethod(cmbPaymentMethod != null ? (String) cmbPaymentMethod.getSelectedItem() : "");

        project.setFabricCost(parseDouble(txtFabricCost != null ? txtFabricCost.getText() : ""));
        project.setThreadCost(parseDouble(txtThreadCost != null ? txtThreadCost.getText() : ""));
        project.setAccessoriesCost(parseDouble(txtAccessoriesCost != null ? txtAccessoriesCost.getText() : ""));
        project.setPackagingCost(parseDouble(txtPackagingCost != null ? txtPackagingCost.getText() : ""));
        project.setOtherMaterialsCost(parseDouble(txtOtherMaterials != null ? txtOtherMaterials.getText() : ""));

        project.setTailoringLabourCost(parseDouble(txtTailoringLabour != null ? txtTailoringLabour.getText() : ""));
        project.setEmbroideryMachineCost(parseDouble(txtEmbroideryMachine != null ? txtEmbroideryMachine.getText() : ""));
        project.setWorkersPayment(parseDouble(txtWorkersPayment != null ? txtWorkersPayment.getText() : ""));
        project.setTransportationCost(parseDouble(txtTransportation != null ? txtTransportation.getText() : ""));
        project.setOtherExpenses(parseDouble(txtOtherExpenses != null ? txtOtherExpenses.getText() : ""));

        project.setProjectStatus(cmbProjectStatus != null ? (String) cmbProjectStatus.getSelectedItem() : "Planning");
        try {
            project.setCompletionPercentage(txtCompletionPercentage != null ? Integer.parseInt(txtCompletionPercentage.getText().trim()) : 0);
        } catch (NumberFormatException e) {
            project.setCompletionPercentage(0);
        }
        project.setNotes(txtNotes != null ? txtNotes.getText().trim() : "");

        try {
            if (designImageFile != null) {
                project.setDesignImagePath(saveImageToFolder(designImageFile, "designs"));
            }
            if (clientImageFile != null) {
                project.setClientReferenceImagePath(saveImageToFolder(clientImageFile, "clients"));
            }
            if (invoiceImageFile != null) {
                project.setInvoiceImagePath(saveImageToFolder(invoiceImageFile, "invoices"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving images: " + e.getMessage());
            return;
        }

        project.calculateTotals();

        boolean success;
        if (isEditMode && currentProject != null) {
            project.setProjectId(currentProject.getProjectId());
            success = SpecialProjectDao.updateProject(project);
        } else {
            success = SpecialProjectDao.saveProject(project);
        }

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Project saved successfully!\nProject Code: " + project.getProjectCode(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadAllProjects();
            loadDashboardSummary();
            generateProjectCode();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save project. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Leefman Record System - Special Projects");
        setSize(1200, 800);
        setLayout(new java.awt.BorderLayout());

        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.setBackground(new java.awt.Color(240, 240, 240));

        // Header Panel
        javax.swing.JPanel headerPanel = new javax.swing.JPanel();
        headerPanel.setBackground(new java.awt.Color(27, 33, 56));
        headerPanel.setPreferredSize(new java.awt.Dimension(1200, 60));
        headerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        javax.swing.JLabel titleLabel = new javax.swing.JLabel("Leefman Record System - Special Projects");
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(java.awt.Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, java.awt.BorderLayout.NORTH);

        // Content panel with tabs
        javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane();

        // ========== PROJECT ENTRY TAB ==========
        javax.swing.JPanel entryPanel = createEntryPanel();
        tabbedPane.addTab("Project Entry", entryPanel);

        // ========== ALL PROJECTS TAB ==========
        javax.swing.JPanel projectsPanel = createProjectsPanel();
        tabbedPane.addTab("All Projects", projectsPanel);

        // ========== DASHBOARD TAB ==========
        javax.swing.JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);

        mainPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        // Sidebar (Navigation)
        javax.swing.JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, java.awt.BorderLayout.WEST);

        add(mainPanel);
        pack();
    }

    private javax.swing.JPanel createEntryPanel() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Project Code
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Project Code:"), gbc);
        gbc.gridx = 1;
        txtProjectCode = new javax.swing.JTextField(15);
        txtProjectCode.setEditable(false);
        txtProjectCode.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtProjectCode, gbc);
        row++;

        // Project Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Project Name:"), gbc);
        gbc.gridx = 1;
        txtProjectName = new javax.swing.JTextField(30);
        panel.add(txtProjectName, gbc);
        row++;

        // Category
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Category:"), gbc);
        gbc.gridx = 1;
        cmbCategory = new javax.swing.JComboBox<>();
        panel.add(cmbCategory, gbc);
        row++;

        // Client Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Client Name:"), gbc);
        gbc.gridx = 1;
        txtClientName = new javax.swing.JTextField(25);
        panel.add(txtClientName, gbc);
        row++;

        // Client Phone
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Client Phone:"), gbc);
        gbc.gridx = 1;
        txtClientPhone = new javax.swing.JTextField(15);
        panel.add(txtClientPhone, gbc);
        row++;

        // Client Address
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Client Address:"), gbc);
        gbc.gridx = 1;
        txtClientAddress = new javax.swing.JTextArea(2, 30);
        JScrollPane addressScroll = new JScrollPane(txtClientAddress);
        panel.add(addressScroll, gbc);
        row++;

        // Event Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Event Date:"), gbc);
        gbc.gridx = 1;
        jDateEvent = new com.toedter.calendar.JDateChooser();
        jDateEvent.setPreferredSize(new java.awt.Dimension(150, 25));
        panel.add(jDateEvent, gbc);
        row++;

        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        jDateStart = new com.toedter.calendar.JDateChooser();
        jDateStart.setDate(new Date());
        jDateStart.setPreferredSize(new java.awt.Dimension(150, 25));
        panel.add(jDateStart, gbc);
        row++;

        // Completion Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Completion Date:"), gbc);
        gbc.gridx = 1;
        jDateCompletion = new com.toedter.calendar.JDateChooser();
        jDateCompletion.setPreferredSize(new java.awt.Dimension(150, 25));
        panel.add(jDateCompletion, gbc);
        row++;

        // Person In Charge
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Person In Charge:"), gbc);
        gbc.gridx = 1;
        txtPersonInCharge = new javax.swing.JTextField(25);
        panel.add(txtPersonInCharge, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Description:"), gbc);
        gbc.gridx = 1;
        txtDescription = new javax.swing.JTextArea(3, 30);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        panel.add(descScroll, gbc);
        row++;

        // Quantity
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        txtQuantity = new javax.swing.JTextField(10);
        panel.add(txtQuantity, gbc);
        row++;

        // Product Type
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Product Type:"), gbc);
        gbc.gridx = 1;
        cmbProductType = new javax.swing.JComboBox<>(new String[]{"Agbada", "Kaftan", "Shirt", "Uniform", "Cap", "Other"});
        panel.add(cmbProductType, gbc);
        row++;

        // Design Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Design Name:"), gbc);
        gbc.gridx = 1;
        txtDesignName = new javax.swing.JTextField(20);
        panel.add(txtDesignName, gbc);
        row++;

        // Embroidery Details
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Embroidery Details:"), gbc);
        gbc.gridx = 1;
        txtEmbroideryDetails = new javax.swing.JTextArea(3, 30);
        JScrollPane embScroll = new JScrollPane(txtEmbroideryDetails);
        panel.add(embScroll, gbc);
        row++;

        // Special Requirements
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Special Requirements:"), gbc);
        gbc.gridx = 1;
        txtSpecialRequirements = new javax.swing.JTextArea(3, 30);
        JScrollPane reqScroll = new JScrollPane(txtSpecialRequirements);
        panel.add(reqScroll, gbc);
        row++;

        // Financial Record Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        javax.swing.JLabel financialLabel = new javax.swing.JLabel("FINANCIAL RECORD");
        financialLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        financialLabel.setForeground(new java.awt.Color(204, 51, 0));
        panel.add(financialLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Total Price
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Total Price:"), gbc);
        gbc.gridx = 1;
        txtTotalPrice = new javax.swing.JTextField(15);
        panel.add(txtTotalPrice, gbc);
        row++;

        // Amount Paid
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Amount Paid:"), gbc);
        gbc.gridx = 1;
        txtAmountPaid = new javax.swing.JTextField(15);
        panel.add(txtAmountPaid, gbc);
        row++;

        // Payment Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Payment Status:"), gbc);
        gbc.gridx = 1;
        cmbPaymentStatus = new javax.swing.JComboBox<>(new String[]{"Paid", "Partially Paid", "Pending"});
        panel.add(cmbPaymentStatus, gbc);
        row++;

        // Payment Method
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Payment Method:"), gbc);
        gbc.gridx = 1;
        cmbPaymentMethod = new javax.swing.JComboBox<>(new String[]{"Cash", "Transfer", "POS", "Other"});
        panel.add(cmbPaymentMethod, gbc);
        row++;

        // Cost Tracking Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        javax.swing.JLabel costLabel = new javax.swing.JLabel("CAPITAL / COST TRACKING");
        costLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        costLabel.setForeground(new java.awt.Color(0, 0, 153));
        panel.add(costLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Materials Costs
        String[] materialLabels = {"  Fabric Cost:", "  Thread Cost:", "  Accessories Cost:", "  Packaging Cost:", "  Other Materials:"};
        javax.swing.JTextField[] materialFields = {txtFabricCost, txtThreadCost, txtAccessoriesCost, txtPackagingCost, txtOtherMaterials};
        for (int i = 0; i < materialLabels.length; i++) {
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new javax.swing.JLabel(materialLabels[i]), gbc);
            gbc.gridx = 1;
            materialFields[i] = new javax.swing.JTextField(15);
            panel.add(materialFields[i], gbc);
            row++;
        }
        txtFabricCost = materialFields[0];
        txtThreadCost = materialFields[1];
        txtAccessoriesCost = materialFields[2];
        txtPackagingCost = materialFields[3];
        txtOtherMaterials = materialFields[4];

        // Production Costs
        String[] prodLabels = {"  Tailoring Labour:", "  Embroidery Machine:", "  Workers Payment:", "  Transportation:", "  Other Expenses:"};
        javax.swing.JTextField[] prodFields = {txtTailoringLabour, txtEmbroideryMachine, txtWorkersPayment, txtTransportation, txtOtherExpenses};
        for (int i = 0; i < prodLabels.length; i++) {
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new javax.swing.JLabel(prodLabels[i]), gbc);
            gbc.gridx = 1;
            prodFields[i] = new javax.swing.JTextField(15);
            panel.add(prodFields[i], gbc);
            row++;
        }
        txtTailoringLabour = prodFields[0];
        txtEmbroideryMachine = prodFields[1];
        txtWorkersPayment = prodFields[2];
        txtTransportation = prodFields[3];
        txtOtherExpenses = prodFields[4];

        // Total Material Cost
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Total Material Cost:"), gbc);
        gbc.gridx = 1;
        txtTotalMaterialCost = new javax.swing.JTextField(15);
        txtTotalMaterialCost.setEditable(false);
        txtTotalMaterialCost.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtTotalMaterialCost, gbc);
        row++;

        // Total Production Cost
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Total Production Cost:"), gbc);
        gbc.gridx = 1;
        txtTotalProductionCost = new javax.swing.JTextField(15);
        txtTotalProductionCost.setEditable(false);
        txtTotalProductionCost.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtTotalProductionCost, gbc);
        row++;

        // Profit Calculation Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        javax.swing.JLabel profitLabel = new javax.swing.JLabel("PROFIT CALCULATION");
        profitLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        profitLabel.setForeground(new java.awt.Color(0, 102, 0));
        panel.add(profitLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Total Income
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Total Income:"), gbc);
        gbc.gridx = 1;
        txtTotalIncome = new javax.swing.JTextField(15);
        txtTotalIncome.setEditable(false);
        txtTotalIncome.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtTotalIncome, gbc);
        row++;

        // Total Expenses
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Total Expenses:"), gbc);
        gbc.gridx = 1;
        txtTotalExpenses = new javax.swing.JTextField(15);
        txtTotalExpenses.setEditable(false);
        txtTotalExpenses.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtTotalExpenses, gbc);
        row++;

        // Net Profit
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Net Profit:"), gbc);
        gbc.gridx = 1;
        txtNetProfit = new javax.swing.JTextField(15);
        txtNetProfit.setEditable(false);
        txtNetProfit.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtNetProfit, gbc);
        row++;

        // Balance Remaining
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("  Balance Remaining:"), gbc);
        gbc.gridx = 1;
        txtBalanceRemaining = new javax.swing.JTextField(15);
        txtBalanceRemaining.setEditable(false);
        txtBalanceRemaining.setBackground(new java.awt.Color(230, 230, 230));
        panel.add(txtBalanceRemaining, gbc);
        row++;

        // Project Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Project Status:"), gbc);
        gbc.gridx = 1;
        cmbProjectStatus = new javax.swing.JComboBox<>(new String[]{"Planning", "Cutting", "Sewing", "Embroidery", "Completed", "Delivered"});
        panel.add(cmbProjectStatus, gbc);
        row++;

        // Completion Percentage
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Completion %:"), gbc);
        gbc.gridx = 1;
        txtCompletionPercentage = new javax.swing.JTextField(5);
        panel.add(txtCompletionPercentage, gbc);
        row++;

        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        txtNotes = new javax.swing.JTextArea(3, 30);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        panel.add(notesScroll, gbc);
        row++;

        // Buttons
        gbc.gridx = 1; gbc.gridy = row;
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnSubmit = new javax.swing.JButton("Submit");
        btnSubmit.setBackground(new java.awt.Color(0, 102, 0));
        btnSubmit.setForeground(java.awt.Color.WHITE);
        btnSubmit.addActionListener(e -> saveProject());
        buttonPanel.add(btnSubmit);
        btnClear = new javax.swing.JButton("Clear");
        btnClear.setBackground(new java.awt.Color(204, 0, 0));
        btnClear.setForeground(java.awt.Color.WHITE);
        btnClear.addActionListener(e -> clearForm());
        buttonPanel.add(btnClear);
        panel.add(buttonPanel, gbc);
        row++;

        // Images
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new javax.swing.JLabel("Images:"), gbc);
        gbc.gridx = 1;
        javax.swing.JPanel imagePanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblDesignImage = new javax.swing.JLabel();
        lblDesignImage.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        lblDesignImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblDesignImage.setText("Design");
        lblDesignImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imagePanel.add(lblDesignImage);
        javax.swing.JButton btnBrowseDesign = new javax.swing.JButton("Browse");
        btnBrowseDesign.addActionListener(e -> browseDesignImage());
        imagePanel.add(btnBrowseDesign);

        lblClientImage = new javax.swing.JLabel();
        lblClientImage.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        lblClientImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblClientImage.setText("Client Ref");
        lblClientImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imagePanel.add(lblClientImage);
        javax.swing.JButton btnBrowseClient = new javax.swing.JButton("Browse");
        btnBrowseClient.addActionListener(e -> browseClientImage());
        imagePanel.add(btnBrowseClient);

        lblInvoiceImage = new javax.swing.JLabel();
        lblInvoiceImage.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        lblInvoiceImage.setPreferredSize(new java.awt.Dimension(100, 100));
        lblInvoiceImage.setText("Invoice");
        lblInvoiceImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imagePanel.add(lblInvoiceImage);
        javax.swing.JButton btnBrowseInvoice = new javax.swing.JButton("Browse");
        btnBrowseInvoice.addActionListener(e -> browseInvoiceImage());
        imagePanel.add(btnBrowseInvoice);
        panel.add(imagePanel, gbc);
        row++;

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(panel);
        scrollPane.setBorder(null);
        javax.swing.JPanel container = new javax.swing.JPanel(new java.awt.BorderLayout());
        container.add(scrollPane, java.awt.BorderLayout.CENTER);

        return container;
    }

    private javax.swing.JPanel createProjectsPanel() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        javax.swing.JPanel searchPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        searchPanel.add(new javax.swing.JLabel("Search:"));
        javax.swing.JTextField searchField = new javax.swing.JTextField(20);
        searchPanel.add(searchField);
        javax.swing.JButton searchBtn = new javax.swing.JButton("Search");
        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                DefaultTableModel model = (DefaultTableModel) jTableProjects.getModel();
                model.setRowCount(0);
                List<SpecialProject> projects = SpecialProjectDao.getAllProjects();
                for (SpecialProject project : projects) {
                    if (project.getProjectName().toLowerCase().contains(searchText.toLowerCase())
                            || project.getProjectCode().toLowerCase().contains(searchText.toLowerCase())
                            || project.getClientName().toLowerCase().contains(searchText.toLowerCase())) {
                        model.addRow(new Object[]{
                            project.getProjectCode(),
                            project.getProjectName(),
                            project.getCategoryName(),
                            project.getClientName(),
                            project.getQuantity(),
                            String.format("₦%.2f", project.getTotalPrice()),
                            String.format("₦%.2f", project.getAmountPaid()),
                            String.format("₦%.2f", project.getBalanceRemaining()),
                            project.getPaymentStatus(),
                            project.getProjectStatus(),
                            project.getStartDate() != null ? dateFormat.format(project.getStartDate()) : "",
                            String.format("₦%.2f", project.getNetProfit())
                        });
                    }
                }
            } else {
                loadAllProjects();
            }
        });
        searchPanel.add(searchBtn);
        panel.add(searchPanel, java.awt.BorderLayout.NORTH);

        String[] columns = {"Project Code", "Project Name", "Category", "Client", "Qty", "Price", "Paid",
            "Balance", "Payment Status", "Project Status", "Start Date", "Profit"};
        jTableProjects = new javax.swing.JTable(new DefaultTableModel(columns, 0));
        jScrollPaneProjects = new javax.swing.JScrollPane(jTableProjects);
        panel.add(jScrollPaneProjects, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel bottomPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnRefresh = new javax.swing.JButton("Refresh");
        btnRefresh.addActionListener(e -> loadAllProjects());
        bottomPanel.add(btnRefresh);
        panel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        return panel;
    }

    private javax.swing.JPanel createDashboardPanel() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new java.awt.Color(240, 240, 240));

        // Top Panel with Title
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        topPanel.setBackground(new java.awt.Color(240, 240, 240));

        javax.swing.JLabel titleLabel = new javax.swing.JLabel("Project Dashboard Summary");
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        titleLabel.setForeground(new java.awt.Color(27, 33, 56));
        topPanel.add(titleLabel, java.awt.BorderLayout.WEST);

        // Stats Cards
        statsPanel = new javax.swing.JPanel(new java.awt.GridLayout(1, 7, 10, 0));
        statsPanel.setBackground(new java.awt.Color(240, 240, 240));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Add empty stats cards (will be updated in loadDashboardSummary)
        statsPanel.add(createStatCard("Total Projects", "0", new Color(27, 33, 56)));
        statsPanel.add(createStatCard("Total Quantity", "0", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("Total Revenue", "₦0", new Color(0, 153, 0)));
        statsPanel.add(createStatCard("Total Cost", "₦0", new Color(204, 102, 0)));
        statsPanel.add(createStatCard("Total Profit", "₦0", new Color(0, 102, 51)));
        statsPanel.add(createStatCard("Pending Payments", "₦0", new Color(204, 0, 0)));
        statsPanel.add(createStatCard("Completed", "0", new Color(0, 150, 0)));

        topPanel.add(statsPanel, java.awt.BorderLayout.SOUTH);
        panel.add(topPanel, java.awt.BorderLayout.NORTH);

        // Table
        String[] columns = {"Category", "Projects", "Qty", "Revenue", "Cost", "Profit",
            "Pending", "Completed", "Largest Project", "Most Profitable"};
        jTableSummary = new javax.swing.JTable(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        jTableSummary.setRowHeight(30);
        jTableSummary.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        jTableSummary.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jTableSummary.getTableHeader().setBackground(new java.awt.Color(27, 33, 56));
        jTableSummary.getTableHeader().setForeground(java.awt.Color.BLACK);
        jTableSummary.getTableHeader().setReorderingAllowed(false);
        jTableSummary.setGridColor(new java.awt.Color(200, 200, 200));
        jTableSummary.setShowGrid(true);
        jTableSummary.setIntercellSpacing(new java.awt.Dimension(5, 5));

        // Alternating row colors
        jTableSummary.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? java.awt.Color.WHITE : new java.awt.Color(248, 248, 248));
                }
                return c;
            }
        });

        // Currency renderer
        jTableSummary.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Double) {
                    setText(String.format("₦%,.2f", (Double) value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    super.setValue(value);
                }
            }
        });

        jScrollPaneSummary = new javax.swing.JScrollPane(jTableSummary);
        jScrollPaneSummary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(jScrollPaneSummary, java.awt.BorderLayout.CENTER);

        return panel;
    }

    private javax.swing.JPanel createSidebar() {
        javax.swing.JPanel sidebar = new javax.swing.JPanel();
        sidebar.setBackground(new java.awt.Color(27, 33, 56));
        sidebar.setPreferredSize(new java.awt.Dimension(200, 800));
        sidebar.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 10, 5, 10);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Logo
        jLabelLogo = new javax.swing.JLabel();
        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setPreferredSize(new java.awt.Dimension(191, 187));
        gbc.gridy = 0;
        sidebar.add(jLabelLogo, gbc);

        // Navigation buttons
        String[] buttons = {"Dashboard", "Customers", "Tailors", "Orders", "Lost", "Materials & Stock",
            "Transaction History", "Special Event", "Logout"};
        for (String text : buttons) {
            javax.swing.JButton btn = new javax.swing.JButton(text);
            btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            btn.setForeground(java.awt.Color.WHITE);
            btn.setBackground(new java.awt.Color(27, 33, 56));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            btn.setPreferredSize(new java.awt.Dimension(180, 35));

            if (text.equals("Logout")) {
                btn.setBackground(new java.awt.Color(204, 0, 0));
                btn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                            "Logout", JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        dispose();
                        new Login().setVisible(true);
                    }
                });
            } else {
                btn.addActionListener(e -> {
                    dispose();
                    if (text.equals("Customers")) {
                        new Customers().setVisible(true);
                    } else if (text.equals("Tailors")) {
                        new Tailors().setVisible(true);
                    } else if (text.equals("Orders")) {
                        new Order().setVisible(true);
                    } else if (text.equals("Lost")) {
                        new Loss().setVisible(true);
                    } else if (text.equals("Materials & Stock")) {
                        new Materials().setVisible(true);
                    } else if (text.equals("Transaction History")) {
                        new TransactionHistory().setVisible(true);
                    } else if (text.equals("Special Event")) {
                        new SpecialProjects().setVisible(true);
                    }else if(text.equals("Dashboard")){
                        new GeneralDashboard().setVisible(true);
                    }
                });
            }
            gbc.gridy++;
            sidebar.add(btn, gbc);
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
            new SpecialProjects().setVisible(true);
        });
    }

    // Variables declaration
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbProductType;
    private javax.swing.JComboBox<String> cmbPaymentMethod;
    private javax.swing.JComboBox<String> cmbPaymentStatus;
    private javax.swing.JComboBox<String> cmbProjectStatus;
    private com.toedter.calendar.JDateChooser jDateEvent;
    private com.toedter.calendar.JDateChooser jDateStart;
    private com.toedter.calendar.JDateChooser jDateCompletion;
    private javax.swing.JLabel lblDesignImage;
    private javax.swing.JLabel lblClientImage;
    private javax.swing.JLabel lblInvoiceImage;
    private javax.swing.JTextField txtProjectCode;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtClientName;
    private javax.swing.JTextField txtClientPhone;
    private javax.swing.JTextArea txtClientAddress;
    private javax.swing.JTextField txtPersonInCharge;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtDesignName;
    private javax.swing.JTextArea txtEmbroideryDetails;
    private javax.swing.JTextArea txtSpecialRequirements;
    private javax.swing.JTextField txtTotalPrice;
    private javax.swing.JTextField txtAmountPaid;
    private javax.swing.JTextField txtBalanceRemaining;
    private javax.swing.JTextField txtFabricCost;
    private javax.swing.JTextField txtThreadCost;
    private javax.swing.JTextField txtAccessoriesCost;
    private javax.swing.JTextField txtPackagingCost;
    private javax.swing.JTextField txtOtherMaterials;
    private javax.swing.JTextField txtTotalMaterialCost;
    private javax.swing.JTextField txtTailoringLabour;
    private javax.swing.JTextField txtEmbroideryMachine;
    private javax.swing.JTextField txtWorkersPayment;
    private javax.swing.JTextField txtTransportation;
    private javax.swing.JTextField txtOtherExpenses;
    private javax.swing.JTextField txtTotalProductionCost;
    private javax.swing.JTextField txtTotalIncome;
    private javax.swing.JTextField txtTotalExpenses;
    private javax.swing.JTextField txtNetProfit;
    private javax.swing.JTextField txtCompletionPercentage;
    private javax.swing.JTextArea txtNotes;
    private javax.swing.JTable jTableProjects;
    private javax.swing.JTable jTableSummary;
    private javax.swing.JScrollPane jScrollPaneProjects;
    private javax.swing.JScrollPane jScrollPaneSummary;
    private javax.swing.JLabel jLabelLogo;
    // End of variables declaration
}