package com.peeradon.android.musicovery;
import org.osmdroid.util.GeoPoint;

public interface StationListIF {
    public void onStationSelected(String streamURL, GeoPoint location, String country, String countryCode);
}