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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies.BuildConfig.API_KEY;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class MainActivity extends AppCompatActivity implements
        MoviesImageAdapter.MoviesImageAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.movies_recycle_view)
    RecyclerView recyclerView;
    @BindString(R.string.api_error)
    String apiError;
    @BindString(R.string.network_error)
    String networkError;

    private static List<Movie> movies = new ArrayList<>();
    private MoviesImageAdapter moviesImageAdapter;

    private SharedPreferences sharedPreferences;
    private static final String APP_PREFERENCES = "APP_PREFERENCES";
    private static final String APP_SORT_PREFERENCE = "SORT_PREFERENCE";
    private static final int APP_PREFERENCE_POPULAR = 0;
    private static final int APP_PREFERENCE_TOP_RATED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), apiError, Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));

        moviesImageAdapter = new MoviesImageAdapter(getApplicationContext(), MainActivity.this);
        recyclerView.setAdapter(moviesImageAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            getData(getSortPreference());
        }
        else {
            movies = savedInstanceState.getParcelableArrayList("movies");
            moviesImageAdapter.setData(movies);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) movies);
        super.onSaveInstanceState(outState);
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
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                movies = response.body().getResults();
                moviesImageAdapter.setData(movies);
                Log.d(TAG, "Number of movies received: " + movies.size());
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(MainActivity.this, networkError, Toast.LENGTH_SHORT).show();
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

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}
