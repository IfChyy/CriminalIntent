package com.example.user.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfChyy)
 * crime Activity - placeholder for each individual crime's list item information
 * displayng a fragment with title and a text allowing the user to change the title
 * the details of the crime with button giving opption to change date
 * and a checkbox if issue solved or not
 * <p>
 * passes infromation for id from each individual list item clicked and then creating
 * the fragment and showing it for particular ID.
 */
//-----------------------REPLACED BY CrimePagerActivity
    //-------------KEPT FOR REFERENCE

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID = "com.example.user.criminalintent.crime_id";


    //creating an intent with extra to pass
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent in = new Intent(packageContext, CrimeActivity.class);
        in.putExtra(EXTRA_CRIME_ID, crimeId);
        return in;
    }

    //init fragment
    @Override
    protected Fragment createFragment() {
//        return new CrimeFragment();

        UUID crimeid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeid);
    }

}
