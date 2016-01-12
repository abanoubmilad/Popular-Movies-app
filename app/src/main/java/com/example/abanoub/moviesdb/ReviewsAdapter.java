package com.example.abanoub.moviesdb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ReviewsAdapter extends ArrayAdapter<Review> {
    public ReviewsAdapter(Activity context, List<Review> reviews) {
        super(context, 0, reviews);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_list_item, parent, false);
        }
        Review temp = getItem(position);
        ((TextView)convertView.findViewById(R.id.author)).setText(temp.getAuthor());
        ((TextView)convertView.findViewById(R.id.content)).setText(temp.getContent());

        return convertView;
    }
}
