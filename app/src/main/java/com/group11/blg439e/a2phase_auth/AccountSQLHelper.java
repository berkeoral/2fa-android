package com.group11.blg439e.a2phase_auth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountSQLHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + AccountContract.Account.TABLE_NAME + " (" +
                    AccountContract.Account._ID + " INTEGER PRIMARY KEY," +
                    AccountContract.Account.COLUMN_NAME_ID + " TEXT," +
                    AccountContract.Account.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AccountContract.Account.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Account.db";

    public AccountSQLHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
