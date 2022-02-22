package com.beidouapp.model.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class id2name {
    /**
     * 将id转换成姓名，输入ID，输出姓名
     * @param writableDatabase
     * @param loginId
     * @param friendId
     * @return
     */
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

    /**
     * 逆转换
     * @param writableDatabase
     * @param loginId
     * @param friendName
     * @return
     */
    public static String reverse(SQLiteDatabase writableDatabase, String loginId, String friendName) {
        Cursor cursor = writableDatabase.query("friend",null,"selfID=? AND friend_name=?",
                new String[]{loginId,friendName},null,null, null);
        if (cursor.getCount()!=0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("friend_id"));
        } else {
            return friendName;
        }
    }

    /**
     * 写入数据库
     * @param writableDatabase
     * @param selfID == 自己的id
     * @param friend_index_id == 好友的数据库索引
     * @param friend_id == 好友的id
     * @param friend_name == 好友的姓名
     * @param flag == 是否是好友
     */
    public static void write2DB(SQLiteDatabase writableDatabase,String selfID,String friend_index_id,String friend_id,String friend_name,String flag) {
        Cursor cursor = writableDatabase.query("friend",null,"selfID=? AND friend_id=?",
                new String[]{selfID,friend_id},null,null, null);
        if (cursor.getCount() == 0) {
            Log.d("zznhy","in");
            ContentValues values = new ContentValues();
            values.put("selfID", selfID);
            values.put("friend_index_id", friend_index_id);
            values.put("friend_id", friend_id);
            values.put("friend_name", friend_name);
            values.put("flag", flag);
            writableDatabase.insert("friend", null, values);
        }
    }
}
