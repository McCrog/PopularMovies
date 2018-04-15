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

package com.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by McCrog on 23/02/2018.
 *
 */

public class Movie {
    @SerializedName("id")
    private final Integer id;

    @SerializedName("poster_path")
    private final String posterPath;

    @SerializedName("original_title")
    private final String originalTitle;

    @SerializedName("overview")
    private final String overview;

    @SerializedName("release_date")
    private final String releaseDate;

    @SerializedName("vote_average")
    private final Double voteAverage;

    private boolean favorite;

    public Movie(Integer id, String posterPath, String originalTitle, String overview, String releaseDate, Double voteAverage, boolean favorite) {
        this.id = id;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.favorite = favorite;
    }

    public Integer getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
