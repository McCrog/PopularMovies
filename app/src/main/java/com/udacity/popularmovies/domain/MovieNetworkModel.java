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

package com.udacity.popularmovies.domain;

import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.udacity.popularmovies.BuildConfig.API_KEY;
import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCE_POPULAR;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class MovieNetworkModel {
    private static final String TAG = MovieNetworkModel.class.getSimpleName();

    private List<Movie> movies = new ArrayList<>();
    private Call<MoviesResponse> popularMovies;

    public void callMovies(int sortPreference, final LoadCallback callback) {
        MoviesAPIService moviesApiService = ApiClient.getClient().create(MoviesAPIService.class);

        if (sortPreference == APP_PREFERENCE_POPULAR) {
            popularMovies = moviesApiService.getPopularMovies(API_KEY);
        } else {
            popularMovies = moviesApiService.getTopRatedMovies(API_KEY);
        }

        popularMovies.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                movies = response.body().getResults();
                callback.onComplete(movies);
                Log.d(TAG, "Number of movies received: " + movies.size());
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                callback.onError();
            }
        });
    }

    public Movie get(int index) {
        if (movies != null) {
            return movies.get(index);
        }
        return null;
    }

    public void canselCallback() {
        if (popularMovies != null) {
            popularMovies.cancel();
        }
    }

    public List<Movie> getList() {
        return movies;
    }


    private List<Trailer> trailers = new ArrayList<>();
    public void callTrailers(String id, final LoadCallback callback) {
        MoviesAPIService moviesApiService = ApiClient.getClient().create(MoviesAPIService.class);

        Call<TrailerResponse> trailersCall = moviesApiService.getMovieTrailers(id, API_KEY);

        trailersCall.enqueue(new Callback<TrailerResponse>() {

            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                trailers = response.body().getResults();
                callback.onComplete(trailers);
                Log.d(TAG, "Number of trailers received: " + trailers.size());
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                callback.onError();
            }
        });
    }

    private List<Review> reviews = new ArrayList<>();
    public void callReviews(String id, final LoadCallback callback) {
        MoviesAPIService moviesApiService = ApiClient.getClient().create(MoviesAPIService.class);

        Call<ReviewResponse> reviewCall = moviesApiService.getMovieReviews(id, API_KEY);

        reviewCall.enqueue(new Callback<ReviewResponse>() {

            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                reviews = response.body().getResults();
                callback.onComplete(reviews);
                Log.d(TAG, "Number of reviews received: " + reviews.size());
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                callback.onError();
            }
        });
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
