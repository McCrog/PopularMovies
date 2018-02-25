package com.udacity.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.MoviesResponse;
import com.udacity.popularmovies.utilities.ApiClient;
import com.udacity.popularmovies.utilities.MoviesAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        MoviesImageAdapter.MoviesImageAdapterOnClickHandler {

    // TODO - insert your themoviedb.org API KEY here
    private final static String API_KEY = "";
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private static List<Movie> movies;

    private boolean sortByPopular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Please obtain your API KEY first from themoviedb.org",
                    Toast.LENGTH_LONG).show();
            return;
        }

        movies = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.movies_recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        defaultSetup();

        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                getData();
                return true;
            case R.id.sort_by:
                return true;
            case R.id.sort_by_popular:
                sortByPopular = true;
                item.setChecked(true);
                getData();
                return true;
            case R.id.sort_by_top_rated:
                sortByPopular = false;
                item.setChecked(true);
                getData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData() {
        MoviesAPIService moviesApiService = ApiClient.getClient().create(MoviesAPIService.class);

        Call<MoviesResponse> popularMovies;
        if (sortByPopular) {
            popularMovies = moviesApiService.getPopularMovies(API_KEY);
        } else {
            popularMovies = moviesApiService.getTopRatedMovies(API_KEY);
        }

        popularMovies.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                movies = response.body().getResults();
                recyclerView.setAdapter(new MoviesImageAdapter(getApplicationContext(), movies, MainActivity.this));
                Log.d(TAG, "Number of movies received: " + movies.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                // TODO Add text view for error
            }
        });
    }

    private void defaultSetup() {
        Resources res = getResources();
        sortByPopular = res.getBoolean(R.bool.movie_sort_by_popular);
    }

    @Override
    public void onClick(int index) {
        Movie movie = movies.get(index);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movieTag", movie);
        startActivity(intent);
    }
}
