package com.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utilities.ImageUtil;

import java.util.List;

/**
 * Created by alex on 24/02/2018.
 */

public class MoviesImageAdapter extends RecyclerView.Adapter<MoviesImageAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Context context;
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final static String[] IMAGE_SIZE = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    private final static String RECOMMEND_IMAGE_SIZE = IMAGE_SIZE[2];

    final private MoviesImageAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesImageAdapterOnClickHandler {
        void onClick(int index);
    }

    public MoviesImageAdapter(Context context, List<Movie> movies, MoviesImageAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.movies = movies;
        this.mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_image_item, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        ImageUtil.loadImage(context, movie.getPosterPath(), holder.poster, 2);
    }

    @Override
    public int getItemCount() {
        return (movies == null) ? 0 : movies.size();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster_iv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

}
