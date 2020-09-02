package com.example.butcher.DBClasses;

public class Product
{
    private String name,price,description,menuID,image,productID,quantity;

    public Product()
    {}

    public Product(String name, String price, String description, String menuID, String image,String productID,String quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.menuID = menuID;
        this.image = image;
        this.productID = productID;
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
