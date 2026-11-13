/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dbUtils;

import dbConnection.ConnectionProvider;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author oyeyemi
 */
public class DbOperation {

    public static void saveUpdateDelete(String query, String message) {
        try {
            Connection con = ConnectionProvider.getCon();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            if (!message.isEmpty()) {
                JOptionPane.showMessageDialog(null, message);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public static ResultSet getData(String query) {
        try {
            Connection con = ConnectionProvider.getCon();
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query);
            return res;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return null;
    }

}
