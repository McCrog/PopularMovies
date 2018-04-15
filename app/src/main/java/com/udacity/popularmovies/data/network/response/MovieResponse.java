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

package com.udacity.popularmovies.data.network.response;

import com.google.gson.annotations.SerializedName;
import com.udacity.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by McCrog on 24/02/2018.
 *
 */

public class MovieResponse {
    @SerializedName("page")
    private Integer page;
    @SerializedName("total_pages")
    private Integer totalPages;
    @SerializedName("results")
    private List<Movie> results;

    public Integer getPage() {
        return page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }
}
