package com.notifySeabank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    Context context;
    public static String DB_NAME = "data.db";
    public static int DB_VERSION = 1;
    private static SQLiteDatabase myDatabase;

    private static DatabaseManager databaseManager;

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }

        databaseManager.open();
        return databaseManager;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.disableWriteAheadLogging();
        super.onOpen(db);
    }

    private DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        boolean isDbExist = checkDatabase();
        if (!isDbExist) {
            createDatabase();
        }
    }

    private void createDatabase() {
        this.getReadableDatabase();
        try {
            copyDatabase();
        } catch (IOException e) {
            LogUtil.d("exceptionnn", e.getMessage());
        }
    }

    SQLiteDatabase getMyDatabase() {
        return myDatabase;
    }

    private void copyDatabase() throws IOException {
        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = context.getDatabasePath(DB_NAME).getPath();
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    private void open() {
        if (myDatabase == null || !myDatabase.isOpen()) {
            String myPath = context.getDatabasePath(DB_NAME).getPath();
            myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            myDatabase.close();
            databaseManager.close();
        } catch (Exception e) {
            LogUtil.d("exceptionnn", e.getMessage());
        }
    }

    private boolean checkDatabase() {
        boolean checkdb = false;
        try {
            String myPath = context.getDatabasePath(DB_NAME).getPath();
            File dbFile = new File(myPath);
            checkdb = dbFile.exists();
        } catch (SQLiteException e) {
            LogUtil.d("exceptionnn", e.getMessage());
        }
        return checkdb;
    }

    private DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            LogUtil.d("snvkdvksmvns", "upgrade");
            createDatabase();
        }
    }

    public void addNotification(NotificationInfo notificationInfo) {
        try {
            ContentValues values = new ContentValues();
            values.put("notification_id", notificationInfo.getId());
            values.put("package_name", notificationInfo.getPackageName());
            values.put("title", notificationInfo.getTitle());
            values.put("text", notificationInfo.getText());
            values.put("post_time", notificationInfo.getPostTime());
            values.put("post_date", Utils.getFormattedDateByTimeMillis(notificationInfo.getPostTime()));
            values.put("sender", notificationInfo.getConversationSender());
            values.put("large_icon", notificationInfo.getLargeIcon());

            DatabaseManager.getInstance(context).getMyDatabase().insert("Notification", null, values);
        } catch (Exception e) {
            LogUtil.d("exceptionnn", e.getMessage());
        }
    }

    public List<NotificationInfo> getAllNotification() {
        List<NotificationInfo> listNotification = new ArrayList<>();

        Cursor cursor = null;
        try {
            String sql = "select * from Notification order by post_time desc";
            cursor = DatabaseManager.getInstance(context).getMyDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                listNotification.add(getNotificationInfoFromCursor(cursor));
                LogUtil.d("sncanvsv", "saved time " + cursor.getString(6));
            }
            return listNotification;
        } catch (Exception e) {
            LogUtil.d("exceptionnn", e.getMessage());
            return listNotification;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public long getLastNotificationPostTime() {
        NotificationInfo notificationInfo = null;

        Cursor cursor = null;
        try {
            String sql = "select * from Notification order by post_time desc limit 1";
            cursor = DatabaseManager.getInstance(context).getMyDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                notificationInfo = getNotificationInfoFromCursor(cursor);
                LogUtil.d("sncanvsv", "saved time " + cursor.getString(6));
            }
            return notificationInfo.getPostTime();
        } catch (Exception e) {
            LogUtil.d("exceptionnn", e.getMessage());
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private NotificationInfo getNotificationInfoFromCursor(Cursor cursor) {
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setId(cursor.getInt(1));
        notificationInfo.setPackageName(cursor.getString(2));
        notificationInfo.setTitle(cursor.getString(3));
        notificationInfo.setText(cursor.getString(4));
        notificationInfo.setPostTime(cursor.getLong(5));
        notificationInfo.setPostDate(cursor.getInt(6));
        notificationInfo.setConversationSender(cursor.getString(7));
        notificationInfo.setLargeIcon(cursor.getBlob(8));
        return notificationInfo;
    }
}