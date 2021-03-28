package com.example.atthatcustomerwithcal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_EVENTS_TABLE = "create table " + DBStructure.EVENT_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBStructure.EVENT + " TEXT, " + DBStructure.TIME + " TEXT, " + DBStructure.DATE + " TEXT, " + DBStructure.MONTH + " TEXT, "
            + DBStructure.YEAR + " TEXT, " + DBStructure.RETOUCH + " TEXT, " + DBStructure.PRICE + " TEXT, " + DBStructure.COMPLETE + " TEXT, " + DBStructure.SHORTMEMO
            + " TEXT, " + DBStructure.NUMBER + " TEXT, " + DBStructure.NOSHOW + " TEXT, " + DBStructure.MENU + " TEXT, " +
            DBStructure.CARDCASH + " TEXT, " + DBStructure.MATERIALMEMO + " TEXT, " + DBStructure.CONTENTMEMO + " TEXT, " + DBStructure.CRTDATE +
            " TEXT)";

    private static final String DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS " + DBStructure.EVENT_TABLE_NAME;


    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStructure.DB_NAME, null, DBStructure.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        db.execSQL("ALTER TABLE "+DBStructure.EVENT_TABLE_NAME + " ADD COLUMN " + DBStructure.CARDCASH + " TEXT");
    }


    public void SaveEvent(String event, String time, String date, String month, String year, String retouch, int price, boolean complete,
                          String shortmemo, String number, boolean noshow, String menu, String cardcash, String materialmemo, String contentmemo,
                          String crtdate, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.EVENT, event);
        contentValues.put(DBStructure.TIME, time);
        contentValues.put(DBStructure.DATE, date);
        contentValues.put(DBStructure.MONTH, month);
        contentValues.put(DBStructure.YEAR, year);
        contentValues.put(DBStructure.RETOUCH, retouch);
        contentValues.put(DBStructure.PRICE, price);
        contentValues.put(DBStructure.COMPLETE, complete);
        contentValues.put(DBStructure.SHORTMEMO, shortmemo);
        contentValues.put(DBStructure.NUMBER, number);
        contentValues.put(DBStructure.NOSHOW, noshow);
        contentValues.put(DBStructure.MENU, menu);
        contentValues.put(DBStructure.CARDCASH, cardcash);
        contentValues.put(DBStructure.MATERIALMEMO, materialmemo);
        contentValues.put(DBStructure.CONTENTMEMO, contentmemo);
        contentValues.put(DBStructure.CRTDATE, crtdate);


        database.insert(DBStructure.EVENT_TABLE_NAME, null, contentValues);

    }

    public Cursor ReadEvents(String date, SQLiteDatabase database) {
        String[] Projections = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR
                , DBStructure.RETOUCH, DBStructure.PRICE, DBStructure.COMPLETE, DBStructure.SHORTMEMO
                , DBStructure.NUMBER, DBStructure.NOSHOW, DBStructure.MENU, DBStructure.CARDCASH,
                DBStructure.MATERIALMEMO, DBStructure.CONTENTMEMO, DBStructure.CRTDATE};
        String Selection = DBStructure.DATE + "=?";
        String[] SelectionArgs = {date};

        return database.query(DBStructure.EVENT_TABLE_NAME, Projections, Selection, SelectionArgs, null, null, null);
    }

    public Cursor ReadEvents_name(String name, String number, SQLiteDatabase database) {
        String[] Projections = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR
                , DBStructure.RETOUCH, DBStructure.PRICE, DBStructure.COMPLETE, DBStructure.SHORTMEMO
                , DBStructure.NUMBER, DBStructure.NOSHOW, DBStructure.MENU, DBStructure.CARDCASH
                , DBStructure.MATERIALMEMO, DBStructure.CONTENTMEMO, DBStructure.CRTDATE};
        String Selection = DBStructure.EVENT + "=? and "
                + DBStructure.NUMBER + "=?";
        String[] SelectionArgs = {name, number};

        return database.query(DBStructure.EVENT_TABLE_NAME, Projections, Selection, SelectionArgs, null, null, null);
    }


    public Cursor ReadEventsperMonth(String month, String year, SQLiteDatabase database) {
        String[] Projections = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR, DBStructure.RETOUCH
                , DBStructure.PRICE, DBStructure.COMPLETE, DBStructure.SHORTMEMO, DBStructure.NUMBER, DBStructure.NOSHOW, DBStructure.MENU
                , DBStructure.CARDCASH, DBStructure.MATERIALMEMO, DBStructure.CONTENTMEMO, DBStructure.CRTDATE};
        String Selection = DBStructure.MONTH + "=? and " + DBStructure.YEAR + "=?";
        String[] SelectionArgs = {month, year};
        return database.query(DBStructure.EVENT_TABLE_NAME, Projections, Selection, SelectionArgs, null, null, null);
    }

    public void deleteEvent(String event, String date, String time, SQLiteDatabase database) {
        String selection = DBStructure.EVENT + "=? and "
                + DBStructure.DATE + "=? and "
                + DBStructure.TIME + "=?";
        String[] selectionArg = {event, date, time};
        database.delete(DBStructure.EVENT_TABLE_NAME, selection, selectionArg);

    }

    public void updateEventForPrice(String event, String date, String time, int price, SQLiteDatabase database) {

        ContentValues values = new ContentValues();
        values.put(DBStructure.PRICE, price);

        String selection = DBStructure.EVENT + "=? and "
                + DBStructure.DATE + "=? and "
                + DBStructure.TIME + "=?";

        String[] selectionArgs = {event, date, time};

        int count = database.update(DBStructure.EVENT_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("price", "Count : " + count);
        }

    }

    public void updateEventForMenu(String event, String date, String time, String menu, SQLiteDatabase database) {

        ContentValues values = new ContentValues();
        values.put(DBStructure.MENU, menu);

        String selection = DBStructure.EVENT + "=? and "
                + DBStructure.DATE + "=? and "
                + DBStructure.TIME + "=?";

        String[] selectionArgs = {event, date, time};

        int count = database.update(DBStructure.EVENT_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("price", "Count : " + count);
        }

    }

    public void completeEvent(String event, String date, String time, String complete, String noshow, String cardCash
            , String materialMemo, String contentMemo, SQLiteDatabase database) {

        ContentValues values = new ContentValues();
        values.put(DBStructure.COMPLETE, complete);
        values.put(DBStructure.NOSHOW, noshow);
        values.put(DBStructure.CARDCASH, cardCash);
        values.put(DBStructure.MATERIALMEMO, materialMemo);
        values.put(DBStructure.CONTENTMEMO, contentMemo);

        String selection = DBStructure.EVENT + "=? and "
                + DBStructure.DATE + "=? and "
                + DBStructure.TIME + "=?";

        String[] selectionArgs = {event, date, time};

        int count = database.update(DBStructure.EVENT_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("complete", "Count : " + count);
        }

    }

    public void reviseEvent(String event, String date, String beforeTime, String updateTime, String memo, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.TIME, updateTime);
        values.put(DBStructure.SHORTMEMO, memo);

        String selection = DBStructure.EVENT + "=? and "
                + DBStructure.DATE + "=? and "
                + DBStructure.TIME + "=?";

        String[] selectionArgs = {event, date, beforeTime};

        int count = database.update(DBStructure.EVENT_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("price", "Count : " + count);
        }
    }

    public void UpdateEventData(String beforeName, String beforeNumber, String updateName, String updateNumber, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.EVENT, updateName);
        values.put(DBStructure.NUMBER, updateNumber);

        String selection = DBStructure.EVENT + "=? and "
                + DBStructure.NUMBER + "=?";

        String[] selectionArgs = {beforeName, beforeNumber};

        int count = database.update(DBStructure.EVENT_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("doOrNot", "eventCount : " + count);
        }
    }

    public void UpdateEventData2(String crtdate, String updateName, String updateNumber, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.EVENT, updateName);
        values.put(DBStructure.NUMBER, updateNumber);

        String selection = DBStructure.CRTDATE + "=?";

        String[] selectionArgs = {crtdate};

        int count = database.update(DBStructure.EVENT_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Log.d("doOrNot", "eventCount : " + count);
        }
    }
}
