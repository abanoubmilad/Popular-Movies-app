package com.example.abanoub.moviesdb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity implements Callback{

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, new MainActivityFragment())
                    .commit();
        }

        if (findViewById(R.id.main_fragment_large) != null)
            mTwoPane = true;
         else
            mTwoPane = false;

    }

    @Override
    public void onItemSelected(Movie movie) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putSerializable("movie", movie);

            MovieDetailsActivityFragment fragment = new MovieDetailsActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, fragment)
                    .commit();
        }else{
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
        }

    }
}
