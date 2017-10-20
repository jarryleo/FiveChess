package cn.leo.fivechess.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Leo on 2017/10/20.
 */

public class AIDao {

    private AIMemory mAiMemory;

    public AIDao(Context context) {
        mAiMemory = new AIMemory(context);
    }

    public long insert(String trend, int winColor) {
        SQLiteDatabase db = mAiMemory.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AIMemory.FILED_WIN, winColor);
        values.put(AIMemory.FILED_TREND, true);
        long insert = db.insert(AIMemory.TABLE_NAME, null, values);
        db.close();
        return insert;
    }

    public String search(String trend) {
        String result = null;
        SQLiteDatabase db = mAiMemory.getReadableDatabase();
        String sql = "select * from " + AIMemory.TABLE_NAME
                + " where " + AIMemory.FILED_TREND
                + " like %?%";
        Cursor cursor = db.rawQuery(sql, new String[]{trend});
        int winColumnIndex = cursor.getColumnIndex(AIMemory.FILED_WIN);
        int trandColumnIndex = cursor.getColumnIndex(AIMemory.FILED_TREND);

        if (cursor.moveToNext()) {
            result = cursor.getString(trandColumnIndex);
        }
        cursor.close();
        db.close();
        return result;
    }

}
