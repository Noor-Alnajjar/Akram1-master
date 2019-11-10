package com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.models.Gifts;
import com.models.Pokemon;
import com.models.Rating;
import com.models.Sighting;
import com.models.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mg on 19/07/16.
 */
public class Queries {

    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public Queries(SQLiteDatabase db, DbHelper dbHelper) {
        this.db = db;
        this.dbHelper = dbHelper;
    }

    public void deleteTable(String tableName) {
        db = dbHelper.getWritableDatabase();
        try{
            db.delete(tableName, null, null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        db.close();
    }


    public void insertSighting(Sighting entry) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("updated_at", entry.getUpdated_at());
        values.put("name", entry.getName());
        values.put("created_at", entry.getCreated_at());
        values.put("date_found", entry.getDate_found());
        values.put("distance", entry.getDistance());
        values.put("entity_id", entry.getEntity_id());
        values.put("entity_name", entry.getEntity_name());
        //todo add this if custom image
        //values.put("image", entry.getImage());
        values.put("lat", entry.getLat());
        values.put("lon", entry.getLon());
        values.put("pokemon_id", entry.getPokemon_id());
        values.put("sighting_id", entry.getSighting_id());
        values.put("type", entry.getType());
        values.put("updated_at", entry.getUpdated_at());
        values.put("user_id", entry.getUser_id());
        values.put("flag_count", entry.getFlag_count());

        db.insert("sightings", null, values);
    }

    public void insertGift(Gifts entry) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("update_at", entry.getUpdate_at());
        values.put("name", entry.getName());
        values.put("creat_at", entry.getCreat_at());
        values.put("date_found", entry.getDate_found());
        values.put("distance", entry.getDistance());
        values.put("lat", entry.getLat());
        values.put("lon", entry.getLon());
        values.put("id", entry.getId());
        values.put("gift_map_id", entry.getGift_map_id());
        values.put("marchent_id", entry.getMerchant_id());

        db.insert("gifts", null, values);
    }


    public void insertPokemon(Pokemon entry) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("updated_at", entry.getUpdated_at());
        values.put("type", entry.getType());
        values.put("pokemon_id", entry.getPokemon_id());
        values.put("created_at", entry.getCreated_at());
        values.put("entity_id", entry.getEntity_id());
        values.put("image", entry.getImage());
        values.put("is_deleted", entry.getIs_deleted());
        values.put("name", entry.getName());

        db.insert("pokemons", null, values);
    }

    public void insertRating(Rating entry) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("flag_dislike_count", entry.getFlag_dislike_count());
        values.put("flag_like_count", entry.getFlag_like_count());
        values.put("rating_id", entry.getRating_id());
        values.put("sighting_id", entry.getSighting_id());

        db.insert("ratings", null, values);
    }

    public void insertUser(User entry) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", entry.getName());
        values.put("gender", entry.getGender());
        values.put("image", entry.getImage());
        values.put("score", entry.getScore());
        values.put("role", entry.getRole());
        values.put("facebook_id", entry.getFacebook_id());
        values.put("apikey", entry.getApikey());
        values.put("facebook_profile", entry.getFacebook_profile());
        values.put("insta_profile", entry.getInsta_profile());
        values.put("phone", entry.getPhone());
        values.put("collect", entry.getCollect());
        values.put("redeem", entry.getRedeem());
        values.put("dob", entry.getDob());
        values.put("full_name", entry.getDob());
        values.put("email", entry.getDob());

        //values.put("number",entry.getNumber());

        db.insert("users", null, values);
    }

    public void deleteSighting(int sighting_id) {
        db = dbHelper.getWritableDatabase();
        db.delete("sightings", "sighting_id = " + String.valueOf(sighting_id), null);
    }
    public void deleteGift(int  gift_id) {
        db = dbHelper.getWritableDatabase();
        db.delete("gifts", "gift_map_id = " + String.valueOf(gift_id), null);
    }

    public void deletePokemon(int pokemon_id) {
        db = dbHelper.getWritableDatabase();
        db.delete("pokemons", "pokemon_id = " + String.valueOf(pokemon_id), null);
    }

    public void deleteRating(int rating_id) {
        db = dbHelper.getWritableDatabase();
        db.delete("ratings", "rating_id = " + String.valueOf(rating_id), null);
    }

    public void deleteUser(int user_id) {
        db = dbHelper.getWritableDatabase();
        db.delete("users", "user_id = " + String.valueOf(user_id), null);
    }

    public ArrayList<Sighting> getSightings() {

        ArrayList<Sighting> list = new ArrayList<Sighting>();
        db = dbHelper.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM sightings ORDER BY name ASC", null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Sighting entry = formatSighting(mCursor);
                list.add(entry);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }


    public ArrayList<Gifts> getGifts() {

        ArrayList<Gifts> list = new ArrayList<Gifts>();
        db = dbHelper.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM gifts ORDER BY name ASC", null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Gifts entry = formatGifts(mCursor);
                list.add(entry);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }
    public ArrayList<Pokemon> getPokemons() {

        ArrayList<Pokemon> list = new ArrayList<Pokemon>();
        db = dbHelper.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM pokemons ORDER BY name ASC", null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                Pokemon entry = formatPokemon(mCursor);
                list.add(entry);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }

    public Rating getRatingBySightingId(int sighting_id) {

        Rating entry = null;
        db = dbHelper.getReadableDatabase();
        String sql = String.format("SELECT * FROM ratings WHERE sighting_id = %s ", String.valueOf(sighting_id));
        Cursor mCursor = db.rawQuery(sql, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                entry = formatRating(mCursor);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return entry;
    }

    public Sighting getSightingBySightingId(int sighting_id) {

        Sighting entry = null;
        db = dbHelper.getReadableDatabase();
        String sql = String.format("SELECT * FROM sightings WHERE sighting_id = %s ", String.valueOf(sighting_id));
        Cursor mCursor = db.rawQuery(sql, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                entry = formatSighting(mCursor);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return entry;
    }

    public Pokemon getPokemonByPokemonId(int pokemon_id) {

        Pokemon entry = null;
        db = dbHelper.getReadableDatabase();
        String sql = String.format("SELECT * FROM pokemons WHERE pokemon_id = %s ", String.valueOf(pokemon_id));
        Cursor mCursor = db.rawQuery(sql, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                entry = formatPokemon(mCursor);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return entry;
    }

    public User getUserByUserId(int user_id) {

        User entry = null;
        db = dbHelper.getReadableDatabase();
        String sql = String.format("SELECT * FROM users WHERE user_id = %s ", String.valueOf(user_id));
        Cursor mCursor = db.rawQuery(sql, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                entry = formatUser(mCursor);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return entry;
    }

    public Pokemon formatPokemon(Cursor mCursor) {

        Pokemon entry = new Pokemon();
        entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
        entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
        entry.setEntity_id( mCursor.getInt( mCursor.getColumnIndex("entity_id")) );
        entry.setImage( mCursor.getString( mCursor.getColumnIndex("image")) );
        entry.setIs_deleted( mCursor.getInt( mCursor.getColumnIndex("is_deleted")) );
        entry.setName( mCursor.getString( mCursor.getColumnIndex("name")) );
        entry.setPokemon_id( mCursor.getInt( mCursor.getColumnIndex("pokemon_id")) );
        entry.setType( mCursor.getString( mCursor.getColumnIndex("type")) );

        return entry;
    }

    public Sighting formatSighting(Cursor mCursor) {

        Sighting entry = new Sighting();
        entry.setCreated_at( mCursor.getInt( mCursor.getColumnIndex("created_at")) );
        entry.setUpdated_at( mCursor.getInt( mCursor.getColumnIndex("updated_at")) );
        try{
            entry.setRules(new ArrayList<String>
                    (Arrays.asList("")));

        }catch (Exception ex){
            entry.setRules(new ArrayList<String>
                    (Arrays.asList(mCursor.getString(mCursor.getColumnIndex("rules")).split(","))));
        }

        entry.setType( mCursor.getString( mCursor.getColumnIndex("type")) );
        entry.setPokemon_id( mCursor.getInt( mCursor.getColumnIndex("pokemon_id")) );
        entry.setName( mCursor.getString( mCursor.getColumnIndex("name")) );
        entry.setImage( mCursor.getString( mCursor.getColumnIndex("image")) );
        entry.setDate_found( mCursor.getString( mCursor.getColumnIndex("date_found")) );
        entry.setDistance( mCursor.getFloat( mCursor.getColumnIndex("distance")) );
        entry.setEntity_id( mCursor.getInt( mCursor.getColumnIndex("entity_id")) );
        entry.setEntity_name( mCursor.getString( mCursor.getColumnIndex("entity_name")) );
        entry.setLat( mCursor.getDouble( mCursor.getColumnIndex("lat")) );
        entry.setLon( mCursor.getDouble( mCursor.getColumnIndex("lon")) );
        entry.setSighting_id( mCursor.getInt( mCursor.getColumnIndex("sighting_id")) );
        entry.setUser_id( mCursor.getInt( mCursor.getColumnIndex("user_id")) );
        entry.setFlag_count( mCursor.getInt( mCursor.getColumnIndex("flag_count")) );

        return entry;
    }

    public Gifts formatGifts(Cursor mCursor) {

        Gifts entry = new Gifts();
        entry.setCreat_at( String.valueOf(mCursor.getInt( mCursor.getColumnIndex("creat_at")) ));
        entry.setUpdate_at( String.valueOf(mCursor.getInt( mCursor.getColumnIndex("update_at"))) );
//        try{
//            entry.setRules(new ArrayList<String>
//                    (Arrays.asList("")));
//
//        }catch (Exception ex){
//            entry.setRules(new ArrayList<String>
//                    (Arrays.asList(mCursor.getString(mCursor.getColumnIndex("rules")).split(","))));
//        }

        entry.setId( String.valueOf(mCursor.getInt( mCursor.getColumnIndex("id"))) );
        entry.setName( mCursor.getString( mCursor.getColumnIndex("name")) );
        entry.setDate_found( mCursor.getString( mCursor.getColumnIndex("date_found")) );
        entry.setDistance( String.valueOf(mCursor.getFloat( mCursor.getColumnIndex("distance"))) );
        entry.setLat( String.valueOf(mCursor.getDouble( mCursor.getColumnIndex("lat")) ));
        entry.setLon( String.valueOf(mCursor.getDouble( mCursor.getColumnIndex("lon")) ));
        entry.setGift_map_id(String.valueOf( mCursor.getInt( mCursor.getColumnIndex("gift_map_id")) ));
        entry.setMerchant_id( String.valueOf(mCursor.getInt( mCursor.getColumnIndex("marchent_id")) ));

        return entry;
    }

    public Rating formatRating(Cursor mCursor) {

        Rating entry = new Rating();
        entry.setSighting_id( mCursor.getInt( mCursor.getColumnIndex("sighting_id")) );
        entry.setFlag_dislike_count( mCursor.getInt( mCursor.getColumnIndex("flag_dislike_count")) );
        entry.setFlag_like_count( mCursor.getInt( mCursor.getColumnIndex("flag_like_count")) );
        entry.setRating_id( mCursor.getInt( mCursor.getColumnIndex("rating_id")) );
        return entry;
    }

    public User formatUser(Cursor mCursor) {

        User entry = new User();
        entry.setId( mCursor.getInt( mCursor.getColumnIndex("id")) );
        entry.setName( mCursor.getString( mCursor.getColumnIndex("name")) );
        entry.setEmail( mCursor.getString( mCursor.getColumnIndex("email")) );
        entry.setFull_name( mCursor.getString( mCursor.getColumnIndex("full_name")) );
        entry.setGender( mCursor.getString( mCursor.getColumnIndex("gender")) );
        entry.setImage( mCursor.getString( mCursor.getColumnIndex("image")) );
        entry.setScore( mCursor.getString( mCursor.getColumnIndex("score")) );
        entry.setRole( mCursor.getString( mCursor.getColumnIndex("role")) );
        entry.setFacebook_id( mCursor.getString( mCursor.getColumnIndex("facebook_id")) );
        entry.setApikey( mCursor.getString( mCursor.getColumnIndex("apikey")) );
        entry.setFacebook_profile( mCursor.getString( mCursor.getColumnIndex("facebook_profile")) );
        entry.setInsta_profile( mCursor.getString( mCursor.getColumnIndex("insta_profile")) );
        entry.setPhone( mCursor.getString( mCursor.getColumnIndex("phone")) );
        entry.setCollect( mCursor.getString( mCursor.getColumnIndex("collect")) );
        entry.setRedeem( mCursor.getString( mCursor.getColumnIndex("redeem")) );
        entry.setDob( mCursor.getString( mCursor.getColumnIndex("dob")) );

        //entry.setUser_id( mCursor.getInt( mCursor.getColumnIndex("user_id")) );
        //entry.setNumber(mCursor.getString(mCursor.getColumnIndex("number")));

        return entry;
    }

    public void closeDatabase() {
        dbHelper.close();
    }
}
