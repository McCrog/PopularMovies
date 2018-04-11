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

package com.udacity.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.udacity.popularmovies.data.database.MovieDatabaseSource;
import com.udacity.popularmovies.data.network.MovieNetworkDataSource;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by alex on 08/04/2018.
 */

public class MovieRepository {
    private static final String LOG_TAG = MovieRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieRepository sInstance;

    private final MovieDatabaseSource mMovieDatabaseSource;
    private final MovieNetworkDataSource mMovieNetworkDataSource;
    private final SortPreferences mSortPreferences;

    private boolean mInitialized = false;

    private MovieRepository(MovieDatabaseSource movieDatabaseSource, MovieNetworkDataSource movieNetworkDataSource, SortPreferences sortPreferences) {
        mMovieDatabaseSource = movieDatabaseSource;
        mMovieNetworkDataSource = movieNetworkDataSource;
        mSortPreferences = sortPreferences;

        LiveData<List<Movie>> networkData = mMovieNetworkDataSource.getDownloadedMovies();

        networkData.observeForever(newMoviesFromNetwork -> {
            deleteOldData();
            Log.d(LOG_TAG, "Old movies deleted");
            // Insert new movies data into Movies's database
            mMovieDatabaseSource.saveMovies(newMoviesFromNetwork);
            Log.d(LOG_TAG, "New movies inserted");
            mMovieDatabaseSource.getMovies();
        });
    }

    public synchronized static MovieRepository getInstance(MovieDatabaseSource movieDatabaseSource, MovieNetworkDataSource movieNetworkDataSource, SortPreferences sortPreferences) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieRepository(movieDatabaseSource, movieNetworkDataSource, sortPreferences);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    private synchronized void initializeData() {
        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        if (currentSortPreference() <= 1) {
            startFetchMoviesFromNetwork();
        } else {
            fetchFavoriteMoviesFromDb();
        }
    }

    public LiveData<List<Movie>> getMovies() {
        initializeData();
        return mMovieDatabaseSource.getMovies();
    }

    public LiveData<Movie> getMovie(int id) {
        initializeData();
        return mMovieDatabaseSource.getMovie(id);
    }

    public LiveData<List<Trailer>> getTrailers(int id) {
        return mMovieNetworkDataSource.fetchTrailers(id);
    }

    public LiveData<List<Review>> getReviews(int id) {
        return mMovieNetworkDataSource.fetchReviews(id);
    }

    public void updateData() {
        mInitialized = false;
        initializeData();
    }

    private void startFetchMoviesFromNetwork() {
        mMovieNetworkDataSource.fetchMovies(currentSortPreference());
    }

    // TODO Implement correct method
    private void fetchFavoriteMoviesFromDb() {
        mMovieDatabaseSource.getMovies();
    }

    private int currentSortPreference() {
        return mSortPreferences.getSortPreference();
    }

    private void deleteOldData() {
        mMovieDatabaseSource.deleteMovies();
    }
}
