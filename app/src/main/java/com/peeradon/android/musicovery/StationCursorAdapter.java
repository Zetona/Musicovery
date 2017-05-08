package com.peeradon.android.musicovery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * StationCursorAdaptor.java
 * A CursorAdaptor that populates the layout item_station.xml with the data from the cursor
 */

class StationCursorAdapter extends CursorAdapter {
    private String countryCode;

    public StationCursorAdapter(Context context, Cursor cursor, String countryCode) {
        super(context, cursor, 0);
        this.countryCode = countryCode;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_station, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find views to populate in inflated template
        TextView listStationName = (TextView) view.findViewById(R.id.list_station_name);
        TextView listCountryName = (TextView) view.findViewById(R.id.list_country_name);
        ImageView listFlag = (ImageView) view.findViewById(R.id.list_flag);
        // extract properties from cursor
        String stationName = cursor.getString(cursor.getColumnIndexOrThrow("station_name"));
        String countryName = cursor.getString(cursor.getColumnIndexOrThrow("country_name"));
        String countryCode = cursor.getString(cursor.getColumnIndexOrThrow("country_code"));
        // populate views with extracted properties
        listStationName.setText(stationName);
        listCountryName.setText(countryName);
        listFlag.setImageResource(context.getResources().getIdentifier(countryCode.toLowerCase(), "drawable", context.getPackageName())); // get resourceID based on the country code
        // change background color of the list element if station's country = current location
        if (this.countryCode != null){
            if (this.countryCode.equals(countryCode)){
                view.setBackgroundColor(context.getResources().getColor(R.color.tan_background));
            }
            else{
                view.setBackgroundColor(Color.WHITE);
            }
        }
    }

}
