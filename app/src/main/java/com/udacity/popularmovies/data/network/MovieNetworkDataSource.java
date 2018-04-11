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

package com.udacity.popularmovies.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.popularmovies.data.network.response.MovieResponse;
import com.udacity.popularmovies.data.network.response.ReviewResponse;
import com.udacity.popularmovies.data.network.response.TrailerResponse;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;
import com.udacity.popularmovies.utilities.Filter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCE_POPULAR;

/**
 * Created by McCrog on 23/02/2018.
 */

public class MovieNetworkDataSource {
    private static final String LOG_TAG = MovieNetworkDataSource.class.getSimpleName();

    private final MutableLiveData<List<Movie>> mDownloadedMovies;
    private NetworkDataApi networkDataApi = NetworkDataService.getNetworkData();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;

    private MovieNetworkDataSource() {
        mDownloadedMovies = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static MovieNetworkDataSource getInstance() {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieNetworkDataSource();
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public void fetchMovies(int sortPreference) {
        Call<MovieResponse> popularMovies;
        if (sortPreference == APP_PREFERENCE_POPULAR) {
            popularMovies = networkDataApi.getMovies(Filter.POPULAR);
        } else {
            popularMovies = networkDataApi.getMovies(Filter.TOP_RATED);
        }

        popularMovies.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mDownloadedMovies.postValue(response.body().getResults());
                    Log.d(LOG_TAG, "Movies received");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Log error here since request failed
                Log.e(LOG_TAG, t.toString());
            }
        });
        // TODO: Remove comment
        // return mDownloadedMovies;
    }

    public LiveData<List<Trailer>> fetchTrailers(int id) {
        Call<TrailerResponse> trailersCall = networkDataApi.getMovieTrailers(id);

        final MutableLiveData<List<Trailer>> mDownloadedTrailers = new MutableLiveData<>();

        trailersCall.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mDownloadedTrailers.setValue(response.body().getResults());
                    Log.d(LOG_TAG, "Trailers received");
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });

        return mDownloadedTrailers;
    }

    public LiveData<List<Review>> fetchReviews(int id) {
        Call<ReviewResponse> reviewCall = networkDataApi.getMovieReviews(id);

        final MutableLiveData<List<Review>> mDownloadedReviews = new MutableLiveData<>();

        reviewCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mDownloadedReviews.setValue(response.body().getResults());
                    Log.d(LOG_TAG, "Reviews received");
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });

        return mDownloadedReviews;
    }

    public MutableLiveData<List<Movie>> getDownloadedMovies() {
        return mDownloadedMovies;
    }

    // TODO: Add EMPTY Movie?
    public Movie get(int index) {
        return mDownloadedMovies.getValue() == null ? null : mDownloadedMovies.getValue().get(index);
    }

    // TODO: Remove
    public void cancelMovieCallback() {
        cancelCallback(null);
    }

    public void cancelDetailCallback() {
        cancelCallback(null);
        cancelCallback(null);
    }

    private <T> void cancelCallback(Call<T> call) {
        if (call != null) {
            call.cancel();
        }
    }
}
