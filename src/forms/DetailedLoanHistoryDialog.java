package forms;

import dao.TailorDao;
import models.LoanDetailWithPayments;
import models.LoanPaymentDetail;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DetailedLoanHistoryDialog extends JDialog {
    
    private JTable loanTable;
    private JTable paymentTable;
    private DefaultTableModel loanTableModel;
    private DefaultTableModel paymentTableModel;
    private List<LoanDetailWithPayments> loanDetails;
    
    public DetailedLoanHistoryDialog(JFrame parent) {
        super(parent, "Detailed Loan History", true);
        initComponents();
        loadLoanHistory();
        setLocationRelativeTo(parent);  
        setSize(1200, 700);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(27, 33, 56));
        JLabel titleLabel = new JLabel("Detailed Loan History with Payment Allocations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Split pane for loans and payments
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        
        // Loan Table (Top) - REMOVED Status column
        String[] loanColumns = {"Loan ID", "Tailor", "Item", "Amount", "Paid", "Balance", "Loan Date"};
        loanTableModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loanTable = new JTable(loanTableModel);
        loanTable.setRowHeight(30);
        loanTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showPaymentDetails();
            }
        });
        loanTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        
        JScrollPane loanScroll = new JScrollPane(loanTable);
        loanScroll.setBorder(BorderFactory.createTitledBorder("Loans"));
        splitPane.setTopComponent(loanScroll);
        
        // Payment Table (Bottom)
        String[] paymentColumns = {"Payment ID", "Payment Amount", "Applied to Loan", "Payment Date", "Allocation Date"};
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
        paymentScroll.setBorder(BorderFactory.createTitledBorder("Payment Allocations for Selected Loan"));
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
        btnRefresh.addActionListener(e -> loadLoanHistory());
        buttonPanel.add(btnRefresh);
        
        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(102, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 5, 15, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        summaryPanel.setBackground(new Color(240, 240, 240));
        
        JLabel totalLoansLabel = new JLabel("Total Loans: ₦0.00");
        totalLoansLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel totalPaidLabel = new JLabel("Total Paid: ₦0.00");
        totalPaidLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalPaidLabel.setForeground(new Color(0, 102, 0));
        
        JLabel outstandingLabel = new JLabel("Outstanding: ₦0.00");
        outstandingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        outstandingLabel.setForeground(new Color(204, 0, 0));
        
        JLabel activeLoansLabel = new JLabel("Active Loans: 0");
        activeLoansLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel totalPaymentsLabel = new JLabel("Total Payments: 0");
        totalPaymentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        summaryPanel.add(totalLoansLabel);
        summaryPanel.add(totalPaidLabel);
        summaryPanel.add(outstandingLabel);
        summaryPanel.add(activeLoansLabel);
        summaryPanel.add(totalPaymentsLabel);
        
        summaryPanel.putClientProperty("totalLoansLabel", totalLoansLabel);
        summaryPanel.putClientProperty("totalPaidLabel", totalPaidLabel);
        summaryPanel.putClientProperty("outstandingLabel", outstandingLabel);
        summaryPanel.putClientProperty("activeLoansLabel", activeLoansLabel);
        summaryPanel.putClientProperty("totalPaymentsLabel", totalPaymentsLabel);
        
        return summaryPanel;
    }
    
    private void loadLoanHistory() {
        loanTableModel.setRowCount(0);
        loanDetails = TailorDao.getDetailedLoanHistory();
        
        double totalLoanAmount = 0;
        double totalPaidAmount = 0;
        int activeLoans = 0;
        int totalPayments = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (LoanDetailWithPayments loan : loanDetails) {
            totalLoanAmount += loan.getLoanAmount();
            double paid = loan.getTotalPaid();
            totalPaidAmount += paid;
            totalPayments += loan.getPayments().size();
            
            if ("unpaid".equalsIgnoreCase(loan.getStatus()) && loan.getRemainingBalance() > 0) {
                activeLoans++;
            }
            
            String item = loan.getItemName();
            if (item == null || item.isEmpty()) {
                item = loan.getItemDescription();
            }
            if (item == null || item.isEmpty()) {
                item = "-";
            }
            
            // Status column removed - only 7 columns now
            loanTableModel.addRow(new Object[]{
                loan.getLoanId(),
                loan.getTailorName(),
                item,
                loan.getLoanAmount(),
                paid,
                loan.getRemainingBalance(),
                loan.getLoanDate() != null ? sdf.format(loan.getLoanDate()) : "N/A"
            });
        }
        
        // Update summary
        JPanel summaryPanel = (JPanel) ((BorderLayout) getContentPane().getLayout())
            .getLayoutComponent(BorderLayout.SOUTH);
        
        if (summaryPanel instanceof JPanel) {
            Component[] components = summaryPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String text = label.getText();
                    if (text.startsWith("Total Loans:")) {
                        label.setText(String.format("Total Loans: ₦%.2f", totalLoanAmount));
                    } else if (text.startsWith("Total Paid:")) {
                        label.setText(String.format("Total Paid: ₦%.2f", totalPaidAmount));
                    } else if (text.startsWith("Outstanding:")) {
                        label.setText(String.format("Outstanding: ₦%.2f", totalLoanAmount - totalPaidAmount));
                    } else if (text.startsWith("Active Loans:")) {
                        label.setText(String.format("Active Loans: %d", activeLoans));
                    } else if (text.startsWith("Total Payments:")) {
                        label.setText(String.format("Total Payments: %d", totalPayments));
                    }
                }
            }
        }
    }
    
    private void showPaymentDetails() {
        paymentTableModel.setRowCount(0);
        int selectedRow = loanTable.getSelectedRow();
        
        if (selectedRow >= 0 && selectedRow < loanDetails.size()) {
            LoanDetailWithPayments loan = loanDetails.get(selectedRow);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (LoanPaymentDetail payment : loan.getPayments()) {
                paymentTableModel.addRow(new Object[]{
                    payment.getPaymentId(),
                    payment.getPaymentAmount(),
                    payment.getAmountApplied(),
                    payment.getPaymentDate() != null ? sdf.format(payment.getPaymentDate()) : "N/A",
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
                setText(String.format("₦%.2f", (Double) value));
                setHorizontalAlignment(RIGHT);
            } else {
                super.setValue(value);
            }
        }
    }
}