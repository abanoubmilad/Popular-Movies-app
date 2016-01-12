package com.example.abanoub.moviesdb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class TrailersAdapter extends ArrayAdapter<Trailer> {
    public TrailersAdapter(Activity context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_list_item, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.name)).setText(getItem(position).getName());

        return convertView;
    }
}
