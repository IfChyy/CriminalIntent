package com.example.user.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;

import com.example.user.criminalintent.Crime;
import com.example.user.criminalintent.database.CrimeSQLliteTable.CrimeTable.Columns;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfCHyy)
 * used for getting the data from the database for each particular column
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


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(issolved != 0);
        crime.setSuspectName(suspect);

        return crime;
    }
}
