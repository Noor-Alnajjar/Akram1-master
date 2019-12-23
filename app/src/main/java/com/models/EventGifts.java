package com.models;

import android.os.Parcel;
import android.os.Parcelable;

public class EventGifts implements Parcelable {

    private String id= "";
    private String gift_id = "";
    private String date_found = "";
    private String lat = "";
    private String lon = "";
    private String visible = "";
    private String creat_at = "";
    private String update_at = "";
    private String is_deleted = "";
    private String country = "";

    public EventGifts(String id,
                 String gift_id,
                 String date_found,
                      String lat,
                      String lon,
                 String visible,
                 String creat_at,
                 String update_at,
                 String is_deleted,
                 String country) {

        this.id = id;
        this.gift_id = gift_id;
        this.date_found = date_found;
        this.visible = visible;
        this.creat_at = creat_at;
        this.update_at = update_at;
        this.is_deleted = is_deleted;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    protected EventGifts(Parcel in) {
        id = in.readString();
        gift_id = in.readString();
        date_found = in.readString();
        visible = in.readString();
        creat_at = in.readString();
        update_at = in.readString();
        is_deleted = in.readString();
        country = in.readString();
        lat = in.readString();
        lon = in.readString();
    }

    public static final Creator<EventGifts> CREATOR = new Creator<EventGifts>() {
        @Override
        public EventGifts createFromParcel(Parcel in) {
            return new EventGifts(in);
        }

        @Override
        public EventGifts[] newArray(int size) {
            return new EventGifts[size];
        }
    };

    public String getid() {
        return id;
    }

    public String getgift_id() {
        return gift_id;
    }

    public String getdate_found() {
        return date_found;
    }

    public String getvisible() {
        return visible;
    }

    public String getcreat_at() {
        return creat_at;
    }

    public String getupdate_at() {
        return update_at;
    }

    public String getis_deleted() {
        return is_deleted;
    }

    public String getcountry() {
        return country;
    }

    public String getlat() {
        return lat;
    }


    public String getlon() {
        return lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(gift_id);
        dest.writeString(date_found);
        dest.writeString(lat);
        dest.writeString(creat_at);
        dest.writeString(update_at);
        dest.writeString(is_deleted);
        dest.writeString(country);
        dest.writeString(lon);
    }
}

