package com.peeradon.android.musicovery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

class SongCursorAdapter extends CursorAdapter {
    public SongCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO: set country flag
        // Find fields to populate in inflated template
        TextView listName = (TextView) view.findViewById(R.id.list_name);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        // Populate fields with extracted properties
        listName.setText(name);
    }

}
