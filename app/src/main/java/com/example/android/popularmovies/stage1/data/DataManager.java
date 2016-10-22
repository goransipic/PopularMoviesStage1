package com.example.android.popularmovies.stage1.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.example.android.popularmovies.stage1.App;
import com.example.android.popularmovies.stage1.BuildConfig;
import com.example.android.popularmovies.stage1.data.api.ApiMovies;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;
import com.example.android.popularmovies.stage1.data.api.MovieTrailers;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.data.api.Review;

import java.io.ByteArrayOutputStream;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by User on 8.10.2016..
 */

public class DataManager {

    private static DataManager sDATA_MANAGER;

    private final Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    private ApiMovies mApiMovies;

    private MovieFavoriteDBHelper mMovieFavoriteDBHelper = new MovieFavoriteDBHelper(App.getApplication());

    private DataManager() {

    }
    //  Database Operations

    public void writePopularmovieData(final Result result, Bitmap bitmap) {

        final ContentValues contentValues = new ContentValues();

        Observable.just(bitmap)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Bitmap, byte[]>() {
                    @Override
                    public byte[] call(Bitmap bitmap) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        return stream.toByteArray();
                    }
                })
                .subscribe(new Subscriber<byte[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(byte[] bitmap) {
                        SQLiteDatabase sqLiteDatabase = mMovieFavoriteDBHelper.getWritableDatabase();

                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_ORIGINAL_TITLE, result.getOriginalTitle());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE, bitmap);
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_RELEASE_DATE, result.getReleaseDate());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_VOTE_AVERAGE, result.getVoteAverage());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_OVERVIEW, result.getOverview());

                        sqLiteDatabase.insert(MovieFavoriteContract.Entry.TABLE_NAME, null, contentValues);
                    }
                });


    }

    //  Network operations
    public static DataManager getInstance() {
        if (sDATA_MANAGER == null) {
            synchronized (DataManager.class) {
                if (sDATA_MANAGER == null) {
                    sDATA_MANAGER = new DataManager();
                }
            }
        }
        return sDATA_MANAGER;
    }

    public void getPopularMovies(final NetworkTaskMovieDBResult networkTask) {
        if (mApiMovies == null) {
            mApiMovies = mRetrofit.create(ApiMovies.class);
        }
        mApiMovies.getPopularMovies(BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<MovieDbResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        networkTask.onError(e);
                    }

                    @Override
                    public void onNext(Response<MovieDbResult> movieDbResultResponse) {
                        networkTask.onNext(movieDbResultResponse);
                    }
                });
    }

    public void getTopRatedMovies(final NetworkTaskMovieDBResult networkTask) {
        if (mApiMovies == null) {
            mApiMovies = mRetrofit.create(ApiMovies.class);
        }
        mApiMovies.getTopRatedMovies(BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<MovieDbResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        networkTask.onError(e);
                    }

                    @Override
                    public void onNext(Response<MovieDbResult> movieDbResultResponse) {
                        networkTask.onNext(movieDbResultResponse);
                    }
                });
    }

    public void getMovieTrailers(Integer id, final NetworkTaskMoviesTrailers networkTask) {
        if (mApiMovies == null) {
            mApiMovies = mRetrofit.create(ApiMovies.class);
        }
        mApiMovies.getMovieTrailers(id, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<MovieTrailers>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        networkTask.onError(e);
                    }

                    @Override
                    public void onNext(Response<MovieTrailers> movieDbResultResponse) {
                        networkTask.onNext(movieDbResultResponse);
                    }
                });
    }

    public void getMovieReview(Integer id, final ReadReview networkTask) {
        if (mApiMovies == null) {
            mApiMovies = mRetrofit.create(ApiMovies.class);
        }
        mApiMovies.getMovieReview(id, BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Review>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        networkTask.onError(e);
                    }

                    @Override
                    public void onNext(Response<Review> movieDbResultResponse) {
                        networkTask.onInternet(movieDbResultResponse.body().getResults().get(0).getUrl());
                    }
                });
    }

    public interface NetworkTaskMovieDBResult {
        void onError(Throwable e);

        void onNext(Response<MovieDbResult> movieDbResultResponse);
    }

    public interface NetworkTaskMoviesTrailers {
        void onError(Throwable e);

        void onNext(Response<MovieTrailers> movieDbResultResponse);
    }

    public interface ReadReview {
        void onInternet(String url);

        void onError(Throwable e);
    }
}
