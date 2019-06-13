package com.example.refresh;

public class Delivery_Item {
    private String orderNumber;
    private String orderString;
    private String item;
    private String status;
    private String recipient;
    private String signature;

    public Delivery_Item(){
        this.orderNumber = "N/A";
        this.orderString = "N/A";
        this.item = "N/A";
        this.status = "Incomplete";
        this.recipient = "N/A";
        this.signature = "No Signature Yet";

    }

    public Delivery_Item(String orderNum, String orderString, String recipient, String item){
        this.orderNumber = orderNum;
        this.orderString = orderString;
        this.status = "Incomplete";
        this.recipient = recipient;
        this.item = item;
        this.signature = "No Signature Yet";

    }

    public Delivery_Item(String orderNum, String orderStr) {
        this.orderNumber = orderNum;
        this.orderString = orderStr;
        this.status = "Incomplete";
        this.recipient = "database.getRecipient()";
        this.item = "database.getItem()";
        this.signature = "No Signature Yet";

    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderString() {
        return orderString;
    }

    public String getStatus(){ return status; }

    public void setStatus(String str){
        status = str;
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
