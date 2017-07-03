package com.marcondes.app.alltasty.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "discosDB";
    public static final int DB_VERSION = 1;

    public RecipeDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ RecipeContract.TABLE_RECIPE +" (" +
                        RecipeContract._ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                        RecipeContract.COL_LABEL +" TEXT NOT NULL UNIQUE," +
                        RecipeContract.COL_IMAGE +" TEXT, " +
                        RecipeContract.COL_RECIPE +" TEXT) " );

        db.execSQL("CREATE TABLE "+ RecipeContract.TABLE_INGREDIENTS +" (" +
                RecipeContract.COL_RECIPE_ID +" INTEGER, " +
                RecipeContract.COL_INGREDIENTS +" TEXT," +
                "FOREIGN KEY("+ RecipeContract.COL_RECIPE_ID + ") " +
                "REFERENCES "+ RecipeContract.TABLE_RECIPE +"("+ RecipeContract._ID + ") " +
                "ON DELETE CASCADE)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

