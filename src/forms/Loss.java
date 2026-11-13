/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.LossDao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author oyeyemi
 */
public class Loss extends javax.swing.JFrame {

    private File imageFile1;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private String costPattern = "^[0-9]*[.]*[0-9]+$";

    /**
     * Creates new form Extras
     */
    public Loss() {
        initComponents();
        btnSubmit.setEnabled(false);

        jTable1.getColumnModel().getColumn(8)
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

//        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                jTable1MouseClicked(evt);
//            }
//        });
    }

    public void validateField() {
        String title = txtTitle.getText();
        String description = txtDescription.getText();
        String purpose = txtPurpose.getText();
        String cost = txtCost.getText();
//        String profitLoss = txtProfitLost.getText();
        String responsiblePerson = txtResponsiblePerson.getText();
        String note = txtNote.getText();
        String category = txtCategory.getText();

        if (!title.equals("") && !description.equals("") && !category.equals("")
                && !purpose.equals("") && cost.matches(costPattern)
                && !responsiblePerson.equals("") && !note.equals("")) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    public void clear() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtCategory.setText("");
        txtPurpose.setText("");
        txtCost.setText("");
//        txtProfitLost.setText("");
        txtResponsiblePerson.setText("");
        txtNote.setText("");
        btnSubmit.setEnabled(false);
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

        String baseFolder = "uploads/extras/";

        File folder = new File(baseFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String newFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        File destination = new File(baseFolder + newFileName);

        Files.copy(sourceFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return baseFolder + newFileName;
    }

    private void updateSummaryStats(List<models.Loss> extras) {
        int totalRecords = extras.size();
        double totalLoss = 0;

        for (models.Loss ex : extras) {
            totalLoss += ex.getCost();
        }

        lblTotalRecords.setText(String.valueOf(totalRecords));
        lblTotalLoss.setText(String.format("₦%,.2f", totalLoss));
    }

    public void getAllExtras() {
        List<models.Loss> allExtras = LossDao.getAllExtras();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);
        for (models.Loss ex : allExtras) {

            ImageIcon icon = null;

            if (ex.getImagePath() != null && !ex.getImagePath().isEmpty()) {
                File file = new File(ex.getImagePath());

                if (file.exists()) {
                    ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());

                    Image scaledImage = originalIcon.getImage()
                            .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

                    icon = new ImageIcon(scaledImage);
                }
            }

            model.addRow(new Object[]{
                ex.getExtra_id(),
                ex.getTitle(),
                ex.getDescription(),
                ex.getCategory(),
                ex.getPurpose(),
                ex.getCost(),
                //                ex.getProfitLost(),
                ex.getResponsiblePerson(),
                ex.getNotes(),
                icon,
                ex.getExtraDate()
            });
        }

        jTable1.setRowHeight(70);

        updateSummaryStats(allExtras);
    }

    public void getAllExtras(String searchText) {
        List<models.Loss> allExtras = LossDao.getAllExtras(searchText);
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);
        for (models.Loss ex : allExtras) {

            ImageIcon icon = null;

            if (ex.getImagePath() != null && !ex.getImagePath().isEmpty()) {
                File file = new File(ex.getImagePath());

                if (file.exists()) {
                    ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());

                    Image scaledImage = originalIcon.getImage()
                            .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

                    icon = new ImageIcon(scaledImage);
                }
            }

            model.addRow(new Object[]{
                ex.getExtra_id(),
                ex.getTitle(),
                ex.getDescription(),
                ex.getCategory(),
                ex.getPurpose(),
                ex.getCost(),
                //                ex.getProfitLost(),
                ex.getResponsiblePerson(),
                ex.getNotes(),
                icon,
                ex.getExtraDate()
            });
        }

        jTable1.setRowHeight(70);

        updateSummaryStats(allExtras);
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
        txtTitle = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPurpose = new javax.swing.JTextField();
        txtCost = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtResponsiblePerson = new javax.swing.JTextField();
        txtNote = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        txtBrowse = new javax.swing.JButton();
        btnSubmit = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        btnCustomer = new javax.swing.JButton();
        btnTailors = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnMaterialsAndStock = new javax.swing.JButton();
        btnLogout3 = new javax.swing.JButton();
        btnOrders3 = new javax.swing.JButton();
        btnTransactionHistory = new javax.swing.JButton();
        btnOrders4 = new javax.swing.JButton();
        btnCustomer1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTotalRecords = new javax.swing.JLabel();
        lblTotalLoss = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Leefman Record System - Loss");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(253, 0, 500, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Title");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 164, -1, -1));

        txtTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtTitle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTitleKeyReleased(evt);
            }
        });
        getContentPane().add(txtTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 164, 350, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Description");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 207, -1, -1));

        txtDescription.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescriptionKeyReleased(evt);
            }
        });
        getContentPane().add(txtDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 207, 350, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Purpose");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 297, -1, -1));

        txtPurpose.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtPurpose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPurposeKeyReleased(evt);
            }
        });
        getContentPane().add(txtPurpose, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 297, 350, -1));

        txtCost.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtCost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCostKeyReleased(evt);
            }
        });
        getContentPane().add(txtCost, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 340, 350, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Cost");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 340, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Responsible Person");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(736, 167, -1, -1));

        txtResponsiblePerson.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtResponsiblePerson.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtResponsiblePersonKeyReleased(evt);
            }
        });
        getContentPane().add(txtResponsiblePerson, new org.netbeans.lib.awtextra.AbsoluteConstraints(907, 164, 350, -1));

        txtNote.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtNote.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoteKeyReleased(evt);
            }
        });
        getContentPane().add(txtNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(907, 207, 350, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Note");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(736, 207, -1, -1));

        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(979, 250, 168, 151));

        txtBrowse.setBackground(new java.awt.Color(0, 0, 51));
        txtBrowse.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtBrowse.setForeground(new java.awt.Color(255, 255, 255));
        txtBrowse.setText("Browse");
        txtBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBrowseActionPerformed(evt);
            }
        });
        getContentPane().add(txtBrowse, new org.netbeans.lib.awtextra.AbsoluteConstraints(1165, 306, -1, -1));

        btnSubmit.setBackground(new java.awt.Color(0, 0, 51));
        btnSubmit.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSubmit.setForeground(new java.awt.Color(255, 255, 255));
        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        getContentPane().add(btnSubmit, new org.netbeans.lib.awtextra.AbsoluteConstraints(933, 413, -1, -1));

        btnClear.setBackground(new java.awt.Color(204, 0, 51));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        getContentPane().add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(1176, 413, -1, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Title", "Description", "Category", "Purpose", "Cost", "Responsible Person", "Note", "Image", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 489, 948, 160));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Category");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 250, -1, -1));

        txtCategory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCategoryKeyReleased(evt);
            }
        });
        getContentPane().add(txtCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 250, 350, -1));

        jPanel5.setBackground(new java.awt.Color(27, 33, 56));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leefmancare-logo.png"))); // NOI18N
        jLabel13.setText("jLabel1");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 6, 191, 187));

        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer.setText("Dashboard");
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        jPanel5.add(btnCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 224, 141, -1));

        btnTailors.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTailors.setText("Tailors");
        btnTailors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTailorsActionPerformed(evt);
            }
        });
        jPanel5.add(btnTailors, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 313, 141, -1));

        btnOrders.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders.setText("Orders");
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });
        jPanel5.add(btnOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 363, 141, -1));

        btnMaterialsAndStock.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMaterialsAndStock.setText("Materials & Stock");
        btnMaterialsAndStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaterialsAndStockActionPerformed(evt);
            }
        });
        jPanel5.add(btnMaterialsAndStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 463, 199, -1));

        btnLogout3.setBackground(new java.awt.Color(204, 0, 0));
        btnLogout3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout3.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout3.setText("Logout");
        btnLogout3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        jPanel5.add(btnLogout3, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 611, -1, -1));

        btnOrders3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders3.setText("Loss");
        btnOrders3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders3ActionPerformed(evt);
            }
        });
        jPanel5.add(btnOrders3, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 413, 141, -1));

        btnTransactionHistory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTransactionHistory.setText("Transaction History");
        btnTransactionHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionHistoryActionPerformed(evt);
            }
        });
        jPanel5.add(btnTransactionHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 513, 199, -1));

        btnOrders4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders4.setText("Special Events");
        btnOrders4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders4ActionPerformed(evt);
            }
        });
        jPanel5.add(btnOrders4, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 561, -1, -1));

        btnCustomer1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer1.setText("Customers");
        btnCustomer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomer1ActionPerformed(evt);
            }
        });
        jPanel5.add(btnCustomer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 269, 141, -1));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 244, 656));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 51));
        jLabel12.setText("Total Records");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 51));
        jLabel14.setText("Total Lost");

        lblTotalRecords.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalRecords.setForeground(new java.awt.Color(153, 0, 0));
        lblTotalRecords.setText("000000");

        lblTotalLoss.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalLoss.setForeground(new java.awt.Color(153, 0, 0));
        lblTotalLoss.setText("000000 ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(161, 161, 161)
                        .addComponent(jLabel12))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(184, 184, 184)
                        .addComponent(lblTotalRecords)))
                .addGap(183, 183, 183)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalLoss)
                    .addComponent(jLabel14))
                .addContainerGap(221, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalRecords)
                    .addComponent(lblTotalLoss))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 47, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("All Losses");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 449, -1, -1));

        txtSearch.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 450, 318, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Search:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 450, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNoteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoteKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtNoteKeyReleased

    private void txtResponsiblePersonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtResponsiblePersonKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtResponsiblePersonKeyReleased

    private void txtCostKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCostKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtCostKeyReleased

    private void txtPurposeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPurposeKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtPurposeKeyReleased

    private void txtDescriptionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescriptionKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtDescriptionKeyReleased

    private void txtTitleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTitleKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtTitleKeyReleased

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCategoryKeyReleased
        // TODO add your handling code here:
        validateField();
    }//GEN-LAST:event_txtCategoryKeyReleased

    private void txtBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBrowseActionPerformed
        // TODO add your handling code here:
        imageFile1 = chooseImageFile();
        if (imageFile1 != null) {
            showImagePreview(imageFile1, lblImage);
        }
    }//GEN-LAST:event_txtBrowseActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        try {
            // TODO add your handling code here:
            String path1 = imageFile1 != null ? saveImageToFolder(imageFile1) : null;

            models.Loss ex = new models.Loss();
            ex.setTitle(txtTitle.getText());
            ex.setDescription(txtDescription.getText());
            ex.setCategory(txtCategory.getText());
            ex.setPurpose(txtPurpose.getText());
            ex.setCost(Double.parseDouble(txtCost.getText()));
//            ex.setProfitLost(Double.parseDouble(txtProfitLost.getText()));
            ex.setResponsiblePerson(txtResponsiblePerson.getText());
            ex.setNotes(txtNote.getText());
            ex.setImagePath(path1);

            LossDao.saveExtras(ex);
            clear();

            getAllExtras();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        getAllExtras();
    }//GEN-LAST:event_formComponentShown

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int id = (int) jTable1.getValueAt(selectedRow, 0);
        String title = (String) jTable1.getValueAt(selectedRow, 1);
        String description = (String) jTable1.getValueAt(selectedRow, 2);
        String category = (String) jTable1.getValueAt(selectedRow, 3);
        String purpose = (String) jTable1.getValueAt(selectedRow, 4);
        double cost = (double) jTable1.getValueAt(selectedRow, 5);
//        double profitLost = (double) jTable1.getValueAt(selectedRow, 6);
        String responsiblePerson = (String) jTable1.getValueAt(selectedRow, 6);
        String note = (String) jTable1.getValueAt(selectedRow, 7);
        ImageIcon imageIcon = (ImageIcon) jTable1.getValueAt(selectedRow, 8);

        String dateStr = "";
        Object dateObj = jTable1.getValueAt(selectedRow, 9);
        if (dateObj instanceof Date) {
            dateStr = dateFormat.format((Date) dateObj);
        } else if (dateObj instanceof String) {
            dateStr = (String) dateObj;
        } else if (dateObj != null) {
            dateStr = dateObj.toString();
        }

        showExtraDetailsDialog(id, title, description, category, purpose, cost,
                responsiblePerson, note, imageIcon, dateStr);


    }//GEN-LAST:event_jTable1MouseClicked

    private void btnOrders4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrders4ActionPerformed
        setVisible(false);
        new SpecialProjects().setVisible(true);
    }

    private void showExtraDetailsDialog(int id, String title, String description, String category,
            String purpose, double cost,
            String responsiblePerson, String note, ImageIcon image, String date) {

        JDialog detailDialog = new JDialog(this, "Extra Details - ID: " + id, true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(600, 500);
        detailDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        JLabel titleValue = new JLabel(title);
        titleValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(titleValue, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setBackground(mainPanel.getBackground());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(350, 60));
        mainPanel.add(descScroll, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(category), gbc);

        // Purpose
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Purpose:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(purpose), gbc);

        // Cost
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        JLabel costLabel = new JLabel(String.format("₦%.2f", cost));
        costLabel.setForeground(new Color(204, 0, 0));
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(costLabel, gbc);

        // Profit/Loss
        //        gbc.gridx = 0;
        //        gbc.gridy = 5;
        //        mainPanel.add(new JLabel("Profit/Loss:"), gbc);
        //        gbc.gridx = 1;
        //        JLabel profitLabel = new JLabel(String.format("₦%.2f", profitLost));
        //        if (profitLost > 0) {
        //            profitLabel.setForeground(new Color(0, 102, 0));
        //        } else if (profitLost < 0) {
        //            profitLabel.setForeground(new Color(204, 0, 0));
        //        }
        //        mainPanel.add(profitLabel, gbc);
        // Responsible Person
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Responsible Person:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(responsiblePerson), gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Note:"), gbc);
        gbc.gridx = 1;
        JTextArea noteArea = new JTextArea(note);
        noteArea.setEditable(false);
        noteArea.setWrapStyleWord(true);
        noteArea.setLineWrap(true);
        noteArea.setBackground(mainPanel.getBackground());
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setPreferredSize(new Dimension(350, 50));
        mainPanel.add(noteScroll, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(date), gbc);

        // Image
        if (image != null) {
            gbc.gridx = 0;
            gbc.gridy = 8;
            mainPanel.add(new JLabel("Image:"), gbc);
            gbc.gridx = 1;
            JLabel imageLabel = new JLabel(image);
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            mainPanel.add(imageLabel, gbc);
        }

        // Close Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(102, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(btnClose);

        // Add to dialog
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        detailDialog.add(scrollPane, BorderLayout.CENTER);
        detailDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailDialog.setVisible(true);
    }//GEN-LAST:event_btnOrders4ActionPerformed

    private void btnTransactionHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionHistoryActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new TransactionHistory().setVisible(true);
    }//GEN-LAST:event_btnTransactionHistoryActionPerformed

    private void btnOrders3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrders3ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Loss().setVisible(true);
    }//GEN-LAST:event_btnOrders3ActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        int logout = JOptionPane.showConfirmDialog(null, "Are sure you want to logout?", "Warning", JOptionPane.OK_CANCEL_OPTION);

        if (logout == 0) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

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
        new GeneralDashboard().setVisible(true);
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnCustomer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomer1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new Customers().setVisible(true);
    }//GEN-LAST:event_btnCustomer1ActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String searchText = txtSearch.getText().trim();
        getAllExtras(searchText);
    }//GEN-LAST:event_txtSearchKeyReleased

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
            java.util.logging.Logger.getLogger(Loss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Loss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Loss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Loss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Loss().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnCustomer1;
    private javax.swing.JButton btnLogout3;
    private javax.swing.JButton btnMaterialsAndStock;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnOrders3;
    private javax.swing.JButton btnOrders4;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnTailors;
    private javax.swing.JButton btnTransactionHistory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblTotalLoss;
    private javax.swing.JLabel lblTotalRecords;
    private javax.swing.JButton txtBrowse;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtCost;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtNote;
    private javax.swing.JTextField txtPurpose;
    private javax.swing.JTextField txtResponsiblePerson;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
