package com.example.klassenkassa.data;

import java.time.LocalDate;

public class Category implements Comparable<Category>{
    private int categoryID;
    private String name;
    private LocalDate dueDate;

    public Category(int categoryID, String name, LocalDate dueDate) {
        this.categoryID = categoryID;
        this.name = name;
        this.dueDate = dueDate;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public int compareTo(Category o) {
        return categoryID-o.categoryID;
    }
}
