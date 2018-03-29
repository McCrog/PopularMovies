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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by McCrog on 24/02/2018.
 *
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final List<Trailer> trailers = new ArrayList<>();
    private final TrailerOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerOnClickHandler {
        void onClick(String key);
    }

    public TrailerAdapter(TrailerOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer, parent, false);
        return new TrailerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.trailerButton.setText(trailers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void setData(List<Trailer> newTrailers) {
        trailers.clear();
        trailers.addAll(newTrailers);
        notifyDataSetChanged();
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_button)
        Button trailerButton;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            trailerButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mClickHandler.onClick(trailers.get(position).getKey());
        }
    }
}
