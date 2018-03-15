package com.example.user.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by Ivo Georgiev(IfChyy)
 * Crime List Fragment
 * holding the recycler view of all crimes
 */

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;
    private static final String REQUEST_POSITION = "item_position";

    private RecyclerView crimeRecyclerView;
    private CrimeAdapter adapter;
    public static List<Crime> crimes;

    private String tempTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        UpdateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    protected void UpdateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimes = crimeLab.getCrimes();

        if (adapter == null) {
            adapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(adapter);
        } else {

            adapter.notifyItemChanged(7);

        }

    }

    //get result extra from fragment frament list item
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {

            tempTitle = CrimeFragment.wasAnswerShown(data);
            Toast.makeText(getActivity(), tempTitle + "", Toast.LENGTH_SHORT).show();
        }
    }




    //------------------------------CRIME HOLDER SUBCLASS
// view holder for the recycler view list items
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView titleTextView;
        private TextView dateTextView;
        private CheckBox solvedCheckBox;

        private Crime crime;

        public CrimeHolder(View itemView) {
            super(itemView);
            //set click listener on the whole list item not a particular view
            itemView.setOnClickListener(this);

            titleTextView = itemView.findViewById(R.id.crime_list_item_title_text_view);
            dateTextView = itemView.findViewById(R.id.crime_list_item_date_text_view);
            solvedCheckBox = itemView.findViewById(R.id.crime_list_item_solved_check_box);

        }

        //binding views to information from list
        public void bindCrime(Crime crimee) {
            crime = crimee;
            titleTextView.setText(crime.getTitle());
            dateTextView.setText(crime.getDate().toString());
            solvedCheckBox.setChecked(crime.isSolved());
            solvedCheckBox.setClickable(false);
        }

        @Override
        public void onClick(View view) {
            Intent in = CrimeActivity.newIntent(getActivity(), crime.getId());
            in.putExtra(REQUEST_POSITION, getLayoutPosition());
            startActivityForResult(in, REQUEST_CRIME);

        }

    }


    //------------------------------CRIME ADAPTER SUB CLASS
//crime adapter to get information for each list item of crime and display it
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> crimes;

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

        //uses support list item layout for each list item in the recycler view
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.crime_list_item, parent, false);

            return new CrimeHolder(view);
        }


    }
}
