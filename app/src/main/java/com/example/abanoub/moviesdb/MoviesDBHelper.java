package com.example.abanoub.moviesdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MoviesDBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "favourite_movies_db";
    // These are the names of the objects that need to be stored
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
//            "vote_verage"
//            "vote_count"

    private static final String Tb_NAME = "favourite_movies_tb";
    public static final String ID = "_id", ADULT = "adult", BACKDROP_PATH = "backdrop_path",
            GENRE_IDS = "genre_ids", id = "id", ORIGINAL_LANGUAGE = "original_language",
            ORIGINAL_TITLE = "original_title", OVERVIEW = "overview", RELEASE_DATE = "release_date",
            POSTER_PATH = "poster_path", POPULARITY = "popularity", TITLE = "title", VIDEO = "video",
            VOTE_AVERAGE = "vote_average", VOTE_COUNT = "vote_count";

    private static MoviesDBHelper dbm;
    private SQLiteDatabase readableDB, writableDB;

    public static MoviesDBHelper getInstance(Context context) {
        if (dbm != null )
            return dbm;
        else {
            dbm = new MoviesDBHelper(context);
            return dbm;
        }
    }

    private MoviesDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        readableDB = getReadableDatabase();
        writableDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table " + Tb_NAME + " ( " + ID
                + " integer primary key autoincrement, " + ADULT
                + " text, " + BACKDROP_PATH + " text, " + ORIGINAL_LANGUAGE + " text, "+ORIGINAL_TITLE+" text, "
                + GENRE_IDS + " text, " + id + " text, " + OVERVIEW
                + " text, " + RELEASE_DATE + " text, " + POSTER_PATH + " text, "
                + POPULARITY + " text, " + TITLE + " text, " + VIDEO + " text, "
                + VOTE_AVERAGE + " text, " + VOTE_COUNT + " text) ";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

    public void removeMovie(int id) {
        writableDB.delete(Tb_NAME, ID + " = ?",
                new String[]{String.valueOf(id)});
    }


    public int addMovie(Movie movie) {
        ContentValues values = new ContentValues();

        values.put(ADULT, movie.isAdult());
        values.put(BACKDROP_PATH, movie.getBackdrop_path());
        values.put(GENRE_IDS, movie.getGenre_ids());
        values.put(id, movie.getId());
        values.put(ORIGINAL_LANGUAGE, movie.getOriginal_language());
        values.put(ORIGINAL_TITLE, movie.getOriginal_title());
        values.put(OVERVIEW, movie.getOverview());
        values.put(RELEASE_DATE, movie.getRelease_date());
        values.put(POSTER_PATH, movie.getPoster_path());
        values.put(POPULARITY, movie.getPopularity());
        values.put(TITLE, movie.getTitle());
        values.put(VIDEO, movie.isHasVideo());
        values.put(VOTE_AVERAGE, movie.getVote_average());
        values.put(VOTE_COUNT, movie.getVote_count());

        return (int) writableDB.insert(Tb_NAME, null, values);

    }

    public ArrayList<Movie> getMovies() {
        String selectQuery = "SELECT * FROM " + Tb_NAME;
        Cursor c = readableDB.rawQuery(selectQuery, null);
        ArrayList<Movie> result = new ArrayList<>(c.getCount());

        if (c.moveToFirst()) {
            int COL_ADULT = c.getColumnIndex(ADULT);
            int COL_BACKDROP_PATH = c.getColumnIndex(BACKDROP_PATH);
            int COL_GENRE_IDS = c.getColumnIndex(GENRE_IDS);
            int COL_id = c.getColumnIndex(id);
            int COL_ORIGINAL_LANGUAGE = c.getColumnIndex(ORIGINAL_LANGUAGE);
            int COL_ORIGINAL_TITLE = c.getColumnIndex(ORIGINAL_TITLE);
            int COL_OVERVIEW = c.getColumnIndex(OVERVIEW);
            int COL_RELEASE_DATE = c.getColumnIndex(RELEASE_DATE);
            int COL_POSTER_PATH = c.getColumnIndex(POSTER_PATH);
            int COL_POPULARITY = c.getColumnIndex(POPULARITY);
            int COL_TITLE = c.getColumnIndex(TITLE);
            int COL_VIDEO = c.getColumnIndex(VIDEO);
            int COL_VOTE_AVERAGE = c.getColumnIndex(VOTE_AVERAGE);
            int COL_VOTE_COUNT = c.getColumnIndex(VOTE_COUNT);

            do {
                result.add(new Movie(c.getString(COL_BACKDROP_PATH), c.getString(COL_id),
                        c.getString(COL_ORIGINAL_LANGUAGE), c.getString(COL_ORIGINAL_TITLE),
                        c.getString(COL_OVERVIEW), c.getString(COL_RELEASE_DATE),
                        c.getString(COL_POSTER_PATH), c.getString(COL_POPULARITY),
                        c.getString(COL_TITLE), c.getString(COL_VOTE_AVERAGE),
                        c.getString(COL_VOTE_COUNT), c.getString(COL_GENRE_IDS),
                        c.getString(COL_ADULT).equals("true"), c.getString(COL_VIDEO).equals("true")));

            } while (c.moveToNext());
        }
        c.close();

        return result;

    }

}
