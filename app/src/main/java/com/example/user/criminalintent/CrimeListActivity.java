package com.example.user.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Ivo Georgiev(IfChyy)
 * helper class used to retriev information from CrimeListFragment class
 * passign a single abstract method create fragment and
 * returning a new instance of CrimeListFragment(containing adapter and viewholder classes)
 * for inflating each individual crime list item
 */

public class CrimeListActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
