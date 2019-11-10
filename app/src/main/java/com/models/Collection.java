package com.models;

/**
 * Created by user on 2/1/18.
 */

public class Collection {
    private String Sighting_id,Name,Type,Loc,Distance,item_id,expiry;
    private String scan = "0";

    public Collection() {
    }

    public Collection(String sighting_id, String name, String type, String loc, String distance,String item_id,String expiry,String scan) {
        this.Sighting_id = sighting_id;
        this.Name = name;
        this.Type = type;
        this.Loc = loc;
        this.Distance = distance;
        this.item_id = item_id;
        this.expiry = expiry;
        this.scan = scan;
    }
    public Collection(String sighting_id, String name, String type, String loc, String distance,String item_id,String expiry) {
        this.Sighting_id = sighting_id;
        this.Name = name;
        this.Type = type;
        this.Loc = loc;
        this.Distance = distance;
        this.item_id = item_id;
        this.expiry = expiry;
    }

    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }

    public String getSighting_id() {
        return Sighting_id;
    }

    public String getName() {
        return Name;
    }

    public String getType() {
        return Type;
    }

    public String getLoc() {
        return Loc;
    }

    public String getDistance() {
        return Distance;
    }

    public void setSighting_id(String sighting_id) {
        Sighting_id = sighting_id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setLoc(String loc) {
        Loc = loc;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
