package com.example.android.popularmovies.stage1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.stage1.BuildConfig;
import com.example.android.popularmovies.stage1.R;
import com.example.android.popularmovies.stage1.data.api.MovieTrailerItem;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

/**
 * Created by User on 15.10.2016..
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int HEADER = 0, ITEMS = 1;
    private final List<Object> mObjects;
    private final Context mContext;

    public DetailAdapter(Context context, List<Object> objects) {
        this.mContext = context;
        this.mObjects = objects;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mDetailDate;
        TextView mDetailVoteAverage;
        TextView mDetailOverview;

        HeaderViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            mDetailDate = (TextView) itemView.findViewById(R.id.tv_detail_date);
            mDetailVoteAverage = (TextView) itemView.findViewById(R.id.tv_detail_vote_average);
            mDetailOverview = (TextView) itemView.findViewById(R.id.tv_detail_overview);
        }
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder {
        TextView mName;

        public ItemsViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_detail_trailer);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.activity_detail_items, parent, false);
                viewHolder = new HeaderViewHolder(v1);
                break;
            case ITEMS:
                View v2 = inflater.inflate(R.layout.activity_detail_items_trailers, parent, false);
                viewHolder = new ItemsViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                Picasso.with(mContext).load(BuildConfig.BASE_POSTER_PATH + ((Result) mObjects.get(position)).getPosterPath()).into(headerViewHolder.mImageView);
                headerViewHolder.mDetailDate.setText(((Result) mObjects.get(position)).getReleaseDate().split("-")[0]);
                headerViewHolder.mDetailOverview.setText(((Result) mObjects.get(position)).getOverview());
                headerViewHolder.mDetailVoteAverage.setText(String.format(Locale.getDefault(), "%.1f/10", ((Result) mObjects.get(position)).getVoteAverage()));
                break;
            case ITEMS:
                ItemsViewHolder itemsViewHolder = (ItemsViewHolder) holder;
                itemsViewHolder.mName.setText(((MovieTrailerItem)mObjects.get(position)).getName());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEMS;
        }
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

}
