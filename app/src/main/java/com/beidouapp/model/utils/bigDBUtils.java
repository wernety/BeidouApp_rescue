package com.beidouapp.model.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.beidouapp.model.DataBase.DBHelper;

public class bigDBUtils {
    public static SQLiteDatabase db(Context context)
    {
        return  new DBHelper(context, "bigDB", null, 1).getWritableDatabase();
    }
}
