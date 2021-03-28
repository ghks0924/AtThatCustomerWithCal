package com.example.atthatcustomerwithcal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBOpenHelper_Menu extends SQLiteOpenHelper {

    private static final String CREATE_MENU_TABLE = "create table " + DBStructure_Menu.MENU_TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBStructure_Menu.OWNER_NAME+ " TEXT, " + DBStructure_Menu.MENUNAME+ " TEXT, " +DBStructure_Menu.MENUPRICE+ " TEXT)";

    private static final  String DROP_MENU_TABLE = "DROP TABLE IF EXISTS "+DBStructure_Menu.MENU_TABLE_NAME;

    public DBOpenHelper_Menu(@Nullable Context context) {
        super(context, DBStructure_Menu.DB_NAME, null, DBStructure_Menu.DB_VERSION);
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

    public void SaveMenu(String ownername, String menuname,int menuprice, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure_Menu.OWNER_NAME, ownername);
        contentValues.put(DBStructure_Menu.MENUNAME, menuname);
        contentValues.put(DBStructure_Menu.MENUPRICE, menuprice);

        database.insert(DBStructure_Menu.MENU_TABLE_NAME, null,contentValues);
    }

    public Cursor ReadMenus(String ownername, SQLiteDatabase database){
        String [] Projections = {DBStructure_Menu.OWNER_NAME,DBStructure_Menu.MENUNAME, DBStructure_Menu.MENUPRICE};
        String Selection = DBStructure_Menu.OWNER_NAME + "=?";
        String [] SelectionArgs = {ownername};

        return  database.query(DBStructure_Menu.MENU_TABLE_NAME,Projections,Selection,SelectionArgs, null,null,null);
    }

    public void deleteMenu(String name, int number, SQLiteDatabase database){
        String selection = DBStructure_Menu.MENUNAME+"=? and "
                +DBStructure_Menu.MENUPRICE+"=?";
        String[] selectionArg = {name, String.valueOf(number)};
        database.delete(DBStructure_Menu.MENU_TABLE_NAME,selection,selectionArg);
    }
}
