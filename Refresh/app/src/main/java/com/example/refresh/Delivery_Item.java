package com.example.refresh;

public class Delivery_Item {
    public final static int INCOMPLETE = 0;
    public final static int SCANNED = 1;
    public final static int COMPLETE = 2;
    public final static int SELECTED = 3;
    private String orderNumber;
    private String orderString;
    private String item;
    private int status;
    private String recipient;
    private String signature;

    public Delivery_Item(){
        this.orderNumber = "N/A";
        this.orderString = "N/A";
        this.item = "N/A";
        this.status = INCOMPLETE;
        this.recipient = "N/A";
        this.signature = "No Signature Yet";

    }

    public Delivery_Item(String orderNum, String orderString, String recipient, String item){
        this.orderNumber = orderNum;
        this.orderString = orderString;
        this.status = INCOMPLETE;
        this.recipient = recipient;
        this.item = item;
        this.signature = "No Signature Yet";

    }

    public Delivery_Item(String o, String a, String r, String i, int s){
        this.orderNumber = o;
        this.orderString = a;
        this.recipient = r;
        this.item = i;
        this.status = s;
    }

    public Delivery_Item(String orderNum, String orderStr) {
        this.orderNumber = orderNum;
        this.orderString = orderStr;
        this.status = INCOMPLETE;
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

    public int getStatus(){ return status; }

    public void setStatus(int newstatus){
        status = newstatus;
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
