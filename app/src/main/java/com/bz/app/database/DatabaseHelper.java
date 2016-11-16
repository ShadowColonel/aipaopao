package com.bz.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ThinkPad User on 2016/11/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "running_record";
    private final static int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //数据库创建的时候调用次方法

        /**
         * Integer
         * Text
         * Blob
         * null
         * REAL
         *
         * 无类型的——数据类型跟列的类型不一定要完全匹配
         *
         */

        String sql = "CREATE TABLE record (id Integer primary key autoincrement," +
                " start_point text," +
                " end_point text," +
                " path_line text," +
                " distance text," +
                " duration text," +
                " average_speed, text," +
                " date text);";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //在数据库升级的的时候会调用
    }
}
