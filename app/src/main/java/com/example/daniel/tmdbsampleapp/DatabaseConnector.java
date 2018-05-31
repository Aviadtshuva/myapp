package com.example.daniel.tmdbsampleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by daniel on 12/28/2017.
 */

public class DatabaseConnector {

    private static final String DATABASE_NAME = "MoviesDB";   // database name

    private DatabaseOpenHelper databaseOpenHelper; // database helper
    private SQLiteDatabase database; // database object

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context) {

        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    // open the database connection
    public void open() throws SQLException {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    }

    // close the database connection
    public void close() {
        if (database != null)
            database.close(); // close the database connection
    }

    // inserts new movie to database
    public void insertMovie(String title, String rating, String releaseDate, String description, String imageUrl) {

        ContentValues newMovie = new ContentValues();
        newMovie.put("title", title);
        newMovie.put("rating", rating);
        newMovie.put("releaseDate", releaseDate);
        newMovie.put("description", description);
        newMovie.put("imageUrl", imageUrl);

        open();
        database.insert("movies", null, newMovie);
        close();
    }

    // updates exiting movie in database
    public void updateMovie(long id, String title, String rating, String releaseDate, String description, String imageUrl) {

        ContentValues editMovie = new ContentValues();
        editMovie.put("title", title);
        editMovie.put("rating", rating);
        editMovie.put("releaseDate", releaseDate);
        editMovie.put("description", description);
        editMovie.put("imageUrl", imageUrl);

        open(); // open the database

        database.update(
                "movies",
                editMovie,
                "_id=" + id,// is the where condition
                null);// null is the where parameters replaced by ?

        close();  // close the database
    }

    // return a Cursor with all movies information from the database
    public Cursor getAllMovies() {
        return database.query(
                "movies",
                new String[]{"_id", "title", "rating", "releaseDate", "description", "imageUrl"},
                null,
                null,
                null,
                null,
                "title COLLATE NOCASE"
        );
    }

    // return a Cursor with a specific movie from the database
    public Cursor getOneMovie(long id) {
        return database.rawQuery("SELECT * FROM movies WHERE _id=" + id, null);
    }

    // Delete movies table
    public void deleteMoviesTable(long id) {

        open();
        database.delete("movies",
                "_id=" + id,//where
                null);//where argument for where placeholder's
        close();
    }

    public class DatabaseOpenHelper extends SQLiteOpenHelper {

        // public constructor
        private DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // creates the movies table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db) {

            // query to create a new table named movies
            String createQuery =
                    "CREATE TABLE movies" +
                            "(_id integer primary key autoincrement," +
                            "title TEXT," +
                            "rating TEXT," +
                            "releaseDate TEXT," +
                            "description TEXT," +
                            "imageUrl TEXT);";
            db.execSQL(createQuery); // execute the query for movies
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
