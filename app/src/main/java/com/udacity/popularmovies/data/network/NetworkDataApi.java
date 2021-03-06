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

import com.udacity.popularmovies.data.network.response.MovieResponse;
import com.udacity.popularmovies.data.network.response.ReviewResponse;
import com.udacity.popularmovies.data.network.response.TrailerResponse;
import com.udacity.popularmovies.utilities.SortOrder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

interface NetworkDataApi {
    @GET("movie/{sort_order}")
    Call<MovieResponse> getMovies(@Path("sort_order") @SortOrder String sortOrder, @Query("page") int pageId);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getMovieTrailers(@Path("id") int id);

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getMovieReviews(@Path("id") int id);
}
