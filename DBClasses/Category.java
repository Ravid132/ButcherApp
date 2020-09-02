package com.example.butcher.DBClasses;

public class Category {

    private String name,image,categoryID,description;

    public Category()
    {}

    public Category(String name, String image, String categoryID,String description) {
        this.name = name;
        this.image = image;
        this.categoryID = categoryID;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
