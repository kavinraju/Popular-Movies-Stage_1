package com.example.kavinraju.popularmovies.HelperClass;

import android.view.View;
import android.widget.ProgressBar;

public class HelperMethods {


    // Helper Method to show ProgressBar
    public static void showProgressBar(ProgressBar progressBar, boolean show){
        if (show){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public static String getdetiledDate(String dateString){

        String year, month, date;
        year = dateString.substring(0,4);
        month = getMonth(dateString.substring(5,7));
        date = dateString.substring(8,10);
        return month + " " + date + " " + year;
    }

    public static String getMonth(String month){

        switch (month){
            case "01":
                return "Jan";
            case "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Apr";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "Jul";
            case "08":
                return "Aug";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dec";
            default:
                return "";
        }
    }
    }
