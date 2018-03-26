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

package com.udacity.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.base.DetailContract;
import com.udacity.popularmovies.domain.MovieNetworkModel;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;
import com.udacity.popularmovies.presenter.DetailPresenter;
import com.udacity.popularmovies.utilities.ImageUtils;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by McCrog on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements DetailContract.View {
    public static final String MOVIE_DETAILS = "MOVIE_DETAILS";

    private Movie movie;
    private DetailPresenter presenter;

    @BindView(R.id.poster_small_iv)
    ImageView posterIv;
    @BindView(R.id.original_title_tv)
    TextView mOriginalTitle;
    @BindView(R.id.overview_tv)
    TextView mOverview;
    @BindView(R.id.release_date_tv)
    TextView mReleaseDate;
    @BindView(R.id.vote_average_tv)
    TextView mVoteAverage;
    @BindView(R.id.detail_favorite_button)
    MaterialFavoriteButton materialFavoriteButton;
    @BindString(R.string.detail_error_message)
    String detailErrorMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {
            movie = intent.getExtras().getParcelable(MOVIE_DETAILS);
        } else {
            closeOnError();
        }

        if (movie == null) {
            // Movie data unavailable
            closeOnError();
            return;
        }

        init(movie);

        ImageUtils.loadImage(this, movie.getPosterPath(), posterIv, 1);

        materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite) {
                    movie.setFavorite(true);
                    Log.d("Detail", "" + movie.isFavorite());
                } else {
                    movie.setFavorite(false);
                    Log.d("Detail", "" + movie.isFavorite());
                }
            }
        });

        String id = String.valueOf(movie.getId());

        MovieNetworkModel movieNetworkModel = new MovieNetworkModel();
        presenter = new DetailPresenter(movieNetworkModel, id);
        presenter.attachView(this);

        presenter.viewIsReady();
    }

    private void init(Movie movie) {
        mOriginalTitle.setText(movie.getOriginalTitle());
        mOverview.setText(movie.getOverview());
        String year = getReleaseYear(movie.getReleaseDate());
        mReleaseDate.setText(year);
        mVoteAverage.setText(getString(R.string.vote_average, movie.getVoteAverage()));
        materialFavoriteButton.setFavorite(movie.isFavorite(), false);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, detailErrorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getReleaseYear(String releaseDate) {
        return releaseDate.split("-")[0];
    }

    @Override
    public void showTrailers(List<Trailer> trailers) {

    }

    @Override
    public void showReviews(List<Review> reviews) {

    }

    @Override
    public void showToast(int resId) {
        Toast toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        toast.show();
    }
}
