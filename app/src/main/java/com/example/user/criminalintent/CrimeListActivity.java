package com.example.user.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.user.criminalintent.CrimeListFragment.Callbacks;
import com.example.user.criminalintent.dataclasses.Crime;
import com.example.user.criminalintent.utilities.SingleFragmentActivity;

/**
 * Created by Ivo Georgiev(IfChyy)
 * helper class used to retriev information from CrimeListFragment class
 * passing a single abstract method create fragment and
 * returning a new instance of CrimeListFragment(containing adapter and viewholder classes)
 * for inflating each individual crime list item and displaying it in Recycler view to the user
 */

public class CrimeListActivity extends SingleFragmentActivity implements Callbacks, CrimeFragment.Callbacks{

    //return new CrimeListFragment // inflates crimeListFragment when this activity is called
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    //get layout int for masterdetail layout or other
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    //method from callbacks interface to determine if to call CrimePagerActivity on phone
    //or put crime fragment in layout for tablet
    @Override
    public void onCrimeSelected(Crime crime) {
        //check if container layout exists
        if(findViewById(R.id.detail_fragment_container) == null){
            //if no fire the crimePagerActivity with intent
            Intent in = CrimePagerActicity.newIntent(this, crime.getId());
            startActivity(in);
        }else{
            //if yes get the fragment
            Fragment detail = CrimeFragment.newInstance(crime.getId());
        //and put the fragment onto the container layout
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, detail).commit();
        }
    }

    //updates the ui from callback interface in crimefragment class
    //used for tablets to update their list of items
    @Override
    public void onCrimeUpdate(Crime crme) {
        //update the crimeListFragment in its container after Crime data changed
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.UpdateUI();
    }
}
