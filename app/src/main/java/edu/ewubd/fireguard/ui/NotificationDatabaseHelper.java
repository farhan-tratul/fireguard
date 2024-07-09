package edu.ewubd.fireguard.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.ewubd.fireguard.ui.notifications.Notification;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIMESTAMP = "timestamp";


    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_MESSAGE + " TEXT, " +
                    COLUMN_TIMESTAMP + " TEXT" +
                    ");";

    public NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public void insertstatus(String Title, String Msg, String time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", Title);
        values.put("message", Msg);
        values.put("timestamp", time);
        db.insert("notifications", null, values);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }
    public void deleteOldRecords() {
        SQLiteDatabase db = getWritableDatabase();

        // Get the current date in the same format as your timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(Calendar.getInstance().getTime());

        // Delete records where the timestamp is before today
        String whereClause = COLUMN_TIMESTAMP + " < ?";
        String[] whereArgs = { today };

        int rowsDeleted = db.delete(TABLE_NOTIFICATIONS, whereClause, whereArgs);

        Log.d("DatabaseUtils", "Rows deleted: " + rowsDeleted);
    }
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Notification notification = new Notification(cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_MESSAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_TIMESTAMP)));

                notifications.add(notification);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return notifications;
    }

}
