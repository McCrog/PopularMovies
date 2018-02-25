package com.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utilities.ImageUtil;

/**
 * Created by alex on 25/02/2018.
 */

public class DetailActivity extends AppCompatActivity {
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final static String[] IMAGE_SIZE = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    private final static String RECOMMEND_IMAGE_SIZE = IMAGE_SIZE[1];

    private Movie movie;

    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mVoteAverage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView posterIv = findViewById(R.id.poster_small_iv);

        Intent intent = getIntent();

        if (intent != null) {
            movie = intent.getExtras().getParcelable("movieTag");
        } else {
            closeOnError();
        }

        if (movie == null) {
            // Movie data unavailable
            closeOnError();
            return;
        }

        populateUI(movie);

        ImageUtil.loadImage(this, movie.getPosterPath(), posterIv, 1);
    }

    private void populateUI(Movie movie) {
        mOriginalTitle = findViewById(R.id.original_title_tv);
        mOverview = findViewById(R.id.overview_tv);
        mReleaseDate = findViewById(R.id.release_date_tv);
        mVoteAverage = findViewById(R.id.vote_average_tv);

        mOriginalTitle.setText(movie.getOriginalTitle());
        mOverview.setText(movie.getOverview());
        String year = getReleaseYear(movie.getReleaseDate());
        mReleaseDate.setText(year);
        mVoteAverage.setText(getString(R.string.vote_average, movie.getVoteAverage()));
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private String getReleaseYear(String releaseDate) {
        return releaseDate.split("-")[0];
    }
}
