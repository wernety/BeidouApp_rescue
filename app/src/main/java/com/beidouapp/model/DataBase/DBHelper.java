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

    /**
     * 第一个用于聊天记录的表，在chatActivity里面进行加库
     * （个人觉得应该在收到消息的时候进行加库，而不是在chatActivity显示的时候进行加库）
     */
    private static final String T_USerInfo = "CREATE TABLE chat (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "toID text, "+
            "selfID text, "+
            "flag text, "+
            "contentChat text, "+
            "time text," +
            "message_type text" +
           // "ismineChat integer NOT NULL DEFAULT (0)" +
            ");";

    /**
     * groupID 群ID
     * flag 确定发送者 1自己发 138是好友发送
     */
    private static final String T_GroupUSerInfo = "CREATE TABLE chat_group (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "groupID text, "+
            "selfID text, "+
            "flag text, "+
            "contentChat text, "+
            "time text," +
            "message_type text" +
            ");";



    private static final String Friend_info = "CREATE TABLE friend (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "selfID text," +
            "friend_id text," +
            "friend_name text,"+
            "flag text" +
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
            db.execSQL(T_GroupUSerInfo);
            db.execSQL(Friend_info);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists chat");
        db.execSQL("drop table if exists chat_group");
        db.execSQL("drop table if exists friend");
        onCreate(db);
    }
}
