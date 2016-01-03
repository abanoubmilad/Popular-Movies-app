package com.example.abanoub.moviesdb;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment, new MovieDetailsActivityFragment())
//                    .commit();
//        }
    }

}
