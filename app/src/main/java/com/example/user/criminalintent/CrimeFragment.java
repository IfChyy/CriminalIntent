package com.example.user.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ivo Georgiev(IfChyy)
 * Crime fragment shows infomration for each particular clicked crime
 * At the top is the Title followed by crime titleField(name, person)
 * after that are the details of the crime: Date ( a button alowing to specify date
 * and a check box informing the crime specialist if the crime was solved or not
 */

public class CrimeFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_CRIME_ID = "crime_id";
    public static final String ITEM_ID = "item_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int DIALOG_DATE1 = 25;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private CheckBox solvedCheckBox;

    private FragmentManager manager;
    private UUID crimeId;

    //called when new instance of crimefragment is needed to be creade
    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //oncreate added to add the menu using setHasOptionsMenu
    //and init crime with crime id gotten from serializable bundle
    //return result method to return if fragment after closing returnrs reuslt to parent CHALLENGE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //instance of crime

        crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        returnResult();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);


        //edit text title field for crime fragment
        //added text changed listener
        titleField = v.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                crime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //date button to pick the date of the crime
        dateButton = v.findViewById(R.id.crime_date);
        dateButton.setOnClickListener(this);

        //time button to pick the time of the crime
        timeButton = v.findViewById(R.id.crime_time);
        timeButton.setOnClickListener(this);
        //using method updateDate to setText of the buttons
        updateDate();
        updateTime();

        //solved check box and ad a listener if checked which updates crime checked state
        solvedCheckBox = v.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });


        return v;

    }


    //override options menu to add + add button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

    }

    //get menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab crimeLab = CrimeLab.get(getActivity());
                for (int i = 0; i < crimeLab.getCrimes().size(); i++) {
                    if(crimeLab.getCrimes().get(i).getId().equals(crimeId)){
                        crimeLab.getCrimes().remove(i);
                        getActivity().finish();

                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //returns a result to parent fragment/activity after completion of this one
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra(ITEM_ID, crime.getTitle()));
    }

    //returns intent extra with key extra_answer_shown and value false
    public static String wasAnswerShown(Intent result) {
        return result.getStringExtra(ITEM_ID);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == dateButton.getId()) {
           /* manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            //requests information using parametar from previous fragment
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(manager, DIALOG_DATE);*/

            Intent in = DatePickerActivity.newIntent(getActivity(), crime.getDate());
            // startActivity(in);
            startActivityForResult(in, DIALOG_DATE1);

        }

        if (view.getId() == timeButton.getId()) {
            manager = getFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(crime.getDate());
            //requests information using parametar from previous fragment
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            dialog.show(manager, DIALOG_TIME);

        }
    }

    //get the resulted date from date picker dialog fragment in onactivity result method

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }//add or dialog date 1 for dialog beeing in activity fragment not a dialog itself
        if (requestCode == REQUEST_DATE || requestCode == DIALOG_DATE1) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();


        } else if (requestCode == REQUEST_TIME) {

            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            crime.getDate().setMinutes(time.getMinutes());
            crime.getDate().setHours(time.getHours());
            //time button
            updateTime();


        }
    }

    //method to update dateButton text and time button text
    private void updateDate() {
        //date button
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        Date date = crime.getDate();
        dateButton.setText(simpleDateFormat.format(date));

    }

    //method to update dateButton text and time button text
    private void updateTime() {
        //date button
        String hours = String.valueOf(crime.getDate().getHours());
        String minutes = String.valueOf(crime.getDate().getMinutes());
        timeButton.setText(hours + ":" + minutes);

    }


}
