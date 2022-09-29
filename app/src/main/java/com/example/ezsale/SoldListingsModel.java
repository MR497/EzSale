package com.example.ezsale;

public class SoldListingsModel {

    private String date, author, cost, description, name, zipcode;

    private SoldListingsModel() {}

    private SoldListingsModel(String date, String author, String cost, String description, String name, String zipcode) {
        this.date = date;
        this.author = author;
        this.cost = cost;
        this.description = description;
        this.name = name;
        this.zipcode = zipcode;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
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


}
