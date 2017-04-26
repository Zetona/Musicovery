package com.peeradon.android.musicovery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class StationCursorAdapter extends CursorAdapter {
    public StationCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_station, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views to populate in inflated template
        TextView listStationName = (TextView) view.findViewById(R.id.list_station_name);
        TextView listCountryName = (TextView) view.findViewById(R.id.list_country_name);
        ImageView listFlag = (ImageView) view.findViewById(R.id.list_flag);
        // Extract properties from cursor
        String stationName = cursor.getString(cursor.getColumnIndexOrThrow("station_name"));
        String countryName = cursor.getString(cursor.getColumnIndexOrThrow("country_name"));
        String countryCode = cursor.getString(cursor.getColumnIndexOrThrow("country_code"));
        // Populate views with extracted properties
        listStationName.setText(stationName);
        listCountryName.setText(countryName);
        listFlag.setImageResource(context.getResources().getIdentifier(countryCode.toLowerCase(), "drawable", context.getPackageName())); // get resourceID based on the country code
    }

}
