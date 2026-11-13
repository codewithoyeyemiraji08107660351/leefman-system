/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author oyeyemi
 */
public class ProjectSummary {

    private String categoryName;
    private int totalProjects;
    private int totalQuantity;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;
    private double pendingPayments;
    private int completedProjects;
    private String largestProjectName;
    private double largestProjectRevenue;
    private String mostProfitableProject;
    private double mostProfitableProfit;

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public double getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(double pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public int getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(int completedProjects) {
        this.completedProjects = completedProjects;
    }

    public String getLargestProjectName() {
        return largestProjectName;
    }

    public void setLargestProjectName(String largestProjectName) {
        this.largestProjectName = largestProjectName;
    }

    public double getLargestProjectRevenue() {
        return largestProjectRevenue;
    }

    public void setLargestProjectRevenue(double largestProjectRevenue) {
        this.largestProjectRevenue = largestProjectRevenue;
    }

    public String getMostProfitableProject() {
        return mostProfitableProject;
    }

    public void setMostProfitableProject(String mostProfitableProject) {
        this.mostProfitableProject = mostProfitableProject;
    }

    public double getMostProfitableProfit() {
        return mostProfitableProfit;
    }

    public void setMostProfitableProfit(double mostProfitableProfit) {
        this.mostProfitableProfit = mostProfitableProfit;
    }

}
