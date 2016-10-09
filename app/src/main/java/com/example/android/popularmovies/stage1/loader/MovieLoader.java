package com.example.android.popularmovies.stage1.loader;

import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.android.popularmovies.stage1.BuildConfig;
import com.example.android.popularmovies.stage1.data.DataManager;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;

import retrofit2.Response;


/**
 * Created by User on 8.10.2016..
 */

public class MovieLoader extends Loader<MovieDbResult> {

    private static final String TAG = MovieLoader.class.getSimpleName();
    private MovieDbResult mMovieDbResult;
    private Bundle mBundle;

    public MovieLoader(Context context, @NonNull Bundle bundle) {
        super(context);
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        if (mMovieDbResult != null) {
            deliverResult(mMovieDbResult);
        }
        if (takeContentChanged() || mMovieDbResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {

        if ((mBundle != null) && (mBundle.getString(BuildConfig.API_ENDPOINT) != null)) {
            if (mBundle.getString(BuildConfig.API_ENDPOINT).equals(BuildConfig.POPULAR_END_POINT)) {
                DataManager.getInstance().getPopularMovies(new DataManager.NetworkTask() {
                    @Override
                    public void onError(Throwable e) {
                        deliverResult(null);
                    }

                    @Override
                    public void onNext(Response<MovieDbResult> movieDbResultResponse) {
                        deliverResult(movieDbResultResponse.body());
                    }
                });
            } else {
                DataManager.getInstance().getTopRatedMovies(new DataManager.NetworkTask() {
                    @Override
                    public void onError(Throwable e) {
                        deliverResult(null);
                    }

                    @Override
                    public void onNext(Response<MovieDbResult> movieDbResultResponse) {
                        deliverResult(movieDbResultResponse.body());
                    }
                });
            }
        }
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset: called");
    }

    @Override
    public void deliverResult(MovieDbResult data) {
        mMovieDbResult = data;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }
    }

}
