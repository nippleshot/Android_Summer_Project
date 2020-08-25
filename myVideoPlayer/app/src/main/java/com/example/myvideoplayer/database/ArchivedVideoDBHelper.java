package com.example.myvideoplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.myvideoplayer.database.ArchivedVideoContract.SQL_CREATE_ENTRY;

public class ArchivedVideoDBHelper extends SQLiteOpenHelper {


    public ArchivedVideoDBHelper(@Nullable Context context) {
        super(context, "ArchivedVideoList.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
        onCreate(sqLiteDatabase);
        if (oldVersion == 1 && newVersion == 2) {
            sqLiteDatabase.execSQL("");
        }
    }
}
