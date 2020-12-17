package com.example.webim_sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes";
    private static final int DB_VERSION = 1;

    public NoteDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTELIST_TABLE = "CREATE TABLE NOTES (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TITLE TEXT NOT NULL, "
                + "DESCRIPTION TEXT NOT NULL, "
                + "TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"; //время добавления заметки

        db.execSQL(SQL_CREATE_NOTELIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NOTES");
        onCreate(db);
    }
}
