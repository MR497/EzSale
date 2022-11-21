package com.example.ezsale.models;

public class RecentContactsListingModel {

    private String date, item, seller;

    private RecentContactsListingModel() {}

    private RecentContactsListingModel(String date, String item, String seller) {
        this.date = date;
        this.item = item;
        this.seller = seller;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public String getSeller() {
        return seller;
    }
    public void setSeller(String seller) {
        this.seller = seller;
    }

}
