package com.example.klassenkassa.data;

public class Student implements Comparable<Student>{
    private int studentID;
    private int categoryID;
    private String firstname;
    private String lastname;
    private double cost;
    private Status status;
    private String additionalData;

    public Student(int studentID, int categoryID, String firstname, String lastname, double cost, Status status, String additionalData) {
        this.studentID = studentID;
        this.categoryID = categoryID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.cost = cost;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
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
