package com.udacity.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    // TODO - insert your themoviedb.org API KEY here
    private final static String API_KEY = "";
    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    List<Movie> movies;

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

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        MoviesAPIService moviesApiService = ApiClient.getClient().create(MoviesAPIService.class);

        Call<MoviesResponse> popularMovies = moviesApiService.getPopularMovies(API_KEY);
        popularMovies.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                movies = response.body().getResults();
                recyclerView.setAdapter(new MoviesImageAdapter(getApplicationContext(), movies));
                Log.d(TAG, "Number of movies received: " + movies.size());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
