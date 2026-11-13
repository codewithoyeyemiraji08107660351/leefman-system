/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author oyeyemi
 */
import dbConnection.ConnectionProvider;
import dbUtils.DbOperation;
import java.util.ArrayList;
import java.util.List;
import models.Tailor;
import java.sql.*;
import javax.swing.JOptionPane;
import models.LoanDetailWithPayments;
import models.LoanDetails;
import models.LoanPayment;
import models.LoanPaymentDetail;
import models.Orders;

public class TailorDao {

    public static void deleteTailor(int id) {
        try {
            String deleteAllocations = "DELETE cpa FROM commission_payment_allocation cpa "
                    + "INNER JOIN commission_payments cp ON cpa.payment_id = cp.payment_id "
                    + "WHERE cp.tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteAllocations, "");

            String deleteCommissionPayments = "DELETE FROM commission_payments WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteCommissionPayments, "");

            String deleteCommissions = "DELETE FROM tailor_commissions WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteCommissions, "");

            String deleteLoanAllocations = "DELETE lpa FROM loan_payment_allocation lpa "
                    + "INNER JOIN tailor_payments tp ON lpa.payment_id = tp.payment_id "
                    + "WHERE tp.tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteLoanAllocations, "");

            String deletePayments = "DELETE FROM tailor_payments WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(deletePayments, "");

            String deleteLoans = "DELETE FROM tailor_loans WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteLoans, "");

            String updateOrders = "UPDATE orders SET tailor_id = NULL WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(updateOrders, "");

            String deleteWorkImages = "DELETE twi FROM tailor_work_images twi "
                    + "INNER JOIN tailor_work tw ON twi.work_id = tw.work_id "
                    + "WHERE tw.tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteWorkImages, "");

            String deleteWork = "DELETE FROM tailor_work WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteWork, "");

            String deleteTailor = "DELETE FROM tailors WHERE tailor_id = " + id;
            DbOperation.saveUpdateDelete(deleteTailor, "Tailor removed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting tailor: " + e.getMessage());
        }
    }

    public static void saveTailor(Tailor tailor) {
        String saveQuery = "INSERT INTO tailors(name,phone) VALUES('" + tailor.getName() + "', '" + tailor.getPhoneNo() + "')";
        DbOperation.saveUpdateDelete(saveQuery, "Tailor saved successfully!");

    }

    public static List<Tailor> allTailor() {
        List<Tailor> tailorList = new ArrayList<>();
        ResultSet res = null;
        try {
            String allCustomers = "SELECT * FROM tailors";
            res = DbOperation.getData(allCustomers);
            while (res.next()) {
                Tailor tailor = new Tailor();
                tailor.setTailorId(res.getInt("tailor_id"));
                tailor.setName(res.getString("name"));
                tailor.setPhoneNo(res.getString("phone"));

                tailorList.add(tailor);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tailorList;
    }

    public static List<Tailor> findTailorByName(String name) {
        List<Tailor> tailorList = new ArrayList<>();
        ResultSet res = null;

        try {
            String customer = "SELECT * FROM tailors WHERE name LIKE '%" + name + "%'";
            res = DbOperation.getData(customer);

            while (res.next()) {
                Tailor tailor = new Tailor();
                tailor.setTailorId(res.getInt("tailor_id"));
                tailor.setName(res.getString("name"));
                tailor.setPhoneNo(res.getString("phone"));

                tailorList.add(tailor);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tailorList;
    }

    public static List<Tailor> findTailorByNameID() {
        List<Tailor> tailorList = new ArrayList<>();
        ResultSet res = null;

        try {
            String tailor = "SELECT tailor_id, name FROM tailors";
            res = DbOperation.getData(tailor);

            while (res.next()) {
                Tailor tailorObj = new Tailor();
                tailorObj.setTailorId(res.getInt("tailor_id"));
                tailorObj.setName(res.getString("name"));
                tailorList.add(tailorObj);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tailorList;
    }

    public static double getTotalEarnings(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COALESCE(SUM(tailor_commission),0)
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

    public static int getTotalClotheSewed(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COUNT(*) AS total
        FROM orders
        WHERE tailor_id = ?
          AND date_received BETWEEN ? AND ?
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tailorId);
            ps.setDate(2, java.sql.Date.valueOf(fromDate));
            ps.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static double getTotalAmountPaid(String fromDate, String toDate, int tailorId) {

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

    public static double getTotalBalance(String fromDate, String toDate, int tailorId) {

        String sql = """
        SELECT COALESCE(SUM(balance),0)
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

    public static List<Orders> getOrdersWithImages(String fromDate, String toDate, int tailorId) {

        List<Orders> list = new ArrayList<>();

        String sql = """
        SELECT 
            o.order_id,
            c.name AS customer_name,
            o.cost,
            o.amount_paid,
            o.balance,
            o.due_date,
            o.status,

            oi.image_path,
            oi.sewed_image1

        FROM orders o
        JOIN customers c ON o.customer_id = c.customer_id

        LEFT JOIN order_images oi 
            ON oi.order_id = o.order_id
            AND oi.image_type = 'Before'

        WHERE o.tailor_id = ?
        AND o.date_received BETWEEN ? AND ?

        ORDER BY o.created_at DESC
    """;

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tailorId);
            ps.setString(2, fromDate);
            ps.setString(3, toDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Orders o = new Orders();

                o.setOrderId(rs.getInt("order_id"));
                o.setCustomerName(rs.getString("customer_name"));

                o.setTotalCost(rs.getDouble("cost"));
                o.setAmountPaid(rs.getDouble("amount_paid"));
                o.setBalance(rs.getDouble("balance"));

                o.setDueDate(rs.getString("due_date"));
                o.setStatus(rs.getString("status"));

                o.setImagePath(rs.getString("image_path"));
                o.setSewedImage1(rs.getString("sewed_image1"));

                list.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static double getOutstandingLoan(int tailorId) {
        String sql = "SELECT COALESCE(SUM(balance), 0) FROM tailor_loans WHERE tailor_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tailorId);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                return res.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static double getTotalLoan(int tailorId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM tailor_loans WHERE tailor_id = ?";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tailorId);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                return res.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static double getTotalPayments(int tailorId) {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) FROM tailor_payments WHERE tailor_id = ?";

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

    public static double getRemainingLoan(int tailorId) {
        return getOutstandingLoan(tailorId);
    }

    public static boolean makeLoanPayment(int tailorId, double amountPaid) {
        Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement insertAllocationStmt = null;

        try {
            con = ConnectionProvider.getCon();
            con.setAutoCommit(false);

            String insertSql = "INSERT INTO tailor_payments (tailor_id, amount_paid, payment_date) VALUES (?, ?, CURDATE())";
            pstmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, tailorId);
            pstmt.setDouble(2, amountPaid);
            int result = pstmt.executeUpdate();

            if (result == 0) {
                con.rollback();
                return false;
            }

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int paymentId = 0;
            if (generatedKeys.next()) {
                paymentId = generatedKeys.getInt(1);
            }

            String selectSql = "SELECT loan_id, balance FROM tailor_loans WHERE tailor_id = ? AND balance > 0 ORDER BY loan_date ASC";
            PreparedStatement selectStmt = con.prepareStatement(selectSql);
            selectStmt.setInt(1, tailorId);
            ResultSet rs = selectStmt.executeQuery();

            double remainingPayment = amountPaid;

            String insertAllocationSql = "INSERT INTO loan_payment_allocation (payment_id, loan_id, amount_applied) VALUES (?, ?, ?)";
            insertAllocationStmt = con.prepareStatement(insertAllocationSql);

            while (rs.next() && remainingPayment > 0) {
                int loanId = rs.getInt("loan_id");
                double balance = rs.getDouble("balance");

                double amountToApply = Math.min(balance, remainingPayment);

                String updateBalanceSql
                        = "UPDATE tailor_loans "
                        + "SET balance = balance - ?, "
                        + "status = CASE "
                        + "WHEN balance - ? <= 0 THEN 'paid' "
                        + "ELSE 'unpaid' END "
                        + "WHERE loan_id = ?";

                try (PreparedStatement updateBalanceStmt = con.prepareStatement(updateBalanceSql)) {
                    updateBalanceStmt.setDouble(1, amountToApply);
                    updateBalanceStmt.setDouble(2, amountToApply);
                    updateBalanceStmt.setInt(3, loanId);
                    updateBalanceStmt.executeUpdate();
                }

                // Record allocation
                insertAllocationStmt.setInt(1, paymentId);
                insertAllocationStmt.setInt(2, loanId);
                insertAllocationStmt.setDouble(3, amountToApply);
                insertAllocationStmt.executeUpdate();

                remainingPayment -= amountToApply;
            }

            con.commit();
            System.out.println("Payment of ₦" + amountPaid + " processed successfully");
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
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (insertAllocationStmt != null) {
                    insertAllocationStmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean updateLoanStatus(int tailorId) {
        String selectSql = "SELECT loan_id, amount FROM tailor_loans WHERE tailor_id = ? AND status = 'unpaid' ORDER BY loan_date ASC";
        String updateSql = "UPDATE tailor_loans SET status = 'paid' WHERE loan_id = ?";

        double totalPaid = getTotalPayments(tailorId);

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement selectStmt = con.prepareStatement(selectSql); PreparedStatement updateStmt = con.prepareStatement(updateSql)) {

            con.setAutoCommit(false);

            selectStmt.setInt(1, tailorId);
            ResultSet rs = selectStmt.executeQuery();

            double remainingToAllocate = totalPaid;
            boolean anyUpdate = false;

            while (rs.next() && remainingToAllocate > 0) {
                int loanId = rs.getInt("loan_id");
                double loanAmount = rs.getDouble("amount");

                if (remainingToAllocate >= loanAmount) {
                    updateStmt.setInt(1, loanId);
                    int result = updateStmt.executeUpdate();
                    if (result > 0) {
                        anyUpdate = true;
                        System.out.println("Loan " + loanId + " marked as PAID");
                    }
                    remainingToAllocate -= loanAmount;
                } else {
                    break;
                }
            }

            con.commit();
            return anyUpdate;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<LoanPayment> getPaymentHistory(int tailorId) {
        List<LoanPayment> payments = new ArrayList<>();
        String sql = "SELECT payment_id, amount_paid, payment_date FROM tailor_payments WHERE tailor_id = ? ORDER BY payment_date DESC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tailorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LoanPayment payment = new LoanPayment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setAmountPaid(rs.getDouble("amount_paid"));
                payment.setPaymentDate(rs.getDate("payment_date"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public static boolean verifyPaymentRecorded(int tailorId, double expectedAmount) {
        String sql = "SELECT COUNT(*) FROM tailor_payments WHERE tailor_id = ? AND amount_paid = ? AND DATE(payment_date) = CURDATE()";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, tailorId);
            pstmt.setDouble(2, expectedAmount);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<LoanDetails> getTailorLoanDetails() {
        List<LoanDetails> loans = new ArrayList<>();
        String sql = "SELECT "
                + "    tl.loan_id, "
                + "    tl.tailor_id, "
                + "    t.name AS tailor_name, "
                + "    tl.item_description, "
                + "    tl.amount AS loan_amount, "
                + "    tl.balance AS remaining_balance, "
                + "    tl.loan_date, "
                + "    tl.status, "
                + "    COALESCE((SELECT SUM(amount_paid) FROM tailor_payments WHERE loan_id = tl.loan_id), 0) AS total_paid, "
                + "    (SELECT MAX(payment_date) FROM tailor_payments WHERE loan_id = tl.loan_id) AS last_payment_date "
                + "FROM tailor_loans tl "
                + "INNER JOIN tailors t ON tl.tailor_id = t.tailor_id "
                + "ORDER BY tl.loan_date DESC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LoanDetails loan = new LoanDetails();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setTailorId(rs.getInt("tailor_id"));
                loan.setTailorName(rs.getString("tailor_name"));
                loan.setItemDescription(rs.getString("item_description"));
                loan.setLoanAmount(rs.getDouble("loan_amount"));
                loan.setRemainingBalance(rs.getDouble("remaining_balance"));
                loan.setLoanDate(rs.getDate("loan_date"));
                loan.setStatus(rs.getString("status"));
                loan.setTotalPaid(rs.getDouble("total_paid"));
                loan.setLastPaymentDate(rs.getDate("last_payment_date"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public static List<LoanDetailWithPayments> getDetailedLoanHistory() {
        List<LoanDetailWithPayments> loans = new ArrayList<>();

        String sql = "SELECT "
                + "    tl.loan_id, "
                + "    tl.tailor_id, "
                + "    t.name AS tailor_name, "
                + "    tl.item_name, "
                + "    tl.item_description, "
                + "    tl.amount AS loan_amount, "
                + "    tl.balance AS remaining_balance, "
                + "    tl.loan_date, "
                + "    tl.status, "
                + "    tp.payment_id, "
                + "    tp.amount_paid AS payment_amount, "
                + "    tp.payment_date, "
                + "    lpa.amount_applied, "
                + "    lpa.created_at AS allocation_date "
                + "FROM tailor_loans tl "
                + "INNER JOIN tailors t ON tl.tailor_id = t.tailor_id "
                + "LEFT JOIN loan_payment_allocation lpa ON tl.loan_id = lpa.loan_id "
                + "LEFT JOIN tailor_payments tp ON lpa.payment_id = tp.payment_id "
                + "ORDER BY tl.loan_date DESC, tp.payment_date ASC";

        try (Connection con = ConnectionProvider.getCon(); PreparedStatement pstmt = con.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            LoanDetailWithPayments currentLoan = null;
            int currentLoanId = -1;

            while (rs.next()) {
                int loanId = rs.getInt("loan_id");

                if (currentLoanId != loanId) {
                    if (currentLoan != null) {
                        loans.add(currentLoan);
                    }
                    currentLoan = new LoanDetailWithPayments();
                    currentLoan.setLoanId(loanId);
                    currentLoan.setTailorId(rs.getInt("tailor_id"));
                    currentLoan.setTailorName(rs.getString("tailor_name"));
                    currentLoan.setItemName(rs.getString("item_name"));
                    currentLoan.setItemDescription(rs.getString("item_description"));
                    currentLoan.setLoanAmount(rs.getDouble("loan_amount"));
                    currentLoan.setRemainingBalance(rs.getDouble("remaining_balance"));
                    currentLoan.setLoanDate(rs.getTimestamp("loan_date"));
                    currentLoan.setStatus(rs.getString("status"));
                    currentLoan.setPayments(new ArrayList<>());
                    currentLoanId = loanId;
                }

                // Add payment allocation if exists
                if (rs.getObject("payment_id") != null) {
                    LoanPaymentDetail payment = new LoanPaymentDetail();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setPaymentAmount(rs.getDouble("payment_amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setAmountApplied(rs.getDouble("amount_applied"));
                    payment.setAllocationDate(rs.getTimestamp("allocation_date"));
                    currentLoan.getPayments().add(payment);
                }
            }

            if (currentLoan != null) {
                loans.add(currentLoan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public static double getTotalCommissionFromOrders(int tailorId) {
        String sql = "SELECT COALESCE(SUM(tailor_commission), 0) "
                + "FROM orders "
                + "WHERE tailor_id = ? ";

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
}
