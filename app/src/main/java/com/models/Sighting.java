package com.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mg on 19/07/16.
 */
public class Sighting implements Serializable {

    int created_at;
    String date_found;
    double distance;
    int entity_id;
    String entity_name;
    String image;
    double lat;
    double lon;
    String name;
    int pokemon_id;
    int sighting_id;
    String type;
    int updated_at;
    int user_id;
    int flag_count;
    private ArrayList rules;

    public ArrayList getRules() {
        return rules;
    }

    public void setRules(ArrayList rules) {
        this.rules = rules;
    }

    public int getFlag_count() {
        return flag_count;
    }

    public void setFlag_count(int flag_count) {
        this.flag_count = flag_count;
    }

    User user;
    double rating =0;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public String getDate_found() {
        return date_found;
    }

    public void setDate_found(String date_found) {
        this.date_found = date_found;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(int entity_id) {
        this.entity_id = entity_id;
    }

    public String getEntity_name() {
        return entity_name;
    }

    public void setEntity_name(String entity_name) {
        this.entity_name = entity_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPokemon_id() {
        return pokemon_id;
    }

    public void setPokemon_id(int pokemon_id) {
        this.pokemon_id = pokemon_id;
    }

    public int getSighting_id() {
        return sighting_id;
    }

    public void setSighting_id(int sighting_id) {
        this.sighting_id = sighting_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
