package com.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Entity implements Serializable {
	
	int entity_id;
	String entity_name;

	public Entity(int entity_id, String entity_name) {
		this.entity_id = entity_id;
		this.entity_name = entity_name;
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
}
