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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utilities.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by McCrog on 24/02/2018.
 *
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movies = new ArrayList<>();
    private final Context context;
    private final MovieOnClickHandler mClickHandler;
    private final MovieOnFavoriteChangeHandler movieOnFavoriteChangeHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieOnClickHandler {
        void onClick(int index);
    }

    public interface MovieOnFavoriteChangeHandler {
        void onFavoriteClick(int index, boolean favorite);
    }

    public MovieAdapter(Context context, MovieOnClickHandler clickHandler, MovieOnFavoriteChangeHandler favoriteChangeHandler) {
        this.context = context;
        this.mClickHandler = clickHandler;
        this.movieOnFavoriteChangeHandler = favoriteChangeHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_image_item, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.materialFavoriteButton.setFavorite(movie.isFavorite(), false);

        ImageUtils.loadImage(context, movie.getPosterPath(), holder.poster, 2);
    }

    @Override
    public int getItemCount() {
        return (movies == null) ? 0 : movies.size();
    }

    public void setData(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MaterialFavoriteButton.OnFavoriteChangeListener {
        @BindView(R.id.poster_iv)
        ImageView poster;
        @BindView(R.id.main_favorite_button)
        MaterialFavoriteButton materialFavoriteButton;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            materialFavoriteButton.setOnFavoriteChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }

        @Override
        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
            int adapterPosition = getAdapterPosition();
            movieOnFavoriteChangeHandler.onFavoriteClick(adapterPosition, favorite);
        }
    }

}
