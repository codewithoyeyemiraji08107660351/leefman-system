/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.OrderDao;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import models.OrderDetail;
import models.OrderImage;

/**
 *
 * @author oyeyemi
 */
public final class CustomerOrderDetails extends javax.swing.JFrame {

    private int orderId;
    private String currentStatus;
    private double balance;
    private double cost;
    private double paid;
    private String paymentMethod;

    private File imageFile1;
    private File imageFile2;
    private File imageFile3;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * Creates new form CustomerOrderDetai
     *
     * @param orderId
     * @param currentStatus
     * @param balance
     * @param cost
     * @param paid
     * @param paymentMethod
     */
    public CustomerOrderDetails(int orderId, String currentStatus, double balance, double cost, double paid, String paymentMethod) {
        initComponents();
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.balance = balance;
        this.cost = cost;
        this.paid = paid;
        this.paymentMethod = paymentMethod;

        btnUpdate.setEnabled(false);
        txtBalance.setEditable(false);

        getOrderDetailsById(this.orderId);
        getAllBeforeImages(this.orderId);
        getAllAfterImages(this.orderId);
    }

    public void getOrderDetailsById(int orderId) {
        List<OrderDetail> orderDetailList = OrderDao.getOrderDetailsById(orderId);

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (OrderDetail detail : orderDetailList) {
            model.addRow(new Object[]{
                detail.getDetialsId(),
                detail.getSewingStyle(),
                detail.getItemUsed(),
                detail.getAccessories(),
                detail.getMeasurement()
            });
        }
    }

    public void validateFields() {
        String sewingStyle = txtSewingStyle.getText();
        String itemUsed = txtItemUsed.getText();
        String accessories = txtAccessories.getText();
        String measurement = txtMeasurement.getText();

        if (!sewingStyle.equals("") && !itemUsed.equals("") && !accessories.equals("") && !measurement.equals("")) {
            btnUpdate.setEnabled(true);
        } else {
            btnUpdate.setEnabled(false);
        }
    }

    public void clearFields() {
        txtAccessories.setText("");
        txtSewingStyle.setText("");
        txtItemUsed.setText("");
        txtMeasurement.setText("");
        btnUpdate.setEnabled(false);
    }

    private void loadImageFromPath(String path, JLabel label) {

        if (path == null || path.isEmpty()) {
            label.setIcon(null);
            label.setText("No Image");
            return;
        }

        File file = new File(path);

        if (!file.exists()) {
            label.setText("Image not found");
            return;
        }

        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        Image img = icon.getImage().getScaledInstance(
                label.getWidth(),
                label.getHeight(),
                Image.SCALE_SMOOTH
        );

        label.setIcon(new ImageIcon(img));
        label.setText("");
    }

    public void getAllBeforeImages(int orderId) {
        List<OrderImage> orderImageList = OrderDao.getAllBeforeImages(orderId);

        for (OrderImage image : orderImageList) {
            loadImageFromPath(image.getImagePath(), lblImage1);
            loadImageFromPath(image.getSecondImagePath(), lblImage2);
            loadImageFromPath(image.getThirdImagePath(), lblImage3);
            lblMaterialColor.setText(image.getMaterialColor());
            lblPurchaseFrom.setText(image.getPurchaseFrom());

        }
    }

    public void getAllAfterImages(int orderId) {
        List<OrderImage> orderImageList = OrderDao.getAllAfterImages(orderId);

        for (OrderImage image : orderImageList) {
            loadImageFromPath(image.getImageAfterFirst(), lblImageAfter1);
            loadImageFromPath(image.getImageAfterSecond(), lblImageAfter2);
//            loadImageFromPath(image.getImageAfterThird(), lblImageAfter3);
            loadImageFromPath(image.getMaterialUsed(), lblImageAfter3);

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtAccessories = new javax.swing.JTextField();
        txtItemUsed = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSewingStyle = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMeasurement = new javax.swing.JTextArea();
        btnUpdate = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblImage1 = new javax.swing.JLabel();
        lblImage2 = new javax.swing.JLabel();
        lblImage3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblImageAfter1 = new javax.swing.JLabel();
        lblImageAfter3 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        btnUploadImage1 = new javax.swing.JButton();
        btnUploadImage2 = new javax.swing.JButton();
        btnUploadImage3 = new javax.swing.JButton();
        btnUploadImages = new javax.swing.JButton();
        lblImageAfter2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jComboStatus = new javax.swing.JComboBox<>();
        btnUpdateStatus = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        btnBalance = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblCost = new javax.swing.JLabel();
        lblPaid = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        lblPurchaseFrom = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblPaymentMethod = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblMaterialColor = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnOrders4 = new javax.swing.JButton();
        btnTransactionHistory = new javax.swing.JButton();
        btnMaterialsAndStock = new javax.swing.JButton();
        btnOrders3 = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnTailors = new javax.swing.JButton();
        btnHome = new javax.swing.JButton();
        btnCustomer1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 784, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1864, 0, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Leefman Record System -  Order Summary");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 600, -1));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Accessories");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 161, -1, -1));

        txtAccessories.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtAccessories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccessoriesKeyReleased(evt);
            }
        });
        jPanel3.add(txtAccessories, new org.netbeans.lib.awtextra.AbsoluteConstraints(142, 158, 283, -1));

        txtItemUsed.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtItemUsed.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemUsedKeyReleased(evt);
            }
        });
        jPanel3.add(txtItemUsed, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 109, 283, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Items Used");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 112, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Measurement");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 207, -1, -1));

        txtSewingStyle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtSewingStyle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSewingStyleKeyReleased(evt);
            }
        });
        jPanel3.add(txtSewingStyle, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 60, 283, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Sewing Style");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 63, -1, -1));

        txtMeasurement.setColumns(20);
        txtMeasurement.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtMeasurement.setRows(5);
        txtMeasurement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMeasurementKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(txtMeasurement);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 207, 284, 101));

        btnUpdate.setBackground(new java.awt.Color(153, 0, 0));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel3.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(234, 314, -1, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 51));
        jLabel12.setText("Update Measurement Details");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 17, -1, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(267, 47, 440, 360));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 51));
        jLabel4.setText("Before");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 0, 0));
        jLabel5.setText("Image 1");

        lblImage1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage1MouseClicked(evt);
            }
        });

        lblImage2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage2MouseClicked(evt);
            }
        });

        lblImage3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImage3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImage3MouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 0, 0));
        jLabel13.setText("Image 2");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(153, 0, 0));
        jLabel14.setText("Image 3");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblImage1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImage2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImage3, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jLabel5)
                        .addGap(171, 171, 171)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(96, 96, 96)
                                .addComponent(jLabel14)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImage3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(lblImage1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblImage2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 51));
        jLabel7.setText("After/Materials");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 0));
        jLabel8.setText("Image 1");

        lblImageAfter1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImageAfter1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageAfter1MouseClicked(evt);
            }
        });

        lblImageAfter3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImageAfter3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageAfter3MouseClicked(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(153, 0, 0));
        jLabel15.setText("Image 2");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(153, 0, 0));
        jLabel16.setText("Material Used");

        btnUploadImage1.setBackground(new java.awt.Color(153, 0, 0));
        btnUploadImage1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUploadImage1.setForeground(new java.awt.Color(255, 255, 255));
        btnUploadImage1.setText("Browse");
        btnUploadImage1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImage1ActionPerformed(evt);
            }
        });

        btnUploadImage2.setBackground(new java.awt.Color(153, 0, 0));
        btnUploadImage2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUploadImage2.setForeground(new java.awt.Color(255, 255, 255));
        btnUploadImage2.setText("Browse");
        btnUploadImage2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImage2ActionPerformed(evt);
            }
        });

        btnUploadImage3.setBackground(new java.awt.Color(153, 0, 0));
        btnUploadImage3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUploadImage3.setForeground(new java.awt.Color(255, 255, 255));
        btnUploadImage3.setText("Browse");
        btnUploadImage3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImage3ActionPerformed(evt);
            }
        });

        btnUploadImages.setBackground(new java.awt.Color(0, 0, 51));
        btnUploadImages.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUploadImages.setForeground(new java.awt.Color(255, 255, 255));
        btnUploadImages.setText("Upload");
        btnUploadImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImagesActionPerformed(evt);
            }
        });

        lblImageAfter2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImageAfter2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageAfter2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(lblImageAfter1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(lblImageAfter2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblImageAfter3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addComponent(jLabel8)
                                .addGap(171, 171, 171)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                                .addComponent(jLabel16)
                                .addGap(88, 88, 88))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(308, 308, 308)
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(btnUploadImage1)
                .addGap(124, 124, 124)
                .addComponent(btnUploadImage2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUploadImage3)
                .addGap(53, 53, 53))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUploadImages, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblImageAfter3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblImageAfter1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblImageAfter2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUploadImage1)
                    .addComponent(btnUploadImage2)
                    .addComponent(btnUploadImage3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUploadImages)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 330, -1, 350));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 51));
        jLabel3.setText("Measurement Details");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 470, -1, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Sewing Style", "Items Used", "Accessories", "Measurements"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
        jScrollPane2.setViewportView(jTable1);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 500, 430, 70));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 51));
        jLabel17.setText("Status ");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 570, -1, 20));

        jComboStatus.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "In Progress", "Completed" }));
        getContentPane().add(jComboStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 600, 260, 30));

        btnUpdateStatus.setBackground(new java.awt.Color(153, 0, 0));
        btnUpdateStatus.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpdateStatus.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateStatus.setText("Update Status");
        btnUpdateStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStatusActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdateStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 640, -1, 30));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 102, 0));
        jLabel18.setText("Status:");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 20, -1, -1));

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStatus.setText("jLabel19");
        getContentPane().add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 20, 130, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 102));
        jLabel19.setText("Balance");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 410, -1, -1));

        txtBalance.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 440, 150, -1));

        btnBalance.setBackground(new java.awt.Color(153, 0, 0));
        btnBalance.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBalance.setForeground(new java.awt.Color(255, 255, 255));
        btnBalance.setText("Pay Up Balance");
        btnBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBalanceActionPerformed(evt);
            }
        });
        getContentPane().add(btnBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 440, -1, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 102, 0));
        jLabel20.setText("Cost");
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 0, -1, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 102, 0));
        jLabel21.setText("Amount Paid");
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 0, -1, -1));

        lblCost.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCost.setForeground(new java.awt.Color(0, 102, 0));
        lblCost.setText("jLabel22");
        getContentPane().add(lblCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 20, -1, -1));

        lblPaid.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPaid.setForeground(new java.awt.Color(153, 0, 0));
        lblPaid.setText("jLabel23");
        getContentPane().add(lblPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 20, -1, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 51));
        jLabel24.setText("Purchase From:");
        getContentPane().add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 580, -1, -1));

        lblPurchaseFrom.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblPurchaseFrom.setText("jLabel25");
        getContentPane().add(lblPurchaseFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 600, -1, -1));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 51));
        jLabel25.setText("Payment Method: ");
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 430, -1, -1));

        lblPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPaymentMethod.setText("jLabel25");
        getContentPane().add(lblPaymentMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 450, -1, -1));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 51));
        jLabel26.setText("Material Color:");
        getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 640, -1, -1));

        lblMaterialColor.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblMaterialColor.setText("jLabel25");
        getContentPane().add(lblMaterialColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 640, -1, -1));

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

        btnHome.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHome.setText("Home");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        jPanel1.add(btnHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 141, -1));

        btnCustomer1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer1.setText("Customers");
        btnCustomer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomer1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnCustomer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 141, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 780));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtAccessoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccessoriesKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtAccessoriesKeyReleased

    private void txtItemUsedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemUsedKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtItemUsedKeyReleased

    private void txtSewingStyleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSewingStyleKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtSewingStyleKeyReleased

    private void txtMeasurementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMeasurementKeyReleased
        // TODO add your handling code here:
        validateFields();
    }//GEN-LAST:event_txtMeasurementKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        lblStatus.setText(currentStatus);
        lblPaymentMethod.setText(paymentMethod);
        lblCost.setText(String.format("%.2f", cost));
        lblPaid.setText(String.format("%.2f", paid));
        txtBalance.setText(String.format("%.2f", balance));
    }//GEN-LAST:event_formComponentShown

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        TableModel model = jTable1.getModel();

        String sewingStyle = model.getValueAt(index, 1).toString();
        String itemUsed = model.getValueAt(index, 2).toString();
        String accessories = model.getValueAt(index, 3).toString();
        String measurement = model.getValueAt(index, 4).toString();

        txtSewingStyle.setText(sewingStyle);
        txtItemUsed.setText(itemUsed);
        txtAccessories.setText(accessories);
        txtMeasurement.setText(measurement);
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        String sewingStyle = txtSewingStyle.getText();
        String itemUsed = txtItemUsed.getText();
        String accessories = txtAccessories.getText();
        String measurements = txtMeasurement.getText();

        OrderDao.updateOrderDetail(orderId, sewingStyle, itemUsed, accessories, measurements);
        getOrderDetailsById(this.orderId);
        clearFields();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnUpdateStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusActionPerformed
        // TODO add your handling code here:
        String status = (String) jComboStatus.getSelectedItem();
        OrderDao.updateOrderStatus(this.orderId, status);
        lblStatus.setText(status);
    }//GEN-LAST:event_btnUpdateStatusActionPerformed

    private void btnBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBalanceActionPerformed
        // TODO add your handling code here:
        double currentBalance = Double.parseDouble(txtBalance.getText());
        if (currentBalance < 0) {
            JOptionPane.showMessageDialog(this, "Balance has been fully Paid!");
        }
        OrderDao.markOrderAsFullyPaid(this.orderId);

        txtBalance.setText("");
        lblPaid.setText(String.format("%.2f", this.cost));
        btnBalance.setEnabled(false);
    }//GEN-LAST:event_btnBalanceActionPerformed

    private void btnUploadImage1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImage1ActionPerformed
        // TODO add your handling code here:
        imageFile1 = chooseImageFile();
        if (imageFile1 != null) {
            showImagePreview(imageFile1, lblImageAfter1);
        }
    }//GEN-LAST:event_btnUploadImage1ActionPerformed

    private void btnUploadImage2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImage2ActionPerformed
        // TODO add your handling code here:
        imageFile2 = chooseImageFile();
        if (imageFile2 != null) {
            lblImage2.setText(imageFile2.getName());
            showImagePreview(imageFile2, lblImageAfter2);
        }
    }//GEN-LAST:event_btnUploadImage2ActionPerformed

    private void btnUploadImage3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImage3ActionPerformed
        // TODO add your handling code here:
        imageFile3 = chooseImageFile();
        if (imageFile3 != null) {
            lblImage3.setText(imageFile3.getName());
            showImagePreview(imageFile3, lblImageAfter3);
        }
    }//GEN-LAST:event_btnUploadImage3ActionPerformed

    private void btnUploadImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImagesActionPerformed
        // TODO add your handling code here:
        if (orderId <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid order.");
            return;
        }

        try {

            String path1 = imageFile1 != null ? saveImageToFolder(imageFile1, orderId) : null;
            String path2 = imageFile2 != null ? saveImageToFolder(imageFile2, orderId) : null;
            String path3 = imageFile3 != null ? saveImageToFolder(imageFile3, orderId) : null;

            OrderDao.uploadSewedImage(orderId, path1, path2, path3);
            getAllAfterImages(this.orderId);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving images.");
        }
    }//GEN-LAST:event_btnUploadImagesActionPerformed

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

    private void lblImageAfter1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageAfter1MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImageAfter1);

    }//GEN-LAST:event_lblImageAfter1MouseClicked

    private void lblImageAfter3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageAfter3MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImageAfter3);
    }//GEN-LAST:event_lblImageAfter3MouseClicked

    private void lblImageAfter2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageAfter2MouseClicked
        // TODO add your handling code here:
        showLargeImage(lblImageAfter2);
    }//GEN-LAST:event_lblImageAfter2MouseClicked

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

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new GeneralDashboard().setVisible(true);
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnCustomer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomer1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCustomer1ActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(CustomerOrderDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(CustomerOrderDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(CustomerOrderDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(CustomerOrderDetails.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new CustomerOrderDetails(orderId).setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBalance;
    private javax.swing.JButton btnCustomer1;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMaterialsAndStock;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnOrders3;
    private javax.swing.JButton btnOrders4;
    private javax.swing.JButton btnTailors;
    private javax.swing.JButton btnTransactionHistory;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateStatus;
    private javax.swing.JButton btnUploadImage1;
    private javax.swing.JButton btnUploadImage2;
    private javax.swing.JButton btnUploadImage3;
    private javax.swing.JButton btnUploadImages;
    private javax.swing.JComboBox<String> jComboStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblImage1;
    private javax.swing.JLabel lblImage2;
    private javax.swing.JLabel lblImage3;
    private javax.swing.JLabel lblImageAfter1;
    private javax.swing.JLabel lblImageAfter2;
    private javax.swing.JLabel lblImageAfter3;
    private javax.swing.JLabel lblMaterialColor;
    private javax.swing.JLabel lblPaid;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblPurchaseFrom;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField txtAccessories;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextField txtItemUsed;
    private javax.swing.JTextArea txtMeasurement;
    private javax.swing.JTextField txtSewingStyle;
    // End of variables declaration//GEN-END:variables
}
