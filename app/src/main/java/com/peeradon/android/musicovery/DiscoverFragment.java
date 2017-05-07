package com.peeradon.android.musicovery;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.osmdroid.util.GeoPoint;

public class DiscoverFragment extends ListFragment {

    StationListIF mListener;
    private String countryCode;

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        // update ListView
        update(countryCode);
    }

    public void onAttach(Context context) {
        // check if activity implements StationListIF
        super.onAttach(context);
        try{
            mListener = (StationListIF) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StationListIF");
        }
    }

    public void onListItemClick(ListView l, View v, int pos, long id) {
        // database query to get the info of the song that is clicked
        // get access to database
        StationsOpenHelper dbHelper = new StationsOpenHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // query for the clicked song
        // SELECT * FROM <STATION_TABLE_NAME> INNER JOIN <COUNTRY_TABLE_NAME> ON <STATION_TABLE_NAME.KEY_COUNTRY_CODE> = <COUNTRY_TABLE_NAME.KEY_COUNTRY_CODE> WHERE <KEY_ID> = id
        Cursor stationCursor = db.rawQuery("SELECT * FROM " + StationsOpenHelper.STATION_TABLE_NAME +
                " INNER JOIN " + StationsOpenHelper.COUNTRY_TABLE_NAME + " ON " +
                StationsOpenHelper.STATION_TABLE_NAME + "."+ StationsOpenHelper.KEY_COUNTRY_CODE + " = " +
                StationsOpenHelper.COUNTRY_TABLE_NAME + "."+ StationsOpenHelper.KEY_COUNTRY_CODE +
                " WHERE " + StationsOpenHelper.KEY_ID + " = " + Long.toString(id), null);
        stationCursor.moveToFirst();

        // get the stream URL of song to be played
        String streamURL = stationCursor.getString(stationCursor.getColumnIndex(StationsOpenHelper.KEY_STREAM_URL));
        // get the location to be shown on MapFragment
        GeoPoint location = new GeoPoint(stationCursor.getFloat(stationCursor.getColumnIndex(StationsOpenHelper.KEY_LATITUDE)), stationCursor.getFloat(stationCursor.getColumnIndex(StationsOpenHelper.KEY_LONGITUDE)));
        // get the name of the country to be used on InfoFragment
        String country = stationCursor.getString(stationCursor.getColumnIndex(StationsOpenHelper.KEY_COUNTRY_NAME));
        // get the country code to be used by music control on MainActivity
        String countryCode = stationCursor.getString(stationCursor.getColumnIndex(StationsOpenHelper.KEY_COUNTRY_CODE));

        // pass the gathered info to MainActivity via the interface StationListIF
        mListener.onStationSelected(streamURL, location, country, countryCode);
    }

    public void update(String countryCode){
        this.countryCode = countryCode;

        // get access to database
        StationsOpenHelper dbHelper = new StationsOpenHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // query for stations in database
        Cursor stationCursor;

        if (countryCode == null) { // execute when location service is not available

            // SELECT * FROM <STATION_TABLE_NAME>
            // INNER JOIN <COUNTRY_TABLE_NAME> ON <STATION_TABLE_NAME.KEY_COUNTRY_CODE> = <COUNTRY_TABLE_NAME.KEY_COUNTRY_CODE>
            // ORDER BY <COUNTRY_TABLE_NAME.KEY_COUNTRY_NAME>, <KEY_STATION_NAME>
            stationCursor = db.rawQuery("SELECT * FROM " + StationsOpenHelper.STATION_TABLE_NAME +
                    " INNER JOIN " + StationsOpenHelper.COUNTRY_TABLE_NAME + " ON " +
                    StationsOpenHelper.STATION_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE + " = " +
                    StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE +
                    " ORDER BY " + StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_NAME +
                    ", " + StationsOpenHelper.KEY_STATION_NAME, null);
        }
        else { // execute when location is available

            // first cursor : select the country at the current location, to be shown as the first elements of ListView
            // SELECT * FROM <STATION_TABLE_NAME>
            // INNER JOIN <COUNTRY_TABLE_NAME> ON <STATION_TABLE_NAME.KEY_COUNTRY_CODE> = <COUNTRY_TABLE_NAME.KEY_COUNTRY_CODE>
            // WHERE <COUNTRY_TABLE_NAME.KEY_COUNTRY_CODE> = countryCode
            // ORDER BY <KEY_STATION_NAME>
            Cursor stationCurrentCountryCursor = db.rawQuery("SELECT * FROM " + StationsOpenHelper.STATION_TABLE_NAME +
                    " INNER JOIN " + StationsOpenHelper.COUNTRY_TABLE_NAME + " ON " +
                    StationsOpenHelper.STATION_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE + " = " +
                    StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE +
                    " WHERE " + StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE + " = '" + countryCode +
                    "' ORDER BY " + StationsOpenHelper.KEY_STATION_NAME, null);

            // second cursor : select the countries that is not the current location
            // SELECT * FROM <STATION_TABLE_NAME>
            // INNER JOIN <COUNTRY_TABLE_NAME> ON <STATION_TABLE_NAME.KEY_COUNTRY_CODE> = <COUNTRY_TABLE_NAME.KEY_COUNTRY_CODE>
            // WHERE <COUNTRY_TABLE_NAME.KEY_COUNTRY_CODE> != countryCode
            // ORDER BY <COUNTRY_TABLE_NAME.KEY_COUNTRY_NAME>, <KEY_STATION_NAME>
            Cursor stationNotCurrentCountryCursor = db.rawQuery("SELECT * FROM " + StationsOpenHelper.STATION_TABLE_NAME +
                    " INNER JOIN " + StationsOpenHelper.COUNTRY_TABLE_NAME + " ON " +
                    StationsOpenHelper.STATION_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE + " = " +
                    StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE +
                    " WHERE " + StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_CODE + " <> '" + countryCode +
                    "' ORDER BY " + StationsOpenHelper.COUNTRY_TABLE_NAME + "." + StationsOpenHelper.KEY_COUNTRY_NAME +
                    ", " + StationsOpenHelper.KEY_STATION_NAME, null);

            // merge the two cursors
            stationCursor = new MergeCursor(new Cursor[] {stationCurrentCountryCursor, stationNotCurrentCountryCursor});
        }

        // bind CursorAdaptor to ListAdapter
        StationCursorAdapter stationAdapter = new StationCursorAdapter(this.getContext(), stationCursor, countryCode);
        setListAdapter(stationAdapter);
    }

}