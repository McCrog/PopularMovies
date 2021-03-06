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
import com.udacity.popularmovies.utilities.SortOrder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCE_POPULAR;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class MovieNetworkDataSource {
    private static final String LOG_TAG = MovieNetworkDataSource.class.getSimpleName();

    private final MutableLiveData<List<Movie>> mDownloadedMovies;
    private final NetworkDataApi mNetworkDataApi = NetworkDataService.getNetworkService();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;

    private MovieNetworkDataSource() {
        mDownloadedMovies = new MutableLiveData<>();
    }

    private int mCurrentPage = 1;
    private int mTotalPages = 0;
    private boolean mIsLoading = false;
    private final List<Movie> mTempList = new ArrayList<>();

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
            popularMovies = mNetworkDataApi.getMovies(SortOrder.POPULAR, mCurrentPage);
        } else {
            popularMovies = mNetworkDataApi.getMovies(SortOrder.TOP_RATED, mCurrentPage);
        }

        popularMovies.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mTempList.addAll(response.body().getResults());
                    mDownloadedMovies.postValue(mTempList);
                    mCurrentPage = response.body().getPage();
                    mTotalPages = response.body().getTotalPages();
                    mIsLoading = false;
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // Log error here since request failed
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    public LiveData<List<Trailer>> fetchTrailers(int id) {
        Call<TrailerResponse> trailersCall = mNetworkDataApi.getMovieTrailers(id);

        final MutableLiveData<List<Trailer>> downloadedTrailers = new MutableLiveData<>();

        trailersCall.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    downloadedTrailers.postValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });

        return downloadedTrailers;
    }

    public LiveData<List<Review>> fetchReviews(int id) {
        Call<ReviewResponse> reviewCall = mNetworkDataApi.getMovieReviews(id);

        final MutableLiveData<List<Review>> downloadedReviews = new MutableLiveData<>();

        reviewCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    downloadedReviews.postValue(response.body().getResults());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });

        return downloadedReviews;
    }

    public LiveData<List<Movie>> getDownloadedMovies() {
        return mDownloadedMovies;
    }

    public LiveData<Movie> getMovie(int index) {
        MutableLiveData<Movie> movie = new MutableLiveData<>();
        movie.setValue(mDownloadedMovies.getValue().get(index));
        return movie;
    }

    public void fetchNewMovies(int sortPreference) {
        if (mCurrentPage != mTotalPages) {
            mCurrentPage++;
            mIsLoading = true;
            fetchMovies(sortPreference);
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void clearNetworkDataState() {
        mCurrentPage = 1;
        mTotalPages = 0;
        mTempList.clear();
        mIsLoading = false;
    }
}
