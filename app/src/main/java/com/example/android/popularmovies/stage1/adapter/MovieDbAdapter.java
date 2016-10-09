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
import com.example.android.popularmovies.stage1.data.api.Result;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 8.10.2016..
 */

public class MovieDbAdapter extends RecyclerView.Adapter<MovieDbAdapter.MovieViewHolder> {

    private MovieDbResult mMovieDbResult;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MovieDbAdapter(Context mContext, MovieDbResult mMovieDbResult, OnItemClickListener onItemClickListener) {
        this.mMovieDbResult = mMovieDbResult;
        this.mContext = mContext;
        this.mOnItemClickListener = onItemClickListener;
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
        final int tempPosition = position;
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(mMovieDbResult.getResults().get(tempPosition));
            }
        });
        Picasso.with(getContext()).load(BuildConfig.BASE_POSTER_PATH + mMovieDbResult.getResults().get(position).getPosterPath()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieDbResult.getResults().size();
    }

    public void resetList() {
        notifyItemRangeRemoved(0, mMovieDbResult.getResults().size());
    }

    public interface OnItemClickListener {

        void onItemClick(Result item);

    }

}
