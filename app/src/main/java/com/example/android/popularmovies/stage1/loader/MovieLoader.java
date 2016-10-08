package com.example.android.popularmovies.stage1.loader;

import android.content.Context;
import android.graphics.Movie;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.stage1.data.DataManager;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;

import retrofit2.Response;


/**
 * Created by User on 8.10.2016..
 */

public class MovieLoader extends Loader<MovieDbResult> {

    private static final String TAG = MovieLoader.class.getSimpleName();
    private MovieDbResult mMovieDbResult;

    public MovieLoader(Context context) {
        super(context);
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
        DataManager.getInstance().getPopularMovies(new DataManager.NetworkTask() {
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(Response<MovieDbResult> movieDbResultResponse) {
                deliverResult(movieDbResultResponse.body());
            }
        });
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
