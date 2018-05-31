package com.example.daniel.tmdbsampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 9/5/2017.
 */

class JsonAdapter extends SimpleAdapter {

    private Context mContext;
    public int resourceId;

    public JsonAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        mContext = context;
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.tmdb_search_result_list_row, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvRating = view.findViewById(R.id.tv_rating);
        TextView tvReleaseDate = view.findViewById(R.id.tv_release_date);
        ImageView ivMovieImg = view.findViewById(R.id.iv_movie_img);

        tvTitle.setText(data.get("title").toString());
        tvRating.setText(data.get("rating").toString());
        tvReleaseDate.setText(data.get("release_date").toString());

        if (!data.get("image").toString().equals("http://image.tmdb.org/t/p/w200//" + "null")) { // --> if image != null
            Glide.with(mContext)
                    .load(data.get("image"))
                    .into(ivMovieImg);
        } else {
            ivMovieImg.setImageResource(R.drawable.ic_no_image_found);
        }
        return view;
    }
}
