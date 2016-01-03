package com.example.abanoub.moviesdb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GridAdapter gridadapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView grid = (GridView) rootView.findViewById(R.id.grid_view);
        gridadapter = new GridAdapter(getActivity(), new ArrayList<Movie>(0));
        grid.setAdapter(gridadapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("movie", gridadapter.getItem(position));
                startActivity(intent);
            }
        });

        Spinner spin = (Spinner) rootView.findViewById(R.id.spin);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    new FetchMoviesTask().execute("popularity.desc");
                else if (position == 1)
                    new FetchMoviesTask().execute("vote_average.desc");
                else
                    new FetchFavouriteMoviesTask().execute();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

// These are the names of the JSON objects that need to be extracted.
//            "page"
//            "results"
//            "adult"
//            "backdrop_path"
//            "genre_ids"
//            "id"
//            "original_language"
//            "original_title"
//            "overview"
//            "release_date"
//            "poster_path"
//            "popularity"
//            "title"
//            "video"
//            "vote_average"
//            "vote_count"

            JSONArray moviesArray = new JSONObject(moviesJsonStr).getJSONArray("results");
            ArrayList<Movie> movies = new ArrayList<>(moviesArray.length());
            JSONObject movie;
            for (int i = 0; i < moviesArray.length(); i++) {
                movie = moviesArray.getJSONObject(i);
                movies.add(new Movie(movie.getString("backdrop_path"),
                        movie.getString("id"), movie.getString("original_language"),
                        movie.getString("original_title"), movie.getString("overview"),
                        movie.getString("release_date"), movie.getString("poster_path"),
                        movie.getString("popularity"), movie.getString("title"),
                        movie.getString("vote_average"), movie.getString("vote_count"),
                        movie.getString("genre_ids"), movie.getString("adult").equals("true"),
                        movie.getString("video").equals("true")));
            }

            return movies;

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                URL url = new URL("https://api.themoviedb.org/3/discover/movie?api_key=" + com.example.abanoub.moviesdb.BuildConfig.MOVIE_DB_API_KEY + "&sort_by=" + params[0]);

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
                moviesJsonStr = buffer.toString();
                try {
                    return getMoviesDataFromJson(moviesJsonStr);
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
        protected void onPostExecute(ArrayList<Movie> movies) {
            if(movies!=null) {
                gridadapter.clear();
                gridadapter.addAll(movies);
            }
        }
    }

    public class FetchFavouriteMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            MoviesDBHelper mdb = MoviesDBHelper.getInstance(getActivity());
            return mdb.getMovies();

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if(movies!=null) {
                gridadapter.clear();
                gridadapter.addAll(movies);
            }
        }
    }
}
