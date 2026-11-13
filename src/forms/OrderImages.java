/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.MaterialDao;
import dao.OrderDao;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import models.Material;
import models.OrderImage;

/**
 *
 * @author oyeyemi
 */
public class OrderImages extends javax.swing.JFrame {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static int orderId;
    private static int customerId;
    private List<String> materialImagePaths = new ArrayList<>();

    private File imageFile1;
    private File imageFile2;
    private File imageFile3;
    private File imageFile4;

    /**
     * Creates new form OrderImages
     *
     * @param orderId
     * @param customerId
     */
    public OrderImages(int orderId, int customerId) {
        initComponents();
        btnSave.setEnabled(false);
        OrderImages.orderId = orderId;
        OrderImages.customerId = customerId;

        if (orderId <= 0) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Invalid or missing order.\nPlease create an order first.",
                        "Order Required",
                        JOptionPane.ERROR_MESSAGE);
                this.dispose();
                new Order().setVisible(true);
            });
            return;
        }

        setTitle("Order Images - Order #" + orderId);
    }

    private File chooseImageFile() {

        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter filter
                = new FileNameExtensionFilter("Image files (JPG, PNG)", "jpg", "jpeg", "png");

        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            if (file.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this,
                        "Image size must be less than 2MB",
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

    private String saveImageToFolder(File sourceFile, int orderId) throws IOException {

        String baseFolder = "uploads/orders/order_" + orderId + "/";

        File folder = new File(baseFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String newFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        File destination = new File(baseFolder + newFileName);

        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        //System.out.println(new File(baseFolder).getAbsolutePath());
        return baseFolder + newFileName;
    }

    private void showImagePreview(File imageFile, JLabel label) {

        try {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());

            Image img = icon.getImage();

            Image scaled = img.getScaledInstance(
                    label.getWidth(),
                    label.getHeight(),
                    Image.SCALE_SMOOTH
            );

            label.setIcon(new ImageIcon(scaled));
            label.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLargeImage(JLabel sourceLabel) {
        if (sourceLabel.getIcon() == null) {
            return;
        }

        ImageIcon icon = (ImageIcon) sourceLabel.getIcon();

        JLabel bigLabel = new JLabel(icon);
        bigLabel.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(bigLabel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JDialog dialog = new JDialog();
        dialog.setTitle("Image Preview");
        dialog.setModal(true);
        dialog.add(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

    }

    public void getCustomerMaterials(int customerId) {
        List<Material> materials = MaterialDao.getCustomerMaterials(customerId);

        jComboMaterialPurchased.removeAllItems();
        materialImagePaths.clear();

        if (materials.isEmpty()) {
            jComboMaterialPurchased.addItem("No materials found");
            jComboMaterialPurchased.setEnabled(false);
            return;
        }

        for (Material m : materials) {
            String name = m.getName();
            String imagePath = m.getImagePath();

            materialImagePaths.add(imagePath);
            jComboMaterialPurchased.addItem(name);
        }

        jComboMaterialPurchased.setEnabled(true);

        for (java.awt.event.ActionListener al : jComboMaterialPurchased.getActionListeners()) {
            jComboMaterialPurchased.removeActionListener(al);
        }

        jComboMaterialPurchased.addActionListener(e -> displaySelectedMaterialImage());
        if (!materialImagePaths.isEmpty()) {
            displayMaterialImage(0);
        }
    }

    private void displaySelectedMaterialImage() {
        int selectedIndex = jComboMaterialPurchased.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < materialImagePaths.size()) {
            displayMaterialImage(selectedIndex);
        }
    }

    private void displayMaterialImage(int index) {
        String imagePath = materialImagePaths.get(index);
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image scaledImage = icon.getImage().getScaledInstance(
                        lblImage4.getWidth() > 0 ? lblImage4.getWidth() : 200,
                        lblImage4.getHeight() > 0 ? lblImage4.getHeight() : 200,
                        Image.SCALE_SMOOTH
                );
                lblImage4.setIcon(new ImageIcon(scaledImage));
                lblImage4.setText("");
            } else {
                lblImage4.setIcon(null);
                lblImage4.setText("Image not found");
            }
        } else {
            lblImage4.setIcon(null);
            lblImage4.setText("No Image Available");
        }
    }

    private void validateFields(){
        Icon image1 = lblImage1.getIcon();
        
        if(image1 != null){
            btnSave.setEnabled(true);
        }else{
            btnSave.setEnabled(false);
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

        jLabel2 = new javax.swing.JLabel();
        lblImage1 = new javax.swing.JLabel();
        lblImage2 = new javax.swing.JLabel();
        lblImage3 = new javax.swing.JLabel();
        btnUpload1 = new javax.swing.JButton();
        btnUpload2 = new javax.swing.JButton();
        btnUpload3 = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboPurchaseFrom = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtMaterialColor = new javax.swing.JTextField();
        lblImage4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboMaterialPurchased = new javax.swing.JComboBox<>();
        btnUpload4 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btnLogout1 = new javax.swing.JButton();
        btnOrders4 = new javax.swing.JButton();
        btnTransactionHistory = new javax.swing.JButton();
        btnMaterialsAndStock = new javax.swing.JButton();
        btnOrders3 = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnTailors = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnHome3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Leefman Record System -  Order Images");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(279, 6, 632, -1));

        lblImage1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblImage1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage1MouseClicked(evt);
            }
        });
        getContentPane().add(lblImage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 59, 243, 237));

        lblImage2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblImage2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage2MouseClicked(evt);
            }
        });
        getContentPane().add(lblImage2, new org.netbeans.lib.awtextra.AbsoluteConstraints(636, 59, 243, 237));

        lblImage3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblImage3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage3MouseClicked(evt);
            }
        });
        getContentPane().add(lblImage3, new org.netbeans.lib.awtextra.AbsoluteConstraints(891, 59, 243, 237));

        btnUpload1.setBackground(new java.awt.Color(0, 0, 51));
        btnUpload1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpload1.setForeground(new java.awt.Color(255, 255, 255));
        btnUpload1.setText("Browse");
        btnUpload1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpload1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpload1, new org.netbeans.lib.awtextra.AbsoluteConstraints(429, 302, -1, -1));

        btnUpload2.setBackground(new java.awt.Color(0, 0, 51));
        btnUpload2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpload2.setForeground(new java.awt.Color(255, 255, 255));
        btnUpload2.setText("Browse");
        btnUpload2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpload2ActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpload2, new org.netbeans.lib.awtextra.AbsoluteConstraints(717, 302, -1, -1));

        btnUpload3.setBackground(new java.awt.Color(0, 0, 51));
        btnUpload3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpload3.setForeground(new java.awt.Color(255, 255, 255));
        btnUpload3.setText("Browse");
        btnUpload3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpload3ActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpload3, new org.netbeans.lib.awtextra.AbsoluteConstraints(972, 302, -1, -1));

        btnSave.setBackground(new java.awt.Color(102, 0, 0));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 490, 286, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Purchase From");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 363, -1, -1));

        jComboPurchaseFrom.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboPurchaseFrom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Our Shop", "Others" }));
        getContentPane().add(jComboPurchaseFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 360, 208, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Material Color");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(738, 363, -1, -1));

        txtMaterialColor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtMaterialColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(865, 360, 269, -1));

        lblImage4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblImage4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage4MouseClicked(evt);
            }
        });
        getContentPane().add(lblImage4, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 400, 243, 237));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Materials Purchased");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 450, -1, -1));

        jComboMaterialPurchased.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(jComboMaterialPurchased, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 480, 250, -1));

        btnUpload4.setBackground(new java.awt.Color(0, 0, 51));
        btnUpload4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpload4.setForeground(new java.awt.Color(255, 255, 255));
        btnUpload4.setText("Browse");
        btnUpload4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpload4ActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpload4, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 650, -1, -1));

        jPanel3.setBackground(new java.awt.Color(27, 33, 56));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leefmancare-logo.png"))); // NOI18N
        jLabel8.setText("jLabel1");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 6, 191, 187));

        btnLogout1.setBackground(new java.awt.Color(204, 0, 0));
        btnLogout1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout1.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout1.setText("Logout");
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel3.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, -1, -1));

        btnOrders4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders4.setText("Special Events");
        btnOrders4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders4ActionPerformed(evt);
            }
        });
        jPanel3.add(btnOrders4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 580, -1, -1));

        btnTransactionHistory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTransactionHistory.setText("Transaction History");
        btnTransactionHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionHistoryActionPerformed(evt);
            }
        });
        jPanel3.add(btnTransactionHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 530, 199, -1));

        btnMaterialsAndStock.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMaterialsAndStock.setText("Materials & Stock");
        btnMaterialsAndStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaterialsAndStockActionPerformed(evt);
            }
        });
        jPanel3.add(btnMaterialsAndStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 199, -1));

        btnOrders3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders3.setText("Loss");
        btnOrders3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders3ActionPerformed(evt);
            }
        });
        jPanel3.add(btnOrders3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, 141, -1));

        btnOrders.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders.setText("Orders");
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });
        jPanel3.add(btnOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 141, -1));

        btnTailors.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTailors.setText("Tailors");
        btnTailors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTailorsActionPerformed(evt);
            }
        });
        jPanel3.add(btnTailors, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 141, -1));

        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer.setText("Customers");
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        jPanel3.add(btnCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 141, -1));

        btnHome3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHome3.setText("Dashboard");
        btnHome3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHome3ActionPerformed(evt);
            }
        });
        jPanel3.add(btnHome3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 141, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 780));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpload1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpload1ActionPerformed
        // TODO add your handling code here:
        
        imageFile1 = chooseImageFile();
        if (imageFile1 != null) {
             btnSave.setEnabled(true);
            showImagePreview(imageFile1, lblImage1);
        }else{
            btnSave.setEnabled(false);
        }
    }//GEN-LAST:event_btnUpload1ActionPerformed

    private void btnUpload2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpload2ActionPerformed
        // TODO add your handling code here:
        imageFile2 = chooseImageFile();
        if (imageFile2 != null) {
            lblImage2.setText(imageFile2.getName());
            showImagePreview(imageFile2, lblImage2);
        }
    }//GEN-LAST:event_btnUpload2ActionPerformed

    private void btnUpload3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpload3ActionPerformed
        // TODO add your handling code here:
        imageFile3 = chooseImageFile();
        if (imageFile3 != null) {
            lblImage3.setText(imageFile3.getName());
            showImagePreview(imageFile3, lblImage3);
        }
    }//GEN-LAST:event_btnUpload3ActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        if (orderId <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid order.");
            return;
        }

        try {
            String path1 = imageFile1 != null ? saveImageToFolder(imageFile1, orderId) : null;
            String path2 = imageFile2 != null ? saveImageToFolder(imageFile2, orderId) : null;
            String path3 = imageFile3 != null ? saveImageToFolder(imageFile3, orderId) : null;
            
            String path4 = null;
            if (imageFile4 != null) {
                
                path4 = saveImageToFolder(imageFile4, orderId);
            } else {
                
                int selectedIndex = jComboMaterialPurchased.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < materialImagePaths.size()) {
                    path4 = materialImagePaths.get(selectedIndex);
                }
            }

            OrderImage image = new OrderImage();
            image.setOrderId(orderId);
            image.setImageType("Before");
            image.setImagePath(path1);
            image.setSecondImagePath(path2);
            image.setThirdImagePath(path3);
            image.setMaterialUsed(path4); 
            image.setPurchaseFrom((String) jComboPurchaseFrom.getSelectedItem());
            image.setMaterialColor(txtMaterialColor.getText());

            int result = OrderDao.saveOrder(image);

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Images saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                new Order().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save images.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving images.");
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void lblImage1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage1MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImage1);
    }//GEN-LAST:event_lblImage1MouseClicked

    private void lblImage2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage2MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImage2);
    }//GEN-LAST:event_lblImage2MouseClicked

    private void lblImage3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage3MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImage3);
    }//GEN-LAST:event_lblImage3MouseClicked

    private void lblImage4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImage4MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImage4);
    }//GEN-LAST:event_lblImage4MouseClicked

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        
        getCustomerMaterials(customerId);
    }//GEN-LAST:event_formComponentShown

    private void btnUpload4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpload4ActionPerformed
        // TODO add your handling code here:
        imageFile4 = chooseImageFile();
        if (imageFile4 != null) {
            lblImage4.setText(imageFile4.getName());
            showImagePreview(imageFile4, lblImage4);
        }
    }//GEN-LAST:event_btnUpload4ActionPerformed

    private void btnLogout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogout1ActionPerformed
        // TODO add your handling code here:
        int logout = JOptionPane.showConfirmDialog(null, "Are sure you want to logout?", "Warning", JOptionPane.OK_CANCEL_OPTION);

        if (logout == 0) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }//GEN-LAST:event_btnLogout1ActionPerformed

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

    private void btnOrders3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrders3ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Loss().setVisible(true);
    }//GEN-LAST:event_btnOrders3ActionPerformed

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

    private void btnHome3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHome3ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new GeneralDashboard().setVisible(true);
    }//GEN-LAST:event_btnHome3ActionPerformed

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
            java.util.logging.Logger.getLogger(OrderImages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderImages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderImages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderImages.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrderImages(orderId, customerId).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnHome2;
    private javax.swing.JButton btnHome3;
    private javax.swing.JButton btnLogout1;
    private javax.swing.JButton btnMaterialsAndStock;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnOrders3;
    private javax.swing.JButton btnOrders4;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTailors;
    private javax.swing.JButton btnTransactionHistory;
    private javax.swing.JButton btnUpload1;
    private javax.swing.JButton btnUpload2;
    private javax.swing.JButton btnUpload3;
    private javax.swing.JButton btnUpload4;
    private javax.swing.JComboBox<String> jComboMaterialPurchased;
    private javax.swing.JComboBox<String> jComboPurchaseFrom;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblImage1;
    private javax.swing.JLabel lblImage2;
    private javax.swing.JLabel lblImage3;
    private javax.swing.JLabel lblImage4;
    private javax.swing.JTextField txtMaterialColor;
    // End of variables declaration//GEN-END:variables
}
