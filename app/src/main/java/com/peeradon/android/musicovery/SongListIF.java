package com.peeradon.android.musicovery;
import org.osmdroid.util.GeoPoint;

public interface SongListIF {
    public void onSongSelected(String trackID, GeoPoint location, String country);
}