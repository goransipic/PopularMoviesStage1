package com.example.android.popularmovies.stage1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.stage1.BuildConfig;
import com.example.android.popularmovies.stage1.R;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 8.10.2016..
 */

public class MovieDbAdapter extends RecyclerView.Adapter<MovieDbAdapter.MovieViewHolder> {

    private MovieDbResult mMovieDbResult;
    private Context mContext;

    public MovieDbAdapter(Context mContext, MovieDbResult mMovieDbResult) {
        this.mMovieDbResult = mMovieDbResult;
        this.mContext = mContext;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.img_movie);
        }
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_activity, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Picasso.with(getContext()).load(BuildConfig.BASE_POSTER_PATH + mMovieDbResult.getResults().get(position).getPosterPath()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieDbResult.getResults().size();
    }
}
