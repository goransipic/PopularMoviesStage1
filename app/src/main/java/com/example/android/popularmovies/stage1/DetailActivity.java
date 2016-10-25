package com.example.android.popularmovies.stage1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.stage1.adapter.DetailAdapter;
import com.example.android.popularmovies.stage1.data.DataManager;
import com.example.android.popularmovies.stage1.data.api.MovieTrailerItem;
import com.example.android.popularmovies.stage1.data.api.MovieTrailers;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.loader.DetailLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieTrailers> {

    private Result mResult;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mResult = getIntent().getParcelableExtra(MainActivity.EXTRAS_FOR_DETAIL_ACTIVITY);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_detail);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView textView = (TextView) findViewById(R.id.tv_detail_name);
        textView.setText(mResult.getOriginalTitle());

        if (getIntent().getBooleanExtra(MainActivity.EXTRAS_FOR_OFFLINE, false)) {
            DataManager.getInstance().readPopularMoviesTrailers(getIntent().getIntExtra(MainActivity.EXTRAS_FOR_TABLE_ID,0)).subscribe(new Subscriber<MovieTrailers>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(MovieTrailers movieTrailers) {
                    populateRecyclerView(movieTrailers, true);
                }
            });
        } else {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<MovieTrailers> onCreateLoader(int id, Bundle args) {
        return new DetailLoader(DetailActivity.this, mResult.getId());
    }

    @Override
    public void onLoadFinished(Loader<MovieTrailers> loader, MovieTrailers data) {
        populateRecyclerView(data, false);
    }

    @Override
    public void onLoaderReset(Loader<MovieTrailers> loader) {

    }

    private void populateRecyclerView(MovieTrailers data, boolean offline) {
        List<Object> objects = new ArrayList<>();
        objects.add(mResult);
        final List<MovieTrailerItem> movieTrailerItems = data.getResults();
        objects.addAll(movieTrailerItems);
        mRecyclerView.setAdapter(new DetailAdapter(this, objects, offline,getIntent().getIntExtra(MainActivity.EXTRAS_FOR_TABLE_ID,0),new DetailAdapter.OnItemClicked() {
            @Override
            public void item(String key) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(BuildConfig.YOUTUBE_BASE_URL + key));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, R.string.install_app, Toast.LENGTH_LONG).show();
                }
            }
        }, new DetailAdapter.OnButtonClicked() {
            @Override
            public void doAction() {
                Picasso.with(DetailActivity.this).load(BuildConfig.BASE_POSTER_PATH + mResult.getPosterPath()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        DataManager.getInstance().writePopularMovieData(mResult, movieTrailerItems, bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }, new DetailAdapter.OnButtonClicked() {
            @Override
            public void doAction() {
                DataManager.getInstance().getMovieReview(mResult.getId(), new DataManager.ReadReview() {
                    @Override
                    public void onInternet(String url) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(DetailActivity.this, R.string.install_app, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this,"There is not any Review",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }));
    }
}
