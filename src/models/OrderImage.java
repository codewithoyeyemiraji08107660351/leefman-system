/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author oyeyemi
 */
public class OrderImage {
    
    private int orderId;
    private String imageType;
    private String imagePath;
    private String secondImagePath;
    private String thirdImagePath;
    
    private String imageAfterFirst;
    private String imageAfterSecond;
    private String imageAfterThird;
    
    private String purchaseFrom;
    private String materialColor;
    private String materialUsed;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSecondImagePath() {
        return secondImagePath;
    }

    public void setSecondImagePath(String secondImagePath) {
        this.secondImagePath = secondImagePath;
    }

    public String getThirdImagePath() {
        return thirdImagePath;
    }

    public void setThirdImagePath(String thirdImagePath) {
        this.thirdImagePath = thirdImagePath;
    }

    public String getImageAfterFirst() {
        return imageAfterFirst;
    }

    public void setImageAfterFirst(String imageAfterFirst) {
        this.imageAfterFirst = imageAfterFirst;
    }

    public String getImageAfterSecond() {
        return imageAfterSecond;
    }

    public void setImageAfterSecond(String imageAfterSecond) {
        this.imageAfterSecond = imageAfterSecond;
    }

    public String getImageAfterThird() {
        return imageAfterThird;
    }

    public void setImageAfterThird(String imageAfterThird) {
        this.imageAfterThird = imageAfterThird;
    }

    public String getPurchaseFrom() {
        return purchaseFrom;
    }

    public void setPurchaseFrom(String purchaseFrom) {
        this.purchaseFrom = purchaseFrom;
    }

    public String getMaterialColor() {
        return materialColor;
    }

    public void setMaterialColor(String materialColor) {
        this.materialColor = materialColor;
    }

    public String getMaterialUsed() {
        return materialUsed;
    }

    public void setMaterialUsed(String materialUsed) {
        this.materialUsed = materialUsed;
    }
    
    
    
}
