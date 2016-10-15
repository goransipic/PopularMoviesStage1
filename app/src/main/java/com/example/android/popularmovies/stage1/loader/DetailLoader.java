package com.example.android.popularmovies.stage1.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.android.popularmovies.stage1.data.DataManager;
import com.example.android.popularmovies.stage1.data.api.MovieTrailers;

import retrofit2.Response;

/**
 * Created by User on 15.10.2016..
 */

public class DetailLoader extends Loader<MovieTrailers> {

    private static final String TAG = DetailLoader.class.getSimpleName();
    private MovieTrailers mMovieTrailers;
    private Integer mId;

    public DetailLoader(Context context, @NonNull Integer id) {
        super(context);
        mId = id;
    }

    @Override
    protected void onStartLoading() {
        if (mMovieTrailers != null) {
            deliverResult(mMovieTrailers);
        }
        if (takeContentChanged() || mMovieTrailers == null) {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {

        DataManager.getInstance().getMovieTrailers(mId, new DataManager.NetworkTaskMoviesTrailers() {
            @Override
            public void onError(Throwable e) {
                deliverResult(null);
            }

            @Override
            public void onNext(Response<MovieTrailers> movieDbResultResponse) {
                deliverResult(movieDbResultResponse.body());
            }
        });
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset: called");
    }

    @Override
    public void deliverResult(MovieTrailers data) {
        mMovieTrailers = data;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }
    }


}
