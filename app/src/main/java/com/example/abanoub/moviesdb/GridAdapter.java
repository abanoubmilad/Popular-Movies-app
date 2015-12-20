package com.example.abanoub.moviesdb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class GridAdapter extends ArrayAdapter<Movie> {
    public GridAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.image, parent, false);
        }
        Picasso
                .with(getContext())
                .load("http://image.tmdb.org/t/p/w185" + getItem(position).getPoster_path())
                .error(R.mipmap.ic_launcher)
                .into((ImageView) convertView);
        return convertView;
    }
}
