package com.example.refresh.Model;
/*
Description:
    The purpose of this class is to create virtual employees that contain all necessary information related to the employee.

Specific Features:
    Getters and Setters
    Construct EmployeeModel Object

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
public class EmployeeModel {

    /*
    private instance variables
     */
    private String firstname;
    private String lastname;
    private String username;

    /*
    Constructor that creates an EmployeeModel object
     */
    public EmployeeModel(String firstname, String lastname, String username){
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
