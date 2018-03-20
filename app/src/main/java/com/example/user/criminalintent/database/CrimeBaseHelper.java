package com.example.user.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.criminalintent.Crime;
import com.example.user.criminalintent.database.CrimeSQLliteTable.CrimeTable;
import com.example.user.criminalintent.database.CrimeSQLliteTable.CrimeTable.Columns;

/**
 * Created by Ivo Georgiev (IfChyy)
 * <p>
 * CrimeBaseHelper class help differentiate SQLite verison, database name and fields
 * from the class holding the database methods
 *
 * here we easily create the verison, database name,  and query the fields of the database we want to be returned
 * in this situation we return all our fields
 *
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERISON = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    //init the database
    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERISON);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + CrimeTable.NAME + "("
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.UUID + ", "
                + Columns.TITLE + ", "
                + Columns.DATE + ", "
                + Columns.TIME + ", "
                + Columns.SOLVED + ", "
                + Columns.SUSPECT + ", "
                + Columns.SUSPECT_NUMBER + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
