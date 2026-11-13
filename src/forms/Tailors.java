/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package forms;

import dao.CommissionDao;
import dao.TailorDao;
import dbUtils.DbOperation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import models.Orders;
import models.Tailor;
import java.sql.*;

/**
 *
 * @author oyeyemi
 */
public class Tailors extends javax.swing.JFrame {

    private String phoneNoPattern = "^[0-9]+$";

    /**
     * Creates new form Tailors
     */
    public Tailors() {
        initComponents();
        btnSave.setEnabled(false);

        CommissionDao.syncCommissionsFromOrders();
        setImageColumnsRenderer();
    }

    private void setImageColumnsRenderer() {

        DefaultTableCellRenderer imageRenderer = new DefaultTableCellRenderer() {
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
        };

        jTableWorks.getColumnModel().getColumn(4).setCellRenderer(imageRenderer);
        jTableWorks.getColumnModel().getColumn(5).setCellRenderer(imageRenderer);

    }

    public void validateCustomerFields() {
        String name = txtFullName.getText();
        String phoneNo = txtPhoneNo.getText();

        if (!name.equals("") && phoneNo.matches(phoneNoPattern) && phoneNo.length() == 11) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }

    public void getAllTailors() {
        List<Tailor> tailorList = TailorDao.allTailor();
        Iterator<Tailor> itr = tailorList.iterator();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        while (itr.hasNext()) {
            Tailor tailorObj = itr.next();
            model.addRow(new Object[]{
                tailorObj.getTailorId(),
                tailorObj.getName(),
                tailorObj.getPhoneNo()
            });
        }
    }

    public void clear() {
        txtFullName.setText("");
        txtPhoneNo.setText("");
        btnSave.setEnabled(false);
    }

    public void findTailorByNameIDFilter() {
        List<Tailor> allTailor = TailorDao.findTailorByNameID();

        jComboTailorFilter.removeAllItems();

        for (Tailor tailorObj : allTailor) {
            jComboTailorFilter.addItem(tailorObj);
        }
    }

    private String formatDate(com.toedter.calendar.JDateChooser chooser) {

        if (chooser.getDate() == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(chooser.getDate());
    }

    public void refreshTailorStats() {
        if (!isTailorSelected()) {
            return;
        }

        String fromDate = formatDate(jDateFrom);
        String toDate = formatDate(jDateTo);
        Tailor selectedTailor = getSelectedTailor();
        int tailorId = selectedTailor.getTailorId();

        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both From and To dates");
            return;
        }

        int totalClothSewed = TailorDao.getTotalClotheSewed(fromDate, toDate, tailorId);
        double earnings = TailorDao.getTotalEarnings(fromDate, toDate, tailorId);
        double amountPaid = TailorDao.getTotalAmountPaid(fromDate, toDate, tailorId);
        double balance = TailorDao.getTotalBalance(fromDate, toDate, tailorId);

        // Get commission from tailor_commissions table (not from orders directly)
        double totalCommission = CommissionDao.getTotalCommission(tailorId);

        double totalLoan = TailorDao.getTotalLoan(tailorId);
        double totalPaid = TailorDao.getTotalPayments(tailorId);
        double remainingLoan = totalLoan - totalPaid;

        if (remainingLoan < 0) {
            remainingLoan = 0;
        }

        lblClotheSewed.setText(String.valueOf(totalClothSewed));
//        lblTotalCommission.setText(String.format("₦%.2f", totalCommission));
//        lblAmountPaid.setText(String.format("₦%.2f", amountPaid));
        lblLoan.setText(String.format("₦%.2f", totalLoan));
        lblTotalPaid.setText(String.format("₦%.2f", totalPaid));
        lblRemainingLoan.setText(String.format("₦%.2f", remainingLoan));

        lblTailorName.setText(selectedTailor.getName());
        lblFrom.setText(fromDate);
        lblTo.setText(toDate);

        btnPayLoan.setEnabled(remainingLoan > 0);

        getTailorWork(fromDate, toDate, tailorId);

        jPanel3.revalidate();
        jPanel3.repaint();
    }

    private void showTwoIconsPopupZoom(ImageIcon icon1, ImageIcon icon2) {

        BufferedImage img1 = iconToBufferedImage(icon1);
        BufferedImage img2 = iconToBufferedImage(icon2);

        if (img1 == null && img2 == null) {
            JOptionPane.showMessageDialog(this, "No images found!");
            return;
        }

        ZoomableImagePanel panel1 = new ZoomableImagePanel(img1);
        ZoomableImagePanel panel2 = new ZoomableImagePanel(img2);

        JScrollPane sp1 = new JScrollPane(panel1);
        JScrollPane sp2 = new JScrollPane(panel2);

        JPanel imagesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        imagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imagesPanel.add(sp1);
        imagesPanel.add(sp2);

        JButton btnZoomIn = new JButton("Zoom In");
        JButton btnZoomOut = new JButton("Zoom Out");
        JButton btnReset = new JButton("Reset");

        btnZoomIn.addActionListener(e -> {
            panel1.zoomIn();
            panel2.zoomIn();
        });

        btnZoomOut.addActionListener(e -> {
            panel1.zoomOut();
            panel2.zoomOut();
        });

        btnReset.addActionListener(e -> {
            panel1.resetZoom();
            panel2.resetZoom();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        controls.add(btnZoomIn);
        controls.add(btnZoomOut);
        controls.add(btnReset);

        JDialog dialog = new JDialog(this, "Image Preview (Zoom Supported)", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(imagesPanel, BorderLayout.CENTER);
        dialog.add(controls, BorderLayout.SOUTH);

        dialog.setSize(1100, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private BufferedImage iconToBufferedImage(ImageIcon icon) {

        if (icon == null) {
            return null;
        }

        Image img = icon.getImage();

        BufferedImage buffered = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = buffered.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        return buffered;
    }

    private void refreshLoanLabelsOnly() {
        if (jComboTailorFilter.getSelectedIndex() < 0 || jComboTailorFilter.getSelectedItem() == null) {
            resetLoanLabels();
            return;
        }

        Tailor selectedTailor = (Tailor) jComboTailorFilter.getSelectedItem();
        int tailorId = selectedTailor.getTailorId();

        if (tailorId <= 0) {
            resetLoanLabels();
            return;
        }

        double totalLoan = TailorDao.getTotalLoan(tailorId);
        double totalPaid = TailorDao.getTotalPayments(tailorId);
        double outstandingLoan = TailorDao.getOutstandingLoan(tailorId);

        lblLoan.setText(String.format("₦%.2f", totalLoan));
        lblTotalPaid.setText(String.format("₦%.2f", totalPaid));
        lblRemainingLoan.setText(String.format("₦%.2f", outstandingLoan));
        btnPayLoan.setEnabled(outstandingLoan > 0);
    }

    private void resetLoanLabels() {
        lblLoan.setText("₦0.00");
        lblTotalPaid.setText("₦0.00");
        lblRemainingLoan.setText("₦0.00");
        btnPayLoan.setEnabled(false);
    }

    public void getTailorWork(String fromDate, String toDate, int tailorId) {

        List<Orders> works = TailorDao.getOrdersWithImages(fromDate, toDate, tailorId);
        DefaultTableModel model = (DefaultTableModel) jTableWorks.getModel();

        model.setRowCount(0);

        for (Orders work : works) {

            ImageIcon icon1 = null;
            ImageIcon icon2 = null;

            if (work.getImagePath() != null && !work.getImagePath().isEmpty()) {

                File file = new File(work.getImagePath());

                if (file.exists()) {
                    ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
                    Image scaledImage = originalIcon.getImage()
                            .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

                    icon1 = new ImageIcon(scaledImage);
                }
            }

            if (work.getSewedImage1() != null && !work.getSewedImage1().isEmpty()) {

                File file = new File(work.getSewedImage1());

                if (file.exists()) {
                    ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
                    Image scaledImage = originalIcon.getImage()
                            .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

                    icon2 = new ImageIcon(scaledImage);
                }
            }

            model.addRow(new Object[]{
                work.getCustomerName(),
                work.getTotalCost(),
                work.getAmountPaid(),
                work.getBalance(),
                icon1,
                icon2,
                work.getDueDate(),
                work.getStatus()
            });
        }

        jTableWorks.setRowHeight(70);
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
        btnLoanHistory = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jDateTo = new com.toedter.calendar.JDateChooser();
        jLabel23 = new javax.swing.JLabel();
        jDateFrom = new com.toedter.calendar.JDateChooser();
        jLabel22 = new javax.swing.JLabel();
        jComboTailorFilter = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        btnFetch = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        lblLoan = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblTotalPaid = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblRemainingLoan = new javax.swing.JLabel();
        btnPayLoan = new javax.swing.JButton();
        btnCommHistory = new javax.swing.JButton();
        btnCommission = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lblClotheSewed = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        txtPhoneNo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTailorName = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblFrom = new javax.swing.JLabel();
        lblTo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableWorks = new javax.swing.JTable();
        btnTakeLoan = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnLogout1 = new javax.swing.JButton();
        btnOrders4 = new javax.swing.JButton();
        btnTransactionHistory = new javax.swing.JButton();
        btnMaterialsAndStock = new javax.swing.JButton();
        btnOrders3 = new javax.swing.JButton();
        btnOrders = new javax.swing.JButton();
        btnTailors = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        btnHome4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Leefman Record System - Tailors");

        btnLoanHistory.setBackground(new java.awt.Color(153, 153, 0));
        btnLoanHistory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLoanHistory.setForeground(new java.awt.Color(255, 255, 255));
        btnLoanHistory.setText("Loan History");
        btnLoanHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoanHistoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 396, Short.MAX_VALUE)
                .addComponent(btnLoanHistory)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
            .addComponent(btnLoanHistory, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(267, 6, 1017, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jDateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(287, 11, 198, -1));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setText("To");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 11, -1, -1));
        jPanel3.add(jDateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 11, 198, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("From");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 11, -1, -1));

        jComboTailorFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboTailorFilter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboTailorFilterItemStateChanged(evt);
            }
        });
        jComboTailorFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboTailorFilterActionPerformed(evt);
            }
        });
        jPanel3.add(jComboTailorFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(566, 11, 177, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setText("Tailors");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(503, 14, -1, -1));

        btnFetch.setBackground(new java.awt.Color(51, 102, 0));
        btnFetch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFetch.setForeground(new java.awt.Color(255, 255, 255));
        btnFetch.setText("Work");
        btnFetch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFetchActionPerformed(evt);
            }
        });
        jPanel3.add(btnFetch, new org.netbeans.lib.awtextra.AbsoluteConstraints(761, 7, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(204, 51, 0));
        jLabel14.setText("Loan");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, -1));

        lblLoan.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblLoan.setForeground(new java.awt.Color(204, 51, 0));
        lblLoan.setText("0000");
        jPanel3.add(lblLoan, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, -1, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 51, 0));
        jLabel17.setText("Loan Paid");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, -1, -1));

        lblTotalPaid.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotalPaid.setForeground(new java.awt.Color(0, 51, 0));
        lblTotalPaid.setText("0000");
        jPanel3.add(lblTotalPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 100, -1, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(153, 0, 0));
        jLabel18.setText("Loan Balance");
        jPanel3.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 60, -1, -1));

        lblRemainingLoan.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblRemainingLoan.setForeground(new java.awt.Color(153, 0, 0));
        lblRemainingLoan.setText("0000");
        jPanel3.add(lblRemainingLoan, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 100, -1, -1));

        btnPayLoan.setBackground(new java.awt.Color(204, 0, 0));
        btnPayLoan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPayLoan.setForeground(new java.awt.Color(255, 255, 255));
        btnPayLoan.setText("Pay Loan");
        btnPayLoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayLoanActionPerformed(evt);
            }
        });
        jPanel3.add(btnPayLoan, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, -1, -1));

        btnCommHistory.setBackground(new java.awt.Color(0, 0, 51));
        btnCommHistory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCommHistory.setForeground(new java.awt.Color(255, 255, 255));
        btnCommHistory.setText("Comm. History");
        btnCommHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommHistoryActionPerformed(evt);
            }
        });
        jPanel3.add(btnCommHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 90, -1, -1));

        btnCommission.setBackground(new java.awt.Color(0, 51, 0));
        btnCommission.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCommission.setForeground(new java.awt.Color(255, 255, 255));
        btnCommission.setText("Pay Comm.");
        btnCommission.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommissionActionPerformed(evt);
            }
        });
        jPanel3.add(btnCommission, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 50, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 51));
        jLabel8.setText("Total Cloth Sewed");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, -1, -1));

        lblClotheSewed.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblClotheSewed.setForeground(new java.awt.Color(0, 0, 51));
        lblClotheSewed.setText("0000");
        jPanel3.add(lblClotheSewed, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, -1, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 1040, 140));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 51));
        jLabel3.setText("Add Tailors");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 25, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel4.setText("Full Name");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 65, -1, -1));

        txtFullName.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        txtFullName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFullNameKeyReleased(evt);
            }
        });
        jPanel4.add(txtFullName, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 65, 270, -1));

        txtPhoneNo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        txtPhoneNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPhoneNoKeyReleased(evt);
            }
        });
        jPanel4.add(txtPhoneNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 111, 270, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel5.setText("Phone No.");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 111, -1, -1));

        btnSave.setBackground(new java.awt.Color(0, 0, 51));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel4.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(126, 145, -1, -1));

        btnClear.setBackground(new java.awt.Color(0, 0, 51));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel4.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 145, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 51));
        jLabel7.setText("All Tailors");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 192, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel6.setText("Search:");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(169, 223, -1, -1));

        txtSearch.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        jPanel4.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(242, 220, 154, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Full Name", "Phone No"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 254, 403, 206));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 190, 420, 490));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Work Evidence");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(229, 14, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel13.setText("Name:");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 51, -1, -1));

        lblTailorName.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jPanel5.add(lblTailorName, new org.netbeans.lib.awtextra.AbsoluteConstraints(74, 51, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel15.setText("From:");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 79, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel16.setText("To:");
        jPanel5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 107, -1, -1));

        lblFrom.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jPanel5.add(lblFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(74, 79, -1, -1));

        lblTo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jPanel5.add(lblTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(74, 107, -1, -1));

        jTableWorks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Customer Name", "Cost", "Amount Paid", "Balance", "ImageBefore", "ImageAfter", "Completion Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableWorks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableWorksMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableWorks);

        jPanel5.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 141, 633, 337));

        btnTakeLoan.setBackground(new java.awt.Color(204, 0, 0));
        btnTakeLoan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTakeLoan.setForeground(new java.awt.Color(255, 255, 255));
        btnTakeLoan.setText("Take Loan");
        btnTakeLoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTakeLoanActionPerformed(evt);
            }
        });
        jPanel5.add(btnTakeLoan, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 100, -1, -1));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 190, -1, -1));

        jPanel6.setBackground(new java.awt.Color(27, 33, 56));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leefmancare-logo.png"))); // NOI18N
        jLabel9.setText("jLabel1");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 6, 191, 187));

        btnLogout1.setBackground(new java.awt.Color(204, 0, 0));
        btnLogout1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout1.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout1.setText("Logout");
        btnLogout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogout1ActionPerformed(evt);
            }
        });
        jPanel6.add(btnLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 620, -1, -1));

        btnOrders4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders4.setText("Special Events");
        btnOrders4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders4ActionPerformed(evt);
            }
        });
        jPanel6.add(btnOrders4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, -1, -1));

        btnTransactionHistory.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTransactionHistory.setText("Transaction History");
        btnTransactionHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionHistoryActionPerformed(evt);
            }
        });
        jPanel6.add(btnTransactionHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, 199, -1));

        btnMaterialsAndStock.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnMaterialsAndStock.setText("Materials & Stock");
        btnMaterialsAndStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaterialsAndStockActionPerformed(evt);
            }
        });
        jPanel6.add(btnMaterialsAndStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, 199, -1));

        btnOrders3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders3.setText("Loss");
        btnOrders3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrders3ActionPerformed(evt);
            }
        });
        jPanel6.add(btnOrders3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, 141, -1));

        btnOrders.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnOrders.setText("Orders");
        btnOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdersActionPerformed(evt);
            }
        });
        jPanel6.add(btnOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 141, -1));

        btnTailors.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTailors.setText("Tailors");
        btnTailors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTailorsActionPerformed(evt);
            }
        });
        jPanel6.add(btnTailors, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 141, -1));

        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCustomer.setText("Customers");
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        jPanel6.add(btnCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 141, -1));

        btnHome4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnHome4.setText("Dashboard");
        btnHome4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHome4ActionPerformed(evt);
            }
        });
        jPanel6.add(btnHome4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 141, -1));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 780));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFullNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFullNameKeyReleased
        // TODO add your handling code here:
        validateCustomerFields();
    }//GEN-LAST:event_txtFullNameKeyReleased

    private void txtPhoneNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneNoKeyReleased
        // TODO add your handling code here:
        validateCustomerFields();
    }//GEN-LAST:event_txtPhoneNoKeyReleased

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        Tailor tailor = new Tailor();
        tailor.setName(txtFullName.getText());
        tailor.setPhoneNo(txtPhoneNo.getText());

        TailorDao.saveTailor(tailor);
        clear();
        getAllTailors();

    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        txtFullName.setText("");
        txtPhoneNo.setText("");
        btnSave.setEnabled(false);
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String name = txtSearch.getText();
        List<Tailor> tailorList = TailorDao.findTailorByName(name);
        Iterator<Tailor> itr = tailorList.iterator();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        while (itr.hasNext()) {
            Tailor tailorObj = itr.next();
            model.addRow(new Object[]{
                tailorObj.getTailorId(),
                tailorObj.getName(),
                tailorObj.getPhoneNo()
            });
        }
    }//GEN-LAST:event_txtSearchKeyReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        getAllTailors();
        findTailorByNameIDFilter();
        refreshLoanLabelsOnly();
//        refreshCommissionInfo();
    }//GEN-LAST:event_formComponentShown

    private void btnFetchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFetchActionPerformed
        // TODO add your handling code here:
        refreshTailorStats();
//        refreshCommissionInfo();

        Tailor selectedTailor = getSelectedTailor();
        if (selectedTailor != null) {
            String fromDate = formatDate(jDateFrom);
            String toDate = formatDate(jDateTo);

            lblTailorName.setText(selectedTailor.getName());
            lblFrom.setText(fromDate);
            lblTo.setText(toDate);

            getTailorWork(fromDate, toDate, selectedTailor.getTailorId());
        }
    }//GEN-LAST:event_btnFetchActionPerformed

    private void jTableWorksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableWorksMouseClicked
        // TODO add your handling code here:
        int row = jTableWorks.getSelectedRow();
        if (row < 0) {
            return;
        }

        Object v1 = jTableWorks.getValueAt(row, 4);
        Object v2 = jTableWorks.getValueAt(row, 5);

        ImageIcon icon1 = (v1 instanceof ImageIcon) ? (ImageIcon) v1 : null;
        ImageIcon icon2 = (v2 instanceof ImageIcon) ? (ImageIcon) v2 : null;

        showTwoIconsPopupZoom(icon1, icon2);
    }//GEN-LAST:event_jTableWorksMouseClicked

    private void btnTakeLoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTakeLoanActionPerformed

        Tailor selectedTailor = (Tailor) jComboTailorFilter.getSelectedItem();
        int tailorId = selectedTailor.getTailorId();

        if (tailorId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a Staff applying for Loan",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalLoan = TailorDao.getTotalLoan(tailorId);
        double totalPaid = TailorDao.getTotalPayments(tailorId);
        double remainingLoan = totalLoan - totalPaid;

        if (remainingLoan > 0) {
            JOptionPane.showMessageDialog(this,
                    String.format("Sorry! You are not qualified to take another loan.\n"
                            + "You have an outstanding balance of ₦%.2f.\n"
                            + "Please pay up existing debt first. Thanks.",
                            remainingLoan),
                    "Loan Not Allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        } else {

            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("You are qualified to take a loan.\n"
                            + "Click OK to proceed."),
                    "Loan Allowed",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (confirm == JOptionPane.OK_OPTION) {
                dispose();
                new TailorLoan(tailorId).setVisible(true);
            }
        }
    }//GEN-LAST:event_btnTakeLoanActionPerformed

    private void btnPayLoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayLoanActionPerformed
        // TODO add your handling code here:
        Tailor selectedTailor = (Tailor) jComboTailorFilter.getSelectedItem();
        int tailorId = selectedTailor.getTailorId();

        if (tailorId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a Staff to make loan payment",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double remainingLoan = TailorDao.getRemainingLoan(tailorId);

        if (remainingLoan <= 0) {
            JOptionPane.showMessageDialog(this,
                    "This staff has no outstanding loan balance.",
                    "No Loan Due",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        TailorPayment paymentDialog = new TailorPayment(this, tailorId, remainingLoan);
        paymentDialog.setVisible(true);

        refreshLoanInfo();
//        refreshTailorStats();
    }//GEN-LAST:event_btnPayLoanActionPerformed

    private void jComboTailorFilterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboTailorFilterItemStateChanged
        // TODO add your handling code here:
//        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
//            refreshLoanLabelsOnly();
        ////            refreshCommissionInfo();
//        }
          refreshLoanLabelsOnly();
    }//GEN-LAST:event_jComboTailorFilterItemStateChanged

    private void jComboTailorFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboTailorFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboTailorFilterActionPerformed

    private void btnLoanHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoanHistoryActionPerformed
        // TODO add your handling code here:
        DetailedLoanHistoryDialog dialog = new DetailedLoanHistoryDialog(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnLoanHistoryActionPerformed

    private void btnCommHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommHistoryActionPerformed
        // TODO add your handling code here:
        CommissionHistoryDialog dialog = new CommissionHistoryDialog(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnCommHistoryActionPerformed

    private void btnCommissionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommissionActionPerformed
        // TODO add your handling code here:
        if (!isTailorSelected()) {
            JOptionPane.showMessageDialog(this, "Please select a tailor first");
            return;
        }

        Tailor selectedTailor = getSelectedTailor();
        int tailorId = selectedTailor.getTailorId();
//        String fromDate = formatDate(jDateFrom);
//        String toDate = formatDate(jDateTo);
//
//        if (fromDate == null || toDate == null) {
//            JOptionPane.showMessageDialog(this, "Please select both From and To dates");
//            return;
//        }
        double outstandingCommission = TailorDao.getTotalCommissionFromOrders(tailorId);

        if (outstandingCommission <= 0) {
            JOptionPane.showMessageDialog(this, "No outstanding commission for this tailor");
            return;
        }

        PayCommissionDialog dialog = new PayCommissionDialog(this);
        dialog.setVisible(true);

//        refreshCommissionInfo();
        refreshTailorStats();
    }//GEN-LAST:event_btnCommissionActionPerformed

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

    private void btnHome4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHome4ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new GeneralDashboard().setVisible(true);
    }//GEN-LAST:event_btnHome4ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            return;
        }

        TableModel model = jTable1.getModel();

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String name = model.getValueAt(row, 1).toString();

        if (hasRelatedRecords(id)) {
            int warning = JOptionPane.showConfirmDialog(null,
                    "This tailor has related records (loans, payments, commissions, orders).\n"
                    + "All related records will also be deleted.\n\n"
                    + "Are you sure you want to delete " + name + "?",
                    "Warning - Related Records Found",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (warning == JOptionPane.YES_OPTION) {
                TailorDao.deleteTailor(id);
                getAllTailors();
            }
        } else {
            int warning = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete " + name + "?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION);
            if (warning == JOptionPane.YES_OPTION) {
                TailorDao.deleteTailor(id);
                getAllTailors();
                findTailorByNameIDFilter();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private boolean hasRelatedRecords(int tailorId) {
        try {
            String sql = "SELECT COUNT(*) FROM ("
                    + "SELECT 1 FROM commission_payments WHERE tailor_id = " + tailorId
                    + " UNION ALL "
                    + "SELECT 1 FROM tailor_commissions WHERE tailor_id = " + tailorId
                    + " UNION ALL "
                    + "SELECT 1 FROM tailor_payments WHERE tailor_id = " + tailorId
                    + " UNION ALL "
                    + "SELECT 1 FROM tailor_loans WHERE tailor_id = " + tailorId
                    + " UNION ALL "
                    + "SELECT 1 FROM orders WHERE tailor_id = " + tailorId
                    + " UNION ALL "
                    + "SELECT 1 FROM tailor_work WHERE tailor_id = " + tailorId
                    + ") AS related";

            ResultSet rs = DbOperation.getData(sql);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTailorSelected() {
        return jComboTailorFilter.getSelectedIndex() >= 0
                && jComboTailorFilter.getSelectedItem() != null;
    }

    private Tailor getSelectedTailor() {
        if (!isTailorSelected()) {
            return null;
        }
        return (Tailor) jComboTailorFilter.getSelectedItem();
    }

    public void refreshLoanInfo() {

        if (jComboTailorFilter.getSelectedIndex() >= 0 && jComboTailorFilter.getSelectedItem() != null) {
            Tailor selectedTailor = (Tailor) jComboTailorFilter.getSelectedItem();
            int tailorId = selectedTailor.getTailorId();

            if (tailorId <= 0) {
                resetLoanLabels();
                return;
            }

            double totalLoan = TailorDao.getTotalLoan(tailorId);
            double totalPaid = TailorDao.getTotalPayments(tailorId);
            double remainingLoan = TailorDao.getOutstandingLoan(tailorId);

            if (remainingLoan < 0) {
                remainingLoan = 0;
            }

            lblLoan.setText(String.format("₦%.2f", totalLoan));
            lblTotalPaid.setText(String.format("₦%.2f", totalPaid));
            lblRemainingLoan.setText(String.format("₦%.2f", remainingLoan));

            btnPayLoan.setEnabled(remainingLoan > 0);
        } else {
            resetLoanLabels();
        }
    }

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
            java.util.logging.Logger.getLogger(Tailors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tailors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tailors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tailors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tailors().setVisible(true);
            }
        });
    }

    public class ZoomableImagePanel extends JPanel {

        private BufferedImage image;
        private double zoom = 1.0;

        public ZoomableImagePanel(BufferedImage image) {
            this.image = image;
            setBackground(Color.WHITE);
        }

        public void zoomIn() {
            zoom += 0.2;
            revalidate();
            repaint();
        }

        public void zoomOut() {
            zoom = Math.max(0.2, zoom - 0.2);
            revalidate();
            repaint();
        }

        public void resetZoom() {
            zoom = 1.0;
            revalidate();
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            if (image == null) {
                return new Dimension(400, 300);
            }

            int w = (int) (image.getWidth() * zoom);
            int h = (int) (image.getHeight() * zoom);

            return new Dimension(w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) {
                g.setColor(Color.GRAY);
                g.drawString("No Image", 20, 20);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int imgW = (int) (image.getWidth() * zoom);
            int imgH = (int) (image.getHeight() * zoom);

            int x = (getWidth() - imgW) / 2;
            int y = (getHeight() - imgH) / 2;

            x = Math.max(x, 0);
            y = Math.max(y, 0);

            g2.drawImage(image, x, y, imgW, imgH, null);

            g2.dispose();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCommHistory;
    private javax.swing.JButton btnCommission;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnFetch;
    private javax.swing.JButton btnHome4;
    private javax.swing.JButton btnLoanHistory;
    private javax.swing.JButton btnLogout1;
    private javax.swing.JButton btnMaterialsAndStock;
    private javax.swing.JButton btnOrders;
    private javax.swing.JButton btnOrders3;
    private javax.swing.JButton btnOrders4;
    private javax.swing.JButton btnPayLoan;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTailors;
    private javax.swing.JButton btnTakeLoan;
    private javax.swing.JButton btnTransactionHistory;
    private javax.swing.JComboBox<Tailor> jComboTailorFilter;
    private com.toedter.calendar.JDateChooser jDateFrom;
    private com.toedter.calendar.JDateChooser jDateTo;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableWorks;
    private javax.swing.JLabel lblClotheSewed;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblLoan;
    private javax.swing.JLabel lblRemainingLoan;
    private javax.swing.JLabel lblTailorName;
    private javax.swing.JLabel lblTo;
    private javax.swing.JLabel lblTotalPaid;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtPhoneNo;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
