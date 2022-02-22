package com.beidouapp.model.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}
