/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.CustomerDao;
import dao.PurchaseDao;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import models.Customer;
import models.PurchaseReceipt;
import models.Totals;

/**
 *
 * @author oyeyemi
 */
public class TransactionHistory extends javax.swing.JFrame {

    /**
     * Creates new form TransactionHistory
     */
    public TransactionHistory() {
        initComponents();
    }

    public void getCustomers() {
        List<Customer> customerList = CustomerDao.allCustomers();
        Iterator<Customer> itr = customerList.iterator();

        DefaultTableModel model = (DefaultTableModel) jTableCustomerList.getModel();
        while (itr.hasNext()) {
            Customer custObj = itr.next();
            model.addRow(new Object[]{
                custObj.getCustomerId(),
                custObj.getName(),
                custObj.getPhoneNo()
            });
        }
    }

    public void getPurchasesByOrderId() {
        List<PurchaseReceipt> purchaseList = PurchaseDao.getPurchasesByOrderId();

        DefaultTableModel model = (DefaultTableModel) jTableAllTransactions.getModel();
        model.setRowCount(0);

        for (PurchaseReceipt pr : purchaseList) {
            model.addRow(new Object[]{
                pr.getOrderId(),
                pr.getMaterialName(),
                pr.getPurchasePrice(),
                pr.getQuantityBought(),
                pr.getSubtotal(),
                pr.getPurchaseDate()
            });
        }
    }

    public void getPurchasesByOrderId(int customerId) {
        List<PurchaseReceipt> purchaseList = PurchaseDao.getPurchasesByCustomerId(customerId);

        DefaultTableModel model = (DefaultTableModel) jTableAllTransactions.getModel();
        model.setRowCount(0);

        for (PurchaseReceipt pr : purchaseList) {
            model.addRow(new Object[]{
                pr.getOrderId(),
                pr.getMaterialName(),
                pr.getPurchasePrice(),
                pr.getQuantityBought(),
                pr.getSubtotal(),
                pr.getPurchaseDate()
            });
        }
    }

    private void displayReceipt(String orderId) {

        List<PurchaseReceipt> list = PurchaseDao.getPurchasesByOrderId(orderId);

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No purchase found for this Order ID!");
            return;
        }

        double grandTotal = 0;
        PurchaseReceipt first = list.get(0);

        StringBuilder sb = new StringBuilder();

        sb.append("      ★★ LEEFMAN FASHION DESIGNER ★★\n");
        sb.append("             PURCHASE RECEIPT\n");
        sb.append("--------------------------------\n");
        sb.append(String.format("Order ID : %s\n", first.getOrderId()));
        sb.append(String.format("Customer : %s\n", first.getCustomerName()));
        sb.append(String.format("Phone    : %s\n", first.getCustomerPhone()));
        sb.append(String.format("Date     : %s\n", first.getPurchaseDate()));
        sb.append("--------------------------------\n");

        sb.append(String.format("%-10s %5s %8s %8s\n", "Item", "Qty", "Price", "Subtotal"));
        sb.append("--------------------------------\n");

        for (PurchaseReceipt pr : list) {
            sb.append(String.format("%-10.10s %5.2f %8.2f %8.2f\n",
                    pr.getMaterialName(),
                    pr.getQuantityBought(),
                    pr.getPurchasePrice(),
                    pr.getSubtotal()
            ));
            grandTotal += pr.getSubtotal();
        }

        sb.append("--------------------------------\n");
        sb.append(String.format("%-23s %8.2f\n", "GRAND TOTAL:", grandTotal));
        sb.append("--------------------------------\n");
        sb.append("      THANK YOU FOR SHOPPING!\n");
        sb.append("          Visit Again ★\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));

        JOptionPane.showMessageDialog(null, scrollPane, "Purchase Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    public void getOrdersByDateRange(String from, String to) {
        List<PurchaseReceipt> orderList = PurchaseDao.getOrdersByDateRange(from, to);

        DefaultTableModel model = (DefaultTableModel) jTableAllTransactions.getModel();
        model.setRowCount(0);

        for (PurchaseReceipt pr : orderList) {
            model.addRow(new Object[]{
                pr.getOrderId(),
                pr.getMaterialName(),
                pr.getPurchasePrice(),
                pr.getQuantityBought(),
                pr.getSubtotal(),
                pr.getPurchaseDate()
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCustomerList = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAllTransactions = new javax.swing.JTable();
        jLabel22 = new javax.swing.JLabel();
        jDateFrom = new com.toedter.calendar.JDateChooser();
        jLabel23 = new javax.swing.JLabel();
        jDateTo = new com.toedter.calendar.JDateChooser();
        btnSearch = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblFrom = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblCostPrice = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblRevenue = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblProfit = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblQuantitySold = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("Leefman Record System  -  Transactions History");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 6, 736, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setText("Customer List");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 123, -1, -1));

        jTableCustomerList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Phone Number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCustomerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCustomerListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCustomerList);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 154, 437, 377));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("All Transactions");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(832, 123, -1, -1));

        jTableAllTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Transaction ID", "Material Name", "Price", "Quantity", "SubTotal", "Purchase Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableAllTransactions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableAllTransactionsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableAllTransactions);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 154, 663, 340));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("From");
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 78, -1, -1));
        getContentPane().add(jDateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 78, 198, -1));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setText("To");
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(814, 78, -1, -1));
        getContentPane().add(jDateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(837, 78, 198, -1));

        btnSearch.setBackground(new java.awt.Color(204, 0, 0));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        getContentPane().add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(1047, 78, -1, -1));

        btnBack.setBackground(new java.awt.Color(0, 0, 51));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        getContentPane().add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 16, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Materials Sold between");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 506, -1, -1));

        lblFrom.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblFrom.setForeground(new java.awt.Color(204, 0, 0));
        lblFrom.setText("0");
        getContentPane().add(lblFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(766, 506, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("to");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(801, 506, -1, -1));

        lblTo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTo.setForeground(new java.awt.Color(153, 0, 0));
        lblTo.setText("0");
        getContentPane().add(lblTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(837, 506, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 51, 0));
        jLabel7.setText("Cost Price:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 568, -1, -1));

        lblCostPrice.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCostPrice.setForeground(new java.awt.Color(204, 51, 0));
        lblCostPrice.setText("0000");
        getContentPane().add(lblCostPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 568, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 51));
        jLabel9.setText("Revenue:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 537, -1, -1));

        lblRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblRevenue.setForeground(new java.awt.Color(0, 0, 51));
        lblRevenue.setText("0000");
        getContentPane().add(lblRevenue, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 537, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 51, 0));
        jLabel11.setText("Profit: ");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 630, -1, -1));

        lblProfit.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblProfit.setForeground(new java.awt.Color(0, 51, 0));
        lblProfit.setText("000");
        getContentPane().add(lblProfit, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 630, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 0, 255));
        jLabel8.setText("Quantity Sold: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 599, -1, -1));

        lblQuantitySold.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblQuantitySold.setForeground(new java.awt.Color(51, 0, 255));
        lblQuantitySold.setText("0000");
        getContentPane().add(lblQuantitySold, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 599, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Materials().setVisible(true);
    }//GEN-LAST:event_btnBackActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        getCustomers();
        getPurchasesByOrderId();
    }//GEN-LAST:event_formComponentShown

    private void jTableCustomerListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCustomerListMouseClicked
        // TODO add your handling code here:
        int row = jTableCustomerList.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Please select a customer you view his/her transaction history",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TableModel model = jTableCustomerList.getModel();
        int customerId = Integer.parseInt(model.getValueAt(row, 0).toString());

        getPurchasesByOrderId(customerId);
    }//GEN-LAST:event_jTableCustomerListMouseClicked

    private void jTableAllTransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableAllTransactionsMouseClicked
        // TODO add your handling code here:
        int row = jTableAllTransactions.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Please select a customer you view his/her transaction history",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TableModel model = jTableAllTransactions.getModel();
        String orderId = model.getValueAt(row, 0).toString();

        displayReceipt(orderId);
    }//GEN-LAST:event_jTableAllTransactionsMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String from = dateFormatter.format(jDateFrom.getDate());
        String to = dateFormatter.format(jDateTo.getDate());

//        System.out.println(from);
//        System.out.println(to);
        getOrdersByDateRange(from, to);

        Totals total = PurchaseDao.getTotalsByDateRange(from, to);

        lblFrom.setText(from);
        lblTo.setText(to);

        lblRevenue.setText(String.format("%.2f", total.getTotalRevenue()));
        lblCostPrice.setText(String.format("%.2f", total.getTotalCost()));
        lblQuantitySold.setText(String.format("%.2f", total.getTotalQuantity()));
        lblProfit.setText(String.format("%.2f", total.getTotalProfit()));

        jDateFrom.getDateEditor().setDate(null);
        jDateTo.getDateEditor().setDate(null);

    }//GEN-LAST:event_btnSearchActionPerformed

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
            java.util.logging.Logger.getLogger(TransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransactionHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransactionHistory().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSearch;
    private com.toedter.calendar.JDateChooser jDateFrom;
    private com.toedter.calendar.JDateChooser jDateTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableAllTransactions;
    private javax.swing.JTable jTableCustomerList;
    private javax.swing.JLabel lblCostPrice;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblProfit;
    private javax.swing.JLabel lblQuantitySold;
    private javax.swing.JLabel lblRevenue;
    private javax.swing.JLabel lblTo;
    // End of variables declaration//GEN-END:variables
}
