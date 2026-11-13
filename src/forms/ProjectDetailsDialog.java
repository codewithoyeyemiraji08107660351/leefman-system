package forms;

import dao.SpecialProjectDao;
import models.SpecialProject;
import models.ProjectHistoryLog;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import utils.ProjectPDFGenerator;

public class ProjectDetailsDialog extends JDialog {

    private SpecialProject project;
    private JComboBox<String> cmbStatus;
    private JTextField txtCompletion;
    private JTextArea txtNotes;
    private JLabel lblProjectCode, lblProjectName, lblCategory, lblClientName;
    private JLabel lblClientPhone, lblClientAddress, lblPersonInCharge;
    private JLabel lblEventDate, lblStartDate, lblCompletionDate;
    private JLabel lblQuantity, lblProductType, lblDesignName;
    private JLabel lblTotalPrice, lblAmountPaid, lblBalanceRemaining;
    private JLabel lblPaymentStatus, lblPaymentMethod;
    private JLabel lblFabricCost, lblThreadCost, lblAccessoriesCost;
    private JLabel lblPackagingCost, lblOtherMaterials, lblTotalMaterialCost;
    private JLabel lblTailoringLabour, lblEmbroideryMachine, lblWorkersPayment;
    private JLabel lblTransportation, lblOtherExpenses, lblTotalProductionCost;
    private JLabel lblTotalIncome, lblTotalExpenses, lblNetProfit;
    private JLabel lblCreatedAt, lblUpdatedAt;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JLabel lblDesignImage, lblClientImage, lblInvoiceImage;

    // Payment update fields
    private JTextField txtPaymentAmount;
    private JComboBox<String> cmbPaymentMethod;
    private JButton btnRecordPayment;
    private JLabel lblNewBalance;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ProjectDetailsDialog(JFrame parent, SpecialProject project) {
        super(parent, "Project Details - " + project.getProjectCode(), true);
        this.project = project;
        initComponents();
        loadProjectData();
        setLocationRelativeTo(parent);
        setSize(900, 850);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main panel with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnPDF = new JButton("Generate PDF");
        btnPDF.setBackground(new Color(0, 0, 153));
        btnPDF.setForeground(Color.WHITE);
        btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPDF.addActionListener(e -> generatePDF());
        buttonPanel.add(btnPDF);

        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(102, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel);

        // ====== PROJECT INFORMATION ======
        JPanel infoPanel = createSectionPanel("PROJECT INFORMATION", 2);
        addFieldToPanel(infoPanel, "Project Code:", lblProjectCode = new JLabel(), 0);
        addFieldToPanel(infoPanel, "Project Name:", lblProjectName = new JLabel(), 1);
        addFieldToPanel(infoPanel, "Category:", lblCategory = new JLabel(), 2);
        addFieldToPanel(infoPanel, "Client Name:", lblClientName = new JLabel(), 3);
        addFieldToPanel(infoPanel, "Client Phone:", lblClientPhone = new JLabel(), 4);
        addFieldToPanel(infoPanel, "Client Address:", lblClientAddress = new JLabel(), 5);
        addFieldToPanel(infoPanel, "Person In Charge:", lblPersonInCharge = new JLabel(), 6);
        addFieldToPanel(infoPanel, "Event Date:", lblEventDate = new JLabel(), 7);
        addFieldToPanel(infoPanel, "Start Date:", lblStartDate = new JLabel(), 8);
        addFieldToPanel(infoPanel, "Completion Date:", lblCompletionDate = new JLabel(), 9);
        mainPanel.add(infoPanel);

        // ====== ORDER DETAILS ======
        JPanel orderPanel = createSectionPanel("ORDER DETAILS", 2);
        addFieldToPanel(orderPanel, "Description:", lblQuantity = new JLabel(), 0);
        addFieldToPanel(orderPanel, "Quantity:", lblQuantity = new JLabel(), 1);
        addFieldToPanel(orderPanel, "Product Type:", lblProductType = new JLabel(), 2);
        addFieldToPanel(orderPanel, "Design Name:", lblDesignName = new JLabel(), 3);
        mainPanel.add(orderPanel);

        // ====== FINANCIAL RECORD ======
        JPanel financialPanel = createSectionPanel("FINANCIAL RECORD", 2);
        addFieldToPanel(financialPanel, "Total Price:", lblTotalPrice = new JLabel(), 0);
        addFieldToPanel(financialPanel, "Amount Paid:", lblAmountPaid = new JLabel(), 1);
        addFieldToPanel(financialPanel, "Balance Remaining:", lblBalanceRemaining = new JLabel(), 2);
        addFieldToPanel(financialPanel, "Payment Status:", lblPaymentStatus = new JLabel(), 3);
        addFieldToPanel(financialPanel, "Payment Method:", lblPaymentMethod = new JLabel(), 4);
        mainPanel.add(financialPanel);

        // ====== PAYMENT UPDATE SECTION ======
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 153, 0)),
                "RECORD PAYMENT",
                TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(5, 10, 5, 10);
        pgbc.anchor = GridBagConstraints.WEST;

        pgbc.gridx = 0;
        pgbc.gridy = 0;
        paymentPanel.add(new JLabel("Payment Amount:"), pgbc);
        pgbc.gridx = 1;
        txtPaymentAmount = new JTextField(15);
        paymentPanel.add(txtPaymentAmount, pgbc);

        pgbc.gridx = 2;
        pgbc.gridy = 0;
        paymentPanel.add(new JLabel("Payment Method:"), pgbc);
        pgbc.gridx = 3;
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Transfer", "POS", "Other"});
        paymentPanel.add(cmbPaymentMethod, pgbc);

        pgbc.gridx = 4;
        pgbc.gridy = 0;
        btnRecordPayment = new JButton("Record Payment");
        btnRecordPayment.setBackground(new Color(0, 102, 0));
        btnRecordPayment.setForeground(Color.WHITE);
        btnRecordPayment.addActionListener(e -> recordPayment());
        paymentPanel.add(btnRecordPayment, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 1;
        pgbc.gridwidth = 5;
        lblNewBalance = new JLabel("New Balance: ₦0.00");
        lblNewBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNewBalance.setForeground(new Color(204, 0, 0));
        paymentPanel.add(lblNewBalance, pgbc);
        pgbc.gridwidth = 1;

        mainPanel.add(paymentPanel);

        // ====== COST TRACKING ======
        JPanel costPanel = createSectionPanel("CAPITAL / COST TRACKING", 2);
        addFieldToPanel(costPanel, "Fabric Cost:", lblFabricCost = new JLabel(), 0);
        addFieldToPanel(costPanel, "Thread Cost:", lblThreadCost = new JLabel(), 1);
        addFieldToPanel(costPanel, "Accessories Cost:", lblAccessoriesCost = new JLabel(), 2);
        addFieldToPanel(costPanel, "Packaging Cost:", lblPackagingCost = new JLabel(), 3);
        addFieldToPanel(costPanel, "Other Materials:", lblOtherMaterials = new JLabel(), 4);
        addFieldToPanel(costPanel, "Total Material Cost:", lblTotalMaterialCost = new JLabel(), 5);
        addFieldToPanel(costPanel, "Tailoring Labour:", lblTailoringLabour = new JLabel(), 6);
        addFieldToPanel(costPanel, "Embroidery Machine:", lblEmbroideryMachine = new JLabel(), 7);
        addFieldToPanel(costPanel, "Workers Payment:", lblWorkersPayment = new JLabel(), 8);
        addFieldToPanel(costPanel, "Transportation:", lblTransportation = new JLabel(), 9);
        addFieldToPanel(costPanel, "Other Expenses:", lblOtherExpenses = new JLabel(), 10);
        addFieldToPanel(costPanel, "Total Production Cost:", lblTotalProductionCost = new JLabel(), 11);
        mainPanel.add(costPanel);

        // ====== PROFIT CALCULATION ======
        JPanel profitPanel = createSectionPanel("PROFIT CALCULATION", 2);
        addFieldToPanel(profitPanel, "Total Income:", lblTotalIncome = new JLabel(), 0);
        addFieldToPanel(profitPanel, "Total Expenses:", lblTotalExpenses = new JLabel(), 1);
        addFieldToPanel(profitPanel, "Net Profit:", lblNetProfit = new JLabel(), 2);
        mainPanel.add(profitPanel);

        // ====== UPDATE PROGRESS ======
        JPanel updatePanel = new JPanel(new GridBagLayout());
        updatePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204)),
                "UPDATE PROGRESS",
                TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints ugbc = new GridBagConstraints();
        ugbc.insets = new Insets(5, 10, 5, 10);
        ugbc.anchor = GridBagConstraints.WEST;

        ugbc.gridx = 0;
        ugbc.gridy = 0;
        updatePanel.add(new JLabel("Project Status:"), ugbc);
        ugbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{"Planning", "Cutting", "Sewing", "Embroidery", "Completed", "Delivered"});
        updatePanel.add(cmbStatus, ugbc);

        ugbc.gridx = 2;
        ugbc.gridy = 0;
        updatePanel.add(new JLabel("Completion %:"), ugbc);
        ugbc.gridx = 3;
        txtCompletion = new JTextField(5);
        updatePanel.add(txtCompletion, ugbc);

        ugbc.gridx = 0;
        ugbc.gridy = 1;
        updatePanel.add(new JLabel("Notes:"), ugbc);
        ugbc.gridx = 1;
        ugbc.gridwidth = 3;
        txtNotes = new JTextArea(3, 30);
        JScrollPane noteScroll = new JScrollPane(txtNotes);
        updatePanel.add(noteScroll, ugbc);
        ugbc.gridwidth = 1;

        ugbc.gridx = 3;
        ugbc.gridy = 2;
        JButton btnUpdate = new JButton("Update Progress");
        btnUpdate.setBackground(new Color(0, 102, 0));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateProgress());
        updatePanel.add(btnUpdate, ugbc);

        mainPanel.add(updatePanel);

        // ====== HISTORY LOG ======
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("PROJECT HISTORY LOG"));
        historyPanel.setPreferredSize(new Dimension(800, 150));

        String[] historyColumns = {"Date", "Activity", "Person Responsible", "Note"};
        historyModel = new DefaultTableModel(historyColumns, 0);
        historyTable = new JTable(historyModel);
        historyTable.setRowHeight(25);
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        mainPanel.add(historyPanel);

        // ====== IMAGES ======
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        imagePanel.setBorder(BorderFactory.createTitledBorder("IMAGES"));

        lblDesignImage = new JLabel();
        lblDesignImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblDesignImage.setPreferredSize(new Dimension(100, 100));
        lblDesignImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblDesignImage.setText("Design");
        imagePanel.add(createImagePanel(lblDesignImage, "Design"));

        lblClientImage = new JLabel();
        lblClientImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblClientImage.setPreferredSize(new Dimension(100, 100));
        lblClientImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblClientImage.setText("Client Ref");
        imagePanel.add(createImagePanel(lblClientImage, "Client Ref"));

        lblInvoiceImage = new JLabel();
        lblInvoiceImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblInvoiceImage.setPreferredSize(new Dimension(100, 100));
        lblInvoiceImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblInvoiceImage.setText("Invoice");
        imagePanel.add(createImagePanel(lblInvoiceImage, "Invoice"));

        mainPanel.add(imagePanel);

        // ====== TIMESTAMPS ======
        JPanel timestampPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timestampPanel.setBorder(BorderFactory.createTitledBorder("TIMESTAMPS"));
        timestampPanel.add(new JLabel("Created: "));
        lblCreatedAt = new JLabel();
        timestampPanel.add(lblCreatedAt);
        timestampPanel.add(new JLabel(" | Updated: "));
        lblUpdatedAt = new JLabel();
        timestampPanel.add(lblUpdatedAt);
        mainPanel.add(timestampPanel);

        // ====== CLOSE BUTTON ======
        btnClose.setBackground(new Color(102, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        mainPanel.add(buttonPanel);

        // Scroll pane for main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String title, int columns) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(204, 51, 0)),
                title,
                TitledBorder.LEFT, TitledBorder.TOP));
        return panel;
    }

    private void addFieldToPanel(JPanel panel, String labelText, JLabel valueLabel, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 10, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(valueLabel, gbc);
    }

    private JPanel createImagePanel(JLabel imageLabel, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }

    private void loadProjectData() {
        // Project Information
        lblProjectCode.setText(project.getProjectCode());
        lblProjectName.setText(project.getProjectName());
        lblCategory.setText(project.getCategoryName());
        lblClientName.setText(project.getClientName());
        lblClientPhone.setText(project.getClientPhone());
        lblClientAddress.setText(project.getClientAddress());
        lblPersonInCharge.setText(project.getPersonInCharge());
        lblEventDate.setText(project.getEventDate() != null ? dateOnlyFormat.format(project.getEventDate()) : "N/A");
        lblStartDate.setText(project.getStartDate() != null ? dateOnlyFormat.format(project.getStartDate()) : "N/A");
        lblCompletionDate.setText(project.getCompletionDate() != null ? dateOnlyFormat.format(project.getCompletionDate()) : "N/A");

        // Order Details
        lblQuantity.setText(String.valueOf(project.getQuantity()));
        lblProductType.setText(project.getProductType());
        lblDesignName.setText(project.getDesignName());

        // Financial
        updateFinancialDisplay();

        // Costs
        lblFabricCost.setText(String.format("₦%.2f", project.getFabricCost()));
        lblThreadCost.setText(String.format("₦%.2f", project.getThreadCost()));
        lblAccessoriesCost.setText(String.format("₦%.2f", project.getAccessoriesCost()));
        lblPackagingCost.setText(String.format("₦%.2f", project.getPackagingCost()));
        lblOtherMaterials.setText(String.format("₦%.2f", project.getOtherMaterialsCost()));
        lblTotalMaterialCost.setText(String.format("₦%.2f", project.getTotalMaterialCost()));
        lblTailoringLabour.setText(String.format("₦%.2f", project.getTailoringLabourCost()));
        lblEmbroideryMachine.setText(String.format("₦%.2f", project.getEmbroideryMachineCost()));
        lblWorkersPayment.setText(String.format("₦%.2f", project.getWorkersPayment()));
        lblTransportation.setText(String.format("₦%.2f", project.getTransportationCost()));
        lblOtherExpenses.setText(String.format("₦%.2f", project.getOtherExpenses()));
        lblTotalProductionCost.setText(String.format("₦%.2f", project.getTotalProductionCost()));

        // Profit
        lblTotalIncome.setText(String.format("₦%.2f", project.getTotalIncome()));
        lblTotalExpenses.setText(String.format("₦%.2f", project.getTotalExpenses()));
        lblNetProfit.setText(String.format("₦%.2f", project.getNetProfit()));

        // Status
        cmbStatus.setSelectedItem(project.getProjectStatus());
        txtCompletion.setText(String.valueOf(project.getCompletionPercentage()));
        txtNotes.setText(project.getNotes());

        // Timestamps
        lblCreatedAt.setText(project.getCreatedAt() != null ? dateFormat.format(project.getCreatedAt()) : "N/A");
        lblUpdatedAt.setText(project.getUpdatedAt() != null ? dateFormat.format(project.getUpdatedAt()) : "N/A");

        // History
        loadHistory();

        // Images
        loadImage(lblDesignImage, project.getDesignImagePath(), "Design");
        loadImage(lblClientImage, project.getClientReferenceImagePath(), "Client Ref");
        loadImage(lblInvoiceImage, project.getInvoiceImagePath(), "Invoice");

        // Update new balance label
        updateNewBalanceLabel();
    }

    private void updateFinancialDisplay() {
        lblTotalPrice.setText(String.format("₦%.2f", project.getTotalPrice()));
        lblAmountPaid.setText(String.format("₦%.2f", project.getAmountPaid()));
        lblBalanceRemaining.setText(String.format("₦%.2f", project.getBalanceRemaining()));
        lblPaymentStatus.setText(project.getPaymentStatus());
        lblPaymentMethod.setText(project.getPaymentMethod());

        // Color code payment status
        if ("Paid".equals(project.getPaymentStatus())) {
            lblPaymentStatus.setForeground(new Color(0, 102, 0));
        } else if ("Partially Paid".equals(project.getPaymentStatus())) {
            lblPaymentStatus.setForeground(new Color(204, 102, 0));
        } else {
            lblPaymentStatus.setForeground(new Color(204, 0, 0));
        }
    }

    private void updateNewBalanceLabel() {
        double balance = project.getBalanceRemaining();
        if (balance <= 0) {
            lblNewBalance.setText("✓ Fully Paid!");
            lblNewBalance.setForeground(new Color(0, 102, 0));
        } else {
            lblNewBalance.setText(String.format("Remaining Balance: ₦%.2f", balance));
            lblNewBalance.setForeground(new Color(204, 0, 0));
        }
    }

    private void loadImage(JLabel label, String imagePath, String defaultText) {
        if (imagePath != null && !imagePath.isEmpty()) {
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                label.setText("");
                return;
            }
        }
        label.setText(defaultText);
        label.setIcon(null);
    }

    private void loadHistory() {
        historyModel.setRowCount(0);
        List<ProjectHistoryLog> logs = SpecialProjectDao.getProjectHistory(project.getProjectId());
        for (ProjectHistoryLog log : logs) {
            historyModel.addRow(new Object[]{
                log.getLogDate() != null ? dateFormat.format(log.getLogDate()) : "N/A",
                log.getActivity(),
                log.getPersonResponsible(),
                log.getNote()
            });
        }
    }

    private void recordPayment() {
        String amountText = txtPaymentAmount.getText().trim();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter payment amount");
            return;
        }

        try {
            double paymentAmount = Double.parseDouble(amountText);
            if (paymentAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Payment amount must be greater than 0");
                return;
            }

            double currentBalance = project.getBalanceRemaining();
            if (paymentAmount > currentBalance) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Payment amount (₦%.2f) exceeds remaining balance (₦%.2f).\nDo you want to pay the full balance?",
                                paymentAmount, currentBalance),
                        "Payment Exceeds Balance", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    paymentAmount = currentBalance;
                } else {
                    return;
                }
            }

            // Update project payment
            double newAmountPaid = project.getAmountPaid() + paymentAmount;
            project.setAmountPaid(newAmountPaid);
            project.setBalanceRemaining(project.getTotalPrice() - newAmountPaid);

            // Update payment status
            if (project.getBalanceRemaining() <= 0) {
                project.setPaymentStatus("Paid");
            } else if (project.getAmountPaid() > 0) {
                project.setPaymentStatus("Partially Paid");
            }

            // Update payment method if provided
            String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                project.setPaymentMethod(paymentMethod);
            }

            // Recalculate totals
            project.calculateTotals();

            // Update in database
            boolean success = SpecialProjectDao.updateProject(project);

            if (success) {
                // Add history log
                String activity = String.format("Payment of ₦%.2f recorded. Status: %s",
                        paymentAmount, project.getPaymentStatus());
                SpecialProjectDao.addHistoryLog(project.getProjectId(), activity,
                        project.getPersonInCharge(),
                        String.format("Payment method: %s", paymentMethod));

                // Update display
                updateFinancialDisplay();
                updateNewBalanceLabel();
                loadHistory();
                txtPaymentAmount.setText("");

                JOptionPane.showMessageDialog(this,
                        String.format("Payment of ₦%.2f recorded successfully!\nNew Balance: ₦%.2f",
                                paymentAmount, project.getBalanceRemaining()));

                // Refresh parent
                if (getParent() instanceof SpecialProjects) {
                    ((SpecialProjects) getParent()).loadAllProjects();
                    ((SpecialProjects) getParent()).loadDashboardSummary();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to record payment");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for payment amount");
        }
    }

    private void updateProgress() {
        String status = (String) cmbStatus.getSelectedItem();
        String completionText = txtCompletion.getText().trim();
        String notes = txtNotes.getText().trim();

        if (completionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter completion percentage");
            return;
        }

        try {
            int completion = Integer.parseInt(completionText);
            if (completion < 0 || completion > 100) {
                JOptionPane.showMessageDialog(this, "Completion percentage must be between 0 and 100");
                return;
            }

            // Update project
            project.setProjectStatus(status);
            project.setCompletionPercentage(completion);
            project.setNotes(notes);

            // Calculate updated values
            project.calculateTotals();

            // Update in database
            boolean success = SpecialProjectDao.updateProject(project);

            if (success) {
                String activity = "Status updated to: " + status + " | Completion: " + completion + "%";
                SpecialProjectDao.addHistoryLog(project.getProjectId(), activity,
                        project.getPersonInCharge(), notes);

                JOptionPane.showMessageDialog(this, "Project progress updated successfully!");
                loadHistory();

                // Refresh parent
                if (getParent() instanceof SpecialProjects) {
                    ((SpecialProjects) getParent()).loadAllProjects();
                    ((SpecialProjects) getParent()).loadDashboardSummary();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update project progress");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for completion percentage");
        }
    }

    private void generatePDF() {
        // Show progress dialog
        final JDialog progressDialog = new JDialog(this, "Generating PDF", true);
        progressDialog.setLayout(new BorderLayout());
        JLabel progressLabel = new JLabel("Generating PDF report... Please wait.");
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressDialog.add(progressPanel);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return ProjectPDFGenerator.generateProjectPDF(project);
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    String filePath = get();
                    if (filePath != null) {
                        int open = JOptionPane.showConfirmDialog(ProjectDetailsDialog.this,
                                "PDF report generated successfully!\n\nFile saved \n\n Do you want to open the file?",
                                "PDF Generated", JOptionPane.YES_NO_OPTION);
                        if (open == JOptionPane.YES_OPTION) {
                            try {
                                java.awt.Desktop.getDesktop().open(new File(filePath));
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(ProjectDetailsDialog.this,
                                        "File saved but cannot open automatically.\nPlease open manually: " + filePath,
                                        "Open File", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(ProjectDetailsDialog.this,
                                "Failed to generate PDF report. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ProjectDetailsDialog.this,
                            "Error generating PDF: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
        progressDialog.setVisible(true);
    }
}
