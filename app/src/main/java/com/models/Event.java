package com.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {

    private String id= "";
    private String contract_id = "";
    private String name = "";
    private String description = "";
    private String country = "";
    private String created_date = "";
    private String starting_date = "";
    private String duration = "";
    private String loc_lat = "";
    private String loc_lon = "";
    private String status = "";
    private String publish = "";
    //private String new1 = "";

    public Event(String id,
                   String contract_id,
                   String name,
                   String description,
                   String country,
                   String created_date,
                   String starting_date,
                   String duration,
                   String loc_lat,
                   String loc_lon,
                   String status,
                   String publish) {

        this.id = id;
        this.contract_id = contract_id;
        this.name = name;
        this.description = description;
        this.country = country;
        this.created_date = created_date;
        this.starting_date = starting_date;
        this.duration = duration;
        this.loc_lat = loc_lat;
        this.loc_lon = loc_lon;
        this.status = status;
        this.publish = publish;
        //this.new1 = new1;
    }

    protected Event(Parcel in) {
        id = in.readString();
        contract_id = in.readString();
        name = in.readString();
        description = in.readString();
        country = in.readString();
        created_date = in.readString();
        starting_date = in.readString();
        duration = in.readString();
        loc_lat = in.readString();
        loc_lon = in.readString();
        status = in.readString();
        publish = in.readString();
        //new1 = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getid() {
        return id;
    }

    public String getcontract_id() {
        return contract_id;
    }

    public String getname() {
        return name;
    }

    public String getdescription() {
        return description;
    }

    public String getcountry() {
        return country;
    }

    public String getcreated_date() {
        return created_date;
    }

    public String getstarting_date() {
        return starting_date;
    }

    public String getduration() {
        return duration;
    }

    public String getloc_lat() {
        return loc_lat;
    }


    public String getloc_lon() {
        return loc_lon;
    }

    public String getstatus() {
        return status;
    }

    public String getpublish() {
        return publish;
    }


//    public String getnew1() {
//        return new1;
//    }
//
//    public void setnew1(String new1) {
//        this.new1 = new1;
//    }

    public void setpublish(String publish) {
        this.publish = publish;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(contract_id);
        dest.writeString(name);
        dest.writeString(loc_lat);
        dest.writeString(country);
        dest.writeString(created_date);
        dest.writeString(starting_date);
        dest.writeString(duration);
        dest.writeString(loc_lon);
        dest.writeString(status);
        dest.writeString(publish);
        //dest.writeString(new1);
    }
}

