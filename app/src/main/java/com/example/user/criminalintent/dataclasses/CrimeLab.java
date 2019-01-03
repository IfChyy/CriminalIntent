package com.example.user.criminalintent.dataclasses;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.user.criminalintent.database.CrimeBaseHelper;
import com.example.user.criminalintent.database.CrimeCursorWrapper;
import com.example.user.criminalintent.database.CrimeSQLliteTable;
import com.example.user.criminalintent.database.CrimeSQLliteTable.CrimeTable.Columns;

import java.io.File;
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

        //search for the crime in DB by id using cursor and forming a query getting crimes by crimeID
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

        //get the values for the Crime and insert it in the dataBase
        ContentValues values = getContentValues(c);
        dataBase.insert(CrimeSQLliteTable.CrimeTable.NAME, null, values);
    }
    //delete a crime with UUID = equal to crime id
    public void deleteCrime(Crime c) {
        dataBase.delete(CrimeSQLliteTable.CrimeTable.NAME, Columns.UUID + " == \"" + c.getId() + "\"",null);
    }

    //add rows of ifnormation  about a crime to the databse columns
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(Columns.UUID, crime.getId().toString());
        values.put(Columns.TITLE, crime.getTitle());
        values.put(Columns.DATE, crime.getDate().getTime());
        values.put(Columns.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(Columns.SUSPECT, crime.getSuspectName());
        values.put(Columns.SUSPECT_NUMBER, crime.getSuspectNumber());

        return values;
    }

    //method to update the infromation in the database
    // use "?" for adding the new id and preventing sql injection atack
    //to treat everything as a string
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        dataBase.update(CrimeSQLliteTable.CrimeTable.NAME, values,
                Columns.UUID + " = ?", new String[]{uuidString});
    }

    //do a query in the database to get tha values
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

    //get all crimes from DB using where clause null (*) equal to SELECT *
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

    //add a photo and saev it to external directory on the phone
    public File getPhotoFile(Crime crime, Activity activity) {
        //get the external files directitry of the app
       File externalFilesDir = activity
             .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //if null return null
        if(externalFilesDir == null){
            return null;
        }
        //else add the photo to the dir
        return new File(externalFilesDir, crime.getPhotoFilename());
    }
}
