package com.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Interest implements Serializable {

    private Status status;
    private ArrayList<Category> category;

    public Status getStatus() {
        return status;
    }

    public ArrayList<Category> getCategory() {
        return category;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCategory(ArrayList<Category> category) {
        this.category = category;
    }
}
