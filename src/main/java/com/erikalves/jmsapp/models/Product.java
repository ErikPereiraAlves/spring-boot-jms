package com.erikalves.jmsapp.models;

import org.json.JSONObject;

import java.sql.Timestamp;
public class Product {


    private String productId;
    private String productName;
    private String productTitle;
    private String productDescription;
    private String productImageUrl;
    private double productPrice;
    private Timestamp createdTimestamp;
    private Timestamp updatedTimestamp;

    public Product() {
    }

    public Product(String productId, String productName, String productTitle, String productDescription, String productImageUrl, double productPrice, Timestamp createdTimestamp, Timestamp updatedTimestamp) {
        this.productId = productId;
        this.productName = productName;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.createdTimestamp = createdTimestamp;
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Timestamp getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Timestamp updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String toString() {
        return String.format("Product - {id=%s, name=%s, title=%s, description=%s, url=%s, price=%s,created=%s, updated}",
                getProductId(),getProductTitle(), getProductName(),getProductDescription(), getProductImageUrl(),getProductPrice(),getCreatedTimestamp(),getUpdatedTimestamp());
    }


}
