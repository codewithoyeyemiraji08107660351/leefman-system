/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author oyeyemi
 */
public class CommissionSummary {

    private int tailorId;
    private String tailorName;
    private double totalCommission;
    private double totalPaid;
    private double outstandingCommission;

    // Getters and Setters
    public int getTailorId() {
        return tailorId;
    }

    public void setTailorId(int tailorId) {
        this.tailorId = tailorId;
    }

    public String getTailorName() {
        return tailorName;
    }

    public void setTailorName(String tailorName) {
        this.tailorName = tailorName;
    }

    public double getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(double totalCommission) {
        this.totalCommission = totalCommission;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getOutstandingCommission() {
        return outstandingCommission;
    }

    public void setOutstandingCommission(double outstandingCommission) {
        this.outstandingCommission = outstandingCommission;
    }
}
