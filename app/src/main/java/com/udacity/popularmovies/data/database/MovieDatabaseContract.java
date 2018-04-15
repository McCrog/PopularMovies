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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by McCrog on 02/04/2018.
 *
 */

class MovieDatabaseContract {
    static final String AUTHORITY = "com.udacity.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_MOVIES = "movies";

    static final class MovieEntry implements BaseColumns {
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        static final String TABLE_NAME = "movie";
        static final String COLUMN_MOVIE_ID = "movieId";
        static final String COLUMN_POSTER_PATH = "posterPath";
        static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        static final String COLUMN_OVERVIEW = "overview";
        static final String COLUMN_RELEASE_DATE = "releaseDate";
        static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        static final String COLUMN_FAVORITE = "favorite";
    }
}
