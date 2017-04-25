package com.peeradon.android.musicovery;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.osmdroid.util.GeoPoint;

public class MainActivity extends AppCompatActivity implements SongListIF,SQLIF {
    // fragments for each page of the FragmentPagerAdapter
    private DiscoverFragment discoverFragment = new DiscoverFragment();
    private MapFragment mapFragment = new MapFragment();
    private InfoFragment infoFragment = new InfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create new instance of FragmentPagerAdapter, where we passes in the 3 fragments and the context as arguments
        MainPageAdapter mainPageAdapter = new MainPageAdapter(getSupportFragmentManager(), discoverFragment, mapFragment, infoFragment, getApplicationContext());
        // set adapter for the ViewPager
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewp);
        viewPager.setAdapter(mainPageAdapter);
        // setup TabLayout for the ViewPager
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        WebView mSoundCloudPlayer = (WebView) findViewById(R.id.SoundCloud);

        String VIDEO_URL = "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/318438629&amp;auto_play=true&amp;hide_related=true&amp;show_comments=false&amp;show_user=true&amp;show_reposts=false&amp;visual=true";

        String html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe id=\"sc-widget " +
                "\" width=\"100%\" height=\"50%\"" + // Set Appropriate Width and Height that you want for SoundCloud Player
                " src=\"" + VIDEO_URL   // Set Embedded url
                + "\" frameborder=\"no\" scrolling=\"no\"></iframe>" +
                "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script> </body> </html> ";

        mSoundCloudPlayer.setVisibility(View.VISIBLE);
        mSoundCloudPlayer.getSettings().setJavaScriptEnabled(true);
        mSoundCloudPlayer.getSettings().setLoadWithOverviewMode(true);
        mSoundCloudPlayer.getSettings().setUseWideViewPort(true);
        mSoundCloudPlayer.loadDataWithBaseURL("",html,"text/html", "UTF-8", "");

    }

    @Override
    public void onSongSelected(String trackID, GeoPoint location, String country) {
        // play the song

        // pass location information to MapFragment
        mapFragment.update(location);

        // pass country information to InfoFragment
        infoFragment.update(country);

//        vp.setCurrentItem(1, true);
    }

    @Override
    public void onSQLInsert(String text, int table) {
//        sf.update(text, table);
    }

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

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_about){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
