package com.models;

import java.io.Serializable;

/**
 * Created by mg on 19/07/16.
 */
public class Pokemon implements Serializable {

    int created_at;
    int entity_id;
    String image;
    int is_deleted;
    String name;
    int pokemon_id;
    String type;
    int updated_at;

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(int entity_id) {
        this.entity_id = entity_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
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
}
