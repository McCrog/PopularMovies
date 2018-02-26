package com.udacity.popularmovies.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;

/**
 * Created by alex on 25/02/2018.
 */

public class ImageUtils {
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final static String[] IMAGE_SIZE = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};

    public static void loadImage(Context context, String posterPath, ImageView imageView, int imageSize) {
        Picasso.with(context)
                .load(BASE_IMAGE_URL + IMAGE_SIZE[imageSize] + posterPath)
                .placeholder(R.color.colorAccent)
                .into(imageView);
    }
}
