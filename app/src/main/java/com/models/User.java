package com.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4736646656828092302L;
	
	@JsonProperty("id")
	int id;
	String name,gender,image,score,role,facebook_id,apikey,facebook_profile
			,insta_profile,phone,collect,redeem,dob;
	String full_name;
	String email;
	String password;

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setFacebook_id(String facebook_id) {
		this.facebook_id = facebook_id;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public void setFacebook_profile(String facebook_profile) {
		this.facebook_profile = facebook_profile;
	}

	public void setInsta_profile(String insta_profile) {
		this.insta_profile = insta_profile;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setCollect(String collect) {
		this.collect = collect;
	}

	public void setRedeem(String redeem) {
		this.redeem = redeem;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}





	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getGender() {
		return gender;
	}

	public String getImage() {
		return image;
	}

	public String getScore() {
		return score;
	}

	public String getRole() {
		return role;
	}

	public String getFacebook_id() {
		return facebook_id;
	}

	public String getApikey() {
		return apikey;
	}

	public String getFacebook_profile() {
		return facebook_profile;
	}

	public String getInsta_profile() {
		return insta_profile;
	}

	public String getPhone() {
		return phone;
	}

	public String getCollect() {
		return collect;
	}

	public String getRedeem() {
		return redeem;
	}

	public String getDob() {
		return dob;
	}



	public String getFull_name() {
		return full_name;
	}


	public String getEmail() {
		return email;
	}
}
