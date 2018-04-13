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

package com.udacity.popularmovies.viewmodel.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.popularmovies.data.MovieRepository;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;
import com.udacity.popularmovies.ui.detail.DetailActivity;

import java.util.List;

/**
 * {@link ViewModel} for {@link DetailActivity}
 */
public class DetailActivityViewModel extends ViewModel {

    private final LiveData<Movie> mMovie;
    private final LiveData<List<Trailer>> mTrailer;
    private final LiveData<List<Review>> mReview;
    private MovieRepository mRepository;
    private int mId;
    private int mIndex;

    public DetailActivityViewModel(MovieRepository repository, int id, int index) {
        mRepository = repository;
        mId = id;
        mIndex = index;
        mMovie = mRepository.getMovie(mId, mIndex);
        mTrailer = mRepository.getTrailers(mId);
        mReview = mRepository.getReviews(mId);
    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }

    public LiveData<List<Trailer>> getTrailers() {
        return mTrailer;
    }

    public LiveData<List<Review>> getReviews() {
        return mReview;
    }

    public void addToFavorite() {
        mRepository.saveFavorite(mIndex);
    }

    public void removeFromFavorite() {
        mRepository.deleteFavorite(mId);
    }
}
