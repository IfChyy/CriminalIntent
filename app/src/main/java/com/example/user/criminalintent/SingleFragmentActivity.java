package com.example.user.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Singleton abstract class representing a fragment
 * after calling create fragment of the given type eg; CrimeFragment, CrimeListFragment
 *
 * when invoked oncreate method is called creating fragment manager and
 * setting the fragment container where our new fragment is going to be placed
 * if fragment is null it is created if not the old one is displayed/
 */

public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    private FragmentManager fm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //initialise fragment manager
        fm = getSupportFragmentManager();
        //init the fragment container
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        //if fragment is null init the fragment and add the layout
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
