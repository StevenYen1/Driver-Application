package com.example.refresh.ItemModel;
/*
Description:
    The purpose of this class is to create virtual packages that contain all neccessary order information.

Specific Features:
    Getters and Setters
    Construct PackageModel Object

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
public class PackageModel {

    /*
    status values for packages
     */
    public final static int INCOMPLETE = 0;
    public final static int SCANNED = 1;
    public final static int COMPLETE = 2;
    public final static int SELECTED = 3;
    public final static int FAIL_SEND = 4;

    /*
    private instance variables
     */
    private String orderNumber;
    private String address;
    private String item;
    private int status;
    private String recipient;
    private String signature;
    private int quantity;
    private String cartonNumber;

    /*
    Constructor that creates a PackageModel object
     */
    public PackageModel(String orderNumber, String address, String recipient,
                        String item, int status, int quantity, String cartonNumber){
        this.orderNumber = orderNumber;
        this.address = address;
        this.recipient = recipient;
        this.item = item;
        this.status = status;
        this.quantity = quantity;
        this.cartonNumber = cartonNumber;
    }

    public String getCartonNumber() {
        return cartonNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getAddress() {
        return address;
    }

    public int getStatus(){ return status; }

    public void setStatus(int newStatus){
        status = newStatus;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getItem() {
        return item;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature){
        this.signature = signature;
    }
}
