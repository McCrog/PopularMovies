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

package com.udacity.popularmovies.mvp;

import android.util.Log;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by McCrog on 07/03/2018.
 *
 */

public class MoviePresenter extends PresenterBase<MovieContract.View> implements MovieContract.Presenter {
    private static final String TAG = MoviePresenter.class.getSimpleName();

    private final MovieNetworkModel model;
    private final SortPreferences sortPreferences;

    public MoviePresenter(MovieNetworkModel model, SortPreferences sortPreferences) {
        this.model = model;
        this.sortPreferences = sortPreferences;
    }

    @Override
    public void viewIsReady() {
        loadMovies();
    }

    public void loadMovies() {
        model.getMovies(onLoadSortPreference(), new MovieNetworkModel.LoadCallback() {
            @Override
            public void onComplete(List<Movie> movies) {
                getView().showToast(R.string.network_complete);
                Log.d(TAG, "Befor getView().showMovies(movies)");
                getView().showMovies(movies);
            }

            @Override
            public void onError() {
                getView().showToast(R.string.network_error);
            }
        });
    }

    public Movie onPosterClick(int index) {
        return model.get(index);
    }

    public void onChangeSortPreference(int referense) {
        sortPreferences.saveSortPreference(referense);
        loadMovies();
    }

    public int onLoadSortPreference() {
        return sortPreferences.getSortPreference();
    }

    public List<Movie> onMoviesSaved() {
        return model.getList();
    }
}
