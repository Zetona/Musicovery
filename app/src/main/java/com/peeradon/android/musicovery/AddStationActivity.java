package com.peeradon.android.musicovery;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * AddStationActivity.java
 * An Activity that is shown when user clicks the "plus" icon on MainActivity's toolbar
 *
 * This activity shows a form to user willing to add a new radio station to the database
 * user is asked to give the name of the station, the streaming URL, and the country
 *
 * The Activity performs validation on the form fields to make sure that
 * the fields are not empty and the country is valid
 */

public class AddStationActivity extends Activity implements View.OnClickListener {

    // broadcast
    public static final String Broadcast_REFRESH_VIEW = "com.peeradon.android.musicovery.activity.RefreshView";

    // string ArrayList for mapping country name and country code
    private ArrayList<String> countries_name;
    private ArrayList<String> countries_code;

    // views
    private AutoCompleteTextView countryName;
    private EditText stationName;
    private EditText streamURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_station);

        // initialize string ArrayList from string-array resource
        String[] countries = getResources().getStringArray(R.array.db_country);
        countries_name = new ArrayList<String>();
        countries_code = new ArrayList<String>();
        for (int i = 0; i < countries.length; i++) {
            String[] country_attrs = countries[i].split(",");
            countries_name.add(country_attrs[3]);
            countries_code.add(country_attrs[0]);
        }

        // setup AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, countries_name);
        countryName = (AutoCompleteTextView)
                findViewById(R.id.textView_country_list);
        countryName.setAdapter(adapter);

        // views
        stationName = (EditText) findViewById(R.id.editText_station_name);
        streamURL = (EditText) findViewById(R.id.editText_stream_URL);

        // button
        Button button = (Button) findViewById(R.id.button_add_station);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (validateForm()) {
            // check if country is valid
            for (int i = 0; i < countries_name.size(); i++) {
                if (countries_name.get(i).toLowerCase().equals(countryName.getText().toString().toLowerCase())) {
                    // if country exist, call addStation
                    addStation(stationName.getText().toString(), streamURL.getText().toString(), countries_code.get(i));
                    // notify MainActivity via broadcast to refresh ListView
                    Intent broadcastIntent = new Intent(Broadcast_REFRESH_VIEW);
                    broadcastIntent.putExtra("action", "refresh");
                    sendBroadcast(broadcastIntent);
                    finish();
                }
            }
            // if country does not exist, or empty
            countryName.setError("Invalid country");
        }
    }

    private boolean validateForm(){
        boolean validationResult = true;
        if (TextUtils.isEmpty(stationName.getText())){
            stationName.setError("Cannot be empty");
            validationResult = false;
        }
        if (TextUtils.isEmpty(streamURL.getText())){
            streamURL.setError("Cannot be empty");
            validationResult = false;
        }
        if (TextUtils.isEmpty(countryName.getText())){
            countryName.setError("Cannot be empty");
            validationResult = false;
        }
        return validationResult;
    }

    private void addStation(String stationName, String streamURL, String countryCode){
        // get access to database
        StationsOpenHelper dbHelper = new StationsOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // insert user input to database
        // INSERT INTO <STATION_TABLE_NAME> (KEY_COUNTRY_NAME, KEY_STREAM_URL, KEY_COUNTRY_CODE)
        // VALUES (stationName, streamURL, countryCode);
        db.execSQL("INSERT INTO " + StationsOpenHelper.STATION_TABLE_NAME + " (" +
                StationsOpenHelper.KEY_STATION_NAME + ", " + StationsOpenHelper.KEY_STREAM_URL + ", " +
                StationsOpenHelper.KEY_COUNTRY_CODE + ") VALUES ('" +
                stationName + "', '" + streamURL + "', '" + countryCode + "');");
    }
}
