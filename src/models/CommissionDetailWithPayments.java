package models;

import java.util.Date;
import java.util.List;

public class CommissionDetailWithPayments {
    private int commissionId;
    private int tailorId;
    private String tailorName;
    private int orderId;
    private double commissionAmount;
    private double remainingBalance;
    private Date commissionDate;
    private String status;
    private double orderCost;
    private List<CommissionPaymentDetail> payments;
    
    // Getters and Setters
    public int getCommissionId() { return commissionId; }
    public void setCommissionId(int commissionId) { this.commissionId = commissionId; }
    
    public int getTailorId() { return tailorId; }
    public void setTailorId(int tailorId) { this.tailorId = tailorId; }
    
    public String getTailorName() { return tailorName; }
    public void setTailorName(String tailorName) { this.tailorName = tailorName; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public double getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(double commissionAmount) { this.commissionAmount = commissionAmount; }
    
    public double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }
    
    public double getTotalPaid() {
        double total = 0;
        if (payments != null) {
            for (CommissionPaymentDetail p : payments) {
                total += p.getAmountApplied();
            }
        }
        return total;
    }
    
    public Date getCommissionDate() { return commissionDate; }
    public void setCommissionDate(Date commissionDate) { this.commissionDate = commissionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getOrderCost() { return orderCost; }
    public void setOrderCost(double orderCost) { this.orderCost = orderCost; }
    
    public List<CommissionPaymentDetail> getPayments() { return payments; }
    public void setPayments(List<CommissionPaymentDetail> payments) { this.payments = payments; }
}