package models;

import java.util.Date;

public class CommissionPaymentDetail {
    private int paymentId;
    private double paymentAmount;
    private Date paymentDate;
    private String paymentMethod;
    private double amountApplied;
    private Date allocationDate;
    
    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }
    
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public double getAmountApplied() { return amountApplied; }
    public void setAmountApplied(double amountApplied) { this.amountApplied = amountApplied; }
    
    public Date getAllocationDate() { return allocationDate; }
    public void setAllocationDate(Date allocationDate) { this.allocationDate = allocationDate; }
}