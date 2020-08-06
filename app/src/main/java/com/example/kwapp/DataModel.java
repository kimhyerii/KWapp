package com.example.kwapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DataModel {
    private final String TAG = "DataModel";
    private String currentFolder;
    private ArrayList<Data> memolist;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    DataModel(Context context, String name){
        currentFolder = name;
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        memolist = new ArrayList<>();
    }

    ArrayList getMemolist(){
        return memolist;
    }

    void setMemolist(){
        String[] projection = {
                dbHelper.SQL_COL_KEYWORD,
                dbHelper.SQL_COL_CONTENT,
                dbHelper.SQL_COL_VISIBILITY
        };
        String selection = dbHelper.SQL_COL_FOLDERNAME + " = ?";
        String [] selectionArgs = {currentFolder};
        Cursor cursor = db.query(dbHelper.SQL_TABLE_MEMO, projection, selection, selectionArgs,
                null, null, null);

        memolist.clear();

        while(cursor.moveToNext()) {
            int index_kw = cursor.getColumnIndexOrThrow(dbHelper.SQL_COL_KEYWORD);
            String kw = cursor.getString(index_kw);

            int index_ct = cursor.getColumnIndexOrThrow(dbHelper.SQL_COL_CONTENT);
            String ct = cursor.getString(index_ct);

            int index_visible = cursor.getColumnIndexOrThrow(dbHelper.SQL_COL_VISIBILITY);
            boolean visible;
            if(cursor.getInt(index_visible) == 1){
                visible = true;
            }
            else{
                visible = false;
            }

            Data data = new Data(kw, ct, visible);
            memolist.add(data);
            Log.i(TAG,"[set memo] : " + kw);
        }
        cursor.close();
    }

    void addMemo(String KeyWord, String Content){
        ContentValues value = new ContentValues();

        value.put(dbHelper.SQL_COL_KEYWORD, KeyWord);
        value.put(dbHelper.SQL_COL_CONTENT, Content);
        value.put(dbHelper.SQL_COL_FOLDERNAME, currentFolder);
        value.put(dbHelper.SQL_COL_VISIBILITY, 1);
        long newRowID = db.insert(dbHelper.SQL_TABLE_MEMO, null, value);
        Log.i(TAG, "INSERT new memo : " + KeyWord);

        if(newRowID == -1){
            Log.i(TAG,"[insert error] row id : " + newRowID);
        }
        else{
            Log.i(TAG,"row id : " + newRowID);
            Data data = new Data(KeyWord, Content, true);
            memolist.add(data);
        }
    }

    void deleteMemo(String kw){
        String selection = dbHelper.SQL_COL_KEYWORD + "= ?";
        String [] selectionArgs = {kw};
        db.delete(dbHelper.SQL_TABLE_MEMO, selection, selectionArgs);

        for(int i = 0; i < memolist.size(); i++)
        {
            if(memolist.get(i).getKeyword() == kw){
                memolist.remove(i);
            }
        }
    }

    boolean changeVisibility(String kw){
        ContentValues value = new ContentValues();
        boolean vis = true;
        for(int i = 0; i < memolist.size(); i++)
        {
            if(memolist.get(i).getKeyword() == kw){
                vis = memolist.get(i).changeState();
                int bool;
                if(vis){bool = 1;}
                else{bool = 0;}

                value.put(dbHelper.SQL_COL_VISIBILITY, bool);
                String selection = dbHelper.SQL_COL_KEYWORD + " = ?";
                String [] selectionArgs = {kw};
                db.update(dbHelper.SQL_TABLE_MEMO, value, selection, selectionArgs);
            }
        }
        return vis;
    }

    boolean getVisibility(String kw){
        for(int i = 0; i < memolist.size(); i++)
        {
            if(memolist.get(i).getKeyword() == kw){
                return memolist.get(i).getIsVisible();
            }
        }
        return true;
    }
}

class Data{
    private String Keyword;
    private String Content;
    private boolean isVisible;

    Data(String kw, String ct, boolean visible){
        Keyword = kw;
        Content = ct;
        isVisible = visible;
    }
    String getKeyword(){
        return Keyword;
    }
    String getContent(){
        return Content;
    }

    boolean changeState(){
        if(this.isVisible){
            isVisible = false;
        }
        else{
            isVisible = true;
        }
        return isVisible;
    }

    boolean getIsVisible(){
        return isVisible;
    }
}