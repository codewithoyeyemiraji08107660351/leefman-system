/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.CustomerDao;
import dao.MaterialDao;
import dbConnection.ConnectionProvider;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.sql.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import models.Customer;
import models.Material;

/**
 *
 * @author oyeyemi
 */
public class PlaceMaterialOrder extends javax.swing.JFrame {

    private int customerId;
    private int materialId;
    private double grandTotal;
    private String orderId;

    private String quantityPattern = "^[0-9]*[.]*[0-9]+$";

    /**
     * Creates new form PlaceMaterialOrder
     */
    public PlaceMaterialOrder() {
        initComponents();

        txtProductName.setEditable(false);
        txtSalesPrice.setEditable(false);
        btnAddToCart.setEnabled(false);

        jTableMaterialList.getColumnModel().getColumn(4)
                .setCellRenderer(new DefaultTableCellRenderer() {

                    @Override
                    public void setValue(Object value) {
                        if (value instanceof ImageIcon) {
                            setIcon((ImageIcon) value);
                            setText("");
                        } else {
                            setIcon(null);
                            setText("");
                        }
                    }
                });
    }

    public void validateQuantity() {
        String quantity = txtQuantity.getText();

        if (quantity.matches(quantityPattern)) {
            btnAddToCart.setEnabled(true);
        } else {
            btnAddToCart.setEnabled(false);
        }
    }

    public void findCustomerByNameID() {
        List<Customer> allCustomers = CustomerDao.findCustomerByNameID();

        jComboCustomerList.removeAllItems();

        for (Customer cusObj : allCustomers) {
            jComboCustomerList.addItem(cusObj);
        }
    }

    private void getMaterials() {

        List<Material> allMaterials = MaterialDao.getAllMaterials();
        DefaultTableModel model = (DefaultTableModel) jTableMaterialList.getModel();

        model.setRowCount(0);
        for (Material material : allMaterials) {

            ImageIcon icon = null;

            if (material.getImagePath() != null && !material.getImagePath().isEmpty()) {
                File file = new File(material.getImagePath());

                if (file.exists()) {
                    ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());

                    Image scaledImage = originalIcon.getImage()
                            .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

                    icon = new ImageIcon(scaledImage);
                }
            }

            model.addRow(new Object[]{
                material.getMaterialId(),
                material.getName(),
                material.getQuantity(),
                material.getSellingPrice(),
                icon
            });
        }

        jTableMaterialList.setRowHeight(70);
    }

    public String generatePurchaseId(String prefix) {
        return prefix + System.nanoTime();
    }

    private void showLargeImage(ImageIcon icon) {
        if (icon == null) {
            return;
        }

        ZoomableImageLabel zoomLabel = new ZoomableImageLabel(icon);

        JScrollPane scrollPane = new JScrollPane(zoomLabel);
        scrollPane.setPreferredSize(new Dimension(900, 650));

        // Buttons
        JButton btnZoomIn = new JButton("Zoom In +");
        JButton btnZoomOut = new JButton("Zoom Out -");
        JButton btnReset = new JButton("Reset");

        btnZoomIn.addActionListener(e -> zoomLabel.zoomIn());
        btnZoomOut.addActionListener(e -> zoomLabel.zoomOut());
        btnReset.addActionListener(e -> zoomLabel.resetZoom());

        JPanel controls = new JPanel();
        controls.add(btnZoomIn);
        controls.add(btnZoomOut);
        controls.add(btnReset);

        JDialog dialog = new JDialog();
        dialog.setTitle("Image Preview (Scroll + Mouse Wheel Zoom)");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(controls, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jComboCustomerList = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMaterialList = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCart = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtSalesPrice = new javax.swing.JTextField();
        btnAddToCart = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(0, 0, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 16, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setText("Customer List");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 66, -1, -1));

        jComboCustomerList.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jComboCustomerList, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 97, 208, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("Materials/Stock List");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(469, 66, -1, -1));

        jTableMaterialList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Quantity", "Selling Price", "Image"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableMaterialList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMaterialListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableMaterialList);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 97, 430, 219));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 51));
        jLabel3.setText("Cart");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 70, -1, -1));

        jTableCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Quantity", "Selling Price", "SubTotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCartMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableCart);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(803, 97, 401, 219));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("Leefman Record System  -  Materials Purchase");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(229, 6, 736, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Product Name");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 334, -1, -1));

        txtProductName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 365, 390, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Quantity");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 480, -1, -1));

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantityKeyReleased(evt);
            }
        });
        getContentPane().add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 510, 400, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Sales Price");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 410, -1, -1));

        txtSalesPrice.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtSalesPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 440, 400, -1));

        btnAddToCart.setBackground(new java.awt.Color(0, 0, 51));
        btnAddToCart.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAddToCart.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToCart.setText("Add To Cart");
        btnAddToCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToCartActionPerformed(evt);
            }
        });
        getContentPane().add(btnAddToCart, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 560, 250, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Grand Total: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(803, 334, -1, -1));

        lblGrandTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblGrandTotal.setForeground(new java.awt.Color(255, 0, 0));
        lblGrandTotal.setText("00000");
        getContentPane().add(lblGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 334, -1, -1));

        btnSave.setBackground(new java.awt.Color(0, 0, 51));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 380, 270, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMaterialListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMaterialListMouseClicked
        // TODO add your handling code here:
        int row = jTableMaterialList.getSelectedRow();
        TableModel model = jTableMaterialList.getModel();

        materialId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String productName = model.getValueAt(row, 1).toString();
        double sellingPrice = Double.parseDouble(model.getValueAt(row, 3).toString());

        txtProductName.setText(productName);
        txtSalesPrice.setText(String.format("%.2f", sellingPrice));
        if (row < 0) {
            return;
        }
        Object value = jTableMaterialList.getValueAt(row, 4);
        if (value == null) {
            return;
        }

        if (value instanceof ImageIcon icon) {
            showLargeImage(icon);
        } else {
            JOptionPane.showMessageDialog(this, "No image found in column 4.");
        }


    }//GEN-LAST:event_jTableMaterialListMouseClicked

    private void jTableCartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCartMouseClicked
        // TODO add your handling code here:
        int selectedRow = jTableCart.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an item to remove!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTableCart.getModel();

        String itemName = model.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to remove \"" + itemName + "\" from cart?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            double subTotal = Double.parseDouble(model.getValueAt(selectedRow, 4).toString());

            grandTotal -= subTotal;
            if (grandTotal < 0) {
                grandTotal = 0;
            }

            lblGrandTotal.setText(String.format("%.2f", grandTotal));

            model.removeRow(selectedRow);
        }
    }//GEN-LAST:event_jTableCartMouseClicked

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        getMaterials();
        findCustomerByNameID();
    }//GEN-LAST:event_formComponentShown

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Materials().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        Customer selectedCustomer = (Customer) jComboCustomerList.getSelectedItem();

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(null, "Please select a customer!");
            return;
        }

        if (jTableCart.getRowCount() == 0 || grandTotal == 0) {
            JOptionPane.showMessageDialog(null, "Cart is empty!");
            return;
        }

        customerId = selectedCustomer.getCustomerId();
        String orderId = generatePurchaseId("stock-");

        String insertSQL = """
        INSERT INTO material_purchases
        (material_id, amount, quantity_bought, customer_id, subtotal, order_id)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        String updateStockSQL = """
        UPDATE materials
        SET quantity = quantity - ?
        WHERE material_id = ?
    """;

        try (Connection con = ConnectionProvider.getCon()) {

            con.setAutoCommit(false);

            try (PreparedStatement psInsert = con.prepareStatement(insertSQL); PreparedStatement psUpdateStock = con.prepareStatement(updateStockSQL)) {

                DefaultTableModel model = (DefaultTableModel) jTableCart.getModel();

                for (int i = 0; i < model.getRowCount(); i++) {

                    int stockId = Integer.parseInt(model.getValueAt(i, 0).toString());
                    double qtyBought = Double.parseDouble(model.getValueAt(i, 2).toString());
                    double price = Double.parseDouble(model.getValueAt(i, 3).toString());
                    double subtotal = Double.parseDouble(model.getValueAt(i, 4).toString());

                    psInsert.setInt(1, stockId);
                    psInsert.setDouble(2, price);
                    psInsert.setDouble(3, qtyBought);
                    psInsert.setInt(4, customerId);
                    psInsert.setDouble(5, subtotal);
                    psInsert.setString(6, orderId);
                    psInsert.addBatch();

                    psUpdateStock.setDouble(1, qtyBought);
                    psUpdateStock.setInt(2, stockId);
                    psUpdateStock.addBatch();
                }

                psInsert.executeBatch();
                psUpdateStock.executeBatch();

                con.commit();

                JOptionPane.showMessageDialog(null,
                        "Purchase saved successfully!\nOrder ID: " + orderId);

                model.setRowCount(0);
                grandTotal = 0;
                lblGrandTotal.setText("0.00");

                getMaterials();

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving purchase!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error!");

        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddToCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToCartActionPerformed
        // TODO add your handling code here:
        if (txtQuantity.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter quantity!");
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(txtQuantity.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity!");
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(null, "Quantity must be greater than 0");
            return;
        }

        String name = txtProductName.getText();
        double price = Double.parseDouble(txtSalesPrice.getText());
        double subTotal = price * quantity;

        int checkStock = 0;
        int checkStockAlreadyInCart = 0;

        String check = "SELECT quantity FROM materials WHERE material_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(check)) {

            pstmt.setInt(1, materialId);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                double availableQty = res.getDouble("quantity");

                if (availableQty >= quantity) {
                    checkStock = 1;
                } else {
                    JOptionPane.showMessageDialog(null,
                            name + " is out of stock, Only " + availableQty + " left");
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        DefaultTableModel model = (DefaultTableModel) jTableCart.getModel();

        if (checkStock == 1) {
            for (int i = 0; i < jTableCart.getRowCount(); i++) {
                int stockInCart = Integer.parseInt(model.getValueAt(i, 0).toString());

                if (materialId == stockInCart) {
                    checkStockAlreadyInCart = 1;
                    JOptionPane.showMessageDialog(null, name + " already exist in Cart");
                    txtProductName.setText("");
                    txtQuantity.setText("");
                    txtSalesPrice.setText("");
                    return;
                }
            }
        }
        if (checkStockAlreadyInCart == 0) {

            Object[] cartItems = new Object[5];
            cartItems[0] = materialId;
            cartItems[1] = name;
            cartItems[2] = quantity;
            cartItems[3] = price;
            cartItems[4] = subTotal;

            model.addRow(cartItems);

            grandTotal += subTotal;
            lblGrandTotal.setText(String.format("%.2f", grandTotal));
            txtProductName.setText("");
            txtQuantity.setText("");
            txtSalesPrice.setText("");
        }
    }//GEN-LAST:event_btnAddToCartActionPerformed

    private void txtQuantityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantityKeyReleased
        // TODO add your handling code here:
        validateQuantity();
    }//GEN-LAST:event_txtQuantityKeyReleased

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
            java.util.logging.Logger.getLogger(PlaceMaterialOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlaceMaterialOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlaceMaterialOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlaceMaterialOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlaceMaterialOrder().setVisible(true);
            }
        });
    }

    public class ZoomableImageLabel extends JLabel {

        private Image originalImage;
        private double scale = 1.0;

        public ZoomableImageLabel(ImageIcon icon) {
            this.originalImage = icon.getImage();
            setHorizontalAlignment(JLabel.CENTER);
            updateImage();

            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() < 0) {
                        zoomIn();
                    } else {
                        zoomOut();
                    }
                }
            });
        }

        private void updateImage() {
            int newWidth = (int) (originalImage.getWidth(null) * scale);
            int newHeight = (int) (originalImage.getHeight(null) * scale);

            Image scaled = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaled));

            revalidate();
            repaint();
        }

        public void zoomIn() {
            scale += 0.1;
            updateImage();
        }

        public void zoomOut() {
            if (scale > 0.2) {
                scale -= 0.1;
                updateImage();
            }
        }

        public void resetZoom() {
            scale = 1.0;
            updateImage();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddToCart;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<Customer> jComboCustomerList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableCart;
    private javax.swing.JTable jTableMaterialList;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSalesPrice;
    // End of variables declaration//GEN-END:variables
}
