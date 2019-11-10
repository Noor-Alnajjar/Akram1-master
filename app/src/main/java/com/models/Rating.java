package com.models;

import java.io.Serializable;

/**
 * Created by mg on 19/07/16.
 */
public class Rating implements Serializable {

    int flag_dislike_count;
    int flag_like_count;
    int rating_id;
    int sighting_id;

    public int getFlag_dislike_count() {
        return flag_dislike_count;
    }

    public void setFlag_dislike_count(int flag_dislike_count) {
        this.flag_dislike_count = flag_dislike_count;
    }

    public int getFlag_like_count() {
        return flag_like_count;
    }

    public void setFlag_like_count(int flag_like_count) {
        this.flag_like_count = flag_like_count;
    }

    public int getRating_id() {
        return rating_id;
    }

    public void setRating_id(int rating_id) {
        this.rating_id = rating_id;
    }

    public int getSighting_id() {
        return sighting_id;
    }

    public void setSighting_id(int sighting_id) {
        this.sighting_id = sighting_id;
    }
}
