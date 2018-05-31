package com.example.daniel.tmdbsampleapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

// TMDB API  KEY - 8a34b7badd94d935da8730dac0268ddf
// Example API Request - https://api.themoviedb.org/3/search/movie?&query=spiderman&api_key=8a34b7badd94d935da8730dac0268ddf

// Example Poster Path Request - http://image.tmdb.org/t/p/w500//rZd0y1X1Gw4t5B3f01Qzj8DYY66.jpg


public class MainActivity extends AppCompatActivity {

    private ListView myListView;
    long rowId;

    // adapter for ListView
    private SimpleCursorAdapter myMovieSimpleCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // map each account's name to a TextView in the ListView layout
        String[] from = new String[]{"title", "rating", "releaseDate", "description", "imageUrl"};
        // in file contact_list_item.xml
        int[] to = new int[]{R.id.tv_title, R.id.tv_rating, R.id.tv_release_date, R.id.iv_movie_img};


        myMovieSimpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.tmdb_search_result_list_row, // layout to inflate it
                null, // cursor adapter : if null then it will be created within the constructor
                from,
                to,
                0);

        myListView = findViewById(R.id.list);

        myMovieSimpleCursorAdapter.setViewBinder(new MyViewBinder());
        myListView.setAdapter(myMovieSimpleCursorAdapter); //databinding --> set the adapter
        myListView.setOnItemClickListener(listViewItemListener);

        new GetMoviesTask().execute("");
    }

    private AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

            rowId = id;

            Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
            intent.putExtra("movieRowId",rowId);
            startActivity(intent);
        }
    };


    // Create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Get menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_item) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.add_new_movie);
            builder.setMessage(R.string.select_to_insert_manually_or_from_the_TMDB_API);
            //builder.setIcon(R.drawable.ic_app_logo);
            builder.setNegativeButton(R.string.manually, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
                            startActivity(intent);
                        }
                    }
            );

            builder.setPositiveButton(R.string.tmdb_api, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this, TmdbSearchActivity.class);
                            startActivity(intent);
                        }
                    }
            );

            AlertDialog alert = builder.create();
            alert.show(); // display the Dialog
            //Button pButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);

            //pButton.setTextColor(getResources().getColor(R.color.colorBorderBg));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Get Movies From DB AsyncTask: performs database query outside UI thread
    private class GetMoviesTask extends AsyncTask<String, Void, Cursor> {

        DatabaseConnector databaseConnector = new DatabaseConnector(getApplicationContext()); //this is the DAL

        // perform the database access
        @Override
        protected Cursor doInBackground(String... params) {

            databaseConnector.open();

            // get a cursor containing call accounts
            if (params[0].equals("")) return databaseConnector.getAllMovies();
            else return null;
        }

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            myMovieSimpleCursorAdapter.changeCursor(result); // set the adapter's Cursor and loop over the data

            databaseConnector.close();
        }
    } // end class GetMoviesTask


    // Inner class for the viewBinder
    public class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            int viewID = view.getId();
            switch (viewID) {

                case R.id.tv_title:
                    TextView tvTitle = (TextView) view;
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    tvTitle.setText(title);
                    break;

                case R.id.tv_rating:
                    TextView tvRating = (TextView) view;
                    String rating = cursor.getString(cursor.getColumnIndex("rating"));
                    tvRating.setText(rating);
                    break;

                case R.id.tv_release_date:
                    TextView tvReleaseDate = (TextView) view;
                    String releaseDate = cursor.getString(cursor.getColumnIndex("releaseDate"));
                    tvReleaseDate.setText(releaseDate);
                    break;

                case R.id.iv_movie_img:
                    ImageView ivImage = (ImageView) view;
                    String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                    Glide.with(view)
                            .load(imageUrl)
                            .into(ivImage);
                    break;
            }
            return true;
        }
    }
}
