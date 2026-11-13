/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import dbConnection.ConnectionProvider;

/**
 *
 * @author oyeyemi
 */
public class OrderDashboardDao {

    public static int getTotalOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        return getSingleInt(sql);
    }

    public static int getPendingOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'Pending'";
        return getSingleInt(sql);
    }

    public static int getInProgressOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'In Progress'";
        return getSingleInt(sql);
    }

    public static int getCompletedOrders() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'Completed'";
        return getSingleInt(sql);
    }

    public static double getTotalEarnings() {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) FROM orders";
        return getSingleDouble(sql);
    }

    private static int getSingleInt(String sql) {
        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static double getSingleDouble(String sql) {
        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static int getTotalOrders(String fromDate, String toDate) {

        String sql = """
        SELECT COUNT(*) 
        FROM orders
        WHERE date_received BETWEEN ? AND ?
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getTotalEarnings(String fromDate, String toDate) {

        String sql = """
        SELECT COALESCE(SUM(amount_paid), 0)
        FROM orders
        WHERE date_received BETWEEN ? AND ?
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getCompletedOrders(String fromDate, String toDate) {

        String sql = """
        SELECT COUNT(*)
        FROM orders
        WHERE status = 'Completed'
          AND date_received BETWEEN ? AND ?
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getPendingOrders(String fromDate, String toDate) {

        String sql = """
        SELECT COUNT(*)
        FROM orders
        WHERE status = 'Pending'
          AND date_received BETWEEN ? AND ?
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getInProgressOrders(String fromDate, String toDate) {

        String sql = """
        SELECT COUNT(*)
        FROM orders
        WHERE status = 'In Progress'
          AND date_received BETWEEN ? AND ?
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getTotalOrders(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COUNT(*) 
        FROM orders
        WHERE date_received BETWEEN ? AND ?
        """ + (tailorId > 0 ? " AND tailor_id = ?" : "");

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            if (tailorId > 0) {
                ps.setInt(3, tailorId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getTotalEarnings(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COALESCE(SUM(amount_paid),0)
        FROM orders
        WHERE date_received BETWEEN ? AND ?
        """ + (tailorId > 0 ? " AND tailor_id = ?" : "");

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            if (tailorId > 0) {
                ps.setInt(3, tailorId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getCompletedOrders(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COUNT(*)
        FROM orders
        WHERE status = 'Completed'
        AND date_received BETWEEN ? AND ?
        """ + (tailorId > 0 ? " AND tailor_id = ?" : "");

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            if (tailorId > 0) {
                ps.setInt(3, tailorId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    public static int getPendingOrders(String fromDate, String toDate, int tailorId) {

    String sql = """
        SELECT COUNT(*)
        FROM orders
        WHERE status = 'Pending'
        AND date_received BETWEEN ? AND ?
        """ + (tailorId > 0 ? " AND tailor_id = ?" : "");

    try (Connection con = ConnectionProvider.getCon();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, fromDate);
        ps.setString(2, toDate);

        if (tailorId > 0) {
            ps.setInt(3, tailorId);
        }

        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);

    } catch (Exception e) {
        e.printStackTrace();
    }

    return 0;
}


    public static int getInProgressOrders(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COUNT(*)
        FROM orders
        WHERE status = 'In Progress'
        AND date_received BETWEEN ? AND ?
        """ + (tailorId > 0 ? " AND tailor_id = ?" : "");

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            if (tailorId > 0) {
                ps.setInt(3, tailorId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
