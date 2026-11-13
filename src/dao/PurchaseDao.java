/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dbConnection.ConnectionProvider;
import java.util.List;
import models.PurchaseReceipt;
import java.sql.*;
import java.util.ArrayList;
import models.Totals;

/**
 *
 * @author oyeyemi
 */
public class PurchaseDao {

    public static List<PurchaseReceipt> getPurchasesByOrderId() {

        List<PurchaseReceipt> list = new ArrayList<>();

        String sql = "SELECT mp.order_id, mp.purchase_date, "
                + "c.name AS customer_name, c.phone, "
                + "m.name AS material_name, "
                + "mp.quantity_bought, mp.amount, mp.subtotal "
                + "FROM material_purchases mp "
                + "JOIN customers c ON mp.customer_id = c.customer_id "
                + "JOIN materials m ON mp.material_id = m.material_id ";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseReceipt pr = new PurchaseReceipt();

                pr.setOrderId(rs.getString("order_id"));
                pr.setPurchaseDate(rs.getString("purchase_date"));
                pr.setCustomerName(rs.getString("customer_name"));
                pr.setCustomerPhone(rs.getString("phone"));
                pr.setMaterialName(rs.getString("material_name"));
                pr.setQuantityBought(rs.getDouble("quantity_bought"));
                pr.setPurchasePrice(rs.getDouble("amount"));
                pr.setSubtotal(rs.getDouble("subtotal"));

                list.add(pr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<PurchaseReceipt> getPurchasesByCustomerId(int customerId) {

        List<PurchaseReceipt> list = new ArrayList<>();

        String sql = "SELECT mp.order_id, mp.purchase_date, "
                + "c.name AS customer_name, c.phone, "
                + "m.name AS material_name, "
                + "mp.quantity_bought, mp.amount, mp.subtotal "
                + "FROM material_purchases mp "
                + "JOIN customers c ON mp.customer_id = c.customer_id "
                + "JOIN materials m ON mp.material_id = m.material_id "
                + "WHERE mp.customer_id = ? "
                + "ORDER BY mp.purchase_date DESC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId); // ✅ correct

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseReceipt pr = new PurchaseReceipt();

                pr.setOrderId(rs.getString("order_id"));
                pr.setPurchaseDate(rs.getString("purchase_date"));
                pr.setCustomerName(rs.getString("customer_name"));
                pr.setCustomerPhone(rs.getString("phone"));
                pr.setMaterialName(rs.getString("material_name"));
                pr.setQuantityBought(rs.getDouble("quantity_bought"));
                pr.setPurchasePrice(rs.getDouble("amount"));
                pr.setSubtotal(rs.getDouble("subtotal"));

                list.add(pr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<PurchaseReceipt> getPurchasesByOrderId(String orderId) {

        List<PurchaseReceipt> list = new ArrayList<>();

        String sql = "SELECT mp.order_id, mp.purchase_date, "
                + "c.name AS customer_name, c.phone, "
                + "m.name AS material_name, "
                + "mp.quantity_bought, mp.amount, mp.subtotal "
                + "FROM material_purchases mp "
                + "JOIN customers c ON mp.customer_id = c.customer_id "
                + "JOIN materials m ON mp.material_id = m.material_id "
                + "WHERE mp.order_id = ? "
                + "ORDER BY mp.purchase_id ASC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseReceipt pr = new PurchaseReceipt();

                pr.setOrderId(rs.getString("order_id"));
                pr.setPurchaseDate(rs.getString("purchase_date"));
                pr.setCustomerName(rs.getString("customer_name"));
                pr.setCustomerPhone(rs.getString("phone"));
                pr.setMaterialName(rs.getString("material_name"));
                pr.setQuantityBought(rs.getDouble("quantity_bought"));
                pr.setPurchasePrice(rs.getDouble("amount"));
                pr.setSubtotal(rs.getDouble("subtotal"));

                list.add(pr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<PurchaseReceipt> getOrdersByDateRange(String from, String to) {
        List<PurchaseReceipt> list = new ArrayList<>();

        String sql = "SELECT mp.order_id, mp.purchase_date, "
                + "c.name AS customer_name, c.phone, "
                + "m.name AS material_name, "
                + "mp.quantity_bought, mp.amount, mp.subtotal "
                + "FROM material_purchases mp "
                + "JOIN customers c ON mp.customer_id = c.customer_id "
                + "JOIN materials m ON mp.material_id = m.material_id "
                + "WHERE mp.purchase_date BETWEEN ? AND ?"
                + "ORDER BY mp.purchase_date DESC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, from);
            ps.setString(2, to);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseReceipt pr = new PurchaseReceipt();

                pr.setOrderId(rs.getString("order_id"));
                pr.setPurchaseDate(rs.getString("purchase_date"));
                pr.setCustomerName(rs.getString("customer_name"));
                pr.setCustomerPhone(rs.getString("phone"));
                pr.setMaterialName(rs.getString("material_name"));
                pr.setQuantityBought(rs.getDouble("quantity_bought"));
                pr.setPurchasePrice(rs.getDouble("amount"));
                pr.setSubtotal(rs.getDouble("subtotal"));

                list.add(pr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Totals getTotalsByDateRange(String from, String to) {

        Totals totals = new Totals();

        String sql = "SELECT "
                + "COALESCE(SUM(mp.subtotal), 0) AS totalRevenue, "
                + "COALESCE(SUM(m.purchase_price * mp.quantity_bought), 0) AS totalCost, "
                + "COALESCE(SUM(mp.quantity_bought), 0) AS totalQty, "
                + "COALESCE(SUM(mp.subtotal - (m.purchase_price * mp.quantity_bought)), 0) AS totalProfit "
                + "FROM material_purchases mp "
                + "JOIN materials m ON mp.material_id = m.material_id "
                + "WHERE mp.purchase_date >= ? "
                + "AND mp.purchase_date < DATE_ADD(?, INTERVAL 1 DAY)";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, from);
            ps.setString(2, to);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totals.setTotalRevenue(rs.getDouble("totalRevenue"));
                totals.setTotalCost(rs.getDouble("totalCost"));
                totals.setTotalQuantity(rs.getDouble("totalQty"));
                totals.setTotalProfit(rs.getDouble("totalProfit"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totals;
    }

}
