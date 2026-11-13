package forms;

import dao.CommissionDao;
import models.CommissionDetailWithPayments;
import models.CommissionPaymentDetail;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CommissionHistoryDialog extends JDialog {

    private JTable commissionTable;
    private JTable paymentTable;
    private DefaultTableModel commissionTableModel;
    private DefaultTableModel paymentTableModel;
    private List<CommissionDetailWithPayments> commissionDetails;

    public CommissionHistoryDialog(JFrame parent) {
        super(parent, "Commission History - All Tailors", true);
        initComponents();
        loadCommissionHistory();
        setLocationRelativeTo(parent);
        setSize(1200, 700);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(27, 33, 56));
        JLabel titleLabel = new JLabel("Commission History - All Tailors");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Commission Table (shows all tailors)
        String[] commissionColumns = {"ID", "Tailor", "Order ID", "Amount", "Paid", "Balance", "Date", "Status"};
        commissionTableModel = new DefaultTableModel(commissionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        commissionTable = new JTable(commissionTableModel);
        commissionTable.setRowHeight(30);
        commissionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPaymentDetails();
            }
        });
        commissionTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        commissionTable.setDefaultRenderer(String.class, new StatusRenderer());

        JScrollPane commissionScroll = new JScrollPane(commissionTable);
        commissionScroll.setBorder(BorderFactory.createTitledBorder("All Commissions"));
        splitPane.setTopComponent(commissionScroll);

        // Payment Table (shows allocations for selected commission)
        String[] paymentColumns = {"Payment ID", "Amount Paid", "Date", "Method", "Allocation Date"};
        paymentTableModel = new DefaultTableModel(paymentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(paymentTableModel);
        paymentTable.setRowHeight(25);
        paymentTable.setDefaultRenderer(Double.class, new CurrencyRenderer());

        JScrollPane paymentScroll = new JScrollPane(paymentTable);
        paymentScroll.setBorder(BorderFactory.createTitledBorder("Payment Allocations for Selected Commission"));
        splitPane.setBottomComponent(paymentScroll);

        add(splitPane, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(0, 102, 0));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> loadCommissionHistory());
        buttonPanel.add(btnRefresh);

        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(102, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        summaryPanel.setBackground(new Color(240, 240, 240));

        JLabel totalCommissionLabel = new JLabel("Total Commission: ₦0.00");
        totalCommissionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel totalPaidLabel = new JLabel("Total Paid: ₦0.00");
        totalPaidLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalPaidLabel.setForeground(new Color(0, 102, 0));

        JLabel outstandingLabel = new JLabel("Outstanding: ₦0.00");
        outstandingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        outstandingLabel.setForeground(new Color(204, 0, 0));

        JLabel totalPaymentsLabel = new JLabel("Total Payments: 0");
        totalPaymentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        summaryPanel.add(totalCommissionLabel);
        summaryPanel.add(totalPaidLabel);
        summaryPanel.add(outstandingLabel);
        summaryPanel.add(totalPaymentsLabel);

        summaryPanel.putClientProperty("totalCommissionLabel", totalCommissionLabel);
        summaryPanel.putClientProperty("totalPaidLabel", totalPaidLabel);
        summaryPanel.putClientProperty("outstandingLabel", outstandingLabel);
        summaryPanel.putClientProperty("totalPaymentsLabel", totalPaymentsLabel);

        return summaryPanel;
    }

    private void loadCommissionHistory() {
        commissionTableModel.setRowCount(0);
        // Get data from tailor_commissions table
        commissionDetails = CommissionDao.getAllCommissionHistory();

        double totalCommission = 0;
        double totalPaid = 0;
        int totalPayments = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (commissionDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No commission records found");
            updateSummaryPanel(0, 0, 0);
            return;
        }

        for (CommissionDetailWithPayments commission : commissionDetails) {
            totalCommission += commission.getCommissionAmount();
            double paid = commission.getTotalPaid();
            totalPaid += paid;
            totalPayments += commission.getPayments().size();

            commissionTableModel.addRow(new Object[]{
                commission.getCommissionId(),
                commission.getTailorName(),
                commission.getOrderId(),
                commission.getCommissionAmount(),
                paid,
                commission.getRemainingBalance(),
                commission.getCommissionDate() != null ? sdf.format(commission.getCommissionDate()) : "N/A",
                commission.getStatus()
            });
        }

        updateSummaryPanel(totalCommission, totalPaid, totalPayments);
    }

    private void updateSummaryPanel(double totalCommission, double totalPaid, int totalPayments) {
        JPanel summaryPanel = (JPanel) ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (summaryPanel instanceof JPanel) {
            Component[] components = summaryPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String text = label.getText();
                    if (text.startsWith("Total Commission:")) {
                        label.setText(String.format("Total Commission: ₦%.2f", totalCommission));
                    } else if (text.startsWith("Total Paid:")) {
                        label.setText(String.format("Total Paid: ₦%.2f", totalPaid));
                    } else if (text.startsWith("Outstanding:")) {
                        label.setText(String.format("Outstanding: ₦%.2f", totalCommission - totalPaid));
                    } else if (text.startsWith("Total Payments:")) {
                        label.setText(String.format("Total Payments: %d", totalPayments));
                    }
                }
            }
        }
    }

    private void showPaymentDetails() {
        paymentTableModel.setRowCount(0);
        int selectedRow = commissionTable.getSelectedRow();

        if (selectedRow >= 0 && selectedRow < commissionDetails.size()) {
            CommissionDetailWithPayments commission = commissionDetails.get(selectedRow);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            List<CommissionPaymentDetail> payments = commission.getPayments();
            if (payments.isEmpty()) {
                paymentTableModel.addRow(new Object[]{
                    "No payments", "", "", "", "", ""
                });
                return;
            }

            for (CommissionPaymentDetail payment : payments) {
                paymentTableModel.addRow(new Object[]{
                    payment.getPaymentId(),
                    payment.getPaymentAmount(),
                    //                    payment.getAmountApplied(),
                    payment.getPaymentDate() != null ? sdf.format(payment.getPaymentDate()) : "N/A",
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A",
                    payment.getAllocationDate() != null ? sdf.format(payment.getAllocationDate()) : "N/A"
                });
            }
        }
    }

    // Currency Renderer
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

    // Status Renderer with colors
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
                } else {
                    setForeground(Color.BLACK);
                    setBackground(Color.WHITE);
                }
            } else {
                super.setValue(value);
            }
        }
    }
}
