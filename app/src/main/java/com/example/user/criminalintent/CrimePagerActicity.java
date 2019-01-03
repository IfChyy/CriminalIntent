package com.example.user.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.user.criminalintent.dataclasses.Crime;
import com.example.user.criminalintent.dataclasses.CrimeLab;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfChyy)
 * CrimePagerActivity - a view pager resposnsible for displaying each particular instance of
 * CrimeFragment and its information
 * using left and right swipe to display different CrimeFragmetns
 */

public class CrimePagerActicity extends AppCompatActivity  implements CrimeFragment.Callbacks{

    public static final String EXTRA_CRIME_ID = "com.example.user.criminalintent.crime_id";

    private ViewPager viewPager;
    private List<Crime> crimes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        viewPager = findViewById(R.id.activity_crime_pager_view_pager);
        //init arraylist of crimes
        crimes = CrimeLab.get(this).getCrimes();

        //get the particular id of a crime to display in the view pager
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        //set view pager adapter to display crimes
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                //gets the crime at given position and uses CrimeFragments method to get bundler args
                //to display each crime
                Crime crime = crimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });
        //check if crime clicked in list. if it is display current crime not first one
        for(int i = 0; i < crimes.size();i++){
            if(crimes.get(i).getId().equals(crimeId)){
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    //on update crime method to define functionality on which crime to update
    @Override
    public void onCrimeUpdate(Crime crme) {

    }


    //get the crime id for the item to display and open crimePagerActivity instead of CrimeActivity
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent in = new Intent(packageContext, CrimePagerActicity.class);
        in.putExtra(EXTRA_CRIME_ID, crimeId);
        return in;
    }




}
