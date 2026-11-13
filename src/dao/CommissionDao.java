package dao;

import models.CommissionDetail;
import models.CommissionSummary;
import dbConnection.ConnectionProvider;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.CommissionDetailWithPayments;
import models.CommissionPaymentDetail;

public class CommissionDao {

    // Get total commission from tailor_commissions table
    public static double getTotalCommission(int tailorId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM tailor_commissions WHERE tailor_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, tailorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get outstanding commission from tailor_commissions table
    public static double getOutstandingCommission(int tailorId) {
        String sql = "SELECT COALESCE(SUM(balance), 0) FROM tailor_commissions WHERE tailor_id = ? AND balance > 0";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, tailorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get total paid from commission_payments table
    public static double getTotalPaidCommission(int tailorId) {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) FROM commission_payments WHERE tailor_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, tailorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get all tailor commission summaries from tailor_commissions table
    public static List<CommissionSummary> getAllTailorCommissionSummaries() {
        List<CommissionSummary> summaries = new ArrayList<>();

        String sql = "SELECT "
                + "    t.tailor_id, "
                + "    t.name AS tailor_name, "
                + "    COALESCE(SUM(tc.amount), 0) AS total_commission, "
                + "    COALESCE(SUM(tc.amount - tc.balance), 0) AS total_paid, "
                + "    COALESCE(SUM(tc.balance), 0) AS outstanding "
                + "FROM tailors t "
                + "INNER JOIN tailor_commissions tc ON t.tailor_id = tc.tailor_id "
                + "GROUP BY t.tailor_id, t.name "
                + "HAVING total_commission > 0 "
                + "ORDER BY t.name";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CommissionSummary summary = new CommissionSummary();
                summary.setTailorId(rs.getInt("tailor_id"));
                summary.setTailorName(rs.getString("tailor_name"));
                summary.setTotalCommission(rs.getDouble("total_commission"));
                summary.setTotalPaid(rs.getDouble("total_paid"));
                summary.setOutstandingCommission(rs.getDouble("outstanding"));
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

    // Get commission details for a specific tailor from tailor_commissions
    public static List<CommissionDetail> getCommissionDetailsByTailor(int tailorId) {
        List<CommissionDetail> details = new ArrayList<>();

        String sql = "SELECT "
                + "    commission_id, "
                + "    order_id, "
                + "    amount, "
                + "    (amount - balance) AS paid_amount, "
                + "    balance, "
                + "    commission_date, "
                + "    status "
                + "FROM tailor_commissions "
                + "WHERE tailor_id = ? "
                + "ORDER BY commission_date DESC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tailorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CommissionDetail detail = new CommissionDetail();
                detail.setCommissionId(rs.getInt("commission_id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setAmount(rs.getDouble("amount"));
                detail.setPaidAmount(rs.getDouble("paid_amount"));
                detail.setBalance(rs.getDouble("balance"));
                detail.setCommissionDate(rs.getDate("commission_date"));
                detail.setStatus(rs.getString("status"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public static boolean makeCommissionPayment(int tailorId, double amountPaid, String paymentMethod, String notes) {
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement allocationStmt = null;

        try {
            con = ConnectionProvider.getCon();
            con.setAutoCommit(false);

            // Insert payment record
            String insertSql = "INSERT INTO commission_payments (tailor_id, amount_paid, payment_date, payment_method, notes) "
                    + "VALUES (?, ?, CURDATE(), ?, ?)";
            pstmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, tailorId);
            pstmt.setDouble(2, amountPaid);
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, notes);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int paymentId = 0;
            if (generatedKeys.next()) {
                paymentId = generatedKeys.getInt(1);
            }

            // Get unpaid commissions for this tailor
            String selectSql = "SELECT commission_id, balance FROM tailor_commissions "
                    + "WHERE tailor_id = ? AND balance > 0 ORDER BY commission_date ASC";
            PreparedStatement selectStmt = con.prepareStatement(selectSql);
            selectStmt.setInt(1, tailorId);
            ResultSet rs = selectStmt.executeQuery();

            double remainingPayment = amountPaid;

            String updateCommissionSql = "UPDATE tailor_commissions "
                    + "SET balance = balance - ?, "
                    + "status = CASE WHEN balance - ? <= 0 THEN 'paid' ELSE 'unpaid' END, "
                    + "payment_date = CASE WHEN balance - ? <= 0 THEN CURDATE() ELSE payment_date END "
                    + "WHERE commission_id = ?";
            updateStmt = con.prepareStatement(updateCommissionSql);

            String allocationSql = "INSERT INTO commission_payment_allocation (payment_id, commission_id, amount_applied) VALUES (?, ?, ?)";
            allocationStmt = con.prepareStatement(allocationSql);

            while (rs.next() && remainingPayment > 0) {
                int commissionId = rs.getInt("commission_id");
                double currentBalance = rs.getDouble("balance");
                double amountToApply = Math.min(currentBalance, remainingPayment);

                updateStmt.setDouble(1, amountToApply);
                updateStmt.setDouble(2, amountToApply);
                updateStmt.setDouble(3, amountToApply);
                updateStmt.setInt(4, commissionId);
                updateStmt.executeUpdate();

                allocationStmt.setInt(1, paymentId);
                allocationStmt.setInt(2, commissionId);
                allocationStmt.setDouble(3, amountToApply);
                allocationStmt.executeUpdate();

                remainingPayment -= amountToApply;
            }

            con.commit();
            JOptionPane.showMessageDialog(null, String.format("Commission payment of ₦%.2f recorded successfully!", amountPaid));
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to process commission payment: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (updateStmt != null) {
                    updateStmt.close();
                }
                if (allocationStmt != null) {
                    allocationStmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void syncCommissionsFromOrders() {
        String sql = "INSERT INTO tailor_commissions (tailor_id, order_id, amount, balance, commission_date, status) "
                + "SELECT o.tailor_id, o.order_id, o.tailor_commission, o.tailor_commission, o.date_received, 'unpaid' "
                + "FROM orders o "
                + "WHERE o.tailor_id IS NOT NULL AND o.tailor_commission > 0 "
                + "AND NOT EXISTS (SELECT 1 FROM tailor_commissions tc WHERE tc.order_id = o.order_id)";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            int inserted = pstmt.executeUpdate();
//            System.out.println("Synced " + inserted + " commission records from orders");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get detailed commission history for ALL tailors from tailor_commissions table
    public static List<CommissionDetailWithPayments> getAllCommissionHistory() {
        List<CommissionDetailWithPayments> commissions = new ArrayList<>();

        String sql = "SELECT "
                + "    tc.commission_id, "
                + "    tc.tailor_id, "
                + "    t.name AS tailor_name, "
                + "    tc.order_id, "
                + "    tc.amount AS commission_amount, "
                + "    tc.balance AS remaining_balance, "
                + "    tc.commission_date, "
                + "    tc.status, "
                + "    o.cost AS order_cost, "
                + "    cp.payment_id, "
                + "    cp.amount_paid AS payment_amount, "
                + "    cp.payment_date, "
                + "    cp.payment_method, "
                + "    cpa.amount_applied, "
                + "    cpa.created_at AS allocation_date "
                + "FROM tailor_commissions tc "
                + "INNER JOIN tailors t ON tc.tailor_id = t.tailor_id "
                + "INNER JOIN orders o ON tc.order_id = o.order_id "
                + "LEFT JOIN commission_payment_allocation cpa ON tc.commission_id = cpa.commission_id "
                + "LEFT JOIN commission_payments cp ON cpa.payment_id = cp.payment_id "
                + "ORDER BY tc.commission_date DESC, cp.payment_date ASC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            CommissionDetailWithPayments currentCommission = null;
            int currentCommissionId = -1;

            while (rs.next()) {
                int commissionId = rs.getInt("commission_id");

                if (currentCommissionId != commissionId) {
                    if (currentCommission != null) {
                        commissions.add(currentCommission);
                    }
                    currentCommission = new CommissionDetailWithPayments();
                    currentCommission.setCommissionId(commissionId);
                    currentCommission.setTailorId(rs.getInt("tailor_id"));
                    currentCommission.setTailorName(rs.getString("tailor_name"));
                    currentCommission.setOrderId(rs.getInt("order_id"));
                    currentCommission.setCommissionAmount(rs.getDouble("commission_amount"));
                    currentCommission.setRemainingBalance(rs.getDouble("remaining_balance"));
                    currentCommission.setCommissionDate(rs.getDate("commission_date"));
                    currentCommission.setStatus(rs.getString("status"));
                    currentCommission.setOrderCost(rs.getDouble("order_cost"));
                    currentCommission.setPayments(new ArrayList<>());
                    currentCommissionId = commissionId;
                }

                if (rs.getObject("payment_id") != null) {
                    CommissionPaymentDetail payment = new CommissionPaymentDetail();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setPaymentAmount(rs.getDouble("payment_amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    payment.setAmountApplied(rs.getDouble("amount_applied"));
                    payment.setAllocationDate(rs.getTimestamp("allocation_date"));
                    currentCommission.getPayments().add(payment);
                }
            }

            if (currentCommission != null) {
                commissions.add(currentCommission);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commissions;
    }
}
