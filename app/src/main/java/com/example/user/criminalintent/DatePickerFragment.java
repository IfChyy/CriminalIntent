package com.example.user.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
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
 */

public class DatePickerFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.example.user.criminalintent.date";
    private DatePicker datePicker;

    @Override
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

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_title).setPositiveButton(android.R.string.ok, this).create();
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

    //dialog interface click listener to send the date pack with resultCode ok
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year, month, day).getTime();
        sendResult(Activity.RESULT_OK, date);
    }
}
