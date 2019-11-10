package com.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Gifts implements Serializable {

    String id,name,category_id,repetition,creat_at,update_at,lat,lon,date_found,visible,contract_id
            ,distance,gift_map_id,merchant_name,merchant_email,contract,merchant_id;

    ArrayList<Rules> rules ;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public String getMerchant_email() {
        return merchant_email;
    }

    public void setMerchant_email(String merchant_email) {
        this.merchant_email = merchant_email;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public ArrayList<Rules> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rules> rules) {
        this.rules = rules;
    }

    public String getGift_map_id() {
        return gift_map_id;
    }

    public void setGift_map_id(String gift_map_id) {
        this.gift_map_id = gift_map_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public void setCreat_at(String creat_at) {
        this.creat_at = creat_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setDate_found(String date_found) {
        this.date_found = date_found;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }



    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getRepetition() {
        return repetition;
    }

    public String getCreat_at() {
        return creat_at;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getDate_found() {
        return date_found;
    }

    public String getVisible() {
        return visible;
    }

    public String getContract_id() {
        return contract_id;
    }



    public String getDistance() {
        return distance;
    }
}
