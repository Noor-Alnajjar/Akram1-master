package com.models;

import java.io.Serializable;

public class Contracts implements Serializable {
    String id,contract_id,marchent_id,sales_emp_id,company_name,country,gift_number,
            collect_number,redem_number,date,expiry_date,status,total,shop_lat,shop_lon,gift_event;

    public void setId(String id) {
        this.id = id;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }

    public void setMarchent_id(String marchent_id) {
        this.marchent_id = marchent_id;
    }

    public void setSales_emp_id(String sales_emp_id) {
        this.sales_emp_id = sales_emp_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setGift_number(String gift_number) {
        this.gift_number = gift_number;
    }

    public void setCollect_number(String collect_number) {
        this.collect_number = collect_number;
    }

    public void setRedem_number(String redem_number) {
        this.redem_number = redem_number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setShop_lat(String shop_lat) {
        this.shop_lat = shop_lat;
    }

    public void setShop_lon(String shop_lon) {
        this.shop_lon = shop_lon;
    }

    public void setGift_event(String gift_event) {
        this.gift_event = gift_event;
    }

    public String getId() {
        return id;
    }

    public String getContract_id() {
        return contract_id;
    }

    public String getMarchent_id() {
        return marchent_id;
    }

    public String getSales_emp_id() {
        return sales_emp_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getCountry() {
        return country;
    }

    public String getGift_number() {
        return gift_number;
    }

    public String getCollect_number() {
        return collect_number;
    }

    public String getRedem_number() {
        return redem_number;
    }

    public String getDate() {
        return date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public String getStatus() {
        return status;
    }

    public String getTotal() {
        return total;
    }

    public String getShop_lat() {
        return shop_lat;
    }

    public String getShop_lon() {
        return shop_lon;
    }

    public String getGift_event() {
        return gift_event;
    }
}
