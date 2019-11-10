package com.db;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mg on 19/07/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    static final String TAG = "DbHelper";
    static final String DB_NAME = "db_AkramApp";
    static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS pokemons("
                + "pokemon_id INTEGER PRIMARY KEY,"
                + "entity_id INTEGER,"
                + "image TEXT,"
                + "is_deleted INTEGER,"
                + "name TEXT,"
                + "type TEXT,"
                + "created_at INTEGER, "
                + "updated_at INTEGER "
                + ");");


        db.execSQL("CREATE TABLE IF NOT EXISTS ratings("
                + "rating_id INTEGER PRIMARY KEY,"
                + "flag_dislike_count INTEGER, "
                + "flag_like_count INTEGER, "
                + "sighting_id INTEGER "
                + ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS sightings("
                + "sighting_id INTEGER PRIMARY KEY, "
                + "date_found TEXT, "
                + "distance TEXT, "
                + "entity_id INTEGER, "
                + "entity_name TEXT, "
                + "image TEXT, "
                + "lat TEXT, "
                + "lon TEXT, "
                + "name TEXT, "
                + "pokemon_id INTEGER, "
                + "type TEXT, "
                + "user_id INTEGER, "
                + "created_at INTEGER, "
                + "flag_count INTEGER, "
                + "updated_at INTEGER "
                + ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS gifts("
                + "gift_map_id INTEGER PRIMARY KEY, "
                + "date_found TEXT, "
                + "distance TEXT, "
                + "lat TEXT, "
                + "lon TEXT, "
                + "name TEXT, "
                + "id INTEGER, "
                + "marchent_id INTEGER, "
                + "creat_at INTEGER, "
                + "update_at INTEGER "
                + ");");


        db.execSQL("CREATE TABLE IF NOT EXISTS users("
                + "user_id INTEGER PRIMARY KEY,"
                + "deny_access INTEGER,"
                + "facebook_id TEXT,"
                + "full_name TEXT,"
                + "google_id TEXT,"
                + "login_hash TEXT,"
                + "team TEXT,"
                + "thumb_url TEXT,"
                + "twitter_id TEXT,"
                + "updated_at INTEGER, "
                + "created_at INTEGER "
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS sightings");
        db.execSQL("DROP TABLE IF EXISTS ratings");
        db.execSQL("DROP TABLE IF EXISTS pokemons");
        db.execSQL("DROP TABLE IF EXISTS gifts");
    }
}
