package com.example.kavinraju.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kavinraju.popularmovies.Adapter.MovieListAdapter_HomeActivity;
import com.example.kavinraju.popularmovies.Data_Model.MovieDetail;
import com.example.kavinraju.popularmovies.HelperClass.BottomNavigationBehavior;
import com.example.kavinraju.popularmovies.HelperClass.HelperMethods;
import com.example.kavinraju.popularmovies.Utils.JsonUtils;
import com.example.kavinraju.popularmovies.Utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MovieDetail>>,MovieListAdapter_HomeActivity.MovieTileClickListener, BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // Constants used
    private static int MOVIES_QUERY_LOADER_ID = 99;
    private static String MOVIES_QUERY_EXTRA = "movies_query";
    private static String PAGE_NUMBER_EXTRA = "page_number";
    private static String TOP_RATED = "top_rated";
    private static String POPULAR = "popular";
    private static String currentSelectedbottomNavigation = POPULAR;
    public static String SHARED_ELEMENT_TRANSITION_EXTRA = "sharedElementTransition";
    private static int defaultPageNo = 1;

    //Keys used for savedInstancestate
    private static String MOVIES_KEY ="movie_list";
    private static String MAXIMUM_PAGES_KEY ="maximumPages";
    private static String LOADING_KEY ="loading";
    private static String TOTAL_ITEM_COUNT_KEY ="totalItemCount";
    private static String PREVIOUS_TOTAL_ITEM_COUNT_KEY ="previous_totalItemCount";
    private static String VISIBLE_ITEM_COUNT_KEY ="visibleItemCount";
    private static String PAST_VISIBLE_ITEM_COUNT_KEY ="past_visibileItemCount";
    private static String VISIBLE_THRESHOLD_KEY ="visibleThreshold";
    private static String CURRENT_PAGE_NUMBER_KEY ="pageNo";
    private static String CURRENT_SELECTED_BOTTOM_NAVI_KEY ="currentSelectedbottomNavigation";

    // Variables used to load additional pages
    private static int currentPage = 1;
    private static int maximumPages = 0;
    private boolean loading = true;
    private int totalItemCount = 0;
    private int previous_totalItemCount = 0;
    private int visibleItemCount = 0;
    private int past_visibileItemCount = 0;
    private int visibleThreshold = 15;


    //POJO class
    private ArrayList<MovieDetail> movieDetails;

    // RecyclerView Adapter
    private MovieListAdapter_HomeActivity movieListAdapter_homeActivity;

    // UI components
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    TextView mTextView;
    ImageView mImageView;
    ProgressBar mProgressBar;
    Button mButton_retry;
    BottomNavigationView mBottomNavigationView;
    GridLayoutManager mGridLayoutManager;
    Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialization of UI components
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mTextView = findViewById(R.id.textView_connection_failed);
        mImageView = findViewById(R.id.imageView_connention_failed);
        mProgressBar = findViewById(R.id.progressBar);
        mButton_retry = findViewById(R.id.button_networkRetry);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        mButton_retry.setOnClickListener(this);
        HelperMethods.showProgressBar(mProgressBar,false);
        // Check if Bundle savedInstanceState has any data in it and retrieve if there is any data.
        if (savedInstanceState != null){
            loading = savedInstanceState.getBoolean(LOADING_KEY);

            currentPage = savedInstanceState.getInt(CURRENT_PAGE_NUMBER_KEY);
            totalItemCount = savedInstanceState.getInt(TOTAL_ITEM_COUNT_KEY);
            previous_totalItemCount = savedInstanceState.getInt(PREVIOUS_TOTAL_ITEM_COUNT_KEY);
            visibleItemCount = savedInstanceState.getInt(VISIBLE_ITEM_COUNT_KEY);
            past_visibileItemCount = savedInstanceState.getInt(PAST_VISIBLE_ITEM_COUNT_KEY);
            visibleThreshold = savedInstanceState.getInt(VISIBLE_THRESHOLD_KEY);
            maximumPages = savedInstanceState.getInt(MAXIMUM_PAGES_KEY);

            currentSelectedbottomNavigation = savedInstanceState.getString(CURRENT_SELECTED_BOTTOM_NAVI_KEY);
            movieDetails = (ArrayList<MovieDetail>) savedInstanceState.getSerializable(MOVIES_KEY);

            if (currentSelectedbottomNavigation.equals(POPULAR)){
                mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_popular));
            }else if (currentSelectedbottomNavigation.equals(TOP_RATED)){
                mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_top_rated));
            }

            // Setting up UI with previous DataSet by setting error UI and Progress bar GONE
            setNetworkFailedUI(false);
            HelperMethods.showProgressBar(mProgressBar,false);

            // Setting up UI with previous DataSet only if there is some Data available
            if (movieDetails != null) {

                mGridLayoutManager = getGridLayoutManager();
                mRecyclerView.setLayoutManager(mGridLayoutManager);
                mRecyclerView.setHasFixedSize(false);
                movieListAdapter_homeActivity = new MovieListAdapter_HomeActivity(movieDetails, movieDetails.size(), this);
                mRecyclerView.setAdapter(movieListAdapter_homeActivity);
            }
        }
        else {
            //set Popular by default
            mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_popular));
            runLoaderManager(currentSelectedbottomNavigation, defaultPageNo);
        }

        //Checking if network is available and setting up UI accordingly
        if (isNetworkAvailable()){
            setNetworkFailedUI(false);
        }
        else {
            setNetworkFailedUI(true);
        }

        // Addding addOnScrollListener to listen to UI scroll.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                totalItemCount = mGridLayoutManager.getItemCount();
                visibleItemCount = mGridLayoutManager.getChildCount();
                past_visibileItemCount = mGridLayoutManager.findFirstVisibleItemPosition();


                if (dy > 0) { // True only if the user scrolls down.

                    if (loading){
                        Log.d("RecyclerScroll","(1) loading");
                        if (totalItemCount > previous_totalItemCount){
                            /*If data is loading and  totalItemCount greater than previous_totalItemCount then set loading to false and
                                increase the currentPage number and set previous_totalItemCount to totalItemCount.*/
                            loading = false;
                            previous_totalItemCount = totalItemCount;
                            currentPage++;
                            Log.d("RecyclerScroll","totalItemCount > previous_totalItemCount");
                        }
                    }

                    if (!loading && (totalItemCount - visibleItemCount) <= (past_visibileItemCount + visibleThreshold)){
                        /*
                            If loading is false and
                            if ( total num. of Items - num. of items visible on screen ) is less than or equal to ( num. of item scrolled out + visible threshold )
                            then if network is available currentPage is increase by one, loading is set True and Data for next page is loaded.
                         */
                        if ( currentPage <= maximumPages){
                            Log.d("RecyclerScroll","3");
                            if (isNetworkAvailable()) {
                                currentPage++;
                                loading = true;
                                runLoaderManager(currentSelectedbottomNavigation, currentPage);
                            }else {
                                showToastMessage();
                            }
                        }
                    }else {
                        Log.d("RecyclerScroll","3) " +String.valueOf(totalItemCount)+"-"+String.valueOf(visibleItemCount)+"<="+String.valueOf(past_visibileItemCount)+"+" + String.valueOf(visibleThreshold) );
                    }

                    /*
                        If totalItemCount is less than previous_totalItemCount then we reset the state.
                        currentPage is set to defaultPageNo = 1.
                     */
                    if (totalItemCount < previous_totalItemCount){
                        currentPage = defaultPageNo;
                        previous_totalItemCount = totalItemCount;
                        if (totalItemCount == 0){
                            loading = true;
                        }
                    }
                }
            }
        });


    }// end of onCreate()



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putBoolean(LOADING_KEY,loading);

        outState.putInt(CURRENT_PAGE_NUMBER_KEY,currentPage);
        outState.putInt(TOTAL_ITEM_COUNT_KEY,totalItemCount);
        outState.putInt(PREVIOUS_TOTAL_ITEM_COUNT_KEY,previous_totalItemCount);
        outState.putInt(VISIBLE_ITEM_COUNT_KEY,visibleItemCount);
        outState.putInt(PAST_VISIBLE_ITEM_COUNT_KEY,past_visibileItemCount);
        outState.putInt(VISIBLE_THRESHOLD_KEY,visibleThreshold);
        outState.putInt(MAXIMUM_PAGES_KEY, maximumPages);

        outState.putString(CURRENT_SELECTED_BOTTOM_NAVI_KEY,currentSelectedbottomNavigation);

        outState.putSerializable(MOVIES_KEY,  movieDetails);

    }


    // Loader Methods
    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader< ArrayList<MovieDetail> > onCreateLoader(int id, @Nullable final Bundle args) {

        return new AsyncTaskLoader< ArrayList<MovieDetail> >(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (args == null){
                    return;
                }

                /*
                    If network is not available and Data Set (movieDetails) is null we set Network error UI
                    else If   network is not available and Data Set (movieDetails) is available we just show a Toast message
                    else show progress bar and load set RecyclerView Visible
                 */
                if (!isNetworkAvailable() && movieDetails == null){
                    setNetworkFailedUI(true);
                }else if (!isNetworkAvailable()){
                    showToastMessage();
                }else {
                    setNetworkFailedUI(false);
                    HelperMethods.showProgressBar(mProgressBar,true);
                }


            }

            @Override
            public ArrayList<MovieDetail> loadInBackground() {

                assert args != null;
                String movie_rating_type = args.getString(MOVIES_QUERY_EXTRA);
                int page_number = args.getInt(PAGE_NUMBER_EXTRA);

                if (movie_rating_type == null && page_number <= 0){
                    return null;
                }

                try {
                    // Using NetworkUtils Helper class to build URL and getting its responce as JSON string
                    URL movies_URL = NetworkUtils.buildURL_for_MoivesList(movie_rating_type, page_number);
                    String jsonString =  NetworkUtils.getResponceFromURl(movies_URL);

                    Log.d("pageNo ",String.valueOf(page_number));
                    return JsonUtils.getMovieArrayList(jsonString);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

        };
    }


    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MovieDetail>> loader, ArrayList<MovieDetail> movieDetails) {

        if (movieDetails != null ){

            if (currentPage > defaultPageNo){
                // adding data from nextPage
                movieListAdapter_homeActivity.addMovies(movieDetails);
                HelperMethods.showProgressBar(mProgressBar,false);
            }else {
                // Initialization
                this.movieDetails = movieDetails;

                maximumPages = movieDetails.get(0).getTotalPages(); // get maximum number of pages

                // Checking if the device is in PORTRAIT OR LANDSCAPE mode and then setting the respective span count
                mGridLayoutManager = getGridLayoutManager();

                mRecyclerView.setLayoutManager(mGridLayoutManager);
                mRecyclerView.setHasFixedSize(false);
                movieListAdapter_homeActivity = new MovieListAdapter_HomeActivity(movieDetails,movieDetails.size(),this);
                mRecyclerView.setAdapter(movieListAdapter_homeActivity);
                HelperMethods.showProgressBar(mProgressBar,false);
            }



        }
        else if(!isNetworkAvailable() && this.movieDetails == null) {
            //NetworkError Code goes here
            //Check if MovieDetails obeject has any data
            //Toast.makeText(HomeActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            setNetworkFailedUI(true);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<MovieDetail>> loader) {

    }


    @Override
    public void onMovieTitleClick(View view,int clickedMoviePosition) {

        /*
            Rubric
            <When a movie poster thumbnail is selected, the movie details screen is launched.>
         */
        if(movieDetails.size() > 0) {
            /*
            This condition is necessary if we click on item that hasn't even loaded, this is the situation when there is no network and
            we try load data and do click.
             */
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie", movieDetails.get(clickedMoviePosition));

            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtras(bundle);
            intent.putExtra(SHARED_ELEMENT_TRANSITION_EXTRA, ViewCompat.getTransitionName(view));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    view,
                    ViewCompat.getTransitionName(view));

            startActivity(intent, optionsCompat.toBundle());
        }
    }

    /*
    Rubric
    <UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.>
    Here BottomNavigationView is used.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.bottom_nav_popular:
                mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_popular));
                /*
                    Rubric
                    <When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.>
                 */
                currentSelectedbottomNavigation = POPULAR;
                if (totalItemCount > 0){
                    movieListAdapter_homeActivity.cleatMovieData();
                }
                resetPageDetails();
                if(isNetworkAvailable()){
                    runLoaderManager(POPULAR,currentPage);
                }else {
                    setNetworkFailedUI(true);
                }
                return true;
            case R.id.bottom_nav_top_rated:
                mToolbar.setTitle(getResources().getString(R.string.bottom_navigation_title_top_rated));
                currentSelectedbottomNavigation = TOP_RATED;

                if (totalItemCount > 0){
                    movieListAdapter_homeActivity.cleatMovieData();
                }
                resetPageDetails();
                if(isNetworkAvailable()){
                    runLoaderManager(TOP_RATED,currentPage);
                }else {
                    setNetworkFailedUI(true);
                }
                return true;
            case R.id.bottom_nav_upcoming:
                Toast.makeText(this, "Feature yet to implement", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bottom_nav_favorite:
                Toast.makeText(this, "Feature yet to implement", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bottom_nav_settings:
                Toast.makeText(this, "Feature yet to implement", Toast.LENGTH_SHORT).show();
                return true;
        }

        return false;
    }



    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.button_networkRetry:

                if (isNetworkAvailable()){
                    setNetworkFailedUI(false);
                    runLoaderManager(currentSelectedbottomNavigation,currentPage);
                }else {
                    setNetworkFailedUI(true);
                }

                break;
        }
    }

    // Helper Method to reset Page details
    private void resetPageDetails(){
        currentPage = 1;
        maximumPages = 0;
        loading = true;
        totalItemCount = 0;
        previous_totalItemCount = 0;
        visibleItemCount = 0;
        past_visibileItemCount = 0;
    }

    // Helper Method to start Loader
    private void runLoaderManager(String movie_rating_type, int pageNo){

        Bundle movieQueryBundle = new Bundle();
        movieQueryBundle.putString(MOVIES_QUERY_EXTRA , movie_rating_type);
        movieQueryBundle.putInt(PAGE_NUMBER_EXTRA, pageNo);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieloader = loaderManager.getLoader(MOVIES_QUERY_LOADER_ID);

        if (movieloader == null){
            loaderManager.initLoader(MOVIES_QUERY_LOADER_ID , movieQueryBundle ,this ).forceLoad();
        }else {
            loaderManager.restartLoader(MOVIES_QUERY_LOADER_ID,movieQueryBundle,this).forceLoad();
        }

    }

    // Helper Method to get GridLayoutManager basaed on Screen Orientation
    /*
        Rubric
     <Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails>
     */
    private GridLayoutManager getGridLayoutManager(){
        GridLayoutManager layoutManager;
        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

            // if PORTRAIT mode set spanCount as
            layoutManager = new GridLayoutManager(this,3);
        }
        else {

            // if PORTRAIT mode set spanCount as
            layoutManager = new GridLayoutManager(this,4);
        }

        return layoutManager;
    }


    // Helper Method to show a Network error Toast message
    private void showToastMessage() {
        if (mToast != null){
            mToast.cancel();
            mToast = Toast.makeText(getApplicationContext(),"Check your Internet Connection.",Toast.LENGTH_SHORT);
            mToast.show();
        }else {
            mToast = Toast.makeText(getApplicationContext(),"Check your Internet Connection.",Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    // Helper Method to check if network is available
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Helper Method to set UI on Network Error
    private void setNetworkFailedUI(boolean noConnection){
        if (noConnection){
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
            mButton_retry.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else {
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            mButton_retry.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
