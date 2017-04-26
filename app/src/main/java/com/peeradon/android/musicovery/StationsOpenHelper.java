package com.peeradon.android.musicovery;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StationsOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "musicovery_db";
    private static final int DATABASE_VERSION = 5;

    public static final String KEY_ID = "_id";
    public static final String KEY_STATION_NAME = "station_name";
    public static final String KEY_COUNTRY_NAME = "country_name";
    public static final String KEY_COUNTRY_CODE = "country_code";
    public static final String KEY_STREAM_URL = "stream_url";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public static final java.lang.String COUNTRY_TABLE_NAME = "countries";
    public static final java.lang.String COUNTRY_TABLE_CREATE = "CREATE TABLE "+ COUNTRY_TABLE_NAME + "(" +
            KEY_COUNTRY_CODE + " VARCHAR(4) PRIMARY KEY NOT NULL, " +
            KEY_COUNTRY_NAME + " VARCHAR(24), " +
            KEY_LATITUDE + " FLOAT, " +
            KEY_LONGITUDE + " FLOAT)";

    public static final java.lang.String STATION_TABLE_NAME = "stations";
    public static final java.lang.String STATION_TABLE_CREATE = "CREATE TABLE "+ STATION_TABLE_NAME + "("+
            KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
            KEY_COUNTRY_CODE + " VARCHAR(4), " +
            KEY_STREAM_URL + " VARCHAR(20), " +
            KEY_STATION_NAME + " VARCHAR(20), " +
            "FOREIGN KEY(" + KEY_COUNTRY_CODE + ") REFERENCES country(" + KEY_COUNTRY_CODE + "))";

    public StationsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        db.execSQL(COUNTRY_TABLE_CREATE);
        db.execSQL(STATION_TABLE_CREATE);

        values.put(KEY_COUNTRY_NAME, "Finland");
        values.put(KEY_COUNTRY_CODE, "FI");
        values.put(KEY_LATITUDE, 61.92411);
        values.put(KEY_LONGITUDE, 25.748151);
        db.insert(COUNTRY_TABLE_NAME, null, values);
        values.clear();

        values.put(KEY_COUNTRY_NAME, "Thailand");
        values.put(KEY_COUNTRY_CODE, "TH");
        values.put(KEY_LATITUDE, 15.870032);
        values.put(KEY_LONGITUDE, 100.992541);
        db.insert(COUNTRY_TABLE_NAME, null, values);
        values.clear();

        values.put(KEY_STATION_NAME, "Ievan Polkka");
        values.put(KEY_COUNTRY_CODE, "FI");
        db.insert(STATION_TABLE_NAME, null, values);
        values.clear();

        values.put(KEY_STATION_NAME, "สวัสดีชาวโลก");
        values.put(KEY_COUNTRY_CODE,"TH");
        db.insert(STATION_TABLE_NAME, null, values);
        values.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COUNTRY_TABLE_NAME);
        onCreate(db);
    }
}
