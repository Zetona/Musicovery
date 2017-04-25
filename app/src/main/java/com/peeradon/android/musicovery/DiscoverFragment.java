package com.peeradon.android.musicovery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.osmdroid.util.GeoPoint;

public class DiscoverFragment extends ListFragment {

    SongListIF mListener;

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // get access to database
        SongsOpenHelper dbHelper = new SongsOpenHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // query for songs in database
        Cursor songCursor = db.rawQuery("SELECT * FROM " + SongsOpenHelper.SONG_TABLE_NAME, null);

        // bind CursorAdaptor to ListAdapter
        SongCursorAdapter songAdapter = new SongCursorAdapter(this.getContext(), songCursor);
        setListAdapter(songAdapter);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (SongListIF) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SongListIF");
        }
    }

    public void onListItemClick(ListView l, View v, int pos, long id) {
        // database query to get the info of the song that is clicked
        // get access to database
        SongsOpenHelper dbHelper = new SongsOpenHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // query for the clicked song
        Cursor songCursor = db.rawQuery("SELECT * FROM " + SongsOpenHelper.SONG_TABLE_NAME + " WHERE _id = " + Long.toString(id) , null);
        songCursor.moveToFirst();
        Cursor countryCursor = db.rawQuery("SELECT * FROM " + SongsOpenHelper.COUNTRY_TABLE_NAME + " WHERE _id = " + songCursor.getInt(songCursor.getColumnIndex(SongsOpenHelper.KEY_COUNTRY_ID)) , null);
        countryCursor.moveToFirst();

        Log.v("Song Select:", songCursor.getString(songCursor.getColumnIndex(SongsOpenHelper.KEY_NAME)));
        Log.v("Country:", countryCursor.getString(countryCursor.getColumnIndex(SongsOpenHelper.KEY_NAME)));

        // get the stream URL of song to be played
        String trackID = songCursor.getString(songCursor.getColumnIndex(SongsOpenHelper.KEY_TRACK_ID));

        // get the location to be shown on MapFragment
        GeoPoint location = new GeoPoint(countryCursor.getFloat(countryCursor.getColumnIndex(SongsOpenHelper.KEY_LATITUDE)), countryCursor.getFloat(countryCursor.getColumnIndex(SongsOpenHelper.KEY_LONGITUDE)));

        // get the name of the country to be used on InfoFragment
        String country = countryCursor.getString(countryCursor.getColumnIndex(SongsOpenHelper.KEY_NAME));

        // pass the gathered info to MainActivity via the interface SongListIF
        mListener.onSongSelected(trackID, location, country);
    }

}