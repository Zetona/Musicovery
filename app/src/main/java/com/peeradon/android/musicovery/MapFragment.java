package com.peeradon.android.musicovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MapFragment extends Fragment {
    private IMapController mapController;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // render map
        MapView map = (MapView) v.findViewById(R.id.map);
        mapController = map.getController();
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // TODO: remove zoom button
        // zoom button and gesture
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // set default zoom
        mapController.setZoom(5);

        // TODO: set location to real current location
        mapController.setCenter(new GeoPoint(48.8583, 2.2944));

        return v;
    }

    public void update(GeoPoint location) {
        // set default zoom
        mapController.setZoom(5);

        // set location to current song's location
        mapController.setCenter(location);

        // TODO: add marker
    }
}
