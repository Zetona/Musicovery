package com.peeradon.android.musicovery;
import org.osmdroid.util.GeoPoint;


/**
 * StationListIF.java
 * An Interface used for communication between fragments and MainActivity
 */

public interface StationListIF {
    public void onStationSelected(String streamURL, GeoPoint location, String country, String countryCode);
}