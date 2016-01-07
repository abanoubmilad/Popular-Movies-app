package com.example.abanoub.moviesdb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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


public class MovieDetailsActivityFragment extends Fragment {
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    private TextView adult, original_language,
            original_title, overview,
            release_date, popularity,
            vote_average, vote_count, title, video;
    private ImageView poster;

    private Movie movie;
    public MovieDetailsActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = (Movie) arguments.getSerializable("movie");
        }

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);


        original_language = (TextView) rootView.findViewById(R.id.original_language);
        original_title = (TextView) rootView.findViewById(R.id.original_title);
        overview = (TextView) rootView.findViewById(R.id.overview);
        release_date = (TextView) rootView.findViewById(R.id.release_date);
        popularity = (TextView) rootView.findViewById(R.id.popularity);
        vote_average = (TextView) rootView.findViewById(R.id.vote_average);
        vote_count = (TextView) rootView.findViewById(R.id.vote_count);
        title = (TextView) rootView.findViewById(R.id.title);
        adult = (TextView) rootView.findViewById(R.id.adult);
        poster = (ImageView) rootView.findViewById(R.id.poster);
        video = (TextView) rootView.findViewById(R.id.video);


        rootView.findViewById(R.id.favourite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFavouriteMovieTask().execute();
            }
        });

        ListView trailersList = (ListView) rootView.findViewById(R.id.trailers_listView);
        trailersAdapter = new TrailersAdapter(getActivity(), new ArrayList<Trailer>(0));
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
        ListView reviewsList = (ListView) rootView.findViewById(R.id.reviews_listView);
        reviewsAdapter = new ReviewsAdapter(getActivity(), new ArrayList<Review>(0));
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

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        original_language.setText(movie.getOriginal_language());
        original_title.setText(movie.getOriginal_title());
        release_date.setText(movie.getRelease_date());
        vote_count.setText(movie.getVote_count());
        vote_average.setText(movie.getVote_average());
        overview.setText(movie.getOverview());
        popularity.setText(movie.getPopularity());
        title.setText(movie.getTitle());

        if (movie.isAdult())
            adult.setText("yes");
        else
            adult.setText("no");

        if (movie.isHasVideo())
            video.setText("yes");
        else
            video.setText("no");

        Picasso
                .with(getActivity())
                .load("http://image.tmdb.org/t/p/w185" + movie.getPoster_path())
                .into(poster);

        new FetchReviewsTask().execute(movie.getId());
        new FetchTrailersTask().execute(movie.getId());

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
                URL url = new URL("https://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=" + BuildConfig.MOVIE_DB_API_KEY);

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
                        return null;
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            if (reviews != null) {
                reviewsAdapter.clear();
                reviewsAdapter.addAll(reviews);
            }
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
                URL url = new URL("https://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=" + BuildConfig.MOVIE_DB_API_KEY);

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
                        return null;
                    }
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            if (trailers != null) {
                trailersAdapter.clear();
                trailersAdapter.addAll(trailers);
            }
        }
    }

    public class AddFavouriteMovieTask extends AsyncTask<Movie, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Movie... params) {
            MoviesDBHelper mdb = MoviesDBHelper.getInstance(getActivity());
            return mdb.addMovie(movie) != -1;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Toast.makeText(getActivity(), R.string.msg_favourite_added, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), R.string.msg_favourite_fail, Toast.LENGTH_SHORT).show();

        }

    }

}
