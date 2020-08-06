package com.example.kwapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FolderModel {
    private final String TAG = "FolderModel";
    private ArrayList<String> folderlist;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    FolderModel(Context context){
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        folderlist = new ArrayList<String>();
    }

    void setFolderlist() {
        Cursor cursor = db.rawQuery("select * from " + dbHelper.SQL_TABLE_FOLDER,null);
        folderlist.clear();
        while(cursor.moveToNext()) {
            int index_name = cursor.getColumnIndexOrThrow(dbHelper.SQL_COL_FOLDERNAME);
            String folder = cursor.getString(index_name);
            folderlist.add(folder);
            Log.i(TAG,"[set main] folder name  : " + folder);
        }
        cursor.close();
    }

    ArrayList getFolderlist(){
        return folderlist;
    }

    String getNewItem(){
        int index = folderlist.size() - 1;
        return folderlist.get(index);
    }

    void addFolder(String foldername){
        ContentValues value = new ContentValues();
        String Outer;

        value.put(dbHelper.SQL_COL_FOLDERNAME, foldername);
        long newRowID = db.insert(dbHelper.SQL_TABLE_FOLDER, null, value);
        Log.i(TAG, "INSERT new folder : " + foldername);

        if(newRowID == -1){
            Log.i(TAG,"[insert error] row id : " + newRowID);
        }
        else{ //no error
            Log.i(TAG,"row id : " + newRowID);
            folderlist.add(foldername);
       }
    }

    void deleteFolder(String foldername){
        String selection = dbHelper.SQL_COL_FOLDERNAME + "= ?";
        String [] selectionArgs = {foldername};
        db.delete(dbHelper.SQL_TABLE_FOLDER, selection, selectionArgs);

        for(int i = 0; i < folderlist.size(); i++)
        {
            if(folderlist.get(i) == foldername){
                folderlist.remove(i);
            }
        }
    }
}
