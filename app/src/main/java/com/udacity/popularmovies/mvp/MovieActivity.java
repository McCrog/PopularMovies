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

package com.udacity.popularmovies.mvp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.popularmovies.DetailActivity;
import com.udacity.popularmovies.MoviesImageAdapter;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.popularmovies.common.Constants.APP_PREFERENCE_POPULAR;
import static com.udacity.popularmovies.common.Constants.APP_PREFERENCE_TOP_RATED;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class MovieActivity extends AppCompatActivity implements
        MoviesImageAdapter.MoviesImageAdapterOnClickHandler, MovieContract.View {

    private static final String TAG = MovieActivity.class.getSimpleName();

    @BindView(R.id.movies_recycle_view)
    RecyclerView recyclerView;

    private MoviesImageAdapter moviesImageAdapter;

    private SortPreferences sortPreferences;
    private MoviePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        sortPreferences = new SortPreferences(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));

        moviesImageAdapter = new MoviesImageAdapter(getApplicationContext(), MovieActivity.this);
        recyclerView.setAdapter(moviesImageAdapter);

        MovieNetworkModel movieNetworkModel = new MovieNetworkModel();
        presenter = new MoviePresenter(movieNetworkModel, sortPreferences);
        presenter.attachView(this);
        Log.d(TAG, "Befor viewIsReady()");
//        presenter.viewIsReady();

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            presenter.viewIsReady();
        } else {
            moviesImageAdapter.setData(savedInstanceState.<Movie>getParcelableArrayList("movies"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) presenter.onMoviesSaved());
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
                presenter.loadMovies();
                return true;
            case R.id.sort_by_popular:
                item.setChecked(true);
                presenter.onChangeSortPreference(APP_PREFERENCE_POPULAR);
                return true;
            case R.id.sort_by_top_rated:
                item.setChecked(true);
                presenter.onChangeSortPreference(APP_PREFERENCE_TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(int index) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE_DETAILS, presenter.onPosterClick(index));
        startActivity(intent);
    }

    @Override
    public void showMovies(List<Movie> movies) {
        moviesImageAdapter.setData(movies);
    }

    @Override
    public void showToast(int resId) {
        Toast toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (isFinishing()) {
            presenter.destroy();
        }
    }

    private void setSortMenuItem(Menu menu) {
        MenuItem popularMenuItem = menu.findItem(R.id.sort_by_popular);
        MenuItem topRatedMenuItem = menu.findItem(R.id.sort_by_top_rated);

        int sortPreference = presenter.onLoadSortPreference();

        if (sortPreference == APP_PREFERENCE_POPULAR) {
            popularMenuItem.setChecked(true);
        } else {
            topRatedMenuItem.setChecked(true);
        }
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
