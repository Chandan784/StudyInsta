package com.studyinsta.studyinsta.classes;

public class MyCoursesModel {
    private String productId;
    private String resource;
    private String productTitle;
    private String productDescription;

    public MyCoursesModel(String productId,String resource, String productTitle, String productDescription) {
        this.productId = productId;
        this.resource = resource;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
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
}
