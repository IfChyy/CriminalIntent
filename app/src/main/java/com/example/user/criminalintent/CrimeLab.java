package com.example.user.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(Ifchyy)
 * crimelab singleton class with crimes list to store the crimes
 */

public class CrimeLab {
    private static CrimeLab crimelab;
    private List<Crime> crimes;

    public static CrimeLab get(Context context) {
        if (crimelab == null) {
            crimelab = new CrimeLab(context);
        }
        return crimelab;
    }

    //create a private construcrtor with crime list
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
