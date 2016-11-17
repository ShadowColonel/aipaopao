package com.bz.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bz.app.entity.RunningRecord;
import com.bz.app.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ThinkPad User on 2016/11/16.
 * database adapter 数据库增删改查
 */

public class DBAdapter  {
    public final static String DATABASE_NAME = "running_record";  //数据库名
    public final static int DATABASE_VERSION = 1;  //版本号
    public final static String RECORD_TABLE = "record"; //表名
    //建表语句
    public final static String RECORD_CREATE_SQL = "CREATE TABLE record (id Integer primary key autoincrement," +
            " start_point text," +
            " end_point text," +
            " path_line text," +
            " distance text," +
            " duration text," +
            " average_speed, text," +
            " date text);";

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RECORD_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    private Context mContext;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        mContext = context;
        dbHelper = new DatabaseHelper(mContext);
    }

    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public Cursor getAll() {
        return db.rawQuery("select * from record", null);
    }

    /**
     * 存入一条跑步轨迹
     */
    public long insertRecord(String startPoint, String endPoint, String pathLine, String distance,
                             String duration, String averageSpeed, String date) {

        ContentValues values = new ContentValues();
        values.put("start_point", startPoint);
        values.put("end_point", endPoint);
        values.put("path_line", pathLine);
        values.put("distance", distance);
        values.put("duration", duration);
        values.put("average_speed", averageSpeed);
        values.put("date", date);

        return db.insert(RECORD_TABLE, null, values);
    }

    /**
     * 查询所有轨迹
     * @return
     */
    public List<RunningRecord> queryAllRecord() {
        List<RunningRecord> allRecord = new ArrayList<>();
        Cursor cursor = db.query(RECORD_TABLE, new String[]{"id", "start_point", "end_point", "path_line"
        , "distance", "duration", "average_speed", "date"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            RunningRecord record = new RunningRecord();
            record.setId(cursor.getInt(cursor.getColumnIndex("id")));
            record.setStartPoint(Utils.parseLatLng(cursor.getString(cursor.getColumnIndex("start_point"))));
            record.setEndPoint(Utils.parseLatLng(cursor.getString(cursor.getColumnIndex("end_point"))));
            record.setPathLinePoints(Utils.parseLatLngs(cursor.getString(cursor.getColumnIndex("path_line"))));
            record.setDistance(cursor.getString(cursor.getColumnIndex("distance")));
            record.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
            record.setAverageSpeed(cursor.getString(cursor.getColumnIndex("average_speed")));
            record.setDate(cursor.getString(cursor.getColumnIndex("date")));
            allRecord.add(record);
        }
        Collections.reverse(allRecord);
        return allRecord;
    }
}
