/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Created by McCrog on 23/02/2018.
 */

public class MainActivity extends AppCompatActivity implements
        MoviesImageAdapter.MoviesImageAdapterOnClickHandler {

    // TODO - insert your themoviedb.org API KEY here
    private static final String API_KEY = "";
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private static List<Movie> movies;

    private SharedPreferences sharedPreferences;
    private static final String APP_PREFERENCES = "APP_PREFERENCES";
    private static final String APP_SORT_PREFERENCE = "SORT_PREFERENCE";
    private static final int APP_PREFERENCE_POPULAR = 0;
    private static final int APP_PREFERENCE_TOP_RATED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Please obtain your API KEY first from themoviedb.org",
                    Toast.LENGTH_LONG).show();
            return;
        }

        movies = new ArrayList<>();

        recyclerView = findViewById(R.id.movies_recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        getData(getSortPreference());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        setSortMenuItem(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                getData(getSortPreference());
                return true;
            case R.id.sort_by_popular:
                item.setChecked(true);
                saveSortPreference(APP_PREFERENCE_POPULAR);
                return true;
            case R.id.sort_by_top_rated:
                item.setChecked(true);
                saveSortPreference(APP_PREFERENCE_TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData(int sortPreference) {
        MoviesAPIService moviesApiService = ApiClient.getClient().create(MoviesAPIService.class);

        Call<MoviesResponse> popularMovies;
        if (sortPreference == APP_PREFERENCE_POPULAR) {
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
            }
        });
    }

    @Override
    public void onClick(int index) {
        Movie movie = movies.get(index);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE_DETAILS, movie);
        startActivity(intent);
    }

    private void setSortMenuItem(Menu menu) {
        MenuItem popularMenuItem = menu.findItem(R.id.sort_by_popular);
        MenuItem topRatedMenuItem = menu.findItem(R.id.sort_by_top_rated);

        int sortPreference = getSortPreference();

        if (sortPreference == APP_PREFERENCE_POPULAR) {
            popularMenuItem.setChecked(true);
        } else {
            topRatedMenuItem.setChecked(true);
        }
    }

    private int getSortPreference() {
        return sharedPreferences.getInt(APP_SORT_PREFERENCE, 0);
    }

    private void saveSortPreference(int sortPreference) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(APP_SORT_PREFERENCE, sortPreference);
        editor.apply();
        getData(sortPreference);
    }
}
