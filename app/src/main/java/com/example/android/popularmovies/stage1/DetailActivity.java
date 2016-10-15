package com.example.android.popularmovies.stage1;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.stage1.adapter.DetailAdapter;
import com.example.android.popularmovies.stage1.data.api.MovieTrailerItem;
import com.example.android.popularmovies.stage1.data.api.MovieTrailers;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.loader.DetailLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<MovieTrailers> onCreateLoader(int id, Bundle args) {
        return new DetailLoader(DetailActivity.this, mResult.getId());
    }

    @Override
    public void onLoadFinished(Loader<MovieTrailers> loader, MovieTrailers data) {
        List<Object> objects = new ArrayList<>();
        objects.add(mResult);
        List<MovieTrailerItem> movieTrailerItems =  data.getResults();
        objects.addAll(movieTrailerItems);
        mRecyclerView.setAdapter(new DetailAdapter(this, objects));
    }

    @Override
    public void onLoaderReset(Loader<MovieTrailers> loader) {

    }
}
