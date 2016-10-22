package com.example.android.popularmovies.stage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 22.10.2016..
 */

public class MovieFavoriteDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MovieFavorite.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String TEXT_BLOB = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieFavoriteContract.Entry.TABLE_NAME + " (" +
                    MovieFavoriteContract.Entry._ID + " INTEGER PRIMARY KEY," +
                    MovieFavoriteContract.Entry.COLUMN_NAME_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE + TEXT_BLOB + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_OVERVIEW + TEXT_TYPE + ")";

    public MovieFavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
