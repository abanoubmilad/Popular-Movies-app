package com.example.abanoub.moviesdb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private Movie [] movies;
    private GridAdapter gridadapter;
    public class FetchMoviesTask extends AsyncTask<String, Void, List <String>> {

        private List <String> getMoviesDataFromJson(String moviesJsonStr)
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

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray("results");


            movies = new Movie [moviesArray.length()];
            List <String> movieUrls = new ArrayList<>(moviesArray.length());
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                movies[i]=new Movie(movie.getString("backdrop_path"),
                        movie.getString("id"), movie.getString("original_language"),
                        movie.getString("original_title"),movie.getString("overview"),
                        movie.getString("release_date"), movie.getString("poster_path"),
                        movie.getString("popularity"), movie.getString("title"),
                        movie.getString("vote_average"), movie.getString("vote_count"),
                        movie.getString("genre_ids"),movie.getString("adult").equals("true"),
                        movie.getString("video").equals("true"));

                    movieUrls.add(movie.getString("poster_path"));
                }



            return movieUrls;

        }
        @Override
        protected List <String> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                URL url = new URL("https://api.themoviedb.org/3/discover/movie?api_key=a33a652812714b13f3a679e7a990d1ea&sort_by="+params[0]);

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
        protected void onPostExecute(List <String> result) {
            gridadapter.clear();
            gridadapter.addAll(result);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView grid =(GridView) findViewById(R.id.grid_view);
        gridadapter=new GridAdapter(this,new ArrayList<String>(0));
        grid.setAdapter(gridadapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext() ,DetailsActivity.class);
                intent.putExtra("movie",movies[position]);
                startActivity(intent);
            }
        });

        Spinner spin = (Spinner) findViewById(R.id.spin);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    new FetchMoviesTask().execute("popularity.desc");
                else
                    new FetchMoviesTask().execute("vote_average.desc");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
