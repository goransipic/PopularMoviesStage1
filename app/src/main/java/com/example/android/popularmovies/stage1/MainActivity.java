package com.example.android.popularmovies.stage1;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.stage1.adapter.MovieDbAdapter;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.loader.MovieLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieDbResult> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRAS_FOR_DETAIL_ACTIVITY = "EXTRAS_FOR_DETAIL_ACTIVITY";
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.main_pop_movies);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mTextView = (TextView) findViewById(R.id.tv_main_text_error);
        mRecyclerView.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(0, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.popular_item:
                getSupportActionBar().setTitle(R.string.main_pop_movies);
                Bundle bundlePopular = new Bundle();
                bundlePopular.putString(BuildConfig.API_ENDPOINT, BuildConfig.POPULAR_END_POINT);
                getSupportLoaderManager().restartLoader(0, bundlePopular, MainActivity.this);
                return true;
            case R.id.top_rated:
                getSupportActionBar().setTitle(R.string.main_top_movies);
                Bundle bundleTopRated = new Bundle();
                bundleTopRated.putString(BuildConfig.API_ENDPOINT, BuildConfig.TOP_RATED_ENDPOINT);
                getSupportLoaderManager().restartLoader(0, bundleTopRated, MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<MovieDbResult> onCreateLoader(int id, Bundle args) {
        mTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        if (args == null) {
            args = new Bundle();
            args.putString(BuildConfig.API_ENDPOINT, BuildConfig.POPULAR_END_POINT);
        }
        return new MovieLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<MovieDbResult> loader, final MovieDbResult data) {
        mProgressBar.setVisibility(View.GONE);
        if (data == null){
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        }



        MovieDbAdapter movieDbAdapter = new MovieDbAdapter(MainActivity.this, data, new MovieDbAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result item) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra(EXTRAS_FOR_DETAIL_ACTIVITY,item);

                MainActivity.this.startActivity(intent);
            }
        });
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(movieDbAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onLoadFinished: " + data);
    }

    @Override
    public void onLoaderReset(Loader<MovieDbResult> loader) {

        ((MovieDbAdapter)mRecyclerView.getAdapter()).resetList();

    }
}
