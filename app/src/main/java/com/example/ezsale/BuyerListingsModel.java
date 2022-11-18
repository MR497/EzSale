package com.example.ezsale;

public class BuyerListingsModel {

    private String email, author, cost, description, name, zipcode, picture;

    private BuyerListingsModel() {}

    BuyerListingsModel(String email, String author, String cost, String description, String name, String zipcode, String picture) {
        this.picture = picture;
        this.email = email;
        this.author = author;
        this.cost = cost;
        this.description = description;
        this.name = name;
        this.zipcode = zipcode;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getZipcode() {
        return zipcode;
    }
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
}
