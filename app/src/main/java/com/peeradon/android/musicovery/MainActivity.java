package com.peeradon.android.musicovery;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity.java
 * The MainActivity
 *
 * This class manage communications between fragments (via interface and public methods)
 * and the communication to MediaPlayerService service (via broadcast)
 */

public class MainActivity extends AppCompatActivity implements StationListIF, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // fragments for each page of the FragmentPagerAdapter
    private DiscoverFragment discoverFragment = new DiscoverFragment();
    private MapFragment mapFragment = new MapFragment();
    private InfoFragment infoFragment = new InfoFragment();

    // GoogleAPIClient (for location)
    GoogleApiClient googleApiClient;
    Location lastLocation;

    // variables for Media Player
    private MediaPlayerService player;
    boolean serviceBound = false;
    private PlaybackStatus playbackStatus;

    // Views of media control
    private TextView controlCountryName;
    private ImageView controlFlag;
    private ImageView controlPlayPause;

    // bind MediaPlayer Service
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.peeradon.android.musicovery.audioplayer.PlayNewAudio";
    public static final String Broadcast_STREAM_ACTION = "com.peeradon.android.musicovery.audioplayer.StreamAction";
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // service bounded, cast IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    /**
     * Activity Lifecycle Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // register broadcastReceiver
        register_refreshView();

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup views for media control
        controlCountryName = (TextView) findViewById(R.id.control_country_name);
        controlFlag  = (ImageView) findViewById(R.id.control_flag);
        controlPlayPause = (ImageView) findViewById(R.id.control_play_pause);
        controlPlayPause.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (playbackStatus == PlaybackStatus.PLAYING){
                    pauseAudio();
                }
                else if (playbackStatus == PlaybackStatus.PAUSED){
                    resumeAudio();
                }
            }
        });
        playbackStatus = PlaybackStatus.NULL;

        // create new instance of FragmentPagerAdapter
        MainPageAdapter mainPageAdapter = new MainPageAdapter(getSupportFragmentManager(), discoverFragment, mapFragment, infoFragment, getApplicationContext());
        // set adapter for the ViewPager
        MainViewPager viewPager = (MainViewPager) findViewById(R.id.viewp);
        viewPager.setAdapter(mainPageAdapter);
        // setup TabLayout for the ViewPager
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    protected void onStart() {
        // connect GoogleAPIClient
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        // disconnect GoogleAPIClient
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // save serviceBound state
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // retrieve serviceBound state
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            //service is active; unbind service
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    /**
     * GoogleAPIClient Methods
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (lastLocation == null) { // is executed only once
            // get location
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            if (lastLocation != null) {
                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

                try {
                    // fetch address from location via GeoCoder
                    List<Address> addresses = geoCoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                    Toast.makeText(this, "You are in " + addresses.get(0).getCountryName(), Toast.LENGTH_SHORT).show();

                    // pass location information to MapFragment
                    mapFragment.update(new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude()));
                    // pass country information to InfoFragment
                    infoFragment.update(addresses.get(0).getCountryName());
                    // pass countryCode information to DiscoverFragment
                    discoverFragment.update(addresses.get(0).getCountryCode());

                }
                catch (IOException e) {
                    Log.e("MUSICOVERY", "GeoCoder: ", e);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause){
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection to Google API failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * StationListIF Interface Method
     */
    @Override
    public void onStationSelected(String streamURL, GeoPoint location, String country, String countryCode) {
        // update media control
        controlFlag.setImageResource(getResources().getIdentifier(countryCode.toLowerCase(), "drawable", getPackageName()));
        controlCountryName.setText(country);

        // play the stream
        playAudio(streamURL);

        // pass location information to MapFragment
        mapFragment.update(location);

        // pass country information to InfoFragment
        infoFragment.update(country);
    }

    /**
     * Toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu item.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks.
        int id = item.getItemId();

        // add station menu
        if(id == R.id.action_add){
            Intent intent = new Intent(this, AddStationActivity.class);
            startActivity(intent);
            return true;
        }

        // reset data menu
        else if (id == R.id.action_reset_data) {

            // ask user for confirmation
            new AlertDialog.Builder(this)
                    .setTitle("Reset Data")
                    .setMessage("Do you really want to reset data?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            // reset database
                            StationsOpenHelper dbHelper = new StationsOpenHelper(getApplicationContext());
                            dbHelper.resetDatabase(dbHelper.getWritableDatabase());
                            // update ListView
                            if (lastLocation != null) { // location is available
                                try {
                                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                                    List<Address> addresses = geoCoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                                    discoverFragment.update(addresses.get(0).getCountryCode());
                                    Toast.makeText(getApplicationContext(), "Data reset successful", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Log.e("MUSICOVERY", "GeoCoder: ", e);
                                }
                            }
                            else{ // location is not available
                                discoverFragment.update(null);
                                Toast.makeText(getApplicationContext(), "Data reset successful", Toast.LENGTH_SHORT).show();
                            }
                        }})
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }

        // about menu
        else if(id == R.id.action_about){
            // ask user for confirmation
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage(Html.fromHtml(getResources().getString(R.string.about)))
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Stream controls
     */
    private void playAudio(String media) {
        // set playbackStatus
        playbackStatus = PlaybackStatus.PLAYING;
        controlPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
        // check if service is active
        if (!serviceBound) {
            // service is not active
            // start MediaPlayerService
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            // service is active
            // send media (streamURL) to MediaPlayerService via broadcast
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            broadcastIntent.putExtra("media", media);
            sendBroadcast(broadcastIntent);
        }
    }

    private void pauseAudio() {
        // change playback state to paused
        playbackStatus = PlaybackStatus.PAUSED;
        controlPlayPause.setImageResource(R.drawable.ic_play_circle_outline);
        // send action to MediaPlayerService via broadcast
        Intent broadcastIntent = new Intent(Broadcast_STREAM_ACTION);
        broadcastIntent.putExtra("action", "pause");
        sendBroadcast(broadcastIntent);
    }

    private void resumeAudio() {
        // change playback state to playing
        playbackStatus = PlaybackStatus.PLAYING;
        controlPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
        // send action to MediaPlayerService via broadcast
        Intent broadcastIntent = new Intent(Broadcast_STREAM_ACTION);
        broadcastIntent.putExtra("action", "resume");
        sendBroadcast(broadcastIntent);
    }


    /**
     * Broadcast receiver
     */
    private BroadcastReceiver refreshView = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update ListView
            if (lastLocation != null) { // location is available
                try {
                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                    List<Address> addresses = geoCoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                    discoverFragment.update(addresses.get(0).getCountryCode());
                    Toast.makeText(getApplicationContext(), "Station successfully added", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.e("MUSICOVERY", "GeoCoder: ", e);
                }
            }
            else{ // location is not available
                discoverFragment.update(null);
                Toast.makeText(getApplicationContext(), "Station successfully added", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void register_refreshView() {
        // register refreshView receiver
        IntentFilter filter = new IntentFilter(AddStationActivity.Broadcast_REFRESH_VIEW);
        registerReceiver(refreshView, filter);
    }
}
