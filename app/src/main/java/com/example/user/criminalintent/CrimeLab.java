package com.example.user.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(Ifchyy)
 * CrimeLab is a class holding all the crimes in one list (Recycler View)
 * after creation of a CrimeLab it creates a hundred crimes
 * and initilisez every second one solved true parameter
 */

public class CrimeLab {
    private static CrimeLab crimelab;
    private List<Crime> crimes;

    //get the instance of Crimelab - if already initialized return crimelab else create new
    public static CrimeLab get(Context context) {
        if (crimelab == null) {
            crimelab = new CrimeLab(context);
        }
        return crimelab;
    }

    //create a construcrtor and populate crime list
    CrimeLab(Context context) {

        crimes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle(("Crime #" + i));
            crime.setSolved(i % 2 == 0);
            crimes.add(crime);

        }
    }

    //get all crimes list
    public List<Crime> getCrimes() {
        return crimes;
    }

    //get the crime with current id if present
    public Crime getCrime(UUID id) {
        for (Crime crime : crimes) {
            if (crime.getId().equals(id)) {
                return crime;


            }
        }
        return null;
    }
}
