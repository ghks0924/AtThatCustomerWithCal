package com.example.atthatcustomerwithcal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBOpenHelper_SmsTemplate extends SQLiteOpenHelper {

    private static final String CREATE_MENU_TABLE = "create table " + DBStructure_SmsTemplate.SMS_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBStructure_SmsTemplate.SMSTITLE + " TEXT, " + DBStructure_SmsTemplate.SMSCONTENT + " TEXT)";

    private static final String DROP_MENU_TABLE = "DROP TABLE IF EXISTS " + DBStructure_SmsTemplate.SMS_TABLE_NAME;

    public DBOpenHelper_SmsTemplate(@Nullable Context context) {
        super(context, DBStructure_SmsTemplate.DB_NAME, null, DBStructure_SmsTemplate.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MENU_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_MENU_TABLE);
        onCreate(db);
    }

    public Cursor ReadTemplates(SQLiteDatabase database) {
        String[] Projections = {DBStructure_SmsTemplate.SMSTITLE, DBStructure_SmsTemplate.SMSCONTENT};

        return database.query(DBStructure_SmsTemplate.SMS_TABLE_NAME
                , Projections, null, null, null, null, null);
    }

    public void SaveTemplate(String title, String content, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure_SmsTemplate.SMSTITLE, title);
        contentValues.put(DBStructure_SmsTemplate.SMSCONTENT, content);

        database.insert(DBStructure_SmsTemplate.SMS_TABLE_NAME, null, contentValues);
    }

    public void DeleteTemplate(String title, SQLiteDatabase database) {
        String selection = DBStructure_SmsTemplate.SMSTITLE + "=?";
        String[] selectionArg = {title};
        database.delete(DBStructure_SmsTemplate.SMS_TABLE_NAME, selection, selectionArg);
    }

    public void ReviseTemplate (String beforeTitle, String beforeContent, String updateTitle, String updateContent, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DBStructure_SmsTemplate.SMSTITLE, updateTitle);
        values.put(DBStructure_SmsTemplate.SMSCONTENT, updateContent);

        String selection = DBStructure_SmsTemplate.SMSTITLE + "=?";

        String[] selectionArgs = {beforeTitle};

        int count = database.update(DBStructure_SmsTemplate.SMS_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("price", "Count : " + count);
        }
    }

}
