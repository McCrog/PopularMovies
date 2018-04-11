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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Trailer;
import com.udacity.popularmovies.utilities.ImageUtils;
import com.udacity.popularmovies.utilities.InjectorUtils;
import com.udacity.popularmovies.viewmodel.detail.DetailActivityViewModel;
import com.udacity.popularmovies.viewmodel.detail.DetailViewModelFactory;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.popularmovies.utilities.Constants.MOVIE_ID;

/**
 * Created by McCrog on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements
        TrailerAdapter.TrailerOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

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

    @BindView(R.id.trailers_recycle_view)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.reviews_recycle_view)
    RecyclerView reviewsRecyclerView;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private DetailActivityViewModel mViewModel;
    private DetailViewModelFactory factory;

    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        id = intent.getIntExtra(MOVIE_ID, 0);

        factory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext(), id);

        observeMovieData();

        initFavoriteListener();

        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerAdapter = new TrailerAdapter(DetailActivity.this);
        trailersRecyclerView.setAdapter(trailerAdapter);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter();
        reviewsRecyclerView.setAdapter(reviewAdapter);
    }

    private void init(Movie movie) {
        mOriginalTitle.setText(movie.getOriginalTitle());
        mOverview.setText(movie.getOverview());
        String year = getReleaseYear(movie.getReleaseDate());
        mReleaseDate.setText(year);
        mVoteAverage.setText(getString(R.string.vote_average, movie.getVoteAverage()));
        materialFavoriteButton.setFavorite(movie.isFavorite(), false);

        ImageUtils.loadImage(this, movie.getPosterPath(), posterIv, 1);
    }

    public void showTrailers(List<Trailer> trailers) {
        trailerAdapter.setData(trailers);
    }

    public void showReviews(List<Review> reviews) {
        reviewAdapter.setData(reviews);
    }

    public void showToast(int resId) {
        Toast toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(String key) {
        String baseUrl = "http://www.youtube.com/watch?v=";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + key)));
    }

    private void observeMovieData() {
        mViewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel.class);

        mViewModel.getMovie().observe(this, movie -> {
            // Update the UI
            init(movie);
        });

        mViewModel.getTrailers().observe(this, trailers -> {
            // Update the Adapter
            trailerAdapter.setData(trailers);
        });

        mViewModel.getReviews().observe(this, reviews -> {
            // Update the Adapter
            reviewAdapter.setData(reviews);
        });
    }

    // TODO: replace with lambda
    private void initFavoriteListener() {
        materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite) {
//                    mMovie.setFavorite(true);
                    Log.d(LOG_TAG, "Favorite TRUE" + " And id of film is " + id);
                } else {
//                    mMovie.setFavorite(false);
                    Log.d(LOG_TAG, "Favorite FALSE");
                }
            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, detailErrorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getReleaseYear(String releaseDate) {
        return releaseDate.split("-")[0];
    }
}
