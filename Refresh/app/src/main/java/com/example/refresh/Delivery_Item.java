package com.example.refresh;

public class Delivery_Item {
    private String orderNumber;
    private String orderString;
    private String status;

    public Delivery_Item(){
        this.orderNumber = "0";
        this.orderString = "empty";
        this.status = "Incomplete";

    }

    public Delivery_Item(String orderNum, String orderString){
        this.orderNumber = orderNum;
        this.orderString = orderString;
        this.status = "Incomplete";

    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderString() {
        return orderString;
    }

    public String getStatus(){ return status; }
}
