package com.libraries.usersession;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class UserAccessSession {

	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String GOOGLE_ID	 	= "sekln0LDNKANWds23";
	private static final String FACEBOOK_ID 	= "sekln0LDNKANWdskf";
	private static final String TWITTER_ID 	= "OIanlknfalk3lnk2a";
	private static final String USER_ID 		= "23vponrnkl32brlkn";
	private static final String LOGIN_HASH 	= "340bji4riwbnlrvas";
	private static final String FULL_NAME 		= "5b03i3ipbp3454LLK";
	private static final String USER_NAME 		= "65po7jboyioen2Kid";
	private static final String EMAIL 			= "54690j945safnKNKI";
	private static final String THUMB_URL 		= "sadnka008adklasdk";
	private static final String PHOTO_URL 		= "8dsfu99s121kn3jkk";
	private static final String IS_LOGIN 		= "3b90jKADN3902q3v2";
	private static final String FILTER_RADIUS_DISTANCE 		= "FILTER_RADIUS_DISTANCE";
	private static final String FILTER_RADIUS_DISTANCE_MAX 		= "FILTER_RADIUS_DISTANCE_MAX";


	private static final String email 		= "email";
	private static final String number 	= "number";
	private static final String country 		= "country";
	private static final String name 			= "name";
	private static final String sms 			= "sms";
	private static final String twitter 		= "twitter";
	private static final String fb 				= "fb";
	private static final String company 		= "company";
	private static final String apiKey			= "apikey";
	private static final String gender			= "gender";
	private static final String score			= "score";
	private static final String facebook_profile= "facebook_profile";
	private static final String insta_profile	= "insta_profile";
	private static final String collect		= "collect";
	private static final String redeem			= "redeem";
	private static final String dob				= "dob";
	private static final String password		= "pass";



	private static final String SHARED = "UserSession_Preferences";
	private static UserAccessSession instance;
	
	public static UserAccessSession getInstance(Context context) {
		if(instance == null)
			instance = new UserAccessSession(context);
		return instance;
	}

	public UserAccessSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		editor = sharedPref.edit();
	}

	public void storeUserSession(UserSession session) {
		editor.putString(FACEBOOK_ID, session.getFacebook_id());
		editor.putString(TWITTER_ID, session.getTwiter_id());
		editor.putInt(USER_ID, Integer.valueOf(session.getId()));
		editor.putString(FULL_NAME, session.getFull_name());
		editor.putString(USER_NAME, session.getName());
		editor.putString(THUMB_URL, session.getImage());
		editor.putString(EMAIL, session.getEmail());
		editor.putString(GOOGLE_ID, session.getGoogle_id());
		editor.putString(email, session.getEmail());
		editor.putString(number, session.getPhone());
		editor.putString(dob,session.getDob());
		editor.putString(password,session.getPassword());
		editor.putString(apiKey,session.getApikey());
		editor.putString(gender,session.getGender());
		editor.putString(score,session.getScore());
		editor.putString(facebook_profile,session.getFacebook_profile());
		editor.putString(insta_profile,session.getInsta_profile());
		editor.putString(collect,session.getCollect());
		editor.putString(redeem,session.getRedeem());


		editor.putBoolean(IS_LOGIN, true);
		editor.commit();
	}

	public void clearUserSession() {
		editor.putString(FACEBOOK_ID, null);
		editor.putString(TWITTER_ID, null);
		editor.putInt(USER_ID, -1);
		editor.putString(FULL_NAME, null);
		editor.putString(USER_NAME, null);
		editor.putString(THUMB_URL, null);
		editor.putString(EMAIL, null);
		editor.putString(GOOGLE_ID, null);
		editor.putString(email, null);
		editor.putString(number, null);
		editor.putString(dob,null);
		editor.putString(password,null);
		editor.putString(apiKey,null);
		editor.putString(gender,null);
		editor.putString(score,null);
		editor.putString(facebook_profile,null);
		editor.putString(insta_profile,null);
		editor.putString(collect,null);
		editor.putString(redeem,null);


		editor.commit();
	}

	public Boolean isLoggedIn() {
		if(sharedPref == null)
			return false;
		
		return sharedPref.getBoolean(IS_LOGIN, false);
	}

	public UserSession getUserSession() {
		int userId = sharedPref.getInt(USER_ID, -1);
		if(userId < 0)
			return null;
		
		UserSession session = new UserSession();
		session.setFacebook_id( sharedPref.getString(FACEBOOK_ID, null) );
		session.setTwiter_id( sharedPref.getString(TWITTER_ID, null) );
		session.setId( String.valueOf(sharedPref.getInt(USER_ID, -1) ));
		session.setFull_name( sharedPref.getString(FULL_NAME, null) );
		session.setName( String.valueOf(sharedPref.getString(USER_NAME, null)) );
		session.setImage( sharedPref.getString(THUMB_URL, null) );
		session.setEmail( sharedPref.getString(EMAIL, null) );
		session.setGoogle_id( sharedPref.getString(GOOGLE_ID, null) );
		session.setDob( sharedPref.getString(dob, null) );
		session.setPassword( sharedPref.getString(password, null) );
		session.setApikey( sharedPref.getString(apiKey, null) );
		session.setGender( sharedPref.getString(gender, null) );
		session.setScore( sharedPref.getString(score, null) );
		session.setInsta_profile( sharedPref.getString(insta_profile, null) );
		session.setCollect( sharedPref.getString(collect, null) );
		session.setPhone( sharedPref.getString(number,null));




		return session;
	}
	
	public Boolean isLoggedInFromSocial() {
		if(sharedPref == null)
			return false;
		
		String facebookId = sharedPref.getString(FACEBOOK_ID, null);
		String twitterId = sharedPref.getString(TWITTER_ID, null);
		boolean isSocial = ( (facebookId != null && facebookId.length() != 0) || 
								(twitterId != null && twitterId.length() != 0) ) ? true : false;
		
		return isSocial;
	}

	public void setFilterDistance(float radius) {
		editor.putFloat(FILTER_RADIUS_DISTANCE, radius);
		editor.commit();
	}

	public float getFilterDistance() {
		return  sharedPref.getFloat(FILTER_RADIUS_DISTANCE, 0);
	}

	public void setFilterDistanceMax(float radius) {
		editor.putFloat(FILTER_RADIUS_DISTANCE_MAX, radius);
		editor.commit();
	}

	public float getFilterDistanceMax() {
		return  sharedPref.getFloat(FILTER_RADIUS_DISTANCE_MAX, 0);
	}
}
