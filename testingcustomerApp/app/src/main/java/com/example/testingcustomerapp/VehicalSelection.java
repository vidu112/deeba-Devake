package com.example.testingcustomerapp;

public class VehicalSelection {
    private String name;
    private String imageURL;
    private String truckChild;
    private String price;

    public VehicalSelection(String name, String imageURL, String truckChild,String price) {
        this.name = name;
        this.imageURL = imageURL;
        this.price= price;
        this.truckChild = truckChild;
    }


    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }


    public String getPrice() {
        return price;
    }


    public String getTruckChild() {
        return truckChild;
    }
}
