package com.example.user.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.user.criminalintent.CrimeListFragment.Callbacks;

/**
 * Created by Ivo Georgiev(IfChyy)
 * helper class used to retriev information from CrimeListFragment class
 * passign a single abstract method create fragment and
 * returning a new instance of CrimeListFragment(containing adapter and viewholder classes)
 * for inflating each individual crime list item and displaying it in Recycler view to the user
 */

public class CrimeListActivity extends SingleFragmentActivity implements Callbacks, CrimeFragment.Callbacks{


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    //method from callbacks interface to determine if to call crimepageractivity
    //or put crime fragment in layout for tablet
    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent in = CrimePagerActicity.newIntent(this, crime.getId());
            startActivity(in);
        }else{
            Fragment detail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, detail).commit();
        }
    }

    //updates the ui from callback interface in crimefragment class
    //used for tablets to update their list of items
    @Override
    public void onCrimeUpdate(Crime crme) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.UpdateUI();
    }
}
