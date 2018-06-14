package com.example.kavinraju.popularmovies.Data_Model;

import java.io.Serializable;

public class MovieDetail implements Serializable {

    private int page;
    private int id;
    private double voteAverage;
    private String originalTitle;
    private String original_language;
    private String releaseDate;
    private String overView;
    private String poster_path;
    private String backDrop_path;
    private int totalPages;


    public  MovieDetail(){

    }

    public MovieDetail(int page, int id, double voteAverage,  String originalTitle, String original_language, String releaseDate, String overView, String poster_path, String backDrop_path, int totalPages) {
        this.page = page;
        this.id = id;
        this.voteAverage = voteAverage;
        this.originalTitle = originalTitle;
        this.original_language = original_language;
        this.releaseDate = releaseDate;
        this.overView = overView;
        this.poster_path = poster_path;
        this.backDrop_path = backDrop_path;
        this.totalPages = totalPages;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }


    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackDrop_path() {
        return backDrop_path;
    }

    public void setBackDrop_path(String backDrop_path) {
        this.backDrop_path = backDrop_path;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }


}
