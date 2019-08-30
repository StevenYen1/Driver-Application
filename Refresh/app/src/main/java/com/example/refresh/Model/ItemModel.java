package com.example.refresh.Model;
/*
Description:
    The purpose of this class is to create virtual items that contain all necessary information related to the item.

Specific Features:
    Getters and Setters
    Construct ItemModel Object

Documentation & Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
public class ItemModel {

    /*
    private instance variables
     */
    private String barcode;
    private String item;
    private String barcodeType;

    /*
    Constructor that creates an ItemModel object
     */
    public ItemModel(String barcode, String barcodeType, String item) {
        this.barcode = barcode;
        this.barcodeType = barcodeType;
        this.item = item;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
    }
}
