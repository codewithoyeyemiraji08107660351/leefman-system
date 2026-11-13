/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dbConnection.ConnectionProvider;
import dbUtils.DbOperation;
import java.util.ArrayList;
import java.util.List;
import models.Material;
import java.sql.*;

/**
 *
 * @author oyeyemi
 */
public class MaterialDao {

    public static void saveMaterials(Material material) {
        String saveQuery = "INSERT INTO materials(name,category,description,selling_price,purchase_price,image_path)"
                + "VALUES('" + material.getName() + "', '" + material.getCategory() + "', '" + material.getDescription() + "', '" + material.getSellingPrice() + "',"
                + "'" + material.getPurchasePrice() + "', '" + material.getImagePath() + "')";
        DbOperation.saveUpdateDelete(saveQuery, "Material Added Successfully!!");

    }

    public static List<Material> getAllMaterials() {
        List<Material> materialsList = new ArrayList<>();
        String allMaterials = "SELECT * FROM materials";
        try {

            ResultSet rs = DbOperation.getData(allMaterials);
            while (rs.next()) {

                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setName(rs.getString("name"));
                m.setCategory(rs.getString("category"));
                m.setDescription(rs.getString("description"));
                m.setQuantity(rs.getDouble("quantity"));
                m.setSellingPrice(rs.getDouble("selling_price"));
                m.setPurchasePrice(rs.getDouble("purchase_price"));
                m.setImagePath(rs.getString("image_path"));

                materialsList.add(m);
            }

        } catch (SQLException e) {

        }

        return materialsList;
    }

    public static void update(int id, String name, String category, String description, double quantity, double sales, double purchase, String path) {
        String updateQuery = "UPDATE materials SET name = '" + name + "', category = '" + category + "', description = '" + description + "',"
                + "quantity = '" + quantity + "', selling_price = '" + sales + "', purchase_price = '" + purchase + "', image_path = '" + path + "' WHERE material_id ='" + id + "'";
        DbOperation.saveUpdateDelete(updateQuery, "Materials Details Updated");
    }

    public static double getTotalStockQuantity() {

        String sql = "SELECT IFNULL(SUM(quantity), 0) AS total_qty FROM materials";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_qty");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getTotalStockValueBySellingPrice() {

        String sql = "SELECT IFNULL(SUM(quantity * selling_price), 0) AS total_value FROM materials";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_value");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getTodayTransactionCount() {

        String sql = "SELECT COUNT(DISTINCT order_id) AS total_transactions "
                + "FROM material_purchases "
                + "WHERE DATE(purchase_date) = CURDATE()";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_transactions");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getTodayTotalSales() {

        String sql = "SELECT IFNULL(SUM(subtotal), 0) AS total_sales "
                + "FROM material_purchases "
                + "WHERE DATE(purchase_date) = CURDATE()";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_sales");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static List<Material> getCustomerMaterials(int customerId) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT DISTINCT m.name, m.image_path, mp.purchase_date "
                + "FROM materials m "
                + "INNER JOIN material_purchases mp ON m.material_id = mp.material_id "
                + "WHERE mp.customer_id = ? "
                + "ORDER BY mp.purchase_date DESC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Material material = new Material();
                material.setName(rs.getString("name"));
                material.setImagePath(rs.getString("image_path"));
                materials.add(material);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return materials;
    }

}
