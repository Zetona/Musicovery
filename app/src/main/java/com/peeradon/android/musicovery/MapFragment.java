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
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    private MapView map;
    private IMapController mapController;
    private ItemizedOverlayWithFocus<OverlayItem> overlay;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // render map
        map = (MapView) v.findViewById(R.id.map);
        mapController = map.getController();
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        map.setTileSource(TileSourceFactory.OpenTopo    );

        // zoom button and gesture
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // set default zoom
        mapController.setZoom(5);

        return v;
    }

    public void update(GeoPoint location) {
        // set default zoom
        mapController.setZoom(5);

        // set location to current song's location
        mapController.setCenter(location);

        // marker overlay
        if (overlay != null){
            map.getOverlays().remove(overlay);
        }
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem(null, null, new GeoPoint(location)));
        overlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, getActivity().getApplicationContext());
        overlay.setFocusItemsOnTap(true);

        map.getOverlays().add(overlay);
    }
}
