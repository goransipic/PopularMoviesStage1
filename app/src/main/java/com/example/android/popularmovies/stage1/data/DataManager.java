package com.example.android.popularmovies.stage1.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.example.android.popularmovies.stage1.App;
import com.example.android.popularmovies.stage1.BuildConfig;
import com.example.android.popularmovies.stage1.data.api.ApiMovies;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;
import com.example.android.popularmovies.stage1.data.api.MovieTrailerItem;
import com.example.android.popularmovies.stage1.data.api.MovieTrailers;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.data.api.Review;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
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

    public void writePopularMovieData(final Result result, final List<MovieTrailerItem> movieTrailerItems, Bitmap bitmap) {

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
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_MOVIE_ID,result.getId());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_ORIGINAL_TITLE, result.getOriginalTitle());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE, bitmap);
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_RELEASE_DATE, result.getReleaseDate());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_VOTE_AVERAGE, result.getVoteAverage());
                        contentValues.put(MovieFavoriteContract.Entry.COLUMN_NAME_OVERVIEW, result.getOverview());

                        long rowId = sqLiteDatabase.insert(MovieFavoriteContract.Entry.TABLE_NAME, null, contentValues);

                        contentValues.clear();
                        for (MovieTrailerItem movieTrailerItem : movieTrailerItems) {
                            contentValues.put(MovieFavoriteContract.MovieTrailers.COLUMN_NAME_VIDEO_LINK, movieTrailerItem.getKey());
                            contentValues.put(MovieFavoriteContract.MovieTrailers.COLUMN_NAME_TRAILER_NAME, movieTrailerItem.getName());
                            contentValues.put(MovieFavoriteContract.MovieTrailers.COLUMN_NAME_MOVIE_ENTRY, rowId);
                            sqLiteDatabase.insert(MovieFavoriteContract.MovieTrailers.TABLE_NAME, null, contentValues);
                            contentValues.clear();
                        }


                    }
                });


    }

    public Bitmap readImageFromDatabase(int id) {
        SQLiteDatabase sqLiteDatabase = mMovieFavoriteDBHelper.getReadableDatabase();
        Bitmap bitmap = null;
        Cursor cursor = sqLiteDatabase.query(MovieFavoriteContract.Entry.TABLE_NAME,
                new String[]{MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE},
                MovieFavoriteContract.Entry._ID + "= ?",
                new String[]{Integer.toString(id)}, null, null, null);

        while (cursor.moveToNext()) {
            byte[] image = cursor.getBlob(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE));
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        cursor.close();
        return bitmap;
    }

    public Observable<MovieTrailers> readPopularMoviesTrailers(final int entryId) {
        return Observable.defer(new Func0<Observable<MovieTrailers>>() {
            @Override
            public Observable<MovieTrailers> call() {
                SQLiteDatabase sqLiteDatabase = mMovieFavoriteDBHelper.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.query(MovieFavoriteContract.MovieTrailers.TABLE_NAME,
                        new String[]{MovieFavoriteContract.MovieTrailers.COLUMN_NAME_VIDEO_LINK,
                                MovieFavoriteContract.MovieTrailers.COLUMN_NAME_TRAILER_NAME},
                        MovieFavoriteContract.MovieTrailers.COLUMN_NAME_MOVIE_ENTRY + " = ?",
                        new String[]{Integer.toString(entryId)}, null, null, null);

                List<MovieTrailerItem> movieTrailerItems = new ArrayList<MovieTrailerItem>();

                while (cursor.moveToNext()) {
                    String link = cursor.getString(cursor.getColumnIndex(MovieFavoriteContract.MovieTrailers.COLUMN_NAME_VIDEO_LINK));
                    String name = cursor.getString(cursor.getColumnIndex(MovieFavoriteContract.MovieTrailers.COLUMN_NAME_TRAILER_NAME));
                    MovieTrailerItem movieTrailerItem = new MovieTrailerItem();
                    movieTrailerItem.setKey(link);
                    movieTrailerItem.setName(name);
                    movieTrailerItems.add(movieTrailerItem);
                }
                cursor.close();
                MovieTrailers movieTrailers = new MovieTrailers();
                movieTrailers.setResults(movieTrailerItems);

                return Observable.just(movieTrailers);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Pair<MovieDbResult, Pair<Integer,List<Bitmap>> >> readPopularMoviesData() {
        final List<Result> results = new ArrayList<>();
        final List<Bitmap> bitmaps = new ArrayList<>();

        return Observable.defer(new Func0<Observable<Pair<MovieDbResult,Pair<Integer,List<Bitmap>> >>>() {
            @Override
            public Observable<Pair<MovieDbResult,Pair<Integer, List<Bitmap>> >> call() {
                SQLiteDatabase sqLiteDatabase = mMovieFavoriteDBHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.query(
                        MovieFavoriteContract.Entry.TABLE_NAME,
                        new String[]{
                                MovieFavoriteContract.Entry._ID,
                                MovieFavoriteContract.Entry.COLUMN_NAME_MOVIE_ID,
                                MovieFavoriteContract.Entry.COLUMN_NAME_ORIGINAL_TITLE,
                                MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE,
                                MovieFavoriteContract.Entry.COLUMN_NAME_RELEASE_DATE,
                                MovieFavoriteContract.Entry.COLUMN_NAME_VOTE_AVERAGE,
                                MovieFavoriteContract.Entry.COLUMN_NAME_OVERVIEW}, null, null, null, null, null);

                Pair<Integer,List<Bitmap>> listPair = null;

                while (cursor.moveToNext()) {
                    Integer movieId = cursor.getInt(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_MOVIE_ID));
                    Integer tableId = cursor.getInt(cursor.getColumnIndex(MovieFavoriteContract.Entry._ID));
                    String originalTitle = cursor.getString(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_ORIGINAL_TITLE));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_RELEASE_DATE));
                    String voteAverage = cursor.getString(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_VOTE_AVERAGE));
                    String overview = cursor.getString(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_OVERVIEW));

                    Result result = new Result();
                    result.setId(movieId);
                    result.setOriginalTitle(originalTitle);
                    result.setReleaseDate(releaseDate);
                    result.setVoteAverage(Double.parseDouble(voteAverage));
                    result.setOverview(overview);

                    results.add(result);

                    byte[] image = cursor.getBlob(cursor.getColumnIndex(MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE));

                    bitmaps.add(BitmapFactory.decodeByteArray(image, 0, image.length));

                    listPair = new Pair<Integer, List<Bitmap>>(tableId,bitmaps);
                }

                cursor.close();

                MovieDbResult movieDbResult = new MovieDbResult();
                movieDbResult.setResults(results);

                return Observable.just(new Pair<>(movieDbResult, listPair));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
