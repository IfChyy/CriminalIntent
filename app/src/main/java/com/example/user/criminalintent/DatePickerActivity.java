package com.example.user.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.user.criminalintent.utilities.SingleFragmentActivity;

import java.util.Date;

/**
 * Created by Ivo Georgiev(IfChyy)
 * CHALLENGE FROM CHAPTER 12
 * CREATE DIALOG AS AN WHOLE PAGE EMBEDED IN A ACTIVITY
 */

public class DatePickerActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_DATE = "com.example.user.criminalintent.crime_date";

    @Override
    protected Fragment createFragment() {
        Date crimeDate = (Date) getIntent().getSerializableExtra(EXTRA_CRIME_DATE);
        return DatePickerFragment.newInstance(crimeDate);
    }

    //get the crime id for the item to display and open crimePagerActivity instead of CrimeActivity
    public static Intent newIntent(Context packageContext, Date crimeDate) {
        Intent in = new Intent(packageContext, DatePickerActivity.class);
        in.putExtra(EXTRA_CRIME_DATE, crimeDate);
        return in;
    }
}
