package com.example.user.criminalintent.utilities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.user.criminalintent.R;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Singleton abstract class representing a fragment
 * after calling create fragment of the given type eg; CrimeFragment, CrimeListFragment
 *
 * when invoked oncreate method is called creating fragment manager and
 * setting the fragment container where our new fragment is going to be placed
 * if fragment is null it is created if not the old one is displayed/
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    private FragmentManager fm;


    //subclases of SingleFragmentActivity can choose to overide this method and return different layout
    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

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
