package com.example.abanoub.moviesdb;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
           Bundle arguments = new Bundle();
          arguments.putSerializable("movie", getIntent().getSerializableExtra("movie"));

            MovieDetailsActivityFragment fragment = new MovieDetailsActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, fragment)
                    .commit();
       }
    }

}
