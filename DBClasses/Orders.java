package com.example.butcher.DBClasses;

public class Orders
{
    private String name,phone,price;


    public Orders()
    {}

    public Orders(String name, String phone, String price) {
        this.name = name;
        this.phone = phone;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
