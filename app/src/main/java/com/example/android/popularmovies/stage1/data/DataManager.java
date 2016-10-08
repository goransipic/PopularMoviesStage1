package com.example.android.popularmovies.stage1.data;

import com.example.android.popularmovies.stage1.BuildConfig;
import com.example.android.popularmovies.stage1.data.api.ApiMovies;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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

    private DataManager() {

    }

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

    public void getPopularMovies(final NetworkTask networkTask) {
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

    public void getTopRatedMovies(final NetworkTask networkTask) {
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

   public interface NetworkTask {
        void onError(Throwable e);

        void onNext(Response<MovieDbResult> movieDbResultResponse);
    }
}
