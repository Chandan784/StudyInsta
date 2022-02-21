package com.studyinsta.studyinsta.classes;

public class CommonPurchasedListModel {

    private int materialType;
    private String title;
    private String idOrUrl;

    public CommonPurchasedListModel(int materialType, String title, String idOrUrl) {
        this.title = title;
        this.idOrUrl = idOrUrl;
        this.materialType = materialType;
    }

    public int getMaterialType() {
        return materialType;
    }

    public void setMaterialType(int materialType) {
        this.materialType = materialType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdOrUrl() {
        return idOrUrl;
    }

    public void setIdOrUrl(String idOrUrl) {
        this.idOrUrl = idOrUrl;
    }
}
