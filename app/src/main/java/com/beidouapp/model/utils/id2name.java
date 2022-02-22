package com.beidouapp.model.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class id2name {
    public static String transform(SQLiteDatabase writableDatabase, String loginId, String friendId) {
        Cursor cursor = writableDatabase.query("friend",null,"selfID=? AND friend_id=?",
                new String[]{loginId,friendId},null,null, null);
        if (cursor.getCount()!=0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("friend_name"));
        } else {
            return friendId;
        }
    }

    public static void write2DB(SQLiteDatabase writableDatabase,String selfID,String friend_id,String friend_name,String flag) {
        Cursor cursor = writableDatabase.query("friend",null,"selfID=? AND friend_id=?",
                new String[]{selfID,friend_id},null,null, null);
        if (cursor.getCount() == 0) {
            Log.d("zznhy","in");
            ContentValues values = new ContentValues();
            values.put("selfID", selfID);
            values.put("friend_id", friend_id);
            values.put("friend_name", friend_name);
            values.put("flag", flag);
            writableDatabase.insert("friend", null, values);
        }
    }
}
