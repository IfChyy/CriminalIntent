package com.example.user.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.CancellationException;


/**
 * Created by Ivo Georgiev(IfChyy)
 * <p>
 * DatePickerFragment is a dialog showing a callendar and allowing a user to pick a date
 * CHANGED AFTER A CHALLENGE IN CHAPTER 12 TO ONCREATEVIEW INSTAD OF DIALOG
 * CUSTOM LAYOUT AND MORE
 * AFTER CHAPTER 17 GO BACK TO PAGE 239 TO FINISH THE LAST CHALLENGE:
 * MODIFY CRIMINALINTENT TO PRESENT THE DatePickerFragment AS A FULL SCREEN ACTIVITY
 * WHEN RUNNING ON A PHONE. WHEN RUNNING ON A TABLE, PRESENT THE DatePickerFragment AS A DIAGLO>!!!!
 */

public class DatePickerFragment extends DialogFragment implements View.OnClickListener {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.example.user.criminalintent.date";
    private DatePicker datePicker;
    private Button okButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //get the argumentad passed date and get values for year month and day
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        //initilise datepicked dialog
        datePicker = v.findViewById(R.id.dialog_date_date_picker);
        datePicker.init(year, month, day, null);

        //init ok button to pass the information back to the parent fragment
        okButton = v.findViewById(R.id.date_picker_ok_button);
        okButton.setOnClickListener(this);

        return v;
    }
    //REMOVED AFTER CHALLENGE< KEPT FOR FUTURE REFERENCE
   /* @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get the argumentad passed date and get values for year month and day
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        //initilise datepicked dialog
        datePicker = v.findViewById(R.id.dialog_date_date_picker);
        datePicker.init(year, month, day, null);

        okButton = v.findViewById(R.id.date_picker_ok_button);
        okButton.setOnClickListener(this);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_title).create();
    }*/


    //perform on click of ok button to send the date picked back to parent
    @Override
    public void onClick(View view) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year, month, day).getTime();
        //sendResult(Activity.RESULT_OK, date);
        //getDialog().dismiss();
        //CHALENGE OPEN DATE PICKER DIALOG AS ACTIVITY

        //if tablet mode of display date picker in activt/ else display in dialog
        if (getActivity().findViewById(R.id.detail_fragment_container) != null) {
            sendResult(Activity.RESULT_OK, date);
            getDialog().dismiss();

        } else {
            Intent in = new Intent();
            in.putExtra(EXTRA_DATE, date);
            getActivity().setResult(Activity.RESULT_OK, in);

            getActivity().finish();
        }


    }


    //pass date argument from other fragmentsto this one with containing date element
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);

        return datePickerFragment;
    }

    //METHOD TO send result from dialog fragment(Date picker) to fragment (Crime) shown for the user
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


    //DISSABLED AFTER CHALLENGE KEPT FOR REFERENCE
/*    //dialog interface click listener to send the date pack with resultCode ok
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year, month, day).getTime();
        sendResult(Activity.RESULT_OK, date);
    }*/
}
