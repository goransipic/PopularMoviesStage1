package com.example.android.popularmovies.stage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.stage1.data.api.Result;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private Result mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mResult = getIntent().getParcelableExtra(MainActivity.EXTRAS_FOR_DETAIL_ACTIVITY);

        TextView textView = (TextView) findViewById(R.id.tv_detail_name);
        textView.setText(mResult.getOriginalTitle());

        TextView textViewDate = (TextView) findViewById(R.id.tv_detail_date);
        textViewDate.setText(mResult.getReleaseDate().split("-")[0]);

        TextView textViewVoteAverage = (TextView) findViewById(R.id.tv_detail_vote_average);
        textViewVoteAverage.setText(String.format(Locale.getDefault(),"%.1f/10",mResult.getVoteAverage()));

        ImageView imageView = (ImageView) findViewById(R.id.thumbnail_image);
        Picasso.with(this).load(BuildConfig.BASE_POSTER_PATH + mResult.getPosterPath()).into(imageView);

        TextView textViewOverview = (TextView) findViewById(R.id.tv_detail_overview);
        textViewOverview.setText(mResult.getOverview());

    }
}
