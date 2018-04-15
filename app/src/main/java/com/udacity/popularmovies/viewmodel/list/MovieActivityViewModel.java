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

package com.udacity.popularmovies.viewmodel.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import com.udacity.popularmovies.data.MovieRepository;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.ui.list.MovieActivity;

import java.util.List;

/**
 * {@link ViewModel} for {@link MovieActivity}
 */
public class MovieActivityViewModel extends ViewModel {

    private final MediatorLiveData<List<Movie>> mMoviesMediatorLiveData = new MediatorLiveData<>();

    private final LiveData<List<Movie>> mMovie;
    private final LiveData<List<Movie>> mFavoriteMovies;

    private MovieRepository mRepository;

    public MovieActivityViewModel(MovieRepository repository) {
        mRepository = repository;
        mMovie = mRepository.getMovies();
        mFavoriteMovies = mRepository.getFavoriteMovies();

        if (getSortPreference() <= 1) {
            mMoviesMediatorLiveData.addSource(mMovie, networkObserver);
        } else {
            mMoviesMediatorLiveData.addSource(mFavoriteMovies, databaseObserver);
        }
    }

    private Observer<List<Movie>> networkObserver = mMoviesMediatorLiveData::setValue;

    private Observer<List<Movie>> databaseObserver = mMoviesMediatorLiveData::setValue;

    public MediatorLiveData<List<Movie>> getMoviesMediatorLiveData() {
        return mMoviesMediatorLiveData;
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovie;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public void updateData(int preference) {
        if (preference <= 1) {
            mMoviesMediatorLiveData.removeSource(mFavoriteMovies);
            mMoviesMediatorLiveData.addSource(mMovie, networkObserver);
        } else {
            mMoviesMediatorLiveData.removeSource(mMovie);
            mMoviesMediatorLiveData.addSource(mFavoriteMovies, databaseObserver);
        }

        mRepository.updateData(preference);
    }

    public void refreshData() {
        mRepository.refreshData();
    }

    public void getNewData() {
        mRepository.getNewNetworkData();
    }

    public boolean isLoading() {
        return mRepository.isNetworkDataLoading();
    }

    public int getSortPreference() {
        return mRepository.currentSortPreference();
    }
}
