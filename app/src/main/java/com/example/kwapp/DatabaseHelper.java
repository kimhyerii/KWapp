package com.example.kwapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "kwapp.db";
    private static final int DATABASE_VERSION = 4;
    private final String TAG = "DBHelper";

    public static final String SQL_TABLE_FOLDER = "folder";
    public static final String SQL_TABLE_MEMO = "memo";
    public static final String SQL_COL_FOLDERNAME = "foldername";
    public static final String SQL_COL_KEYWORD = "keyword";
    public static final String SQL_COL_CONTENT = "content";
    public static final String SQL_COL_VISIBILITY = "visibility";

    private static final String SQL_CREATE_FOLDER
            = "CREATE TABLE " + SQL_TABLE_FOLDER + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + SQL_COL_FOLDERNAME + " TEXT UNIQUE);";
    private static final String SQL_CREATE_MEMO
            = "CREATE TABLE " + SQL_TABLE_MEMO + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + SQL_COL_KEYWORD + " TEXT UNIQUE, "+ SQL_COL_CONTENT +" TEXT, "+ SQL_COL_FOLDERNAME + " TEXT, "
        + SQL_COL_VISIBILITY + " INTEGER);";

    public DatabaseHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Database Helper onCreate called for creating Db table..."
            + db.getPath() + " Version " + db.getVersion());
        db.execSQL(SQL_CREATE_FOLDER);
        db.execSQL(SQL_CREATE_MEMO);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SQL_TABLE_FOLDER);
        db.execSQL("DROP TABLE IF EXISTS " + SQL_TABLE_MEMO);
        onCreate(db);
    }

/*
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
*/
}
