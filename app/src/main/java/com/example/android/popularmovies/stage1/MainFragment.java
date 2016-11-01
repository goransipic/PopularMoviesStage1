package com.example.android.popularmovies.stage1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.stage1.adapter.MovieDbAdapter;
import com.example.android.popularmovies.stage1.data.DataManager;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.loader.MovieLoader;

import java.util.List;

import rx.Subscriber;

/**
 * Created by User on 1.11.2016..
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieDbResult> {

    public static final String EXTRAS_FOR_DETAIL_ACTIVITY = "EXTRAS_FOR_DETAIL_ACTIVITY";
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRAS_FOR_OFFLINE = "EXTRAS_FOR_OFFLINE";
    public static final String EXTRAS_FOR_TABLE_ID = "EXTRAS_FOR_TABLE_ID";
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private Integer mTableID;
    private Bundle mSavedInstanceState;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mTextView = (TextView) view.findViewById(R.id.tv_main_text_error);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.main_pop_movies);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


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
        return new MovieLoader(this.getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<MovieDbResult> loader, MovieDbResult data) {
        mProgressBar.setVisibility(View.GONE);
        if (data == null) {
            //mTextView.setVisibility(View.VISIBLE);
            //mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        }
        populateRecyclerView(data, null, false);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "onLoadFinished: " + data);
    }

    @Override
    public void onLoaderReset(Loader<MovieDbResult> loader) {
        ((MovieDbAdapter) mRecyclerView.getAdapter()).resetList();
    }

    private void populateRecyclerView(MovieDbResult data, List<Bitmap> bitmaps, final boolean offline) {
        final MovieDbAdapter movieDbAdapter = new MovieDbAdapter(this.getActivity(), data, bitmaps, offline, new MovieDbAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result item) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRAS_FOR_DETAIL_ACTIVITY, item);

                if (offline) {
                    int i = DataManager.getInstance().getMovieID(item);
                    bundle.putBoolean(EXTRAS_FOR_OFFLINE, true);
                    bundle.putInt(EXTRAS_FOR_TABLE_ID, i);
                }
                if (((MainActivity) getActivity()).mTwoPane) {
                    MainFragment.this.getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.movie_detail_container, DetailFragment.newInstance(bundle))
                            .commit();
                } else {
                    MainFragment.this.getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack("")
                            .replace(R.id.main_content, DetailFragment.newInstance(bundle))
                            .commit();
                }
            }
        });
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(movieDbAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainFragment.this.getActivity(), 2));

        if (mSavedInstanceState == null && (((MainActivity) getActivity()).mTwoPane)) {

            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRAS_FOR_DETAIL_ACTIVITY, data.getResults().get(0));

            if (offline) {
                int i = DataManager.getInstance().getMovieID(data.getResults().get(0));
                bundle.putBoolean(EXTRAS_FOR_OFFLINE, true);
                bundle.putInt(EXTRAS_FOR_TABLE_ID, i);
            }
            MainFragment.this.getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, DetailFragment.newInstance(bundle))
                    .commitAllowingStateLoss();
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.popular_item:
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.main_pop_movies);
                Bundle bundlePopular = new Bundle();
                bundlePopular.putString(BuildConfig.API_ENDPOINT, BuildConfig.POPULAR_END_POINT);
                getLoaderManager().restartLoader(0, bundlePopular, this);
                return true;
            case R.id.top_rated:
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.main_top_movies);
                Bundle bundleTopRated = new Bundle();
                bundleTopRated.putString(BuildConfig.API_ENDPOINT, BuildConfig.TOP_RATED_ENDPOINT);
                getLoaderManager().restartLoader(0, bundleTopRated, this);
                return true;
            case R.id.popular_movies:
                DataManager.getInstance().readPopularMoviesData().subscribe(new Subscriber<Pair<MovieDbResult, List<Bitmap>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<MovieDbResult, List<Bitmap>> movieDbResultList) {
                        populateRecyclerView(movieDbResultList.first, movieDbResultList.second, true);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
