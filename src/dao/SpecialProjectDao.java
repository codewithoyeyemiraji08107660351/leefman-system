// dao/SpecialProjectDao.java
package dao;

import dbConnection.ConnectionProvider;
import models.SpecialProject;
import models.ProjectHistoryLog;
import models.ProjectSummary;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialProjectDao {
    
    // Generate project code
    public static String generateProjectCode() {
        String sql = "SELECT MAX(project_id) FROM special_projects";
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            return "PRJ-" + String.format("%06d", nextId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "PRJ-" + System.currentTimeMillis();
        }
    }
    
    // Save or update project
    public static boolean saveProject(SpecialProject project) {
        String sql = "INSERT INTO special_projects (project_code, project_name, category_id, client_name, "
                + "client_phone, client_address, event_date, start_date, completion_date, person_in_charge, "
                + "description, quantity, product_type, design_name, embroidery_details, special_requirements, "
                + "total_price, amount_paid, balance_remaining, payment_status, payment_method, "
                + "fabric_cost, thread_cost, accessories_cost, packaging_cost, other_materials_cost, "
                + "total_material_cost, tailoring_labour_cost, embroidery_machine_cost, workers_payment, "
                + "transportation_cost, other_expenses, total_production_cost, "
                + "total_income, total_expenses, net_profit, project_status, completion_percentage, "
                + "design_image_path, client_reference_image_path, invoice_image_path, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConnectionProvider.getCon();
           PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            project.calculateTotals();
            
            pstmt.setString(1, project.getProjectCode());
            pstmt.setString(2, project.getProjectName());
            pstmt.setInt(3, project.getCategoryId());
            pstmt.setString(4, project.getClientName());
            pstmt.setString(5, project.getClientPhone());
            pstmt.setString(6, project.getClientAddress());
            pstmt.setDate(7, project.getEventDate() != null ? new java.sql.Date(project.getEventDate().getTime()) : null);
            pstmt.setDate(8, new java.sql.Date(project.getStartDate().getTime()));
            pstmt.setDate(9, project.getCompletionDate() != null ? new java.sql.Date(project.getCompletionDate().getTime()) : null);
            pstmt.setString(10, project.getPersonInCharge());
            pstmt.setString(11, project.getDescription());
            pstmt.setInt(12, project.getQuantity());
            pstmt.setString(13, project.getProductType());
            pstmt.setString(14, project.getDesignName());
            pstmt.setString(15, project.getEmbroideryDetails());
            pstmt.setString(16, project.getSpecialRequirements());
            pstmt.setDouble(17, project.getTotalPrice());
            pstmt.setDouble(18, project.getAmountPaid());
            pstmt.setDouble(19, project.getBalanceRemaining());
            pstmt.setString(20, project.getPaymentStatus());
            pstmt.setString(21, project.getPaymentMethod());
            pstmt.setDouble(22, project.getFabricCost());
            pstmt.setDouble(23, project.getThreadCost());
            pstmt.setDouble(24, project.getAccessoriesCost());
            pstmt.setDouble(25, project.getPackagingCost());
            pstmt.setDouble(26, project.getOtherMaterialsCost());
            pstmt.setDouble(27, project.getTotalMaterialCost());
            pstmt.setDouble(28, project.getTailoringLabourCost());
            pstmt.setDouble(29, project.getEmbroideryMachineCost());
            pstmt.setDouble(30, project.getWorkersPayment());
            pstmt.setDouble(31, project.getTransportationCost());
            pstmt.setDouble(32, project.getOtherExpenses());
            pstmt.setDouble(33, project.getTotalProductionCost());
            pstmt.setDouble(34, project.getTotalIncome());
            pstmt.setDouble(35, project.getTotalExpenses());
            pstmt.setDouble(36, project.getNetProfit());
            pstmt.setString(37, project.getProjectStatus());
            pstmt.setInt(38, project.getCompletionPercentage());
            pstmt.setString(39, project.getDesignImagePath());
            pstmt.setString(40, project.getClientReferenceImagePath());
            pstmt.setString(41, project.getInvoiceImagePath());
            pstmt.setString(42, project.getNotes());
            
            int result = pstmt.executeUpdate();
            
            // Add initial history log if new project
            if (result > 0 && project.getProjectId() == 0) {
                // Get the generated ID
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int projectId = rs.getInt(1);
                    addHistoryLog(projectId, "Project Created", project.getPersonInCharge(), "Project created successfully");
                }
            }
            
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update project
    public static boolean updateProject(SpecialProject project) {
        String sql = "UPDATE special_projects SET project_name = ?, category_id = ?, client_name = ?, "
                + "client_phone = ?, client_address = ?, event_date = ?, start_date = ?, completion_date = ?, "
                + "person_in_charge = ?, description = ?, quantity = ?, product_type = ?, design_name = ?, "
                + "embroidery_details = ?, special_requirements = ?, total_price = ?, amount_paid = ?, "
                + "balance_remaining = ?, payment_status = ?, payment_method = ?, "
                + "fabric_cost = ?, thread_cost = ?, accessories_cost = ?, packaging_cost = ?, "
                + "other_materials_cost = ?, total_material_cost = ?, tailoring_labour_cost = ?, "
                + "embroidery_machine_cost = ?, workers_payment = ?, transportation_cost = ?, "
                + "other_expenses = ?, total_production_cost = ?, total_income = ?, total_expenses = ?, "
                + "net_profit = ?, project_status = ?, completion_percentage = ?, "
                + "design_image_path = ?, client_reference_image_path = ?, invoice_image_path = ?, notes = ? "
                + "WHERE project_id = ?";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            project.calculateTotals();
            
            pstmt.setString(1, project.getProjectName());
            pstmt.setInt(2, project.getCategoryId());
            pstmt.setString(3, project.getClientName());
            pstmt.setString(4, project.getClientPhone());
            pstmt.setString(5, project.getClientAddress());
            pstmt.setDate(6, project.getEventDate() != null ? new java.sql.Date(project.getEventDate().getTime()) : null);
            pstmt.setDate(7, new java.sql.Date(project.getStartDate().getTime()));
            pstmt.setDate(8, project.getCompletionDate() != null ? new java.sql.Date(project.getCompletionDate().getTime()) : null);
            pstmt.setString(9, project.getPersonInCharge());
            pstmt.setString(10, project.getDescription());
            pstmt.setInt(11, project.getQuantity());
            pstmt.setString(12, project.getProductType());
            pstmt.setString(13, project.getDesignName());
            pstmt.setString(14, project.getEmbroideryDetails());
            pstmt.setString(15, project.getSpecialRequirements());
            pstmt.setDouble(16, project.getTotalPrice());
            pstmt.setDouble(17, project.getAmountPaid());
            pstmt.setDouble(18, project.getBalanceRemaining());
            pstmt.setString(19, project.getPaymentStatus());
            pstmt.setString(20, project.getPaymentMethod());
            pstmt.setDouble(21, project.getFabricCost());
            pstmt.setDouble(22, project.getThreadCost());
            pstmt.setDouble(23, project.getAccessoriesCost());
            pstmt.setDouble(24, project.getPackagingCost());
            pstmt.setDouble(25, project.getOtherMaterialsCost());
            pstmt.setDouble(26, project.getTotalMaterialCost());
            pstmt.setDouble(27, project.getTailoringLabourCost());
            pstmt.setDouble(28, project.getEmbroideryMachineCost());
            pstmt.setDouble(29, project.getWorkersPayment());
            pstmt.setDouble(30, project.getTransportationCost());
            pstmt.setDouble(31, project.getOtherExpenses());
            pstmt.setDouble(32, project.getTotalProductionCost());
            pstmt.setDouble(33, project.getTotalIncome());
            pstmt.setDouble(34, project.getTotalExpenses());
            pstmt.setDouble(35, project.getNetProfit());
            pstmt.setString(36, project.getProjectStatus());
            pstmt.setInt(37, project.getCompletionPercentage());
            pstmt.setString(38, project.getDesignImagePath());
            pstmt.setString(39, project.getClientReferenceImagePath());
            pstmt.setString(40, project.getInvoiceImagePath());
            pstmt.setString(41, project.getNotes());
            pstmt.setInt(42, project.getProjectId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all projects
    public static List<SpecialProject> getAllProjects() {
        List<SpecialProject> projects = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM special_projects p "
                + "LEFT JOIN project_categories c ON p.category_id = c.category_id "
                + "ORDER BY p.created_at DESC";
        
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
    
    // Get project by ID
    public static SpecialProject getProjectById(int projectId) {
        String sql = "SELECT p.*, c.category_name FROM special_projects p "
                + "LEFT JOIN project_categories c ON p.category_id = c.category_id "
                + "WHERE p.project_id = ?";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                SpecialProject project = mapResultSetToProject(rs);
                project.setHistoryLogs(getProjectHistory(projectId));
                return project;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get project by code
    public static SpecialProject getProjectByCode(String projectCode) {
        String sql = "SELECT p.*, c.category_name FROM special_projects p "
                + "LEFT JOIN project_categories c ON p.category_id = c.category_id "
                + "WHERE p.project_code = ?";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, projectCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProject(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get project history
    public static List<ProjectHistoryLog> getProjectHistory(int projectId) {
        List<ProjectHistoryLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM project_history_log WHERE project_id = ? ORDER BY log_date DESC";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ProjectHistoryLog log = new ProjectHistoryLog();
                log.setLogId(rs.getInt("log_id"));
                log.setProjectId(rs.getInt("project_id"));
                log.setActivity(rs.getString("activity"));
                log.setPersonResponsible(rs.getString("person_responsible"));
                log.setNote(rs.getString("note"));
                log.setLogDate(rs.getTimestamp("log_date"));
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    // Add history log
    public static boolean addHistoryLog(int projectId, String activity, String personResponsible, String note) {
        String sql = "INSERT INTO project_history_log (project_id, activity, person_responsible, note) VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, projectId);
            pstmt.setString(2, activity);
            pstmt.setString(3, personResponsible);
            pstmt.setString(4, note);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get project summary by category
    public static List<ProjectSummary> getProjectSummary() {
        List<ProjectSummary> summaries = new ArrayList<>();
        String sql = "SELECT "
                + "    c.category_name, "
                + "    COUNT(p.project_id) AS total_projects, "
                + "    SUM(p.quantity) AS total_quantity, "
                + "    SUM(p.total_price) AS total_revenue, "
                + "    SUM(p.total_expenses) AS total_cost, "
                + "    SUM(p.net_profit) AS total_profit, "
                + "    SUM(p.balance_remaining) AS pending_payments, "
                + "    SUM(CASE WHEN p.project_status = 'Completed' OR p.project_status = 'Delivered' THEN 1 ELSE 0 END) AS completed_projects, "
                + "    (SELECT project_name FROM special_projects WHERE category_id = c.category_id ORDER BY total_price DESC LIMIT 1) AS largest_project, "
                + "    (SELECT total_price FROM special_projects WHERE category_id = c.category_id ORDER BY total_price DESC LIMIT 1) AS largest_revenue, "
                + "    (SELECT project_name FROM special_projects WHERE category_id = c.category_id ORDER BY net_profit DESC LIMIT 1) AS most_profitable, "
                + "    (SELECT net_profit FROM special_projects WHERE category_id = c.category_id ORDER BY net_profit DESC LIMIT 1) AS most_profitable_profit "
                + "FROM project_categories c "
                + "LEFT JOIN special_projects p ON c.category_id = p.category_id "
                + "GROUP BY c.category_id, c.category_name";
        
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ProjectSummary summary = new ProjectSummary();
                summary.setCategoryName(rs.getString("category_name"));
                summary.setTotalProjects(rs.getInt("total_projects"));
                summary.setTotalQuantity(rs.getInt("total_quantity"));
                summary.setTotalRevenue(rs.getDouble("total_revenue"));
                summary.setTotalCost(rs.getDouble("total_cost"));
                summary.setTotalProfit(rs.getDouble("total_profit"));
                summary.setPendingPayments(rs.getDouble("pending_payments"));
                summary.setCompletedProjects(rs.getInt("completed_projects"));
                summary.setLargestProjectName(rs.getString("largest_project"));
                summary.setLargestProjectRevenue(rs.getDouble("largest_revenue"));
                summary.setMostProfitableProject(rs.getString("most_profitable"));
                summary.setMostProfitableProfit(rs.getDouble("most_profitable_profit"));
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }
    
    // Delete project
    public static boolean deleteProject(int projectId) {
        // First delete history logs
        String deleteLogsSql = "DELETE FROM project_history_log WHERE project_id = ?";
        String deleteProjectSql = "DELETE FROM special_projects WHERE project_id = ?";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmtLogs = con.prepareStatement(deleteLogsSql);
             PreparedStatement pstmtProject = con.prepareStatement(deleteProjectSql)) {
            
            con.setAutoCommit(false);
            
            pstmtLogs.setInt(1, projectId);
            pstmtLogs.executeUpdate();
            
            pstmtProject.setInt(1, projectId);
            int result = pstmtProject.executeUpdate();
            
            con.commit();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get categories
    public static List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM project_categories ORDER BY category_name";
        
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    // Get category ID by name
    public static int getCategoryIdByName(String categoryName) {
        String sql = "SELECT category_id FROM project_categories WHERE category_name = ?";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("category_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Map ResultSet to Project object
    private static SpecialProject mapResultSetToProject(ResultSet rs) throws SQLException {
        SpecialProject project = new SpecialProject();
        project.setProjectId(rs.getInt("project_id"));
        project.setProjectCode(rs.getString("project_code"));
        project.setProjectName(rs.getString("project_name"));
        project.setCategoryId(rs.getInt("category_id"));
        project.setCategoryName(rs.getString("category_name"));
        project.setClientName(rs.getString("client_name"));
        project.setClientPhone(rs.getString("client_phone"));
        project.setClientAddress(rs.getString("client_address"));
        project.setEventDate(rs.getDate("event_date"));
        project.setStartDate(rs.getDate("start_date"));
        project.setCompletionDate(rs.getDate("completion_date"));
        project.setPersonInCharge(rs.getString("person_in_charge"));
        project.setDescription(rs.getString("description"));
        project.setQuantity(rs.getInt("quantity"));
        project.setProductType(rs.getString("product_type"));
        project.setDesignName(rs.getString("design_name"));
        project.setEmbroideryDetails(rs.getString("embroidery_details"));
        project.setSpecialRequirements(rs.getString("special_requirements"));
        project.setTotalPrice(rs.getDouble("total_price"));
        project.setAmountPaid(rs.getDouble("amount_paid"));
        project.setBalanceRemaining(rs.getDouble("balance_remaining"));
        project.setPaymentStatus(rs.getString("payment_status"));
        project.setPaymentMethod(rs.getString("payment_method"));
        project.setFabricCost(rs.getDouble("fabric_cost"));
        project.setThreadCost(rs.getDouble("thread_cost"));
        project.setAccessoriesCost(rs.getDouble("accessories_cost"));
        project.setPackagingCost(rs.getDouble("packaging_cost"));
        project.setOtherMaterialsCost(rs.getDouble("other_materials_cost"));
        project.setTotalMaterialCost(rs.getDouble("total_material_cost"));
        project.setTailoringLabourCost(rs.getDouble("tailoring_labour_cost"));
        project.setEmbroideryMachineCost(rs.getDouble("embroidery_machine_cost"));
        project.setWorkersPayment(rs.getDouble("workers_payment"));
        project.setTransportationCost(rs.getDouble("transportation_cost"));
        project.setOtherExpenses(rs.getDouble("other_expenses"));
        project.setTotalProductionCost(rs.getDouble("total_production_cost"));
        project.setTotalIncome(rs.getDouble("total_income"));
        project.setTotalExpenses(rs.getDouble("total_expenses"));
        project.setNetProfit(rs.getDouble("net_profit"));
        project.setProjectStatus(rs.getString("project_status"));
        project.setCompletionPercentage(rs.getInt("completion_percentage"));
        project.setDesignImagePath(rs.getString("design_image_path"));
        project.setClientReferenceImagePath(rs.getString("client_reference_image_path"));
        project.setInvoiceImagePath(rs.getString("invoice_image_path"));
        project.setNotes(rs.getString("notes"));
        project.setCreatedAt(rs.getTimestamp("created_at"));
        project.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return project;
    }
}