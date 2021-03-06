package com.example.klassenkassa.data;

import java.io.Serializable;

public class Student implements Comparable<Student>, Serializable {
    private int studentID;
    private int categoryID;
    private String firstname;
    private String lastname;
    private float cost;
    private Status status;
    private String additionalData;

    public Student(int studentID, int categoryID, String firstname, String lastname, double cost, Status status, String additionalData) {
        this.studentID = studentID;
        this.categoryID = categoryID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.cost = (float)((int)(cost*100))/100;
        this.status = status;
        this.additionalData = additionalData;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public int compareTo(Student o) {
        return studentID-o.studentID;
    }
}
