package com.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataResponse {

	private Status status;
    private int result_count;
    private ArrayList<Sighting> sightings;
    private ArrayList<Gifts> gifts;
    private User user;
    private ArrayList<Contracts> contracts;

	Sighting sighting;

	public ArrayList<Contracts> getContracts() {
		return contracts;
	}

	public void setContracts(ArrayList<Contracts> contracts) {
		this.contracts = contracts;
	}

	public ArrayList<Gifts> getGifts() {
		return gifts;
	}

	public void setGifts(ArrayList<Gifts> gifts) {
		this.gifts = gifts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Sighting getSighting() {
		return sighting;
	}

	public void setSighting(Sighting sighting) {
		this.sighting = sighting;
	}

	public ArrayList<Pokemon> getPokemons() {
		return pokemons;
	}

	public void setPokemons(ArrayList<Pokemon> pokemons) {
		this.pokemons = pokemons;
	}

	private ArrayList<Pokemon> pokemons;

	public User getUser_info() {
		return user_info;
	}

	public void setUser_info(User user_info) {
		this.user_info = user_info;
	}

	private User user_info;



	public DataResponse() { }

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getResult_count() {
		return result_count;
	}

	public void setResult_count(int result_count) {
		this.result_count = result_count;
	}

	public ArrayList<Sighting> getSightings() {
		return sightings;
	}

	public void setSightings(ArrayList<Sighting> sightings) {
		this.sightings = sightings;
	}
}
