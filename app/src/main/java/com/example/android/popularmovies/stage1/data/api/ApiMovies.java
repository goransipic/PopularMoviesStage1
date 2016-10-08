package com.example.android.popularmovies.stage1.data.api;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by User on 8.10.2016..
 */

public interface ApiMovies {

    @GET("popular")
    Observable<Response<MovieDbResult>> getPopularMovies(@Query("api_key") String apiKey);

}
