package com.example.refresh.Model;

public class ItemModel {

    private String barcode;
    private String item;
    private String barcodeType;

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
