/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.MaterialDao;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import models.Material;

/**
 *
 * @author oyeyemi
 */
public class Materials extends javax.swing.JFrame {

    private String pricePattern = "^[0-9]*[.]*[0-9]+$";

    private int materialId;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private File imageFile;

    /**
     * Creates new form Materials
     */
    public Materials() {
        initComponents();
        btnSave.setEnabled(false);
        btnUpdate.setEnabled(false);

        jTable1.getColumnModel().getColumn(7)
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

    public void validateField() {
        String name = txtName.getText();
        String category = txtCategory.getText();
        String description = txtDescription.getText();
        String quantity = txtQuantity.getText();
        String purchasePrice = txtPurchasePrice.getText();
        String sellingPrice = txtSellingPrice.getText();

        if (!name.equals("") && !category.equals("") && !description.equals("") && !quantity.equals("")
                && purchasePrice.matches(pricePattern) && sellingPrice.matches(pricePattern)) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
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

    private String saveImageToFolder(File sourceFile) throws IOException {

        String baseFolder = "uploads/stocks/material_stock/";

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

    public void clear() {
        txtName.setText("");
        txtCategory.setText("");
        txtDescription.setText("");
        txtPurchasePrice.setText("");
        txtSellingPrice.setText("");
        txtQuantity.setText("");
        lblImage.setIcon(null);
        btnSave.setEnabled(false);
        btnUpdate.setEnabled(false);
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

    private void getMaterials() {

        List<Material> allMaterials = MaterialDao.getAllMaterials();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

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
                material.getCategory(),
                material.getDescription(),
                material.getQuantity(),
                material.getPurchasePrice(),
                material.getSellingPrice(),
                icon
            });
        }

        jTable1.setRowHeight(70);
    }

    private void showImagePreview(ImageIcon icon, JLabel label) {
        int width = label.getWidth() > 0 ? label.getWidth() : 200;
        int height = label.getHeight() > 0 ? label.getHeight() : 200;

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImage.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(icon.getImage(), 0, 0, width, height, null);
        g2.dispose();

        label.setIcon(new ImageIcon(resizedImage));
        label.setText("");
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
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblStockValue = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblTodayTransaction = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTodaySales = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        txtDescription = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtPurchasePrice = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtSellingPrice = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnClear = new javax.swing.JButton();
        btnMakePurchase = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnLogout1 = new javax.swing.JButton();
        btnOrders4 = new javax.swing.JButton();
        btnTransactionHistory = new javax.swing.JButton();
        btnMaterialsAndStock = new javax.swing.JButton();
        btnOrders3 = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnTailors = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnHome = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Leefman Record System  -  Materials");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(279, 0, 632, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 102, 0));
        jLabel4.setText("Stock Value");

        lblStockValue.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblStockValue.setForeground(new java.awt.Color(51, 102, 0));
        lblStockValue.setText("000");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 0, 0));
        jLabel6.setText("Total Quantity");

        lblQuantity.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblQuantity.setForeground(new java.awt.Color(204, 0, 0));
        lblQuantity.setText("000");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 102, 0));
        jLabel8.setText("Today Transaction");

        lblTodayTransaction.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTodayTransaction.setForeground(new java.awt.Color(51, 102, 0));
        lblTodayTransaction.setText("000");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 51));
        jLabel13.setText("Today Sales");

        lblTodaySales.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTodaySales.setForeground(new java.awt.Color(0, 0, 51));
        lblTodaySales.setText("000");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblStockValue))
                    .addComponent(jLabel4))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel6)
                        .addGap(70, 70, 70))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblQuantity)
                        .addGap(123, 123, 123)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel8)
                        .addContainerGap(34, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblTodaySales)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTodayTransaction)
                        .addGap(112, 112, 112))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTodaySales)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTodayTransaction)
                                .addComponent(lblQuantity))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStockValue)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 710, 90));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Name");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 198, -1, -1));

        txtName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });
        getContentPane().add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(409, 195, 290, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Category");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 255, -1, -1));

        txtCategory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCategoryKeyReleased(evt);
            }
        });
        getContentPane().add(txtCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(409, 252, 290, -1));

        txtDescription.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescriptionKeyReleased(evt);
            }
        });
        getContentPane().add(txtDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(409, 309, 290, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Description");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 312, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Purchase Price");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 413, -1, -1));

        txtPurchasePrice.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtPurchasePrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPurchasePriceKeyReleased(evt);
            }
        });
        getContentPane().add(txtPurchasePrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(409, 410, 290, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Selling Price");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 462, -1, -1));

        txtSellingPrice.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtSellingPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSellingPriceKeyReleased(evt);
            }
        });
        getContentPane().add(txtSellingPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(409, 459, 290, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Upload Image");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 521, -1, -1));

        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });
        getContentPane().add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 510, 181, 171));

        btnBrowse.setBackground(new java.awt.Color(0, 0, 0));
        btnBrowse.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnBrowse.setForeground(new java.awt.Color(255, 255, 255));
        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        getContentPane().add(btnBrowse, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 560, -1, -1));

        btnSave.setBackground(new java.awt.Color(51, 102, 0));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 610, -1, -1));

        btnUpdate.setBackground(new java.awt.Color(153, 0, 0));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 650, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Quantity");
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(273, 364, -1, -1));

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtQuantityKeyReleased(evt);
            }
        });
        getContentPane().add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(409, 361, 290, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Category", "Description", "Quantity", "PurchasePrice", "Selling Price", "Image"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(717, 195, 550, -1));

        btnClear.setBackground(new java.awt.Color(255, 204, 0));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        getContentPane().add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 510, -1, -1));

        btnMakePurchase.setBackground(new java.awt.Color(0, 0, 51));
        btnMakePurchase.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnMakePurchase.setForeground(new java.awt.Color(255, 255, 255));
        btnMakePurchase.setText("Make Purchase");
        btnMakePurchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMakePurchaseActionPerformed(evt);
            }
        });
        getContentPane().add(btnMakePurchase, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 150, -1, -1));

        jPanel3.setBackground(new java.awt.Color(27, 33, 56));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leefmancare-logo.png"))); // NOI18N
        jLabel15.setText("jLabel1");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 6, 191, 187));

        btnLogout1.setBackground(new java.awt.Color(204, 0, 0));
        btnLogout1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout1.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout1.setText("Logout");
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel3.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 620, -1, -1));

        btnOrders4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders4.setText("Special Events");
        btnOrders4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders4ActionPerformed(evt);
            }
        });
        jPanel3.add(btnOrders4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, -1, -1));

        btnTransactionHistory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTransactionHistory.setText("Transaction History");
        btnTransactionHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionHistoryActionPerformed(evt);
            }
        });
        jPanel3.add(btnTransactionHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, 199, -1));

        btnMaterialsAndStock.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMaterialsAndStock.setText("Materials & Stock");
        btnMaterialsAndStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaterialsAndStockActionPerformed(evt);
            }
        });
        jPanel3.add(btnMaterialsAndStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, 199, -1));

        btnOrders3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders3.setText("Loss");
        btnOrders3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders3ActionPerformed(evt);
            }
        });
        jPanel3.add(btnOrders3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 141, -1));

        btnOrders.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders.setText("Orders");
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });
        jPanel3.add(btnOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 141, -1));

        btnTailors.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTailors.setText("Tailors");
        btnTailors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTailorsActionPerformed(evt);
            }
        });
        jPanel3.add(btnTailors, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 141, -1));

        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer.setText("Customers");
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        jPanel3.add(btnCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 141, -1));

        btnHome.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHome.setText("Dashboard");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        jPanel3.add(btnHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 141, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 780));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtNameKeyReleased

    private void txtCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtCategoryKeyReleased

    private void txtDescriptionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescriptionKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtDescriptionKeyReleased

    private void txtQuantityKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantityKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtQuantityKeyReleased

    private void txtPurchasePriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPurchasePriceKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtPurchasePriceKeyReleased

    private void txtSellingPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSellingPriceKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtSellingPriceKeyReleased

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        // TODO add your handling code here:
        imageFile = chooseImageFile();
        if (imageFile != null) {
            showImagePreview(imageFile, lblImage);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImage);
    }//GEN-LAST:event_lblImageMouseClicked

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            // TODO add your handling code here:
            String path1 = imageFile != null ? saveImageToFolder(imageFile) : null;

            Material m = new Material();

            m.setName(txtName.getText());
            m.setCategory(txtCategory.getText());
            m.setDescription(txtDescription.getText());
            m.setQuantity(Double.parseDouble(txtQuantity.getText()));
            m.setPurchasePrice(Double.parseDouble(txtPurchasePrice.getText()));
            m.setSellingPrice(Double.parseDouble(txtSellingPrice.getText()));
            m.setImagePath(path1);

            MaterialDao.saveMaterials(m);
            clear();
        } catch (IOException ex) {
            Logger.getLogger(Materials.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        getMaterials();
        lblStockValue.setText(String.format("%.2f", MaterialDao.getTotalStockValueBySellingPrice()));
        lblQuantity.setText(String.format("%.2f", MaterialDao.getTotalStockQuantity()));
        lblTodaySales.setText(String.format("%.2f", MaterialDao.getTodayTotalSales()));
        lblTodayTransaction.setText(String.format("%d", MaterialDao.getTodayTransactionCount()));
    }//GEN-LAST:event_formComponentShown

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:       
        try {
            String name = txtName.getText();
            String category = txtCategory.getText();
            String description = txtDescription.getText();
            double quantity = Double.parseDouble(txtQuantity.getText());
            double purchasePrice = Double.parseDouble(txtPurchasePrice.getText());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText());
            String path1 = imageFile != null ? saveImageToFolder(imageFile) : null;

            MaterialDao.update(materialId, name, category, description, quantity, sellingPrice, purchasePrice, path1);
            clear();
            getMaterials();
        } catch (IOException ex) {
            Logger.getLogger(Materials.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnUpdateActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        btnUpdate.setEnabled(true);
        int index = jTable1.getSelectedRow();
        TableModel model = jTable1.getModel();

        materialId = Integer.parseInt(model.getValueAt(index, 0).toString());
        String name = model.getValueAt(index, 1).toString();
        String category = model.getValueAt(index, 2).toString();
        String description = model.getValueAt(index, 3).toString();
        double quantity = Double.parseDouble(model.getValueAt(index, 4).toString());
        double purchasePrice = Double.parseDouble(model.getValueAt(index, 5).toString());
        double sellingPrice = Double.parseDouble(model.getValueAt(index, 6).toString());

        Object obj = model.getValueAt(index, 7);
        if (obj instanceof ImageIcon) {
            ImageIcon icon = (ImageIcon) obj;
            showImagePreview(icon, lblImage);
        } else {
            lblImage.setIcon(null);
            lblImage.setText("No Image");
        }

        txtName.setText(name);
        txtCategory.setText(category);
        txtDescription.setText(description);
        txtQuantity.setText(String.format("%.2f", quantity));
        txtPurchasePrice.setText(String.format("%.2f", purchasePrice));
        txtSellingPrice.setText(String.format("%.2f", sellingPrice));


    }//GEN-LAST:event_jTable1MouseClicked

    private void btnMakePurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMakePurchaseActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new PlaceMaterialOrder().setVisible(true);
    }//GEN-LAST:event_btnMakePurchaseActionPerformed

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

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new GeneralDashboard().setVisible(true);
    }//GEN-LAST:event_btnHomeActionPerformed

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
            java.util.logging.Logger.getLogger(Materials.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Materials.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Materials.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Materials.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Materials().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnLogout1;
    private javax.swing.JButton btnMakePurchase;
    private javax.swing.JButton btnMaterialsAndStock;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnOrders3;
    private javax.swing.JButton btnOrders4;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTailors;
    private javax.swing.JButton btnTransactionHistory;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblStockValue;
    private javax.swing.JLabel lblTodaySales;
    private javax.swing.JLabel lblTodayTransaction;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPurchasePrice;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSellingPrice;
    // End of variables declaration//GEN-END:variables
}
