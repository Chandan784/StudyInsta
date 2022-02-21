package com.studyinsta.studyinsta.classes;

public class CartItemModel {

    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT =1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ////Cart Item
    private String productId;
    private String productImage;
    private String productTitle;
    private String productPrice;
    private String cuttedPrice;

    public CartItemModel(int type, String productId , String productImage, String productTitle, String productPrice, String cuttedPrice) {
        this.type = type;
        this.productId = productId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String  getProductImage() {
        return productImage;
    }

    public void setProductImage(String  productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    ////End Cart Item

    ////Cart Total Amount

    public CartItemModel(int type) {
        this.type = type;
    }

////End Cart Total Amount

}
