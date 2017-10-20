package cn.leo.fivechess.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leo on 2017/10/20.
 */

public class AIMemory extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "AIMemory";
    public static final String FILED_TREND = "trend";
    public static final String FILED_WIN = "win";

    public AIMemory(Context context) {
        super(context, "AI_MEMORY", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME +
                " (_id integer primary key autoincrement,"
                + FILED_WIN + " integer, "
                + FILED_TREND + " trend text(900) unique)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
