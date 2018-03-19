package com.example.user.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.criminalintent.database.CrimeBaseHelper;
import com.example.user.criminalintent.database.CrimeCursorWrapper;
import com.example.user.criminalintent.database.CrimeSQLliteTable;
import com.example.user.criminalintent.database.CrimeSQLliteTable.CrimeTable.Columns;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(Ifchyy)
 * CrimeLab is a class holding all the crimes in one list (Recycler View)
 * after creation of a CrimeLab it creates a hundred crimes
 * and initilisez every second one solved true parameter
 * <p>
 * added function to add crime
 */

public class CrimeLab {
    private static CrimeLab crimelab;
    // private List<Crime> crimes;
    //variables to init the database
    private Context context;
    private SQLiteDatabase dataBase;

    //get the instance of Crimelab - if already initialized return crimelab else create new
    public static CrimeLab get(Context context) {
        if (crimelab == null) {
            crimelab = new CrimeLab(context);
        }
        return crimelab;
    }

    //create a construcrtor and populate crime list
    CrimeLab(Context context) {
        //init the context and database
        context = context.getApplicationContext();
        //if database missing create one, if present check version and update if necesary
        dataBase = new CrimeBaseHelper(context).getWritableDatabase();

        // crimes = new ArrayList<>();

        //removed in chapter 13 where you add your own crimes
  /*      for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle(("Crime #" + i));
            crime.setSolved(i % 2 == 0);
            crimes.add(crime);
        }*/

    }

  /*  //get all crimes list
    public List<Crime> getCrimes() {
        //return crimes;
        return new ArrayList<>();
    }*/

    //get the crime with current id if present
    public Crime getCrime(UUID id) {
     /*   for (Crime crime : crimes) {
            if (crime.getId().equals(id)) {
                return crime;


            }
        }*/

        //search for the crime in DB by id using cursor and forming a query
        CrimeCursorWrapper cursor = queryCrimes(Columns.UUID + " = ?",
                new String[]{id.toString()});

        try {
            // if cursor is empty == DB is empty return
            if (cursor.getCount() == 0) {
                return null;
            }
            //sle get the first item and return the item
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            //close the cursro
            cursor.close();
        }

    }

    //add a crime to the list
    public void addCrime(Crime c) {
        //  crimes.add(c);

        //add crime in the database
        ContentValues values = getContentValues(c);
        dataBase.insert(CrimeSQLliteTable.CrimeTable.NAME, null, values);
    }

    public void deleteCrime(Crime c) {
        ContentValues values = getContentValues(c);
        dataBase.delete(CrimeSQLliteTable.CrimeTable.NAME, Columns.UUID + " == \"" + c.getId() + "\"",null);
    }

    //add rows of ifnormation to the databse columns
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(Columns.UUID, crime.getId().toString());
        values.put(Columns.TITLE, crime.getTitle());
        values.put(Columns.DATE, crime.getDate().getTime());
        values.put(Columns.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(Columns.SUSPECT, crime.getSuspectName());

        return values;
    }

    //method to update the infromation in the database
    // use "?" for adding the new id and preventing sql injection atack
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        dataBase.update(CrimeSQLliteTable.CrimeTable.NAME, values,
                Columns.UUID + " = ?", new String[]{uuidString});
    }

    //doa query in the database to get tha values
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = dataBase.query(
                CrimeSQLliteTable.CrimeTable.NAME,
                null, //equal to select *
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }

    //get all crimes from DB
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }
}
