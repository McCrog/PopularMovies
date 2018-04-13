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

package com.udacity.popularmovies.utilities;

import android.content.Context;

import com.udacity.popularmovies.data.MovieRepository;
import com.udacity.popularmovies.data.SortPreferences;
import com.udacity.popularmovies.data.database.MovieDatabaseSource;
import com.udacity.popularmovies.data.network.MovieNetworkDataSource;
import com.udacity.popularmovies.viewmodel.detail.DetailViewModelFactory;
import com.udacity.popularmovies.viewmodel.list.MovieViewModelFactory;

/**
 * Created by alex on 09/04/2018.
 */

public class InjectorUtils {
    public static MovieRepository provideRepository(Context context) {
        MovieDatabaseSource database = MovieDatabaseSource.getInstance(context.getApplicationContext());
        MovieNetworkDataSource networkDataSource =
                MovieNetworkDataSource.getInstance();
        SortPreferences sortPreferences = SortPreferences.getInstance(context.getApplicationContext());
        return MovieRepository.getInstance(database, networkDataSource, sortPreferences);
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, int id, int index) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, id, index);
    }

    public static MovieViewModelFactory provideMovieActivityViewModelFactory(Context context) {
        MovieRepository repository = provideRepository(context.getApplicationContext());
        return new MovieViewModelFactory(repository);
    }
}
