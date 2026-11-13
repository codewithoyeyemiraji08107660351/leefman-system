/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dbConnection.ConnectionProvider;
import javax.swing.JOptionPane;
import models.Loans;
import java.sql.*;

/**
 *
 * @author oyeyemi
 */
public class LoanDao {

    public static boolean saveLoan(Loans loan) {
        String loanQuery = "INSERT INTO tailor_loans (tailor_id, item_name, item_description, amount, balance, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(loanQuery)) {

            pstmt.setInt(1, loan.getTailorId());
            pstmt.setString(2, loan.getItemName());
            pstmt.setString(3, loan.getItemDescription());
            pstmt.setDouble(4, loan.getAmount());
            pstmt.setDouble(5, loan.getAmount()); 
            pstmt.setString(6, "unpaid");

            int result = pstmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(null,
                        "Loan successfully applied!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Failed to apply loan. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}
