package com.example.daniel.tmdbsampleapp;

/**
 * Created by Daniel on 08/03/2018.
 */

public class Movie {

    private long id;

    private String title;
    private String rating;
    private String releaseDate;
    private String description;
    private String imageUrl;

    public Movie() {
    }

    public Movie(String title, String rating, String releaseDate, String description, String imageUrl) {
        this.title = title;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
