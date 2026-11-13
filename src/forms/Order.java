/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.CustomerDao;
import dao.OrderDao;
import dao.TailorDao;
import java.text.SimpleDateFormat;
import dao.OrderDashboardDao;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import models.Customer;
import models.Orders;
import models.Tailor;

/**
 *
 * @author oyeyemi
 */
public class Order extends javax.swing.JFrame {

    private String amountPattern = "^[0-9]*[.]*[0-9]+$";

    /**
     * Creates new form Order
     */
    public Order() {
        initComponents();
        btnSubmit.setEnabled(false);
        txtBalance.setEditable(false);

        jDateFrom.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                refreshOrderStats();
            }
        });

        jDateTo.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                refreshOrderStats();
            }
        });

    }

    public void findCustomerByNameID() {
        List<Customer> allCustomers = CustomerDao.findCustomerByNameID();

        jComboCustomers.removeAllItems();

        for (Customer cusObj : allCustomers) {
            jComboCustomers.addItem(cusObj);
        }
    }

    public void findTailorByNameID() {
        List<Tailor> allTailor = TailorDao.findTailorByNameID();

        jComboTailors.removeAllItems();

        for (Tailor tailorObj : allTailor) {
            jComboTailors.addItem(tailorObj);
        }
    }

    public void findTailorByNameIDFilter() {
        List<Tailor> allTailor = TailorDao.findTailorByNameID();

        jComboTailorFilter.removeAllItems();

        for (Tailor tailorObj : allTailor) {
            jComboTailorFilter.addItem(tailorObj);
        }
    }

    public void validateFields() {
        String totalCost = txtTotalCost.getText();
        String amountPaid = txtAmountPaid.getText();
        String balance = txtBalance.getText();
        String commission = txtTailorCommission.getText();

        if (totalCost.matches(amountPattern) && amountPaid.matches(amountPattern)
                && balance.matches(amountPattern) && commission.matches(amountPattern)) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    private void calculateBalance() {
        try {
            String totalText = txtTotalCost.getText().trim();
            String paidText = txtAmountPaid.getText().trim();

            if (totalText.isEmpty()) {
                totalText = "0";
            }
            if (paidText.isEmpty()) {
                paidText = "0";
            }

            double total = Double.parseDouble(totalText);
            double paid = Double.parseDouble(paidText);

            double balance = total - paid;

            if (balance < 0) {
                balance = 0;
            }

            txtBalance.setText(String.format("%.2f", balance));

        } catch (NumberFormatException e) {
            txtBalance.setText("0.00");
        }
    }

    public void getAllOrders() {

        List<Orders> orderList = OrderDao.getAllOrders();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);

        for (Orders order : orderList) {
            model.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerName(),
                order.getCustomerPhoneNo(),
                order.getTailorName(),
                order.getDateReceived(),
                order.getDueDate(),
                order.getTotalCost(),
                order.getAmountPaid(),
                order.getBalance(),
                order.getTailorCommission(),
                order.getPaymentMethod(),
                order.getStatus()
            });
        }
    }

    public void getAllOrders(String name) {

        List<Orders> orderList = OrderDao.getAllOrders(name);
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);

        for (Orders order : orderList) {
            model.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerName(),
                order.getCustomerPhoneNo(),
                order.getTailorName(),
                order.getDateReceived(),
                order.getDueDate(),
                order.getTotalCost(),
                order.getAmountPaid(),
                order.getBalance(),
                order.getTailorCommission(),
                order.getPaymentMethod(),
                order.getStatus()
            });
        }
    }

    public void getTotalOrders() {
        int totalOrders = OrderDashboardDao.getTotalOrders();
        lblTotalOrder.setText(String.format("%d", totalOrders));
    }

    public void getTotalEarnings() {
        double totalEarnings = OrderDashboardDao.getTotalEarnings();
        lblTotalEarnings.setText("₦" + String.format("%.2f", totalEarnings));
    }

    public void getCompleted() {
        int completed = OrderDashboardDao.getCompletedOrders();
        lblCompleted.setText(String.format("%d", completed));
    }

    public void getInProgress() {
        int inProgress = OrderDashboardDao.getInProgressOrders();
        lblInProgress.setText(String.format("%d", inProgress));
    }

    public void getPending() {
        int pending = OrderDashboardDao.getPendingOrders();
        lblPending.setText(String.format("%d", pending));
    }

    private String formatDate(com.toedter.calendar.JDateChooser chooser) {

        if (chooser.getDate() == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(chooser.getDate());
    }

    private void refreshOrderStats() {

        String fromDate = formatDate(jDateReceived);
        String toDate = formatDate(jDateTo);

        System.out.println(fromDate);

        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both From and To dates");
            return;
        }

        int totalOrders = OrderDashboardDao.getTotalOrders(fromDate, toDate);
        double earnings = OrderDashboardDao.getTotalEarnings(fromDate, toDate);
        int completed = OrderDashboardDao.getCompletedOrders(fromDate, toDate);
        int pending = OrderDashboardDao.getPendingOrders(fromDate, toDate);

        lblTotalOrder.setText(String.valueOf(totalOrders));
        lblTotalEarnings.setText("₦" + String.format("%,.2f", earnings));
        lblCompleted.setText(String.valueOf(completed));
        lblPending.setText(String.valueOf(pending));
    }

    private void refreshTailorStats() {

        String fromDate = formatDate(jDateFrom);
        String toDate = formatDate(jDateTo);
        Tailor selectedTailor = (Tailor) jComboTailors.getSelectedItem();
        int tailorId = selectedTailor.getTailorId();

        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both From and To dates");
            return;
        }

        int totalOrders = OrderDashboardDao.getTotalOrders(fromDate, toDate, tailorId);
        double earnings = OrderDashboardDao.getTotalEarnings(fromDate, toDate, tailorId);
        int completed = OrderDashboardDao.getCompletedOrders(fromDate, toDate, tailorId);
        int pending = OrderDashboardDao.getPendingOrders(fromDate, toDate, tailorId);

        lblTotalOrder.setText(String.valueOf(totalOrders));
        lblTotalEarnings.setText("₦" + String.format("%,.2f", earnings));
        lblCompleted.setText(String.valueOf(completed));
        lblPending.setText(String.valueOf(pending));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboCustomers = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtTotalCost = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtAmountPaid = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jComboTailors = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lblTotalOrder = new javax.swing.JLabel();
        lblTotalEarnings = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblCompleted = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblInProgress = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblPending = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnDateRange = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jComboTailorFilter = new javax.swing.JComboBox<>();
        btnFetch = new javax.swing.JButton();
        jDateFrom = new com.toedter.calendar.JDateChooser();
        jDateTo = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        jComboPaymentMethod = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnOrders4 = new javax.swing.JButton();
        btnTransactionHistory = new javax.swing.JButton();
        btnMaterialsAndStock = new javax.swing.JButton();
        btnOrders3 = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnTailors = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnHome2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtTailorCommission = new javax.swing.JTextField();
        jDateReceived = new com.toedter.calendar.JDateChooser();
        jDueDate = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Leefman Record System -  Order");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(279, 0, 632, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Customer");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 190, -1, -1));

        jComboCustomers.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jComboCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 180, 283, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Date Received");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 240, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Due Date");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 300, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Total Cost");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 240, -1, -1));

        txtTotalCost.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtTotalCost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotalCostKeyReleased(evt);
            }
        });
        getContentPane().add(txtTotalCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 240, 283, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Amount Paid");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, -1, -1));

        txtAmountPaid.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtAmountPaid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAmountPaidKeyReleased(evt);
            }
        });
        getContentPane().add(txtAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 350, 283, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Balance");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 300, -1, -1));

        txtBalance.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtBalance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBalanceKeyReleased(evt);
            }
        });
        getContentPane().add(txtBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 300, 283, -1));

        btnSubmit.setBackground(new java.awt.Color(51, 0, 102));
        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSubmit.setForeground(new java.awt.Color(255, 255, 255));
        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        getContentPane().add(btnSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 350, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 0, 0));
        jLabel9.setText("All Orders");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 400, -1, -1));

        btnClear.setBackground(new java.awt.Color(51, 0, 102));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        getContentPane().add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 350, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Tailors");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 190, -1, -1));

        jComboTailors.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jComboTailors, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 180, 283, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Search");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 430, -1, -1));

        txtSearch.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 420, 335, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Customer Name", "Customer PhoneNo.", "Tailor", "Date Received", "Expected Completion Date", "Cost", "Amount Paid", "Balance", "Tailor Comm.", "Payment Method", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 460, 1030, 210));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 51));
        jLabel12.setText("Total Order");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, -1, -1));

        lblTotalOrder.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalOrder.setForeground(new java.awt.Color(0, 0, 51));
        lblTotalOrder.setText("000");
        jPanel2.add(lblTotalOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, -1, -1));

        lblTotalEarnings.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalEarnings.setText("000");
        jPanel2.add(lblTotalEarnings, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 80, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Total Earning");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 50, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 51, 0));
        jLabel16.setText("Completed");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 50, -1, -1));

        lblCompleted.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCompleted.setForeground(new java.awt.Color(0, 51, 0));
        lblCompleted.setText("000");
        jPanel2.add(lblCompleted, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 80, -1, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(153, 153, 0));
        jLabel18.setText("In Progress");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, -1, -1));

        lblInProgress.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblInProgress.setForeground(new java.awt.Color(102, 102, 0));
        lblInProgress.setText("000");
        jPanel2.add(lblInProgress, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 80, -1, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(204, 0, 0));
        jLabel20.setText("Pending");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 50, -1, -1));

        lblPending.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPending.setForeground(new java.awt.Color(204, 0, 0));
        lblPending.setText("000");
        jPanel2.add(lblPending, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 80, -1, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("From");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 7, -1, -1));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setText("To");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 7, -1, -1));

        btnDateRange.setBackground(new java.awt.Color(153, 0, 0));
        btnDateRange.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDateRange.setForeground(new java.awt.Color(255, 255, 255));
        btnDateRange.setText("Fetch By Date Range");
        btnDateRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateRangeActionPerformed(evt);
            }
        });
        jPanel2.add(btnDateRange, new org.netbeans.lib.awtextra.AbsoluteConstraints(576, 7, -1, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Tailors");
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(838, 14, -1, -1));

        jComboTailorFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel2.add(jComboTailorFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 40, 144, -1));

        btnFetch.setBackground(new java.awt.Color(51, 102, 0));
        btnFetch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFetch.setForeground(new java.awt.Color(255, 255, 255));
        btnFetch.setText("Fetch");
        btnFetch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFetchActionPerformed(evt);
            }
        });
        jPanel2.add(btnFetch, new org.netbeans.lib.awtextra.AbsoluteConstraints(835, 78, -1, -1));
        jPanel2.add(jDateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 220, -1));
        jPanel2.add(jDateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 210, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 950, 110));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Payment Method");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 180, -1, -1));

        jComboPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "POS", "Transfer" }));
        getContentPane().add(jComboPaymentMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 210, 140, -1));

        jPanel1.setBackground(new java.awt.Color(27, 33, 56));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leefmancare-logo.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 6, 191, 187));

        btnLogout.setBackground(new java.awt.Color(204, 0, 0));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        jPanel1.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 630, -1, -1));

        btnOrders4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders4.setText("Special Events");
        btnOrders4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders4ActionPerformed(evt);
            }
        });
        jPanel1.add(btnOrders4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 580, -1, -1));

        btnTransactionHistory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTransactionHistory.setText("Transaction History");
        btnTransactionHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionHistoryActionPerformed(evt);
            }
        });
        jPanel1.add(btnTransactionHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 530, 199, -1));

        btnMaterialsAndStock.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMaterialsAndStock.setText("Materials & Stock");
        btnMaterialsAndStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaterialsAndStockActionPerformed(evt);
            }
        });
        jPanel1.add(btnMaterialsAndStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 199, -1));

        btnOrders3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders3.setText("Loss");
        btnOrders3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders3ActionPerformed(evt);
            }
        });
        jPanel1.add(btnOrders3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, 141, -1));

        btnOrders.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders.setText("Orders");
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });
        jPanel1.add(btnOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 141, -1));

        btnTailors.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTailors.setText("Tailors");
        btnTailors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTailorsActionPerformed(evt);
            }
        });
        jPanel1.add(btnTailors, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 141, -1));

        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer.setText("Customers");
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        jPanel1.add(btnCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 141, -1));

        btnHome2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHome2.setText("Dashboard");
        btnHome2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHome2ActionPerformed(evt);
            }
        });
        jPanel1.add(btnHome2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 141, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 780));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Tailor Commission");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 260, -1, -1));

        txtTailorCommission.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtTailorCommission.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTailorCommissionKeyReleased(evt);
            }
        });
        getContentPane().add(txtTailorCommission, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 290, 156, -1));
        getContentPane().add(jDateReceived, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 240, 280, -1));
        getContentPane().add(jDueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 300, 280, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        findCustomerByNameID();
        findTailorByNameID();
        getAllOrders();
        findTailorByNameIDFilter();

        getTotalOrders();
        getTotalEarnings();
        getCompleted();
        getInProgress();
        getPending();


    }//GEN-LAST:event_formComponentShown

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        Customer selectedCustomer = (Customer) jComboCustomers.getSelectedItem();
        Tailor selectedTailor = (Tailor) jComboTailors.getSelectedItem();

        if (selectedCustomer == null || selectedTailor == null) {
            JOptionPane.showMessageDialog(null, "Select a Customer/ Assign a Tailor");
            return;
        }

        if (jDateReceived.getDate() == null || jDueDate.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Select both dates");
            return;
        }

        double totalCost = Double.parseDouble(txtTotalCost.getText());
        double amountPaid = Double.parseDouble(txtAmountPaid.getText());
        double commission = Double.parseDouble(txtTailorCommission.getText());

        if (amountPaid > totalCost) {
            JOptionPane.showMessageDialog(null, "Amount paid cannot exceed total cost");
            return;
        }

        if (commission > totalCost) {
            JOptionPane.showMessageDialog(null, "Commission cannot exceed total cost");
            return;
        }

        if (amountPaid + commission > totalCost) {
            JOptionPane.showMessageDialog(null,
                    "Amount paid + Tailor Commission cannot exceed total cost. Please adjust.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
        int customerId = selectedCustomer.getCustomerId();

        Orders orders = new Orders();
        orders.setCustomerId(customerId);
        orders.setDateReceived(mysqlFormat.format(jDateReceived.getDate()));
        orders.setDueDate(mysqlFormat.format(jDueDate.getDate()));
        orders.setTotalCost(totalCost);  // Keep original total cost
        orders.setAmountPaid(amountPaid);
        orders.setTailorId(selectedTailor.getTailorId());
        orders.setPaymentMethod((String) jComboPaymentMethod.getSelectedItem());
        orders.setTailorCommission(commission);

        int orderId = OrderDao.saveOrder(orders);

        if (orderId > 0) {
            JOptionPane.showMessageDialog(this,
                    "Order saved successfully, Please proceed to enter order details",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            new OrderDetails(orderId, customerId).setVisible(true);
            setVisible(false);

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save order",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        jComboCustomers.setSelectedItem(-1);
        txtTotalCost.setText("");
        txtAmountPaid.setText("");
        txtBalance.setText("");
        btnSubmit.setEnabled(false);
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtTotalCostKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalCostKeyReleased
        // TODO add your handling code here:
        validateFields();
        calculateBalance();
    }//GEN-LAST:event_txtTotalCostKeyReleased

    private void txtAmountPaidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAmountPaidKeyReleased
        // TODO add your handling code here:
        validateFields();
        calculateBalance();
    }//GEN-LAST:event_txtAmountPaidKeyReleased

    private void txtBalanceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBalanceKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtBalanceKeyReleased

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String name = txtSearch.getText();
        String searchedName = "%" + name.toLowerCase() + "%";
        getAllOrders(searchedName);
    }//GEN-LAST:event_txtSearchKeyReleased

    private void btnDateRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDateRangeActionPerformed
        // TODO add your handling code here:
        refreshOrderStats();
    }//GEN-LAST:event_btnDateRangeActionPerformed

    private void btnFetchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFetchActionPerformed
        // TODO add your handling code here:
        refreshTailorStats();
    }//GEN-LAST:event_btnFetchActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        TableModel model = jTable1.getModel();

        int orderId = Integer.parseInt(model.getValueAt(index, 0).toString());
        String customerName = model.getValueAt(index, 1).toString();
        String currentStatus = model.getValueAt(index, 11).toString();
        double balance = Double.parseDouble(model.getValueAt(index, 8).toString());
        double cost = Double.parseDouble(model.getValueAt(index, 6).toString());
        double paid = Double.parseDouble(model.getValueAt(index, 7).toString());
        String paymentMethod = model.getValueAt(index, 10).toString();

        int exit = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to View detail of Mr/Mrs " + customerName + " ?",
                "Question", JOptionPane.OK_CANCEL_OPTION);

//        System.out.println(orderId);
        if (exit == 0) {
            setVisible(false);
            new CustomerOrderDetails(orderId, currentStatus, balance, cost, paid, paymentMethod).setVisible(true);
        }


    }//GEN-LAST:event_jTable1MouseClicked

    private void txtTailorCommissionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTailorCommissionKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtTailorCommissionKeyReleased

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        int logout = JOptionPane.showConfirmDialog(null, "Are sure you want to logout?", "Warning", JOptionPane.OK_CANCEL_OPTION);

        if (logout == 0) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnOrders4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrders4ActionPerformed
        setVisible(false);
        new SpecialProjects().setVisible(true);
    }//GEN-LAST:event_btnOrders4ActionPerformed

    private void btnTransactionHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionHistoryActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new TransactionHistory().setVisible(true);
    }//GEN-LAST:event_btnTransactionHistoryActionPerformed

    private void btnMaterialsAndStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaterialsAndStockActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Materials().setVisible(true);
    }//GEN-LAST:event_btnMaterialsAndStockActionPerformed

    private void btnOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdersActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Order().setVisible(true);
    }//GEN-LAST:event_btnOrdersActionPerformed

    private void btnTailorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTailorsActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Tailors().setVisible(true);
    }//GEN-LAST:event_btnTailorsActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Customers().setVisible(true);
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnOrders3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrders3ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Loss().setVisible(true);
    }//GEN-LAST:event_btnOrders3ActionPerformed

    private void btnHome2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHome2ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new GeneralDashboard().setVisible(true);
    }//GEN-LAST:event_btnHome2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Order().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnDateRange;
    private javax.swing.JButton btnFetch;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnHome1;
    private javax.swing.JButton btnHome2;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMaterialsAndStock;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnOrders3;
    private javax.swing.JButton btnOrders4;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnTailors;
    private javax.swing.JButton btnTransactionHistory;
    private javax.swing.JComboBox<Customer> jComboCustomers;
    private javax.swing.JComboBox<String> jComboPaymentMethod;
    private javax.swing.JComboBox<Tailor> jComboTailorFilter;
    private javax.swing.JComboBox<Tailor> jComboTailors;
    private com.toedter.calendar.JDateChooser jDateFrom;
    private com.toedter.calendar.JDateChooser jDateReceived;
    private com.toedter.calendar.JDateChooser jDateTo;
    private com.toedter.calendar.JDateChooser jDueDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblCompleted;
    private javax.swing.JLabel lblInProgress;
    private javax.swing.JLabel lblPending;
    private javax.swing.JLabel lblTotalEarnings;
    private javax.swing.JLabel lblTotalOrder;
    private javax.swing.JTextField txtAmountPaid;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTailorCommission;
    private javax.swing.JTextField txtTotalCost;
    // End of variables declaration//GEN-END:variables
}
