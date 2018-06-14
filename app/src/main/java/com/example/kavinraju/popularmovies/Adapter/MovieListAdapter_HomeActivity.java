package com.example.kavinraju.popularmovies.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kavinraju.popularmovies.Data_Model.MovieDetail;
import com.example.kavinraju.popularmovies.R;
import com.example.kavinraju.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter_HomeActivity extends RecyclerView.Adapter<MovieListAdapter_HomeActivity.MovieList_ViewHolder> {

    private ArrayList<MovieDetail> movieDetails;
    private int totalMovies;
    final private MovieTileClickListener movieTileClickListener;
    Context context;

    public MovieListAdapter_HomeActivity(ArrayList<MovieDetail> movieDetails, int totalMovies, MovieTileClickListener movieTileClickListener) {
        this.movieDetails = movieDetails;
        this.totalMovies = totalMovies;
        this.movieTileClickListener = movieTileClickListener;

    }



    public interface MovieTileClickListener{

        void onMovieTitleClick(View view, int clickedMoviePosition);
    }

    @NonNull
    @Override
    public MovieListAdapter_HomeActivity.MovieList_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        int layoutID = R.layout.recyclerview_card_home_activity;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID , parent , false);


        return new MovieList_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter_HomeActivity.MovieList_ViewHolder holder, int position) {

        if (movieDetails.size() > 0) {
            String path = movieDetails.get(position).getPoster_path();
            String url = NetworkUtils.buildURL_for_Image(path, false).toString();
            Picasso.with(context)
                    .load(url)
                    .error(context.getResources().getDrawable(R.drawable.error_bk))
                    .into(holder.imageView);
            ViewCompat.setTransitionName(holder.imageView, movieDetails.get(position).getOriginalTitle());
            holder.textView.setText(String.valueOf(movieDetails.get(position).getVoteAverage()));
            Log.d("onBindViewHolder",url);
        }

    }

    @Override
    public int getItemCount() {
        return totalMovies;
    }

    public class
    MovieList_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;

        public MovieList_ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_recycerView_Card_HomeActivity);
            textView = itemView.findViewById(R.id.textView_rating_recycerView_Card_HomeActivity);

            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            movieTileClickListener.onMovieTitleClick(v,this.getPosition());
        }
    }

    public void addMovies(ArrayList<MovieDetail> movies){

        for (MovieDetail movie: movies){
            movieDetails.add(movie);
        }
        totalMovies = movieDetails.size();
        notifyItemInserted(totalMovies - movies.size());
        notifyDataSetChanged();
    }

    public void cleatMovieData(){
        int size = movieDetails.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                movieDetails.remove(0);
            }
            notifyItemRangeRemoved(0,size);
        }
    }
}


/*

<android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView_HomeActivity"
        tools:listItem ="@layout/recyclerView_HomeActivity"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>*/
