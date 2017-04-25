package com.peeradon.android.musicovery;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongsOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "musicovery_db";
    private static final int DATABASE_VERSION = 4;

    public static final java.lang.String COUNTRY_TABLE_NAME = "countries";
    public static final java.lang.String COUNTRY_TABLE_CREATE = "CREATE TABLE "+COUNTRY_TABLE_NAME+"(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,name VARCHAR(20),flag VARCHAR(20) ,latitude FLOAT ,longitude FLOAT)";

    public static final java.lang.String SONG_TABLE_NAME = "songs";
    public static final java.lang.String SONG_TABLE_CREATE = "CREATE TABLE "+SONG_TABLE_NAME+"(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,country_id VARCHAR(20), track_id VARCHAR(20), name VARCHAR(20), FOREIGN KEY(country_id) REFERENCES country(_id))";

    public static final String KEY_NAME = "name";
    public static final String KEY_COUNTRY_ID = "country_id";
    public static final String KEY_TRACK_ID = "track_id";
    public static final String KEY_FLAG = "flag";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public SongsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        db.execSQL(COUNTRY_TABLE_CREATE);
        db.execSQL(SONG_TABLE_CREATE);

        values.put(KEY_NAME, "Finland");
        values.put(KEY_LATITUDE, 61.92411);
        values.put(KEY_LONGITUDE, 25.748151);
        db.insert(COUNTRY_TABLE_NAME, null, values);
        values.clear();

        values.put(KEY_NAME, "Thailand");
        values.put(KEY_LATITUDE, 15.870032);
        values.put(KEY_LONGITUDE, 100.992541);
        db.insert(COUNTRY_TABLE_NAME, null, values);
        values.clear();

        values.put(KEY_NAME, "Ievan Polkka");
        values.put(KEY_COUNTRY_ID, 1);
        db.insert(SONG_TABLE_NAME, null, values);
        values.clear();

        values.put(KEY_NAME, "Alavus");
        values.put(KEY_COUNTRY_ID, 2);
        db.insert(SONG_TABLE_NAME, null, values);
        values.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COUNTRY_TABLE_NAME);
        onCreate(db);
    }
}
