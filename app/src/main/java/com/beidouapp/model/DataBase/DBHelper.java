package com.beidouapp.model.DataBase;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 这个类是大表的方法
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String T_USerInfo = "CREATE TABLE chat (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "receive_id varchar(64), "+
            "send_id varchar(64), "+
            "contentChat varchar(255), "+
            "time varchar(64)" +
           // "ismineChat integer NOT NULL DEFAULT (0)" +
            ");";

    private static final String Friend_info = "CREATE TABLE friend (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "friend_id varchar(64)," +
            "friend_name varchar(64)"+
            ");";



    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(T_USerInfo);
            db.execSQL(Friend_info);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists chat");
        db.execSQL("drop table if exists friend");
        onCreate(db);
    }
}
