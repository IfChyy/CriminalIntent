package com.example.user.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private CheckBox solvedCheckBox;

    private FragmentManager manager;

    //called when new instance of crimefragment is needed to be creade
    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //instance of crime

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
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
        //using method updateDate to setText of the button
        dateButton = v.findViewById(R.id.crime_date);
        updateDate();
        dateButton.setOnClickListener(this);

        //time button to pick the time of the crime
        timeButton = v.findViewById(R.id.crime_time);
        timeButton.setText("ELLO");
        timeButton.setOnClickListener(this);

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
            manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            //requests information using parametar from previous fragment
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(manager, DIALOG_DATE);

        }

        if(view.getId() == timeButton.getId()){
            manager = getFragmentManager();
            TimePickerFragment dialog = new TimePickerFragment();
            dialog.show(manager, DIALOG_TIME);

        }
    }

    //get the resulted date from date picker dialog fragment in onactivity result method

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDate();

        }
    }

    //method to update dateButton text
    private void updateDate(){
        //date button
        Calendar calendar = Calendar.getInstance();
        String dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String calendarDate = DateFormat.getDateInstance().format(crime.getDate());
        dateButton.setText(dayLongName +",  " + calendarDate);
    }
}
