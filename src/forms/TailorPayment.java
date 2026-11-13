/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package forms;

import dao.TailorDao;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author oyeyemi
 */
public class TailorPayment extends JDialog {

    private int tailorId;
    private JTextField txtAmount;
    private JLabel lblRemainingLoan;
    private JButton btnPay;
    private JButton btnCancel;

    public TailorPayment(JFrame parent, int tailorId, double remainingLoan) {
        super(parent, "Make Loan Payment", true);
        this.tailorId = tailorId;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Remaining loan label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Remaining Loan:"), gbc);

        gbc.gridx = 1;
        lblRemainingLoan = new JLabel(String.format("₦%.2f", remainingLoan));
        lblRemainingLoan.setFont(new Font("Arial", Font.BOLD, 14));
        lblRemainingLoan.setForeground(Color.RED);
        add(lblRemainingLoan, gbc);

        // Amount to pay
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Amount to Pay:"), gbc);

        gbc.gridx = 1;
        txtAmount = new JTextField(15);
        add(txtAmount, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        btnPay = new JButton("Pay");
        btnPay.addActionListener(e -> makePayment());
        add(btnPay, gbc);

        gbc.gridx = 1;
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    private void makePayment() {
        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid amount greater than 0.",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double remainingLoan = Double.parseDouble(lblRemainingLoan.getText().replace("₦", ""));

            if (amount > remainingLoan) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Amount (₦%.2f) exceeds remaining loan (₦%.2f).\n"
                                + "Do you want to pay the full remaining amount?", amount, remainingLoan),
                        "Amount Exceeds Loan",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    amount = remainingLoan;
                } else {
                    return;
                }
            }

           
            boolean success = TailorDao.makeLoanPayment(tailorId, amount);

            if (success) {
                boolean verified = TailorDao.verifyPaymentRecorded(tailorId, amount);

                if (verified) {
                    JOptionPane.showMessageDialog(this,
                            String.format("Payment of ₦%.2f recorded successfully!", amount),
                            "Payment Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Payment recorded but verification failed. Please check payment history.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }

                TailorDao.updateLoanStatus(tailorId);

                dispose();
                
                if (getParent() instanceof Tailors) {
                    Tailors parent = (Tailors) getParent();
                    parent.refreshLoanInfo();
                    parent.refreshTailorStats();

                    parent.findTailorByNameIDFilter();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to process payment. Please try again.",
                        "Payment Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric amount.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
