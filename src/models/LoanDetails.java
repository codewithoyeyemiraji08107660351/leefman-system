// Add missing fields to LoanDetails.java
package models;

import java.util.Date;

public class LoanDetails {
    private int loanId;
    private int tailorId;
    private String tailorName;
    private String itemName;
    private String itemDescription;
    private double loanAmount;
    private double remainingBalance;
    private Date loanDate;
    private String status;
    private double totalPaid;
    private Date lastPaymentDate;
    
    // Getters and Setters
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    
    public int getTailorId() { return tailorId; }
    public void setTailorId(int tailorId) { this.tailorId = tailorId; }
    
    public String getTailorName() { return tailorName; }
    public void setTailorName(String tailorName) { this.tailorName = tailorName; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    
    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }
    
    public double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(double remainingBalance) { this.remainingBalance = remainingBalance; }
    
    public Date getLoanDate() { return loanDate; }
    public void setLoanDate(Date loanDate) { this.loanDate = loanDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
    
    public Date getLastPaymentDate() { return lastPaymentDate; }
    public void setLastPaymentDate(Date lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
}