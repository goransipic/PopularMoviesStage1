package com.example.android.popularmovies.stage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 22.10.2016..
 */

public class MovieFavoriteDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 16;
    public static final String DATABASE_NAME = "MovieFavorite.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_BLOB = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES_FAVORITE =
            "CREATE TABLE " + MovieFavoriteContract.Entry.TABLE_NAME + " (" +
                    MovieFavoriteContract.Entry._ID + " INTEGER PRIMARY KEY," +
                    MovieFavoriteContract.Entry.COLUMN_NAME_MOVIE_ID + INTEGER_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_IMAGE + TEXT_BLOB + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.Entry.COLUMN_NAME_OVERVIEW + TEXT_TYPE + ")";

    private static final String SQL_CREATE_MOVIE_LINK =
            "CREATE TABLE " + MovieFavoriteContract.MovieTrailers.TABLE_NAME + " (" +
                    MovieFavoriteContract.MovieTrailers._ID + " INTEGER PRIMARY KEY," +
                    MovieFavoriteContract.MovieTrailers.COLUMN_NAME_MOVIE_ENTRY + INTEGER_TYPE + COMMA_SEP +
                    MovieFavoriteContract.MovieTrailers.COLUMN_NAME_VIDEO_LINK + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteContract.MovieTrailers.COLUMN_NAME_TRAILER_NAME + TEXT_TYPE + COMMA_SEP +
                    "FOREIGN KEY("+ MovieFavoriteContract.MovieTrailers.COLUMN_NAME_MOVIE_ENTRY +") REFERENCES entry(_id)"+
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieFavoriteContract.Entry.TABLE_NAME;

    private static final String SQL_DELETE_MOVIE_LINK =
            "DROP TABLE IF EXISTS " + MovieFavoriteContract.MovieTrailers.TABLE_NAME;

    public MovieFavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_FAVORITE);
        db.execSQL(SQL_CREATE_MOVIE_LINK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_MOVIE_LINK);

        onCreate(db);
    }
}
