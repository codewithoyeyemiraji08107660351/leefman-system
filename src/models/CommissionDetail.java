/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Date;

/**
 *
 * @author oyeyemi
 */
public class CommissionDetail {
    
        private int commissionId;
    private int orderId;
    private double amount;
    private double paidAmount;
    private double balance;
    private Date commissionDate;
    private String status;
    
    // Getters and Setters
    public int getCommissionId() { return commissionId; }
    public void setCommissionId(int commissionId) { this.commissionId = commissionId; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public Date getCommissionDate() { return commissionDate; }
    public void setCommissionDate(Date commissionDate) { this.commissionDate = commissionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
}
