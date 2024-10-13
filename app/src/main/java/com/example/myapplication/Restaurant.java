package com.example.myapplication;

public class Restaurant {
    private String name;
    private String address;
    private String phone;
    private String type;

    public Restaurant() {}

    public Restaurant(String name, String address, String phone, String type) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }
}

