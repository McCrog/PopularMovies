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

package com.udacity.popularmovies.ui.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.ui.detail.DetailActivity;
import com.udacity.popularmovies.utilities.InjectorUtils;
import com.udacity.popularmovies.viewmodel.list.MovieActivityViewModel;
import com.udacity.popularmovies.viewmodel.list.MovieViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCE_FAVORITE;
import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCE_POPULAR;
import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCE_TOP_RATED;
import static com.udacity.popularmovies.utilities.Constants.MOVIE_ID;
import static com.udacity.popularmovies.utilities.Constants.MOVIE_INDEX;

/**
 * Created by McCrog on 23/02/2018.
 */

public class MovieActivity extends AppCompatActivity implements
        MovieAdapter.MovieOnClickHandler {

    @BindView(R.id.movies_recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    private MovieAdapter mMovieAdapter;
    private MovieActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));

        mMovieAdapter = new MovieAdapter(getApplicationContext(), this);
        mRecyclerView.setAdapter(mMovieAdapter);

        setSupportActionBar(mToolbar);

        initObserver();

        initNavigationItemSelectedListener();
    }

    private void initObserver() {
        MovieViewModelFactory factory = InjectorUtils.provideMovieActivityViewModelFactory(this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(MovieActivityViewModel.class);

        mViewModel.getMoviesMediatorLiveData().observe(this, movies -> {
            mMovieAdapter.setData(movies);
        });
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
                mViewModel.refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(int index) {
        Intent intent = new Intent(this, DetailActivity.class);

        int sortPreference = mViewModel.getSortPreference();

        if (sortPreference <= 1) {
            intent.putExtra(MOVIE_ID, mViewModel.getMovies().getValue().get(index).getId());
        } else {
            intent.putExtra(MOVIE_ID, mViewModel.getFavoriteMovies().getValue().get(index).getId());
        }

        intent.putExtra(MOVIE_INDEX, index);
        startActivity(intent);
    }

    private void initNavigationItemSelectedListener() {

        setNavigationMenuItem();

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.sort_by_popular:
                            mViewModel.updateData(APP_PREFERENCE_POPULAR);
                            break;
                        case R.id.sort_by_top_rated:
                            mViewModel.updateData(APP_PREFERENCE_TOP_RATED);
                            break;
                        case R.id.sort_by_favorite:
                            mViewModel.updateData(APP_PREFERENCE_FAVORITE);
                            break;
                    }
                    return true;
                });
    }

    private void setNavigationMenuItem() {
        Menu menu = mBottomNavigationView.getMenu();

        MenuItem popularMenuItem = menu.findItem(R.id.sort_by_popular);
        MenuItem topRatedMenuItem = menu.findItem(R.id.sort_by_top_rated);
        MenuItem favoriteMenuItem = menu.findItem(R.id.sort_by_favorite);

        int sortPreference = mViewModel.getSortPreference();

        if (sortPreference == APP_PREFERENCE_POPULAR) {
            popularMenuItem.setChecked(true);
        } else if (sortPreference == APP_PREFERENCE_TOP_RATED) {
            topRatedMenuItem.setChecked(true);
        } else {
            favoriteMenuItem.setChecked(true);
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
