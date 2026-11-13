/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Orders;
import java.sql.*;
import dbConnection.ConnectionProvider;
import dbUtils.DbOperation;
import models.OrderDetail;
import models.OrderImage;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author oyeyemi
 */
public class OrderDao {

    public static int saveOrder(Orders orders) {
        String sql = "INSERT INTO orders (customer_id, date_received, due_date, status, cost, amount_paid, "
                + "tailor_id, payment_method, tailor_commission) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, orders.getCustomerId());
            pstmt.setString(2, orders.getDateReceived());
            pstmt.setString(3, orders.getDueDate());
            pstmt.setString(4, "Pending");
            pstmt.setDouble(5, orders.getTotalCost());  
            pstmt.setDouble(6, orders.getAmountPaid());
            pstmt.setInt(7, orders.getTailorId());
            pstmt.setString(8, orders.getPaymentMethod());
            pstmt.setDouble(9, orders.getTailorCommission());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    
                    generateTailorCommission(orderId, orders.getTailorId(), orders.getTailorCommission());

                    return orderId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void generateTailorCommission(int orderId, int tailorId, double commissionAmount) {
        if (tailorId <= 0 || commissionAmount <= 0) {
            return;
        }

        String sql = "INSERT INTO tailor_commissions (tailor_id, order_id, amount, balance, commission_date, status) "
                + "VALUES (?, ?, ?, ?, CURDATE(), 'unpaid')";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tailorId);
            pstmt.setInt(2, orderId);
            pstmt.setDouble(3, commissionAmount);
            pstmt.setDouble(4, commissionAmount); 
            

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int saveOrder(OrderDetail orderDetail) {

        String sql = "INSERT INTO order_details (order_id, sewing_style, measurements, items_used, accessories) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderDetail.getOrderId());
            ps.setString(2, orderDetail.getSewingStyle());
            ps.setString(3, orderDetail.getMeasurement());
            ps.setString(4, orderDetail.getItemUsed());
            ps.setString(5, orderDetail.getAccessories());

            ps.executeUpdate();

            return orderDetail.getOrderId();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int saveOrder(OrderImage image) {

        String sql = "INSERT INTO order_images (order_id, image_type, image_path, second_image_path, third_image_path, material_used, material_color, purchase_from) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, image.getOrderId());
            ps.setString(2, image.getImageType());
            ps.setString(3, image.getImagePath());
            ps.setString(4, image.getSecondImagePath());
            ps.setString(5, image.getThirdImagePath());
            ps.setString(6, image.getMaterialUsed());
            ps.setString(7, image.getMaterialColor());
            ps.setString(8, image.getPurchaseFrom());

            ps.executeUpdate();

            return image.getOrderId();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static List<Orders> getAllOrders() {

        List<Orders> orders = new ArrayList<>();

        String sql = """
        SELECT 
            o.order_id,
            o.date_received,
            o.due_date,
            o.status,
            o.cost,
            o.amount_paid,
            o.balance,
            o.created_at,
            o.payment_method,
            o.tailor_commission,
            c.customer_id,
            c.name AS customer_name,
            t.tailor_id,
            t.name AS tailor_name,
            c.phone
        FROM orders o
        JOIN customers c ON o.customer_id = c.customer_id
        LEFT JOIN tailors t ON o.tailor_id = t.tailor_id ORDER BY created_at DESC
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Orders order = new Orders();

                order.setOrderId(rs.getInt("order_id"));
                order.setDateReceived(rs.getString("date_received"));
                order.setDueDate(rs.getString("due_date"));
                order.setStatus(rs.getString("status"));
                order.setTotalCost(rs.getDouble("cost"));
                order.setAmountPaid(rs.getDouble("amount_paid"));
                order.setBalance(rs.getDouble("balance"));
                order.setCreatedAT(rs.getString("created_at"));

                order.setCustomerName(rs.getString("customer_name"));
                order.setCustomerPhoneNo(rs.getString("phone"));
                order.setTailorName(rs.getString("tailor_name"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setTailorCommission(rs.getDouble("tailor_commission"));
                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public static List<Orders> getAllOrders(String name) {

        List<Orders> orders = new ArrayList<>();

        String sql = """
        SELECT 
            o.order_id,
            o.date_received,
            o.due_date,
            o.status,
            o.cost,
            o.amount_paid,
            o.balance,
            o.created_at,
            o.tailor_commission,
            c.customer_id,
            c.name AS customer_name,
            t.tailor_id,
            t.name AS tailor_name,
            c.phone
        FROM orders o
        JOIN customers c ON o.customer_id = c.customer_id
        LEFT JOIN tailors t ON o.tailor_id = t.tailor_id 
        WHERE LOWER(c.name) LIKE ? OR LOWER(t.name) LIKE ? OR o.date_received LIKE ?
        ORDER BY o.created_at ASC
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            String search = "%" + name + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Orders order = new Orders();

                order.setOrderId(rs.getInt("order_id"));
                order.setDateReceived(rs.getString("date_received"));
                order.setDueDate(rs.getString("due_date"));
                order.setStatus(rs.getString("status"));
                order.setTotalCost(rs.getDouble("cost"));
                order.setAmountPaid(rs.getDouble("amount_paid"));
                order.setBalance(rs.getDouble("balance"));
                order.setCreatedAT(rs.getString("created_at"));

                order.setCustomerName(rs.getString("customer_name"));
                order.setCustomerPhoneNo(rs.getString("phone"));
                order.setTailorName(rs.getString("tailor_name"));
                order.setTailorCommission(rs.getDouble("tailor_commission"));

                orders.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public static List<OrderDetail> getOrderDetailsById(int orderId) {

        List<OrderDetail> orderDetailsList = new ArrayList<>();

        String detailsQuery = "SELECT * FROM order_details WHERE order_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(detailsQuery)) {

            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setDetialsId(rs.getInt("detail_id"));
                detail.setSewingStyle(rs.getString("sewing_style"));
                detail.setItemUsed(rs.getString("items_used"));
                detail.setAccessories(rs.getString("accessories"));
                detail.setMeasurement(rs.getString("measurements"));

                orderDetailsList.add(detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderDetailsList;
    }

    public static void updateOrderDetail(int orderId, String sewingStyle, String itemUsed, String accessories, String measurement) {
        String updateQuery = "UPDATE order_details SET sewing_style = '" + sewingStyle + "', items_used = '" + itemUsed + "', "
                + "accessories = '" + accessories + "', measurements = '" + measurement + "' WHERE order_id = '" + orderId + "'";
        DbOperation.saveUpdateDelete(updateQuery, "Measurement details successfully updated!!");
    }

    public static void updateOrderStatus(int orderId, String status) {
        String updateQuery = "UPDATE orders SET status = '" + status + "' WHERE order_id = '" + orderId + "'";
        DbOperation.saveUpdateDelete(updateQuery, "Status successfully updated!!");
    }

    public static List<OrderImage> getAllBeforeImages(int orderId) {
        List<OrderImage> orderImageList = new ArrayList<>();
        String imagesQuery = "SELECT image_path, second_image_path,third_image_path, material_color, purchase_from"
                + " FROM order_images WHERE order_id = '" + orderId + "'";
        try {
            ResultSet res = DbOperation.getData(imagesQuery);
            while (res.next()) {
                OrderImage images = new OrderImage();
                images.setImagePath(res.getString("image_path"));
                images.setSecondImagePath(res.getString("second_image_path"));
                images.setThirdImagePath(res.getString("third_image_path"));
//                images.setMaterialUsed(res.getString("material_used"));
                images.setMaterialColor(res.getString("material_color"));
                images.setPurchaseFrom(res.getString("purchase_from"));

                orderImageList.add(images);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderImageList;
    }

    public static void markOrderAsFullyPaid(int orderId) {

        String sql = "UPDATE orders SET amount_paid = cost WHERE order_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Balance Paid Completely!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadSewedImage(int orderId, String sewedImage1, String sewedImage2, String sewedImage3) {

        String sql = "UPDATE order_images SET sewed_image1 = ?, sewed_image2 = ?, sewed_image3 = ? WHERE order_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sewedImage1);
            ps.setString(2, sewedImage2);
            ps.setString(3, sewedImage3);
            ps.setInt(4, orderId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Images Uploaded!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<OrderImage> getAllAfterImages(int orderId) {

        List<OrderImage> orderImageList = new ArrayList<>();

        String sql = "SELECT sewed_image1, sewed_image2, material_used FROM order_images WHERE order_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                OrderImage images = new OrderImage();
                images.setImageAfterFirst(res.getString("sewed_image1"));
                images.setImageAfterSecond(res.getString("sewed_image2"));
                images.setMaterialUsed(res.getString("material_used"));

                orderImageList.add(images);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderImageList;
    }

}
