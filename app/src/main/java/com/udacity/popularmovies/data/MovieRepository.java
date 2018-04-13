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
            startFetchFavoriteMoviesFromDb();
        }
    }

    public LiveData<List<Movie>> getMovies() {
        initializeData();
        return mMovieNetworkDataSource.getDownloadedMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        initializeData();
        return mMovieDatabaseSource.getMovies();
    }

    public LiveData<Movie> getMovie(int id, int index) {
        initializeData();
        if (mMovieDatabaseSource.isExist(id)) {
            return mMovieDatabaseSource.getMovie(id);
        } else {
            return mMovieNetworkDataSource.getMovie(index);
        }
    }

    public void saveFavorite(int index) {
        Movie movie = mMovieNetworkDataSource.getMovie(index).getValue();
        movie.setFavorite(true);
        mMovieDatabaseSource.saveMovie(movie);
    }

    public void deleteFavorite(int id) {
        mMovieDatabaseSource.deleteMovie(id);
        mMovieDatabaseSource.loadMovies();
    }

    public LiveData<List<Trailer>> getTrailers(int id) {
        return mMovieNetworkDataSource.fetchTrailers(id);
    }

    public LiveData<List<Review>> getReviews(int id) {
        return mMovieNetworkDataSource.fetchReviews(id);
    }

    public void updateData(int preference) {
        mInitialized = false;
        mSortPreferences.saveSortPreference(preference);
        initializeData();
    }

    public void refreshData() {
        mInitialized = false;
        initializeData();
    }

    public int currentSortPreference() {
        return mSortPreferences.getSortPreference();
    }

    private void startFetchMoviesFromNetwork() {
        mMovieNetworkDataSource.fetchMovies(currentSortPreference());
    }

    private void startFetchFavoriteMoviesFromDb() {
        mMovieDatabaseSource.loadMovies();
    }

    private void deleteOldData() {
        mMovieDatabaseSource.deleteMovies();
    }
}
