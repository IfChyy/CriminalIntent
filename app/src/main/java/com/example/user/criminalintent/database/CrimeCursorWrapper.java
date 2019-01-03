package com.example.user.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.user.criminalintent.dataclasses.Crime;
import com.example.user.criminalintent.database.CrimeSQLliteTable.CrimeTable.Columns;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfCHyy)
 *
 * CrimeCursorWrapper class uses cursor the get the information from the database to our fields
 *
 * with getCrime method we easily get all our information from the database
 * and create a new Crime object with that particuluar data
 * which we return to the appropriete caller of the method
 */

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    //get infromation about crime
    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(Columns.UUID));
        String title = getString(getColumnIndex(Columns.TITLE));
        long date = getLong(getColumnIndex(Columns.DATE));
        int issolved = getInt(getColumnIndex(Columns.SOLVED));
        String suspect = getString(getColumnIndex(Columns.SUSPECT));
        String suspect_number = getString(getColumnIndex(Columns.SUSPECT_NUMBER));


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(issolved != 0);
        crime.setSuspectName(suspect);
        crime.setSuspectNumber(suspect_number);

        return crime;
    }
}
