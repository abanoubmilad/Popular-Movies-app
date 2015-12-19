package com.example.abanoub.moviesdb;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Movie movie = (Movie)getIntent().getSerializableExtra("movie");
        TextView adult =  (TextView) findViewById(R.id.adult);
        if(movie.isAdult())
            adult.setText("yes");
        else
            adult.setText("no");

        //TextView genre_ids =  (TextView) findViewById(R.id.);
        TextView original_language =  (TextView) findViewById(R.id.original_language);
        original_language.setText(movie.getOriginal_language());

        TextView original_title = (TextView) findViewById(R.id.original_title);
        original_title.setText(movie.getOriginal_title());

        TextView overview = (TextView) findViewById(R.id.overview);
        overview.setText(movie.getOverview());

        TextView release_date = (TextView) findViewById(R.id.release_date);
        release_date.setText(movie.getRelease_date());

        ImageView poster = (ImageView) findViewById(R.id.poster);

        Picasso
                .with(getApplicationContext())
                .load("http://image.tmdb.org/t/p/w185"+movie.getPoster_path())
                .into(poster);

        TextView popularity = (TextView) findViewById(R.id.popularity);
        popularity.setText(movie.getPopularity());

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(movie.getTitle());

        TextView video = (TextView) findViewById(R.id.video);
        if(movie.isHasVideo())
            video.setText("yes");
        else
            video.setText("no");
        TextView vote_average = (TextView) findViewById(R.id.vote_average);
        vote_average.setText(movie.getVote_average());

        TextView vote_count = (TextView) findViewById(R.id.vote_count);
        vote_count.setText(movie.getVote_count());




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
