package com.example.atthatcustomerwithcal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBOpenHelper_Customer extends SQLiteOpenHelper {

    private static final String CREATE_CUSTOMER_TABLE = "create table " + DBStructure_Customer.CUSTOMER_TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBStructure_Customer.OWNER_NAME+ " TEXT, " +DBStructure_Customer.CUSTOMER_NAME+ " TEXT, " +DBStructure_Customer.NUMBER+ " TEXT, "+DBStructure_Customer.GRADE+ " TEXT, "+DBStructure_Customer.RECOMMEND+ " TEXT, "
            +DBStructure_Customer.POINT+ " TEXT, " +DBStructure_Customer.VISIT+ " TEXT, " +DBStructure_Customer.MEMO+ " TEXT, " + DBStructure_Customer.SAVEDATE+ " TEXT, " + DBStructure_Customer.NOSHOWCOUNT + " TEXT)";

    private static final  String DROP_CUSTOMER_TABLE = "DROP TABLE IF EXISTS "+DBStructure_Customer.CUSTOMER_TABLE_NAME;

    public DBOpenHelper_Customer(@Nullable Context context) {
        super(context, DBStructure_Customer.DB_NAME, null, DBStructure_Customer.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CUSTOMER_TABLE);
        onCreate(db);
    }

    public void SaveCustomer(String ownername,String customername, String number, String grade, String recommend, String point, String visit, String memo, String savedate, int noshowcount, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure_Customer.OWNER_NAME, ownername);
        contentValues.put(DBStructure_Customer.CUSTOMER_NAME, customername);
        contentValues.put(DBStructure_Customer.NUMBER,number);
        contentValues.put(DBStructure_Customer.GRADE,grade);
        contentValues.put(DBStructure_Customer.RECOMMEND,recommend);
        contentValues.put(DBStructure_Customer.POINT,point);
        contentValues.put(DBStructure_Customer.VISIT,visit);
        contentValues.put(DBStructure_Customer.MEMO,memo);
        contentValues.put(DBStructure_Customer.SAVEDATE,savedate);
        contentValues.put(DBStructure_Customer.NOSHOWCOUNT,noshowcount);
        database.insert(DBStructure_Customer.CUSTOMER_TABLE_NAME, null,contentValues);
    }

    public Cursor ReadCustomers(String ownername, SQLiteDatabase database){
        String [] Projections = {DBStructure_Customer.OWNER_NAME,DBStructure_Customer.CUSTOMER_NAME, DBStructure_Customer.NUMBER, DBStructure_Customer.GRADE, DBStructure_Customer.RECOMMEND,DBStructure_Customer.POINT,DBStructure_Customer.VISIT,DBStructure_Customer.MEMO
                ,DBStructure_Customer.SAVEDATE,DBStructure_Customer.NOSHOWCOUNT};
        String Selection = DBStructure_Customer.OWNER_NAME + "=?";
        String [] SelectionArgs = {ownername};

        return  database.query(DBStructure_Customer.CUSTOMER_TABLE_NAME,Projections,Selection,SelectionArgs, null,null,null);
    }

    public Cursor ReadEachMember(String name, String number, SQLiteDatabase database){
        String [] Projections = {DBStructure_Customer.OWNER_NAME,DBStructure_Customer.CUSTOMER_NAME, DBStructure_Customer.NUMBER, DBStructure_Customer.GRADE, DBStructure_Customer.RECOMMEND,DBStructure_Customer.POINT,DBStructure_Customer.VISIT
                ,DBStructure_Customer.MEMO,DBStructure_Customer.SAVEDATE, DBStructure_Customer.NOSHOWCOUNT};
        String Selection = DBStructure_Customer.CUSTOMER_NAME + "=? and " + DBStructure_Customer.NUMBER + " =? ";
        String [] SelectionArgs = {name, number};

        return  database.query(DBStructure_Customer.CUSTOMER_TABLE_NAME,Projections,Selection,SelectionArgs, null,null,null);
    }

    public void deleteCustomer(String name, String number, SQLiteDatabase database){
        String selection = DBStructure_Customer.CUSTOMER_NAME+"=? and "
                +DBStructure_Customer.NUMBER+"=?";
        String[] selectionArg = {name,number};
        database.delete(DBStructure_Customer.CUSTOMER_TABLE_NAME,selection,selectionArg);
    }

    public void UpdateMemo(String name, String number, String memo, SQLiteDatabase database){
        ContentValues values = new ContentValues();
        values.put(DBStructure_Customer.MEMO, memo);


        String selection = DBStructure_Customer.CUSTOMER_NAME+"=? and "
                +DBStructure_Customer.NUMBER+"=?";

        String[] selectionArgs = {name, number};

        int count = database.update(DBStructure_Customer.CUSTOMER_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if(count > 0){
            Log.d("price", "Count : " + count);
        }
    }

    public void updateNoShow(String name, String number, int noShow, SQLiteDatabase database){
        ContentValues values = new ContentValues();
        values.put(DBStructure_Customer.NOSHOWCOUNT, noShow);

        String selection = DBStructure_Customer.CUSTOMER_NAME+"=? and "
                +DBStructure_Customer.NUMBER+"=?";

        String[] selectionArgs = {name, number};

        int count = database.update(DBStructure_Customer.CUSTOMER_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if(count > 0){
            Log.d("price", "Count : " + count);
        }
    }

    public void UpdateCustomerData (String beforeName, String beforeNumber, String updateName, String updateNumber, String grade, String point, SQLiteDatabase database){
        ContentValues values = new ContentValues();
        values.put(DBStructure_Customer.CUSTOMER_NAME, updateName);
        values.put(DBStructure_Customer.NUMBER, updateNumber);
        values.put(DBStructure_Customer.GRADE, grade);
        values.put(DBStructure_Customer.POINT, point);


        String selection = DBStructure_Customer.CUSTOMER_NAME+"=? and "
                +DBStructure_Customer.NUMBER+"=?";

        String[] selectionArgs = {beforeName, beforeNumber};

        int count = database.update(DBStructure_Customer.CUSTOMER_TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if(count > 0){
            Log.d("doOrNot", "customerCount : " + count);
        }
    }
}
