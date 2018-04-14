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

package com.udacity.popularmovies.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utilities.ImageUtils;
import com.udacity.popularmovies.utilities.InjectorUtils;
import com.udacity.popularmovies.viewmodel.detail.DetailActivityViewModel;
import com.udacity.popularmovies.viewmodel.detail.DetailViewModelFactory;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.udacity.popularmovies.utilities.Constants.MOVIE_ID;
import static com.udacity.popularmovies.utilities.Constants.MOVIE_INDEX;

/**
 * Created by McCrog on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapter.TrailerOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.poster_small_iv)
    ImageView mPosterIv;
    @BindView(R.id.original_title_tv)
    TextView mOriginalTitle;
    @BindView(R.id.overview_tv)
    TextView mOverview;
    @BindView(R.id.release_date_tv)
    TextView mReleaseDate;
    @BindView(R.id.vote_average_tv)
    TextView mVoteAverage;
    @BindString(R.string.detail_error_message)
    String mDetailErrorMessage;

    @BindView(R.id.trailers_recycle_view)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.reviews_recycle_view)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.favorite_button)
    FloatingActionButton mFavoriteButton;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private DetailActivityViewModel mViewModel;
    private DetailViewModelFactory mFactory;

    private boolean mIsFavorite = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        int id = intent.getIntExtra(MOVIE_ID, 0);
        int index = intent.getIntExtra(MOVIE_INDEX, 0);

        mFactory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext(), id, index);

        observeMovieData();

        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailerAdapter = new TrailerAdapter(DetailActivity.this);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewAdapter = new ReviewAdapter();
        mReviewsRecyclerView.setAdapter(mReviewAdapter);
    }

    @Override
    public void onClick(String key) {
        String baseUrl = "http://www.youtube.com/watch?v=";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + key)));
    }

    @OnClick(R.id.favorite_button)
    void onFloatingActionButtonClick() {
        if (mIsFavorite) {
            mViewModel.removeFromFavorite();
            mIsFavorite = false;
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        } else {
            mViewModel.addToFavorite();
            mIsFavorite = true;
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
    }

    private void observeMovieData() {
        mViewModel = ViewModelProviders.of(this, mFactory).get(DetailActivityViewModel.class);

        mViewModel.getMovie().observe(this, movie -> {
            // Update the UI
            init(movie);
        });

        mViewModel.getTrailers().observe(this, trailers -> {
            // Update the Adapter
            mTrailerAdapter.setData(trailers);
        });

        mViewModel.getReviews().observe(this, reviews -> {
            // Update the Adapter
            mReviewAdapter.setData(reviews);
        });
    }

    private void init(Movie movie) {
        mOriginalTitle.setText(movie.getOriginalTitle());
        mOverview.setText(movie.getOverview());
        String year = getReleaseYear(movie.getReleaseDate());
        mReleaseDate.setText(year);
        mVoteAverage.setText(getString(R.string.vote_average, movie.getVoteAverage()));
        mIsFavorite = movie.isFavorite();

        ImageUtils.loadImage(this, movie.getPosterPath(), mPosterIv, 1);

        initFavoriteButton(mIsFavorite);
    }

    private void initFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    private String getReleaseYear(String releaseDate) {
        return releaseDate.split("-")[0];
    }
}
