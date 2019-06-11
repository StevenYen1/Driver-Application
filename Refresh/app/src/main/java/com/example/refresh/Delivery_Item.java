package com.example.refresh;

public class Delivery_Item {
    private String orderNumber;
    private String orderString;

    public Delivery_Item(){
        this.orderNumber = "0";
        this.orderString = "empty";

    }

    public Delivery_Item(String orderNum, String orderString){
        this.orderNumber = orderNum;
        this.orderString = orderString;

    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderString() {
        return orderString;
    }
}
