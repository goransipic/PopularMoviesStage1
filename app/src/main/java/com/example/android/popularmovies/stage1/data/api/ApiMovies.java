package com.example.android.popularmovies.stage1.data.api;

import com.example.android.popularmovies.stage1.BuildConfig;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by User on 8.10.2016..
 */

public interface ApiMovies {

    @GET(BuildConfig.POPULAR_END_POINT)
    Observable<Response<MovieDbResult>> getPopularMovies(@Query("api_key") String apiKey);

    @GET(BuildConfig.TOP_RATED_ENDPOINT)
    Observable<Response<MovieDbResult>> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET(BuildConfig.MOVIE_TRAILERS_ENDPOINT)
    Observable<Response<MovieTrailers>> getMovieTrailers(@Path("id") Integer id, @Query("api_key") String apiKey);
}
