package com.example.skymall.data.model;

import java.io.Serializable;

public class Category implements Serializable {
    public int id;
    public String name;
    public String image;
    public String description;

    public Category() {
    }

    public Category(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = "";
    }

    public Category(int id, String name, String image, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
    }
}
