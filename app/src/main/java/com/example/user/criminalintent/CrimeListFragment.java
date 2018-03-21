package com.example.user.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.security.auth.callback.Callback;


/**
 * Created by Ivo Georgiev(IfChyy)
 * Crime List Fragment
 * Displaying all crimes to the user with title, date and a check box indicating if solved or not
 */

public class CrimeListFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CRIME = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView crimeRecyclerView;
    private CrimeAdapter adapter;
    public static List<Crime> crimes;
    //show title item clicked position
    private String tempTitle;
    private int clickedItemPos;
    //check if subtitle set visible or not
    private boolean subtitleVisible = true;
    //if list of cirmes empty show info and add crime button
    private TextView emptyRecycler;
    private Button addCrime;

    //callback variable to get infromation for updating or creating a crime fragment into the container
    private Callbacks callbacks;

    //oncreate added to add the menu using setHasOptionsMenu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /// on create view creates the crimeListFragment with recycler view and updates its elemnte by
    //UpdateUI method
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        //recycler list view
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //textview showing no crimes detected and button to add a crime
        emptyRecycler = view.findViewById(R.id.empty_recycler_text_view);
        //add crime button if empty recycler
        addCrime = view.findViewById(R.id.empty_recycler_add_button);
        addCrime.setOnClickListener(this);
        //check if bundle saved, if not null set visibility state before returning to this fragment/rotating
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        UpdateUI();
        return view;
    }

    //override options menu to add + add button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        //if show subtitle cliecked recreate the options menu to store the title after rotating screen
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitel);

        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    //get menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if new crime button selected
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                //show the fragment holding each crime
               /* Intent in = CrimePagerActicity.newIntent(getActivity(), crime.getId());
                startActivity(in);*/

                //with callbacks for different devices we call method to check how to display the fragment
                //using callbacks to update the listfragment in tablet mode
                UpdateUI();
                callbacks.onCrimeSelected(crime);
                //udapte the view with items in the list
                updateSubtitle();
                return true;
            //toggle show subtitle clicked and change its boolean
            case R.id.menu_item_show_subtitle:
                subtitleVisible = !subtitleVisible;
                //invalidate the options menu show or hide subtitle
                getActivity().invalidateOptionsMenu();
                //update the subtitle
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //onclick method to add a new crime to the list of crmes
    @Override
    public void onClick(View view) {
        if (view.getId() == addCrime.getId()) {
            // if recycler view is empty and hsowing add button perform menu add item click on this button aswell
            ActionMenuItemView btn = getActivity().findViewById(R.id.menu_item_new_crime);
            btn.performClick();
        }
    }

    //get result extra from fragment child to fragmnet parent CHALLENGE method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {

            tempTitle = CrimeFragment.wasAnswerShown(data);
            Toast.makeText(getActivity(), tempTitle + "", Toast.LENGTH_SHORT).show();
        }
    }

    //save the instance to the bundle to know if sub visible after rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible);
    }

    //after getting back from each crime if clicked resume update the list of crimes
    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    //update each item when changed
    public void UpdateUI() {
        //get all crimes from the list
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimes = crimeLab.getCrimes();

        //check if adapter not existing
        if (adapter == null) {
            //yes then create the adapter and set its list of crimes
            adapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(adapter);
        } else {
            //update the crimes list and set it to the adapter after creating a new crime and query the DB
            adapter.setCrimes(crimes);
            adapter.notifyDataSetChanged();
            //update only one item from the recyrcler view
            //adapter.notifyItemChanged(clickedItemPos);


        }
        //update the subtitle text of list nubmers
        updateSubtitle();

        //CHALLENGE if recyrcler view is empty show text view and add button
        if (CrimeLab.get(getActivity()).getCrimes().size() < 1) {
            Toast.makeText(getActivity(), "EMPTY", Toast.LENGTH_SHORT).show();
            crimeRecyclerView.setVisibility(View.INVISIBLE);
            emptyRecycler.setVisibility(View.VISIBLE);
            addCrime.setVisibility(View.VISIBLE);
        } else {
            crimeRecyclerView.setVisibility(View.VISIBLE);
            emptyRecycler.setVisibility(View.INVISIBLE);
            addCrime.setVisibility(View.INVISIBLE);
        }
    }

    //update subtitle to show number of items in crimes list
    public void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        //if not visible equal to null
        if (!subtitleVisible) {
            subtitle = null;
        }

        //show a little title text(SUBTITLE) under the title in our title bar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //----------------------------INTERFACE CALLBACKS

    /**
     * used to give fragmentList methods to atach and detach crimes and select which crime
     * to apear.. depending on caller
     * with this interface we delaget functionality back to activty which is hosting the fragments
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    //------------------------------CRIME HOLDER SUBCLASS
    //Crime holder is a class holding each crime item in the recycler view
    //and setting its properties
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleTextView;
        private TextView dateTextView;
        private CheckBox solvedCheckBox;

        private Crime crime;

        //init each crime in the list text, date and checkbox
        public CrimeHolder(View itemView) {
            super(itemView);
            //set click listener on the whole list item not a particular view
            itemView.setOnClickListener(this);

            titleTextView = itemView.findViewById(R.id.crime_list_item_title_text_view);
            dateTextView = itemView.findViewById(R.id.crime_list_item_date_text_view);
            solvedCheckBox = itemView.findViewById(R.id.crime_list_item_solved_check_box);

        }

        //binding views to information from list of crimes
        public void bindCrime(Crime crimee) {
            //get crime
            crime = crimee;
            //set title text with crime title
            titleTextView.setText(crime.getTitle());
            //set date in appropriete format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
            dateTextView.setText(simpleDateFormat.format(crime.getDate()));
            //set if solved or not
            solvedCheckBox.setChecked(crime.isSolved());
            solvedCheckBox.setClickable(false);
        }

        //if item click open new fragment
        @Override
        public void onClick(View view) {
            //old code from the challenges to get information back from the fragment to the parent
           /* Intent in = CrimeActivity.newIntent(getActivity(), crime.getId());
            startActivityForResult(in, REQUEST_CRIME);
*/
            //using viewPager to open each fragment
            /*Intent in = CrimePagerActicity.newIntent(getActivity(), crime.getId());
            startActivity(in);*/

            //using callbacks inferface to inflate the appropriete layout depending on device/tablet
            callbacks.onCrimeSelected(crime);
            //get the position of lcicked item to update recyrcler view's only 1 item instead of whole list
            clickedItemPos = getLayoutPosition();
            //get the positon clicked for test
            Toast.makeText(getActivity(), "pos " + clickedItemPos, Toast.LENGTH_SHORT).show();

        }

    }


    //------------------------------CRIME ADAPTER SUB CLASS
    // crime adapter to get information for each list item of crime and populate the Recycler View
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> crimes;

        //init the adapter with list of crimes
        public CrimeAdapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        //get  the size of the list to know how many items to display
        @Override
        public int getItemCount() {
            return crimes.size();
        }

        //get each crime at position and update the View holder(crimeholder) with the crime title
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = crimes.get(position);
            holder.bindCrime(crime);
        }

        //uses  list item layout for each list crime item in the recycler view
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.crime_list_item, parent, false);

            return new CrimeHolder(view);
        }

        //set crimes for the adapter if data changed
        public void setCrimes(List<Crime> crimes) {
            this.crimes = crimes;
        }


    }
}
