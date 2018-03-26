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

package com.udacity.popularmovies.presenter;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.base.BasePresenterImpl;
import com.udacity.popularmovies.base.DetailContract;
import com.udacity.popularmovies.domain.LoadCallback;
import com.udacity.popularmovies.domain.MovieNetworkModel;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by McCrog on 07/03/2018.
 *
 */

public class DetailPresenter extends BasePresenterImpl<DetailContract.View> implements DetailContract.Presenter {
    private static final String TAG = DetailPresenter.class.getSimpleName();

    private final MovieNetworkModel model;
    private final String id;

    public DetailPresenter(MovieNetworkModel model, String id) {
        this.model = model;
        this.id = id;
    }

    @Override
    public void viewIsReady() {
        loadTrailers();
        loadReviews();
    }

    public void loadTrailers() {
        model.callReviews(id, new LoadCallback() {
            @Override
            public <T> void onComplete(List<T> trailers) {
                getView().showToast(R.string.network_complete);
                getView().showTrailers((List<Trailer>) trailers);
            }

            @Override
            public void onError() {
                if (getView() != null) {
                    getView().showToast(R.string.network_error);
                }
            }
        });
    }

    public void loadReviews() {
        model.callReviews(id, new LoadCallback() {
            @Override
            public <T> void onComplete(List<T> reviews) {
                getView().showToast(R.string.network_complete);
                getView().showReviews((List<Review>) reviews);
            }

            @Override
            public void onError() {
                if (getView() != null) {
                    getView().showToast(R.string.network_error);
                }
            }
        });
    }

    @Override
    public void detachView() {
        model.canselCallback();
        super.detachView();
    }

    @Override
    public void onTrailerClick(int index) {

    }
}
