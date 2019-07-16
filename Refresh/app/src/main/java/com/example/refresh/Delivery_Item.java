package com.example.refresh;

public class Delivery_Item {
    public final static int INCOMPLETE = 0;
    public final static int SCANNED = 1;
    public final static int COMPLETE = 2;
    public final static int SELECTED = 3;
    public final static int FAIL_SEND = 4;
    private String orderNumber;
    private String orderString;
    private String item;
    private int status;
    private String recipient;
    private String signature;
    private int quantity;
    private String cartonNumber;

    public String getCartonNumber() {
        return cartonNumber;
    }

    public void setCartonNumber(String cartonNumber) {
        this.cartonNumber = cartonNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    public Delivery_Item(){
        this.orderNumber = "N/A";
        this.orderString = "N/A";
        this.item = "N/A";
        this.status = INCOMPLETE;
        this.recipient = "N/A";
        this.signature = "No Signature Yet";
        this.quantity = 1;
        this.cartonNumber = "0000";

    }

    public Delivery_Item(String orderNum, String orderString, String recipient, String item){
        this.orderNumber = orderNum;
        this.orderString = orderString;
        this.status = INCOMPLETE;
        this.recipient = recipient;
        this.item = item;
        this.signature = "No Signature Yet";
        this.quantity = 1;
        this.cartonNumber = "0000";
    }

    public Delivery_Item(String o, String a, String r, String i, int s){
        this.orderNumber = o;
        this.orderString = a;
        this.recipient = r;
        this.item = i;
        this.status = s;
        this.quantity = 1;
        this.cartonNumber = "0000";
    }

    public Delivery_Item(String o, String a, String r, String i, int s, int q, String cn){
        this.orderNumber = o;
        this.orderString = a;
        this.recipient = r;
        this.item = i;
        this.status = s;
        this.quantity = q;
        this.cartonNumber = cn;
    }

    public Delivery_Item(String orderNum, String orderStr) {
        this.orderNumber = orderNum;
        this.orderString = orderStr;
        this.status = INCOMPLETE;
        this.recipient = "database.getRecipient()";
        this.item = "database.getItem()";
        this.signature = "No Signature Yet";
        this.quantity = 1;
        this.cartonNumber = "0000";
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
