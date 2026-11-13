/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dbConnection.ConnectionProvider;
import dbUtils.DbOperation;
import models.Admin;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author oyeyemi
 */
public class AdminDao {

    public static Admin login(String email, String password) {
        Admin admin = null;
        ResultSet res = null;

        String loginQuery = "SELECT * FROM admin WHERE email = '" + email + "' AND password = '" + password + "'";
        try {
            res = DbOperation.getData(loginQuery);
            while (res.next()) {
                admin = new Admin();
                admin.setEmail(res.getString("email"));
                admin.setPassword("password");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return admin;
    }

    public static boolean resetPassword(String email, String newPassword) {

        if (email == null || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email is required!");
            return false;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "New password is required!");
            return false;
        }

        String checkSql
                = "SELECT email FROM admin WHERE email = ?";

        String updateSql
                = "UPDATE admin SET password = ? WHERE email = ?";

        try (
                Connection con = ConnectionProvider.getCon(); PreparedStatement checkStmt
                = con.prepareStatement(checkSql)) {

            email = email.trim();

            checkStmt.setString(1, email);

            try (ResultSet rs = checkStmt.executeQuery()) {

                if (!rs.next()) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Email not found!"
                    );

                    return false;
                }
            }

            try (
                    PreparedStatement updateStmt
                    = con.prepareStatement(updateSql)) {

                updateStmt.setString(
                        1,
                        newPassword
                );

                updateStmt.setString(
                        2,
                        email
                );

                int updated
                        = updateStmt.executeUpdate();

                if (updated > 0) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Password updated successfully"
                    );

                    return true;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Error resetting password"
            );
        }

        return false;
    }

}
