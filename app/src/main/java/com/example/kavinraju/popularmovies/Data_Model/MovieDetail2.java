package com.example.kavinraju.popularmovies.Data_Model;

import java.util.ArrayList;

public class MovieDetail2  {

    ArrayList<String> genres;
    String original_language;
    String tagline;

    public MovieDetail2(){

    }

    public MovieDetail2(ArrayList<String> genres, String original_language, String tagline) {
        this.genres = genres;
        this.original_language = original_language;
        this.tagline = tagline;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}
