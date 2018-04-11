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

package com.udacity.popularmovies.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.udacity.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 02/04/2018.
 */

public class MovieDatabaseSource {
    private static final String LOG_TAG = MovieDatabaseSource.class.getSimpleName();

    private final ContentResolver contentResolver;
    private final MutableLiveData<List<Movie>> mDatabaseMovies;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieDatabaseSource sInstance;

    private MovieDatabaseSource(Context context) {
        contentResolver = context.getContentResolver();
        mDatabaseMovies = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static MovieDatabaseSource getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the database data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieDatabaseSource(context);
                Log.d(LOG_TAG, "Made new database data source");
            }
        }
        return sInstance;
    }


    public LiveData<List<Movie>> getMovies() {
        loadMovies();
        Log.d(LOG_TAG, "Load movies from DB");
        return mDatabaseMovies;
    }

    public LiveData<Movie> getMovie(int id) {
        Log.d(LOG_TAG, "Get movie from DB with ID " + id);
        String mSelectionClause = MovieDatabaseContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] mSelectionArgs = {String.valueOf(id)};
        Cursor cursor = contentResolver.query(MovieDatabaseContract.MovieEntry.CONTENT_URI,
                null,
                mSelectionClause,
                mSelectionArgs,
                null
        );

        Movie movie;
        MutableLiveData<Movie> movieLiveData = new MutableLiveData<>();

        if (cursor.moveToFirst()) {
            Integer movieId = cursor.getInt(1);
            String posterPath = cursor.getString(2);
            String originalTitle = cursor.getString(3);
            String overview = cursor.getString(4);
            String releaseDate = cursor.getString(5);
            Double voteAverage = cursor.getDouble(6);
            boolean favorite = convertIntegerToBoolean(cursor.getInt(7));
            movie = new Movie(movieId, posterPath, originalTitle, overview, releaseDate, voteAverage, favorite);
            movieLiveData.setValue(movie);
        }
        cursor.close();

        return movieLiveData;
    }

    public void saveMovies(List<Movie> list) {
        ContentValues[] contentValues = new ContentValues[list.size()];

        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_MOVIE_ID, list.get(i).getId());
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_POSTER_PATH, list.get(i).getPosterPath());
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE, list.get(i).getOriginalTitle());
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_OVERVIEW, list.get(i).getOverview());
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE, list.get(i).getReleaseDate());
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE, list.get(i).getVoteAverage());
            values.put(MovieDatabaseContract.MovieEntry.COLUMN_FAVORITE, convertBooleanToInteger(list.get(i).isFavorite()));
            contentValues[i] = values;
        }

        contentResolver.bulkInsert(MovieDatabaseContract.MovieEntry.CONTENT_URI, contentValues);
    }

    public void saveMovie(Movie movie) {
        ContentValues values = new ContentValues();

        values.put(MovieDatabaseContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieDatabaseContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieDatabaseContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        values.put(MovieDatabaseContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieDatabaseContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(MovieDatabaseContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(MovieDatabaseContract.MovieEntry.COLUMN_FAVORITE, convertBooleanToInteger(movie.isFavorite()));

        contentResolver.insert(MovieDatabaseContract.MovieEntry.CONTENT_URI, values);
    }

    public void deleteMovies() {
        contentResolver.delete(MovieDatabaseContract.MovieEntry.CONTENT_URI, null, null);
    }

    public void deleteMovie(Movie movie) {
        String mSelectionClause = MovieDatabaseContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] mSelectionArgs = {String.valueOf(movie.getId())};
        contentResolver.delete(MovieDatabaseContract.MovieEntry.CONTENT_URI, mSelectionClause, mSelectionArgs);
    }

    // TODO: FIX order of the variable
    private void loadMovies() {
        List<Movie> movies = new ArrayList<>();

        Cursor cursor = contentResolver.query(MovieDatabaseContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(1);
            String posterPath = cursor.getString(2);
            String originalTitle = cursor.getString(3);
            String overview = cursor.getString(4);
            String releaseDate = cursor.getString(5);
            Double voteAverage = cursor.getDouble(6);
            boolean favorite = convertIntegerToBoolean(cursor.getInt(7));

            movies.add(new Movie(id, posterPath, originalTitle, overview, releaseDate, voteAverage, favorite));
        }
        cursor.close();
        mDatabaseMovies.setValue(movies);
    }

    private static int convertBooleanToInteger(boolean flag) {
        return flag ? 1 : 0;
    }

    private static boolean convertIntegerToBoolean(int value) {
        return value == 1;
    }
}
