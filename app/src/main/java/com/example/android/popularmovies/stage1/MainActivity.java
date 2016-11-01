package com.example.android.popularmovies.stage1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.stage1.adapter.MovieDbAdapter;
import com.example.android.popularmovies.stage1.data.DataManager;
import com.example.android.popularmovies.stage1.data.MovieFavoriteContract;
import com.example.android.popularmovies.stage1.data.MovieFavoriteDBHelper;
import com.example.android.popularmovies.stage1.data.api.MovieDbResult;
import com.example.android.popularmovies.stage1.data.api.Result;
import com.example.android.popularmovies.stage1.loader.MovieLoader;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRAS_FOR_DETAIL_ACTIVITY = "EXTRAS_FOR_DETAIL_ACTIVITY";
    public static final String EXTRAS_FOR_OFFLINE = "EXTRAS_FOR_OFFLINE";
    public static final String EXTRAS_FOR_TABLE_ID = "EXTRAS_FOR_TABLE_ID";
    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        getSupportActionBar().setTitle(R.string.main_pop_movies);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, new MainFragment())
                        .commit();
            }

        } else {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, new MainFragment())
                        .commit();
            }
            mTwoPane = false;
        }

    }

}
