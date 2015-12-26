package com.example.abanoub.moviesdb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DetailsActivity extends ActionBarActivity {
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;
    private Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        movie = (Movie)getIntent().getSerializableExtra("movie");
        TextView adult =  (TextView) findViewById(R.id.adult);

        TextView favourite_btn =  (TextView) findViewById(R.id.favourite);
        favourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFavouriteMovieTask().execute();
            }
        });

        if(movie.isAdult())
            adult.setText("yes");
        else
            adult.setText("no");

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

        ListView trailersList = (ListView) findViewById(R.id.trailers_listView);
        trailersAdapter =new TrailersAdapter(this,new ArrayList<Trailer>(0));
        trailersList.setAdapter(trailersAdapter);
        trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = ((Trailer) parent.getItemAtPosition(position)).getKey();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key)));
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + key));
                    startActivity(intent);
                }
            }
        });
        trailersList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        ListView reviewsList = (ListView) findViewById(R.id.reviews_listView);
        reviewsAdapter = new ReviewsAdapter(this,new ArrayList<Review>(0));
        reviewsList.setAdapter(reviewsAdapter);
        reviewsList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        new FetchReviewsTask().execute(movie.getId());
        new FetchTrailersTask().execute(movie.getId());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {

        private ArrayList<Review> getReviewsDataFromJson(String reviewsJsonStr)
                throws JSONException {

// These are the names of the JSON objects that need to be extracted.
//            "url"
//            "id"
//            "content"
//            "author"

            JSONArray reviewsArray = new JSONObject(reviewsJsonStr).getJSONArray("results");
            ArrayList<Review> reviews = new ArrayList<>(reviewsArray.length());
            JSONObject review;
            for (int i = 0; i < reviewsArray.length(); i++) {
                review = reviewsArray.getJSONObject(i);
                reviews.add(new Review(review.getString("author"),
                        review.getString("content")));
            }

            return reviews;

        }
        @Override
        protected ArrayList<Review> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewsJsonStr = null;

            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+params[0]+"/reviews?api_key="+BuildConfig.THE_MOVIE_DB_API_KEY);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                reviewsJsonStr = buffer.toString();
                try {
                    return getReviewsDataFromJson(reviewsJsonStr);
                } catch (JSONException e) {
                    return null;
                }

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            reviewsAdapter.clear();
            reviewsAdapter.addAll(reviews);
        }
    }
    public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

        private ArrayList<Trailer> getTrailersDataFromJson(String trailersJsonStr)
                throws JSONException {

// These are the names of the JSON objects that need to be extracted.
//            "key"
//            "name"

            JSONArray trailersArray = new JSONObject(trailersJsonStr).getJSONArray("results");
            ArrayList<Trailer> trailers = new ArrayList<>(trailersArray.length());
            JSONObject review;
            for (int i = 0; i < trailersArray.length(); i++) {
                review = trailersArray.getJSONObject(i);
                trailers.add(new Trailer(review.getString("key"),
                        review.getString("name")));
            }

            return trailers;

        }
        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailersJsonStr = null;

            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+params[0]+"/videos?api_key="+BuildConfig.THE_MOVIE_DB_API_KEY);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                trailersJsonStr = buffer.toString();
                try {
                    return getTrailersDataFromJson(trailersJsonStr);
                } catch (JSONException e) {
                    return null;
                }

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            trailersAdapter.clear();
            trailersAdapter.addAll(trailers);
        }
    }
    public class AddFavouriteMovieTask extends AsyncTask<Void, Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            MoviesDBHelper mdb = MoviesDBHelper.getInstance(getApplicationContext());
           return mdb.addMovie(movie)!=-1;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            Toast.makeText(getApplicationContext(),R.string.msg_favourite_added,Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(),R.string.msg_favourite_fail,Toast.LENGTH_SHORT).show();

        }
    }
}
