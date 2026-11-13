/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Date;
import java.util.List;

/**
 *
 * @author oyeyemi
 */
public class SpecialProject {

    private int projectId;
    private String projectCode;
    private String projectName;
    private int categoryId;
    private String categoryName;
    private String clientName;
    private String clientPhone;
    private String clientAddress;
    private Date eventDate;
    private Date startDate;
    private Date completionDate;
    private String personInCharge;

    // Order Details
    private String description;
    private int quantity;
    private String productType;
    private String designName;
    private String embroideryDetails;
    private String specialRequirements;

    // Financial Records
    private double totalPrice;
    private double amountPaid;
    private double balanceRemaining;
    private String paymentStatus;
    private String paymentMethod;

    // Material Costs
    private double fabricCost;
    private double threadCost;
    private double accessoriesCost;
    private double packagingCost;
    private double otherMaterialsCost;
    private double totalMaterialCost;

    // Production Costs
    private double tailoringLabourCost;
    private double embroideryMachineCost;
    private double workersPayment;
    private double transportationCost;
    private double otherExpenses;
    private double totalProductionCost;

    // Totals and Profit
    private double totalIncome;
    private double totalExpenses;
    private double netProfit;

    // Status
    private String projectStatus;
    private int completionPercentage;

    // Images
    private String designImagePath;
    private String clientReferenceImagePath;
    private String invoiceImagePath;
    private String notes;

    private Date createdAt;
    private Date updatedAt;
    private List<ProjectHistoryLog> historyLogs;

    // Getters and Setters
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getEmbroideryDetails() {
        return embroideryDetails;
    }

    public void setEmbroideryDetails(String embroideryDetails) {
        this.embroideryDetails = embroideryDetails;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getBalanceRemaining() {
        return balanceRemaining;
    }

    public void setBalanceRemaining(double balanceRemaining) {
        this.balanceRemaining = balanceRemaining;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getFabricCost() {
        return fabricCost;
    }

    public void setFabricCost(double fabricCost) {
        this.fabricCost = fabricCost;
    }

    public double getThreadCost() {
        return threadCost;
    }

    public void setThreadCost(double threadCost) {
        this.threadCost = threadCost;
    }

    public double getAccessoriesCost() {
        return accessoriesCost;
    }

    public void setAccessoriesCost(double accessoriesCost) {
        this.accessoriesCost = accessoriesCost;
    }

    public double getPackagingCost() {
        return packagingCost;
    }

    public void setPackagingCost(double packagingCost) {
        this.packagingCost = packagingCost;
    }

    public double getOtherMaterialsCost() {
        return otherMaterialsCost;
    }

    public void setOtherMaterialsCost(double otherMaterialsCost) {
        this.otherMaterialsCost = otherMaterialsCost;
    }

    public double getTotalMaterialCost() {
        return totalMaterialCost;
    }

    public void setTotalMaterialCost(double totalMaterialCost) {
        this.totalMaterialCost = totalMaterialCost;
    }

    public double getTailoringLabourCost() {
        return tailoringLabourCost;
    }

    public void setTailoringLabourCost(double tailoringLabourCost) {
        this.tailoringLabourCost = tailoringLabourCost;
    }

    public double getEmbroideryMachineCost() {
        return embroideryMachineCost;
    }

    public void setEmbroideryMachineCost(double embroideryMachineCost) {
        this.embroideryMachineCost = embroideryMachineCost;
    }

    public double getWorkersPayment() {
        return workersPayment;
    }

    public void setWorkersPayment(double workersPayment) {
        this.workersPayment = workersPayment;
    }

    public double getTransportationCost() {
        return transportationCost;
    }

    public void setTransportationCost(double transportationCost) {
        this.transportationCost = transportationCost;
    }

    public double getOtherExpenses() {
        return otherExpenses;
    }

    public void setOtherExpenses(double otherExpenses) {
        this.otherExpenses = otherExpenses;
    }

    public double getTotalProductionCost() {
        return totalProductionCost;
    }

    public void setTotalProductionCost(double totalProductionCost) {
        this.totalProductionCost = totalProductionCost;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(double netProfit) {
        this.netProfit = netProfit;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getDesignImagePath() {
        return designImagePath;
    }

    public void setDesignImagePath(String designImagePath) {
        this.designImagePath = designImagePath;
    }

    public String getClientReferenceImagePath() {
        return clientReferenceImagePath;
    }

    public void setClientReferenceImagePath(String clientReferenceImagePath) {
        this.clientReferenceImagePath = clientReferenceImagePath;
    }

    public String getInvoiceImagePath() {
        return invoiceImagePath;
    }

    public void setInvoiceImagePath(String invoiceImagePath) {
        this.invoiceImagePath = invoiceImagePath;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProjectHistoryLog> getHistoryLogs() {
        return historyLogs;
    }

    public void setHistoryLogs(List<ProjectHistoryLog> historyLogs) {
        this.historyLogs = historyLogs;
    }

    // Helper methods for calculations
    public void calculateTotals() {
        // Calculate material total
        this.totalMaterialCost = fabricCost + threadCost + accessoriesCost + packagingCost + otherMaterialsCost;

        // Calculate production total
        this.totalProductionCost = tailoringLabourCost + embroideryMachineCost + workersPayment + transportationCost + otherExpenses;

        // Calculate total expenses
        this.totalExpenses = totalMaterialCost + totalProductionCost;

        // Calculate income
        this.totalIncome = totalPrice;

        // Calculate net profit
        this.netProfit = totalIncome - totalExpenses;

        // Calculate remaining balance
        this.balanceRemaining = totalPrice - amountPaid;

        // Update payment status based on balance
        if (balanceRemaining <= 0) {
            this.paymentStatus = "Paid";
        } else if (amountPaid > 0) {
            this.paymentStatus = "Partially Paid";
        } else {
            this.paymentStatus = "Pending";
        }
    }

}
