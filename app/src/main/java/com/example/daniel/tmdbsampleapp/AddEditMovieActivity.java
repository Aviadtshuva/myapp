package com.example.daniel.tmdbsampleapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class AddEditMovieActivity extends AppCompatActivity {

    long rowID; // id of movie being edited, if any
    Long idValue;

    EditText etMovieTitle;
    EditText etMovieRating;
    EditText etMovieReleaseDate;
    EditText etMovieDescription;
    EditText etMovieImageUrl;

    ImageView ivMovieImage;
    Button btnShow;
    Button btnSave;

    String title;
    String rating;
    String releaseDate;
    String description;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_movie_relative_layout);

        try {
            //Retrieve the value from MainActivity (my movie list)
            Intent intent = getIntent();
            idValue = intent.getLongExtra("movieRowId", 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Log.d("myTag", idValue.toString());

        // Find view by id
        etMovieTitle = findViewById(R.id.et_movie_title);
        etMovieRating = findViewById(R.id.et_movie_rating);
        etMovieReleaseDate = findViewById(R.id.et_movie_release_date);
        etMovieDescription = findViewById(R.id.et_movie_description);
        etMovieImageUrl = findViewById(R.id.et_movie_image_url);
        ivMovieImage = findViewById(R.id.iv_movie_image);
        btnShow = findViewById(R.id.btn_show);
        btnSave = findViewById(R.id.btn_save);

        // get Intents
        Intent intent = getIntent();
        title = intent.getStringExtra("movie_title");
        rating = intent.getStringExtra("movie_rating");
        releaseDate = intent.getStringExtra("movie_releaseDate");
        description = intent.getStringExtra("movie_description");
        imageUrl = intent.getStringExtra("movie_image_url");

        // Set Fields
        etMovieTitle.setText(title);
        etMovieRating.setText(rating);
        etMovieReleaseDate.setText(releaseDate);
        etMovieDescription.setText(description);
        etMovieImageUrl.setText(imageUrl);

        // Set Listeners
        btnShow.setOnClickListener(btnShowListener);
        btnSave.setOnClickListener(btnSaveListener);


        //if the data received from MainActivity's list --> LoadMovieTask
        if (idValue != 0) {
            new LoadMovieTask().execute(idValue);
        }
    }

    private View.OnClickListener btnShowListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!etMovieImageUrl.getText().toString().equals("http://image.tmdb.org/t/p/w500//" + "null")) { // --> if image != null
                Glide.with(getApplicationContext())
                        .load(etMovieImageUrl.getText().toString())
                        .into(ivMovieImage);
            } else {
                ivMovieImage.setImageResource(R.drawable.ic_no_image_found);
            }
        }
    };

    private View.OnClickListener btnSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (idValue == 0) {
                SaveNewMovieInDB(
                        etMovieTitle.getText().toString(),
                        rating,
                        releaseDate,
                        etMovieDescription.getText().toString(),
                        etMovieImageUrl.getText().toString());

                Toast.makeText(AddEditMovieActivity.this, title + " Added To Movies DB", Toast.LENGTH_SHORT).show();
            } else {
                UpdateExistingMovieInDB(
                        rowID,
                        etMovieTitle.getText().toString(),
                        etMovieRating.getText().toString(),
                        etMovieReleaseDate.getText().toString(),
                        etMovieDescription.getText().toString(),
                        etMovieImageUrl.getText().toString());

                Toast.makeText(AddEditMovieActivity.this, etMovieTitle.getText().toString() + " Edited Successfully", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(AddEditMovieActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };


    // Save new movie
    private void SaveNewMovieInDB(String title, String rating, String releaseDate, String description, String imageUrl) {

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.insertMovie(
                title,
                rating,
                releaseDate,
                description,
                imageUrl
        );
    }

    // Update Existing Movie
    private void UpdateExistingMovieInDB(long rowID, String title, String rating, String releaseDate, String description, String imageUrl) {

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.updateMovie(
                rowID,
                title,
                rating,
                releaseDate,
                description,
                imageUrl
        );
    }


    // performs database query outside GUI thread
    private class LoadMovieTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(getApplicationContext());

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();

            return databaseConnector.getOneMovie(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            rowID = idValue;
            int titleIndex = result.getColumnIndex("title");
            int ratingIndex = result.getColumnIndex("rating");
            int releaseDateIndex = result.getColumnIndex("releaseDate");
            int descriptionIndex = result.getColumnIndex("description");
            int imageUrlIndex = result.getColumnIndex("imageUrl");

            String titleFieldFromDB = result.getString(titleIndex);
            String ratingFieldFromDB = result.getString(ratingIndex);
            String releaseDateFieldFromDB = result.getString(releaseDateIndex);
            String descriptionFieldFromDB = result.getString(descriptionIndex);
            String imageUrlIndexFieldFromDB = result.getString(imageUrlIndex);

            // fill TextViews with the retrieved data
            etMovieTitle.setText(titleFieldFromDB);
            etMovieRating.setText(ratingFieldFromDB);
            etMovieReleaseDate.setText(releaseDateFieldFromDB);
            etMovieDescription.setText(descriptionFieldFromDB);
            etMovieImageUrl.setText(imageUrlIndexFieldFromDB);
        }
    }
}
