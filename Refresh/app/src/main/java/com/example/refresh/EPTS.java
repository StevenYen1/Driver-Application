package com.example.refresh;

public class EPTS {


    public EPTS(String referenceId){
        this.ReferenceId = referenceId;
        BusinessUnit = "SBD_US";
        ClientChannel = "web";
        ByPassLocal = "false";
        ShipmentNumber = "";
        this.RequestType = "ORD";
    }

    private String ReferenceId;

    private String RequestType;

    private String TrackingId;

    private String SCAC;

    private String ClientChannel;

    private String BusinessUnit;

    private String ByPassLocal;

    private String DestZipCode;

    private String ShippedDate;

    private String ShipmentNumber;

    public String getReferenceId() {
        return ReferenceId;
    }

    public String getRequestType() {
        return RequestType;
    }

    public String getTrackingId() {
        return TrackingId;
    }

    public String getSCAC() {
        return SCAC;
    }

    public String getClientChannel() {
        return ClientChannel;
    }

    public String getBusinessUnit() {
        return BusinessUnit;
    }

    public String getByPassLocal() {
        return ByPassLocal;
    }

    public String getDestZipCode() {
        return DestZipCode;
    }

    public String getShippedDate() {
        return ShippedDate;
    }

    public String getShipmentNumber() {
        return ShipmentNumber;
    }
}
