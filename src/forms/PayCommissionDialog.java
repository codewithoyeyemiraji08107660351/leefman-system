package forms;

import dao.CommissionDao;
import models.CommissionSummary;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import models.CommissionDetail;

public class PayCommissionDialog extends JDialog {

    private JComboBox<String> cmbTailorSelector;
    private JTextField txtAmount;
    private JLabel lblTailorName;
    private JLabel lblTotalCommission;
    private JLabel lblPaidCommission;
    private JLabel lblOutstandingCommission;
    private JComboBox<String> cmbPaymentMethod;
    private JTextArea txtNotes;
    private JTable commissionTable;
    private DefaultTableModel tableModel;
    private List<CommissionSummary> commissionSummaries;

    public PayCommissionDialog(JFrame parent) {
        super(parent, "Pay Commission - All Tailors", true);
        initComponents();
        loadTailorCommissionSummaries();
        setLocationRelativeTo(parent);
        setSize(900, 700);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Panel - Tailor Selection
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Select Tailor"));
        topPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Select Tailor:"), gbc);
        gbc.gridx = 1;
        cmbTailorSelector = new JComboBox<>();
        cmbTailorSelector.addActionListener(e -> loadTailorCommissionDetails());
        topPanel.add(cmbTailorSelector, gbc);

        // Commission Summary Panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Commission Summary"));
        summaryPanel.setBackground(new Color(255, 255, 255));

        lblTotalCommission = new JLabel("Total Commission: ₦0.00");
        lblTotalCommission.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalCommission.setForeground(new Color(204, 51, 0));

        lblPaidCommission = new JLabel("Paid: ₦0.00");
        lblPaidCommission.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPaidCommission.setForeground(new Color(0, 102, 0));

        lblOutstandingCommission = new JLabel("Outstanding: ₦0.00");
        lblOutstandingCommission.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOutstandingCommission.setForeground(new Color(204, 0, 0));

        summaryPanel.add(lblTotalCommission);
        summaryPanel.add(lblPaidCommission);
//        summaryPanel.add(lblOutstandingCommission);

        // Commission Details Table
        String[] columns = {"Commission ID", "Order ID", "Amount", "Paid", "Balance", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        commissionTable = new JTable(tableModel);
        commissionTable.setRowHeight(25);
        commissionTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        commissionTable.setDefaultRenderer(String.class, new StatusRenderer());
        JScrollPane tableScroll = new JScrollPane(commissionTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Commission Details"));
        tableScroll.setPreferredSize(new Dimension(800, 200));

        // Payment Panel
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Make Payment"));
        paymentPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(8, 8, 8, 8);

        pgbc.gridx = 0;
        pgbc.gridy = 0;
        paymentPanel.add(new JLabel("Tailor:"), pgbc);
        pgbc.gridx = 1;
        lblTailorName = new JLabel("-");
        lblTailorName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        paymentPanel.add(lblTailorName, pgbc);

        pgbc.gridx = 2;
        paymentPanel.add(new JLabel("Outstanding:"), pgbc);
        pgbc.gridx = 3;
        lblOutstandingCommission = new JLabel("₦0.00");
        lblOutstandingCommission.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOutstandingCommission.setForeground(Color.RED);
        paymentPanel.add(lblOutstandingCommission, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 1;
        paymentPanel.add(new JLabel("Amount to Pay:"), pgbc);
        pgbc.gridx = 1;
        txtAmount = new JTextField(15);
        paymentPanel.add(txtAmount, pgbc);

        pgbc.gridx = 2;
        paymentPanel.add(new JLabel("Payment Method:"), pgbc);
        pgbc.gridx = 3;
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Bank Transfer", "Cheque", "Mobile Money"});
        paymentPanel.add(cmbPaymentMethod, pgbc);

        pgbc.gridx = 0;
        pgbc.gridy = 2;
        paymentPanel.add(new JLabel("Notes:"), pgbc);
        pgbc.gridx = 1;
        pgbc.gridwidth = 3;
        txtNotes = new JTextArea(3, 30);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        paymentPanel.add(notesScroll, pgbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnPay = new JButton("Pay Commission");
        btnPay.setBackground(new Color(0, 102, 0));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.addActionListener(e -> makePayment());
        buttonPanel.add(btnPay);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(0, 0, 153));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadTailorCommissionSummaries());
        buttonPanel.add(btnRefresh);

        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(102, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        // Assemble main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(paymentPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadTailorCommissionSummaries() {
        commissionSummaries = CommissionDao.getAllTailorCommissionSummaries();
        cmbTailorSelector.removeAllItems();

        if (commissionSummaries == null || commissionSummaries.isEmpty()) {
            cmbTailorSelector.addItem("No commission records found");

            lblTotalCommission.setText("Total Commission: ₦0.00");
            lblPaidCommission.setText("Paid: ₦0.00");
            lblOutstandingCommission.setText("Outstanding: ₦0.00");
            tableModel.setRowCount(0);
            return;
        }

        for (CommissionSummary summary : commissionSummaries) {
            String display = String.format("%s - Outstanding: ₦%.2f",
                    summary.getTailorName(), summary.getOutstandingCommission());
            cmbTailorSelector.addItem(display);
        }

        loadTailorCommissionDetails();
    }

    private void loadTailorCommissionDetails() {
        int selectedIndex = cmbTailorSelector.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= commissionSummaries.size()) {
            return;
        }

        CommissionSummary summary = commissionSummaries.get(selectedIndex);

        lblTailorName.setText(summary.getTailorName());
        lblTotalCommission.setText(String.format("Total Commission: ₦%.2f", summary.getTotalCommission()));
        lblPaidCommission.setText(String.format("Paid: ₦%.2f", summary.getTotalPaid()));
        lblOutstandingCommission.setText(String.format("Outstanding: ₦%.2f", summary.getOutstandingCommission()));

      
        loadCommissionDetailsTable(summary.getTailorId());
    }

    private void loadCommissionDetailsTable(int tailorId) {
        tableModel.setRowCount(0);
        List<CommissionDetail> details = CommissionDao.getCommissionDetailsByTailor(tailorId);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        for (CommissionDetail detail : details) {
            tableModel.addRow(new Object[]{
                detail.getCommissionId(),
                detail.getOrderId(),
                detail.getAmount(),
                detail.getPaidAmount(),
                detail.getBalance(),
                detail.getCommissionDate() != null ? sdf.format(detail.getCommissionDate()) : "N/A",
                detail.getStatus()
            });
        }
    }

    private void makePayment() {
        int selectedIndex = cmbTailorSelector.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= commissionSummaries.size()) {
            JOptionPane.showMessageDialog(this, "Please select a tailor");
            return;
        }

        CommissionSummary summary = commissionSummaries.get(selectedIndex);
        int tailorId = summary.getTailorId();
        double outstanding = Math.round(summary.getOutstandingCommission() * 100.0) / 100.0;

        if (outstanding <= 0) {
            JOptionPane.showMessageDialog(this, "No outstanding commission for this tailor");
            return;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            amount = Math.round(amount * 100.0) / 100.0;

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount greater than 0.");
                return;
            }

            if (amount > outstanding) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Amount (₦%.2f) exceeds outstanding commission (₦%.2f).\nPay full amount?", amount, outstanding),
                        "Amount Exceeds Commission", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    amount = outstanding;
                } else {
                    return;
                }
            }

            String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();
            String notes = txtNotes.getText().trim();

            boolean success = CommissionDao.makeCommissionPayment(tailorId, amount, paymentMethod, notes);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        String.format("Payment of ₦%.2f recorded successfully for %s!", amount, summary.getTailorName()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                txtAmount.setText("");
                txtNotes.setText("");

                // Refresh data
                loadTailorCommissionSummaries();

                if (getParent() instanceof Tailors) {
//                ((Tailors) getParent()).refreshCommissionInfo();
                    ((Tailors) getParent()).refreshTailorStats();
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.");
        }
    }

    // Custom renderers
    class CurrencyRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            if (value instanceof Double) {
                double amount = (Double) value;
                setText(String.format("₦%.2f", Math.round(amount * 100.0) / 100.0));
                setHorizontalAlignment(RIGHT);
            } else {
                super.setValue(value);
            }
        }
    }

    class StatusRenderer extends DefaultTableCellRenderer {

        @Override
        protected void setValue(Object value) {
            if (value instanceof String) {
                String status = (String) value;
                setText(status);
                setHorizontalAlignment(CENTER);
                if ("paid".equalsIgnoreCase(status)) {
                    setForeground(new Color(0, 150, 0));
                    setBackground(new Color(220, 255, 220));
                } else if ("unpaid".equalsIgnoreCase(status)) {
                    setForeground(Color.RED);
                    setBackground(new Color(255, 220, 220));
                }
            } else {
                super.setValue(value);
            }
        }
    }
}
