package com.example.daniel.tmdbsampleapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TmdbSearchActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    ProgressDialog pDialog;
    private ListView lv;

    private EditText etSearchContent;
    // URL to get movies JSON
    private static String url;

    private Button btnGo;

    // ArrayList for mapping values from JSON
    ArrayList<HashMap<String, String>> movieMapArrayList = new ArrayList<>();

    // ArrayList of Movie object
    ArrayList<Movie> movieArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmdb_search_relative_layout);

        lv = findViewById(R.id.list);
        etSearchContent = findViewById(R.id.et_search_content);
        btnGo = findViewById(R.id.btn_go);

        lv.setOnItemClickListener(lvItemListener);
        btnGo.setOnClickListener(btnGoListener);
    }


    // Button GO Listener
    private View.OnClickListener btnGoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            url = "https://api.themoviedb.org/3/search/movie?&query=" + etSearchContent.getText().toString() + "&api_key=8a34b7badd94d935da8730dac0268ddf";

            movieMapArrayList.clear();
            movieArrayList.clear();
            new GetMovies().execute();
        }
    };

    // Async task class to get json by making HTTP call
    public class GetMovies extends AsyncTask<Void, Void, Void> {

        // In onPreExecute() method progress dialog is shown before making the http call.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(TmdbSearchActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // In doInBackground() method, makeServiceCall() is called to get the json from url.
        // Once the json is fetched, it is parsed and each movie is added to array list.
        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler httpHandler = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = httpHandler.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray movies = jsonObj.getJSONArray("results");

                    // looping through All Movies
                    for (int i = 0; i < movies.length(); i++) {

                        JSONObject m = movies.getJSONObject(i);

                        String title = m.getString("title");
                        String image = m.getString("poster_path");
                        String rating = m.getString("vote_average");
                        String releaseDate = m.getString("release_date");
                        String description = m.getString("overview");

                        // tmp hash map for single movie
                        HashMap<String, String> movieMap = new HashMap<>();

                        // adding each child node to HashMap key => value
                        movieMap.put("title", title);
                        movieMap.put("image", "http://image.tmdb.org/t/p/w200//" + image);
                        movieMap.put("rating", rating);
                        movieMap.put("release_date", releaseDate);

                        // adding movieMap to movie list (our ArrayList)
                        movieMapArrayList.add(movieMap);

                        // adding new movie to movie list Array (our object)
                        Movie movie = new Movie(title, rating, releaseDate, description, "http://image.tmdb.org/t/p/w500//" + image);
                        movieArrayList.add(movie);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        // In onPostExecute() method the progress dialog is dismissed and the array list data is
        // displayed in list view using an adapter.
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Updating parsed JSON data into ListView
            JsonAdapter adapter = new JsonAdapter(
                    TmdbSearchActivity.this,
                    movieMapArrayList,
                    R.layout.tmdb_search_result_list_row,
                    new String[]{},
                    new int[]{});

            // updating listView
            lv.setAdapter(adapter);

            // hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }


    // ListView On Item Click Listener
    private AdapterView.OnItemClickListener lvItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intent = new Intent(TmdbSearchActivity.this, AddEditMovieActivity.class);
            intent.putExtra("movie_title", movieArrayList.get(i).getTitle());
            intent.putExtra("movie_rating", movieArrayList.get(i).getRating());
            intent.putExtra("movie_releaseDate", movieArrayList.get(i).getReleaseDate());
            intent.putExtra("movie_description", movieArrayList.get(i).getDescription());
            intent.putExtra("movie_image_url", movieArrayList.get(i).getImageUrl());
            startActivity(intent);
        }
    };
}
