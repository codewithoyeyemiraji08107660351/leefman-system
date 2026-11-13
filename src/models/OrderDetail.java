/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author oyeyemi
 */
public class OrderDetail {
    
    private int detialsId;
    private int orderId;
    private String sewingStyle;
    private String itemUsed;
    private String measurement;
    private String accessories;

    public int getDetialsId() {
        return detialsId;
    }

    public void setDetialsId(int detialsId) {
        this.detialsId = detialsId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSewingStyle() {
        return sewingStyle;
    }

    public void setSewingStyle(String sewingStyle) {
        this.sewingStyle = sewingStyle;
    }

    public String getItemUsed() {
        return itemUsed;
    }

    public void setItemUsed(String itemUsed) {
        this.itemUsed = itemUsed;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }
    
    
    
    
}
